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
    attachment_type character varying DEFAULT 'other',
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

ALTER TABLE query_attachment
    ADD COLUMN attachment_type character varying DEFAULT 'other';

ALTER TABLE comment
    ADD COLUMN attachment boolean DEFAULT false;

ALTER TABLE comment
    ADD COLUMN status character varying DEFAULT 'published';

COMMENT ON COLUMN public.comment.status
    IS 'status: published, deleted, saved';

CREATE TABLE public.query_attachment_comment
(
    id serial NOT NULL,
    query_id integer NOT NULL,
    attachment text NOT NULL,
    attachment_type character varying DEFAULT 'other'::character varying,
    comment_id integer DEFAULT 0,
    CONSTRAINT query_attachment_comment_pkey PRIMARY KEY (id),
    CONSTRAINT query_attachment_comment_query_id_fkey FOREIGN KEY (query_id)
        REFERENCES public.query (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

COMMENT ON TABLE public.query_attachment_comment
    IS 'Table for queries that have one or more attachments uploaded.';

COMMENT ON COLUMN public.query_attachment_comment.query_id
    IS 'This column is a foreign key here, taken from query table';

COMMENT ON COLUMN public.query_attachment_comment.attachment
    IS 'The name of the attached file stored in file system, not including the directory';

CREATE INDEX "queryIdIndexQueryAttachmentComment"
    ON public.query_attachment_comment USING btree
        (query_id)
    TABLESPACE pg_default;