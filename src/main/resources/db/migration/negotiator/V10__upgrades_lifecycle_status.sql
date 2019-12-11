ALTER TABLE public.query_lifecycle_collection
    RENAME biobank_id TO collection_id;

CREATE TABLE public.request_status
(
    id serial,
    query_id integer NOT NULL,
    status character varying,
    status_type character varying,
    status_date timestamp without time zone,
    status_user_id integer,
    status_json jsonb,
    PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    );