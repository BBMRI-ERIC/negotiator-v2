## How to create new DB upgrades
In order to create new DB upgrades, simply add in new SQL files into this directory.
The files need to have the pattern like "V1__database.sql", explaining the parts:

V1 -> version number, next file must be V2 and so on

__ -> doubly underscore!

database -> some human readable description, delimted by underscores (fancy_description_of_what_this_does)

.sql -> file ending .sql for, well, SQL

Please provide a description for what the SQL file is supposed to do above. You can also link to a ticket 
using a filename like "V99__BIO-993.sql"


### Remarks
Once a SQL file is executed, an entry is made into the DB table "schema_version" with a checksum.

If (for development reasons) you need to have an SQL re-executed again, you need to delete the 
according line from the table. Just changing the file will result in an exception, as
flyway checks for such errors.
Removing the file will not disturb flyway, but make you run into problems once you add a new
file with the same version number again, so do not do that.

### Generate Classes
Once the negotiators is redeployed the DB changes are applied. Once the DB is updated (and any new tables are added) 
the classes can be generated with jooq: `mvn org.jooq:jooq-codegen-maven:generate`