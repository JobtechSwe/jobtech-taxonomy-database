#!/bin/bash
#
# Script som:
#  1. installerar datomic-pro i /var/tmp
#  2. installerar apt-paketen openjdk-11-jre och docker.io om de saknas
#  3. startar transaktorn
#  4. skapar datomicdatabasen
#  5. startar peer-servern
#  6. startar docker-mssql-servern
#  7. gör en restore av legacytaxonomin i mssql-servern
#  8. starta Datomic-konsolen på http://localhost:8080/browse
#
#
# Engångsförberedelser:
#  1. Klona ams-taxonomy-backup från gitlab, och konfigurera
#     variabeln AMSTAXONOMYBACKUPDIR nedan så den pekar på den katalogen.
#  2. Välj vad du vill att din datomicdatabas ska heta, och konfigurera
#     DBNAME nedan. Ska matcha det som står i config.clj.
#  3. Skapa filen 'datomic-secrets.properties' i skriptets katalog
#     med följande innehåll (detaljerna från Datomickontot):
#       DATOMICMAIL='your-datomic-login-email'
#       DATOMICPASS='your-datomic-password'
#       DATOMICLICENSEKEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\
#       xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\
#       xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\
#       xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\
#       xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\
#       xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
#
#
# Att köra skriptet:
#  1. Kör igång det bara, och hoppas på det bästa. Ge flaggan -d för att
#     ta bort eventuellt redan existerande databasinnehåll.
#
#
#
# Om du får oväntat java-strul, testa detta:
#   log4j kräver java10.  För att få lein att använda java10 så måste
#   detta finnas i profiles.clj (förutsätter att openjdk-11-jdk är
#   installerat):  {:user { :java-cmd "/usr/bin/java" } }


if [ "$1" == "-d" ]; then
    DELETE_OLD_DB=1
fi


SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
source "$SCRIPTDIR"/datomic-secrets.properties



## Configuration
# Point to the directory where ams-taxonomy-backup is checked out
AMSTAXONOMYBACKUPDIR=~/proj/ams-taxonomy-backup


# The datomic database name
DBNAME=taxonomy_v13
KEY=myaccesskey
SECRET=mysecret
VERBOSE=1
TXIP=localhost


## Datomic params
VERSION='0.9.5786'
DATOMICINSTALLDIR='/var/tmp'
DATOMICDIR="$DATOMICINSTALLDIR/datomic-pro-$VERSION"


## Maintain a list of server pids
PIDFILE=$(mktemp)


## Install Datomic if not already done
if [ ! -d "$DATOMICINSTALLDIR/datomic-pro-$VERSION" ]; then
    if [ ! -f "/var/tmp/datomic-pro-$VERSION.zip" ]; then
	wget --http-user="$DATOMICMAIL" --http-password="$DATOMICPASS" https://my.datomic.com/repo/com/datomic/datomic-pro/"$VERSION"/datomic-pro-"$VERSION".zip -O /var/tmp/datomic-pro-"$VERSION".zip
    fi
    pushd "$DATOMICINSTALLDIR"
    unzip "datomic-pro-$VERSION.zip"

    ## Fix stupid bug in console script:
    sed -i 's|^/usr/bin/env java|exec /usr/bin/env java|' datomic-pro-$VERSION/bin/console

    popd
fi


## Is java installed?
if [ ! -d /usr/lib/jvm/java-8-openjdk-amd64/jre/bin ]; then
    echo "Java 8 not installed in Ubuntu's default location, installing openjdk-8-jre (prompting for sudo pass now)" >&2
    sudo apt-get update && sudo apt-get install openjdk-8-jre
fi


## Datomic Peer REQUIRES java 8 - it does NOT work with java 11
## (you would see error message from MQ "cannot connect").
export PATH=/usr/lib/jvm/java-8-openjdk-amd64/jre/bin:$PATH


## Is docker installed?
if ! which docker; then
    echo "No docker installed, installing docker.io (may prompt for sudo pass now)" >&2
    sudo apt-get update && sudo apt-get install docker.io
fi

## Is user added to the docker group?
if ! groups | grep -q docker; then
    sudo adduser "$USER" docker
    newgrp docker
    newgrp "$USER"
    echo
    echo "**** You were added to the group 'docker'. I tried to force this change in this session. If docker fails, please logout and login again to execute the change properly. This is only necessary once."
    echo
fi


function kill_servers() {
    local PIDS_TO_KILL="$(cat $PIDFILE)"
    echo "**** run kill: $PIDS_TO_KILL" >&2
    if [ ! -z "$PIDS_TO_KILL" ]; then
        echo "**** killing $PIDS_TO_KILL"
        kill $PIDS_TO_KILL
    fi

    if docker exec -it mssql-server true ; then
        docker container kill mssql-server
    fi
}
trap "echo "prekill">&2 ; kill_servers" EXIT



