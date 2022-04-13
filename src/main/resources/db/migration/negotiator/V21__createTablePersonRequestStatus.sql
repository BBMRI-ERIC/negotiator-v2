CREATE TABLE public.person_querylifecycle
(
    person_id integer NOT NULL,
    query_lifecycle_collection_id integer NOT NULL,
    read BOOLEAN,
    date_read timestamp without time zone,
    CONSTRAINT person_querylifecycle_pk PRIMARY KEY (person_id, query_lifecycle_collection_id)
);
