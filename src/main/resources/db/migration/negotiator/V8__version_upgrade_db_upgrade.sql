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