function log() {
    local CALLERNAME=$1

    stdbuf -i0 -o0 -e0 cat - | tee /tmp/log_"$CALLERNAME".log | sed "s|^|$CALLERNAME]\t|" |\
    (if [ "$VERBOSE" != 0 ]; then
        cat -
    else
        cat - > /dev/null
    fi)
}



function start_transactor() {
    pushd "$DATOMICDIR"
    cp config/samples/dev-transactor-template.properties transactor.properties
    sed -i -e "/license-key=/a license-key=$DATOMICLICENSEKEY" -e "/^license-key=$/d" transactor.properties
    #sed -i "/host=localhost/a alt-host=${ALT_HOST:-127.0.0.1}" transactor.properties
    #sed -i "s/host=localhost/host=$TXIP/" transactor.properties
    #sed -i '/^host=/a ping-host=localhost' transactor.properties
    #sed -i '/^host=/a ping-port=9999' transactor.properties
    if [ "$DELETE_OLD_DB" == 1 ]; then
        echo >&2
        echo >&2
        echo "**** Deleting old datomic contents...." >&2
        echo >&2
        rm -rf "$DATOMICDIR"/data/db/datomic*
    fi
    stdbuf -i0 -o0 -e0 bin/transactor transactor.properties 2>&1 &
    echo -n "$! " >> $PIDFILE
    popd
}



function check_transactor() {
    curl -I http://"$TXIP":9999/health | grep -q "HTTP/1.1 200 OK"
}



function create_db() {
    pushd "$DATOMICDIR"
    echo "(require '[datomic.api :as d])(d/create-database \"datomic:dev://$TXIP:4334/$DBNAME\")" | bin/repl
    popd
}



function start_datomic_peer_server() {
    pushd "$DATOMICDIR"
    #bin/run -m datomic.peer-server -h "$TXIP" -p 8998 -a "$KEY","$SECRET" -d "$DBNAME",datomic:dev://$TXIP:4334/"$DBNAME" &
    java -server -Xmx1g -Xms1g -cp resources:lib/*:datomic-transactor-pro-0.9.5786.jar:samples/clj:bin: clojure.main -i bin/bridge.clj -m datomic.peer-server -h "$TXIP" -p 8998 -a "$KEY","$SECRET" -d "$DBNAME",datomic:dev://"$TXIP":4334/"$DBNAME" 2>&1 &
    echo -n "$! " >> $PIDFILE
    popd
}



function start_console() {
    pushd "$DATOMICDIR"
    bin/console -p 8080 dev "datomic:dev://$TXIP:4334/" 2>&1 &
    echo -n "$! " >> $PIDFILE
    popd
}



function start_taxonomydb() {
    if ! docker exec -it mssql-server true 2>/dev/null 1>&2; then
        pushd "$AMSTAXONOMYBACKUPDIR"

        docker run --rm -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=Taxonomy123!' -e 'MSSQL_PID=Express' -v $PWD:/data --name mssql-server -p 1433:1433 -d mcr.microsoft.com/mssql/server:2017-latest

        sleep 10

        docker exec -it mssql-server /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'Taxonomy123!' -Q "RESTORE DATABASE [TaxonomyDBVersion] FROM DISK = N'/data/taxonomydb-version.bak' WITH FILE = 1, NOUNLOAD, REPLACE, NORECOVERY, STATS = 5"

        docker exec -it mssql-server /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'Taxonomy123!' -Q "RESTORE DATABASE TaxonomyDBVersion WITH RECOVERY"

        docker exec -it mssql-server /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'Taxonomy123!' -Q "RESTORE DATABASE [TaxonomiDBSvenskVersion] FROM DISK = N'/data/taxonomydb-svensk-version.bak' WITH FILE = 1, NOUNLOAD, REPLACE, NORECOVERY, STATS = 5"

        docker exec -it mssql-server /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'Taxonomy123!' -Q "RESTORE DATABASE TaxonomiDBSvenskVersion WITH RECOVERY"

        popd
    fi
}



echo "**** start_transactor" >&2
start_transactor | log start_transactor &
sleep 10


echo "**** create_db" >&2
create_db | log create_db &
sleep 5


echo "**** start_datomic_peer_server" >&2
start_datomic_peer_server | log start_datomic_peer_server &
sleep 5


echo "**** start_taxonomydb" >&2
start_taxonomydb | log start_taxonomydb &
sleep 5


echo "**** start_console" >&2
start_console &


wait
