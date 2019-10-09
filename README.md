# jobtech-taxonomy-database

FIXME: description

## Installation



Download a copy of the legacy database here:
https://gitlab.com/af-group/ams-taxonomy-backup


Start datomic

https://docs.datomic.com/on-prem/dev-setup.html


TODO change database name from hello to something more taxnomy-like

    bin/transactor config/samples/dev-transactor-template.properties &

    bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d hello,datomic:dev://localhost:4334/hello &

    bin/console -p 8080 dev datomic:dev://localhost:4334/ &





## Creating a new database when there are already existing ones
0. Edit core.clj, and comment out all converters except the first one (`{:namespace jobtech-taxonomy-database.converters.version-0}`).
1. Configure src/jobtech_taxonomy_database/config.clj to point at an existing database.
2. Start repl.
3. Edit datomic_connection.clj, select a new database name:
 `(def database-name "jobtech-taxonomy-per-dev2")`
4. Run (create-database) in datomic_connection.clj.
5. Quit cider.
6. Configure src/jobtech_taxonomy_database/config.clj to point at the new database.
7. Start repl.
8. Run `(-main)`.

## Usage

FIXME: explanation

    $ java -jar jobtech-taxonomy-database-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
