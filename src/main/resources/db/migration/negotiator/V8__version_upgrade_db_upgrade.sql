-- DB Upgrade to version 8
-- Adding Networks to the data-model
CREATE TABLE network (
    "id" SERIAL NOT NULL,
    "name" CHARACTER VARYING(255) NOT NULL,
    "description" TEXT,
    "acronym" CHARACTER VARYING(255),
    "directory_id" CHARACTER VARYING(255) NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE network_biobank_link (
    "biobank_id" INTEGER NOT NULL,
    "network_id" INTEGER NOT NULL
);
CREATE INDEX "network_biobank_link_index" ON "network_biobank_link" ("biobank_id", "network_id");

CREATE TABLE network_collection_link (
    "collection_id" INTEGER NOT NULL,
    "network_id" INTEGER NOT NULL
);
CREATE INDEX "network_collection_link_index" ON "network_collection_link" ("collection_id", "network_id");

-- Extending the model for attachments
CREATE TABLE query_attachment_private (
    "id" SERIAL NOT NULL,
    "query_id" INTEGER NOT NULL,
    "person_id" INTEGER NOT NULL,
    "biobank_in_private_chat" INTEGER NOT NULL,
    "attachment_time" TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL,
    "attachment" TEXT NOT NULL,
    PRIMARY KEY ("id")
);

-- Adding lifecycle support
CREATE TABLE query_lifecycle_biobank (
    "query_id" INTEGER NOT NULL,
    "person_id" INTEGER NOT NULL,
    "biobank_id" INTEGER NOT NULL,
    "status" CHARACTER VARYING(255),
    "lifecycle_time" TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);

CREATE TABLE query_lifecycle_collection (
    "query_id" INTEGER NOT NULL,
    "person_id" INTEGER NOT NULL,
    "biobank_id" INTEGER NOT NULL,
    "status" CHARACTER VARYING(255),
    "lifecycle_time" TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);