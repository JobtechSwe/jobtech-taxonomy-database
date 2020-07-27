# jobtech-taxonomy-database (no longer maintained)

The code has been moved to https://gitlab.com/JonasClaessson/jobtech-taxonomy-database

Taxonomy database management utilities for JobTech

## ENV
    Add an environment variable named "jobtechtaxonomy" to "PROD" if you want to use the prod datomic

## Installation

Download a copy of the legacy database here:
https://gitlab.com/af-group/ams-taxonomy-backup
Follow the instructions in the readme to start local SQL database server

## Dumping database to preserve new concept ids for future migrations

Run `lein run -m jobtech-taxonomy-database.dump my-database-name`

This will update `resources/concept-to-taxonomy.json` and `resources/taxonomy-to-concept.json`
with latest data from the database.

## Creating a new database when there are already existing ones

Run `lein run my-database-name"`

Alternatively you can start a repl, modify `config.clj` to use your database name and
evaluate transaction steps in `jobtech-taxonomy-database.core` one by one.

Migration process will create a new database and fill it with data from SQL database.
It will also use existing concept ids from `resources/taxonomy-to-concept.json` where
possible to preserve them between different databases.

## License

Copyright © 2020 JobTech

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
