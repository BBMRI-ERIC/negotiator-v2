CREATE TYPE "person_type" AS ENUM ('OWNER', 'RESEARCHER');


CREATE TABLE "location" (
    "id" SERIAL NOT NULL,
    "name" CHARACTER VARYING(255) NOT NULL,
    PRIMARY KEY ("id")
);


COMMENT ON TABLE "location" IS 'Table to store locations of owner';
COMMENT ON COLUMN "location"."id" IS 'primary key';
COMMENT ON COLUMN "location"."name" IS 'location name';



CREATE TABLE "person" (
    "id" SERIAL NOT NULL,
    "person_type" "person_type" NOT NULL,
    "auth_subject" CHARACTER VARYING(255) NOT NULL UNIQUE,
    "auth_name" CHARACTER VARYING(255) NOT NULL,
    "auth_email" CHARACTER VARYING(255) NOT NULL,
    "person_image" BYTEA,
    "location_id" INTEGER,
    PRIMARY KEY ("id"),
    FOREIGN KEY ("location_id") REFERENCES "location"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "locationIdIndexOwner" ON "person" (location_id);


COMMENT ON TABLE "person" IS 'person table which is parent of researcher and owner';
COMMENT ON COLUMN "person"."id" IS 'primary key';
COMMENT ON COLUMN "person".auth_subject IS 'authentication string that comes from the authentication service';
COMMENT ON COLUMN "person".auth_name IS 'the real name of the user, value comes from the authentication service';
COMMENT ON COLUMN "person".auth_email IS 'the email of the user, value comes from the authentication service';
COMMENT ON COLUMN "person".person_type IS 'describes wether the person is researcher or owner - one of the the two child classes. ';
COMMENT ON COLUMN "person".person_image IS 'image/avatar of the person';
COMMENT ON COLUMN "person".location_id IS 'only valid for biobank owners, the ID of the location he belongs to';



CREATE TABLE "query" (
    "id" SERIAL NOT NULL,
    "title" CHARACTER VARYING(255) NOT NULL,
    "text" TEXT,
    "query_creation_time" TIMESTAMP WITHOUT TIME ZONE,
    "researcher_id" INTEGER NOT NULL,
    PRIMARY KEY ("id"),
    FOREIGN KEY ("researcher_id") REFERENCES "person"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "researcherIdIndexQuery" ON "query" (researcher_id);


COMMENT ON TABLE "query" IS 'query table to contain all  queries';
COMMENT ON COLUMN "query"."id" IS 'primary key';
COMMENT ON COLUMN "query"."title" IS 'title of query';
COMMENT ON COLUMN "query"."text" IS 'text of query';
COMMENT ON COLUMN "query".query_creation_time IS 'date and time of query with out time zone';
COMMENT ON COLUMN "query".researcher_id IS 'Foreign key. Exists as primary key in the researcher table(which takes it in turn from the person table)';



CREATE TABLE "tag" (
    "id" SERIAL NOT NULL,
    "query_id" INTEGER NOT NULL,
    "text" CHARACTER VARYING(255) NOT NULL,
    PRIMARY KEY("id"),
    FOREIGN KEY ("query_id") REFERENCES "query"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "queryIdIndexTag" ON "tag" (query_id);


COMMENT ON TABLE "tag" IS 'Table that contains tags for queries';
COMMENT ON COLUMN "tag"."id" IS 'Primary key';
COMMENT ON COLUMN "tag".query_id IS 'Foreign key which exists in the query table as primary key';
COMMENT ON COLUMN "tag"."text" IS 'Text for the given tag id';



CREATE TABLE "comment" (
    "id" SERIAL NOT NULL,
    "query_id" INTEGER NOT NULL,
    "person_id" INTEGER NOT NULL,
    "comment_time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "text" TEXT NOT NULL,
    PRIMARY KEY("id"),
    FOREIGN KEY ("query_id") REFERENCES "query"("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("person_id") REFERENCES "person"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "queryIdIndexComment" ON "comment" (query_id);
CREATE INDEX "personIdIndexComment" ON "comment" (person_id);


COMMENT ON TABLE "comment" IS 'table to store commentCount on a query';
COMMENT ON COLUMN "comment"."id" IS 'Primary key';
COMMENT ON COLUMN "comment".query_id IS 'Foreign key which exists as primary key in the query table. ';
COMMENT ON COLUMN "comment".comment_time IS 'timestamp of when the comment was made.';
COMMENT ON COLUMN "comment".person_id IS 'Foreign key which exists as primary key in the person table. describes the person who made the comment.';
COMMENT ON COLUMN "comment"."text" IS 'Text of the comment.';



CREATE TABLE "tagged_query" (
    "query_id" INTEGER NOT NULL,
    "tag_id" INTEGER NOT NULL,
    PRIMARY KEY("query_id", "tag_id"),
    FOREIGN KEY ("tag_id") REFERENCES "tag"("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("query_id") REFERENCES "query"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "tagIdIndexTaggedQuery" ON "tagged_query" (tag_id);
CREATE INDEX "queryIdIndexTaggedQuery" ON "tagged_query" (query_id);


COMMENT ON TABLE "tagged_query" IS 'Table for queries that are tagged by names e.g. colonCancer,SkinCancer etc. Tag names need to be decided .';
COMMENT ON COLUMN "tagged_query".query_id IS 'This column along with tagId will make the primary key. Its also a foreign key here, taken from query table';
COMMENT ON COLUMN "tagged_query".tag_id IS 'This column along with the Queryid column will make the primary key. Its also a foreign key here, taken from "tag" table';



CREATE TABLE "flagged_query" (
    "query_id" INTEGER NULL,
    "person_id" INTEGER NULL,
    "flag" CHARACTER(1),
    PRIMARY KEY("query_id", "person_id"),
    FOREIGN KEY ("person_id") REFERENCES "person"("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("query_id") REFERENCES "query"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "ownerIdIndexFlaggedQuery" ON "flagged_query" (person_id);
CREATE INDEX "queryIdIndexFlaggedQuery" ON "flagged_query" (query_id);


COMMENT ON TABLE "flagged_query" IS 'Table for queries that are flagged/bookmarked. bookmark options are starred, archived and ignored.';
COMMENT ON COLUMN "flagged_query".query_id IS 'This column along with ownerId will make the primary key. Its also a foreign key here, taken from query table';
COMMENT ON COLUMN "flagged_query".person_id IS 'This column along with the id column will make the primary key. Its also a foreign key here, taken from person table';
COMMENT ON COLUMN "flagged_query"."flag" IS 'The flag of the comment. One character letter. (A) archived, (I) ignored, (S) starred';


CREATE TABLE "query_location" (
    "query_id" INTEGER NOT NULL,
    "location_id" INTEGER NOT NULL,
    PRIMARY KEY("query_id", "location_id"),
    FOREIGN KEY ("query_id") REFERENCES "query"("id") ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY ("location_id") REFERENCES "location"("id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX "queryIdIndexQueryLocation" ON "query_location" (query_id);
CREATE INDEX "locationIdIndexQueryLocation" ON "query_location" (location_id);


COMMENT ON TABLE "query_location" IS 'Table for all the locations that have replied to a query.';
COMMENT ON COLUMN "query_location".query_id IS 'This column along with location_Id will make the primary key. Its also a foreign key here, taken from query table';
COMMENT ON COLUMN "query_location".location_id IS 'This column along with the query_id column will make the primary key. Its also a foreign key here, taken from "location" table';
