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
    status_json text,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.query
    ADD COLUMN researcher_name character varying;

ALTER TABLE public.query
    ADD COLUMN researcher_email character varying;

ALTER TABLE public.query
    ADD COLUMN researcher_organization character varying;

UPDATE public.query SET researcher_name=auth_name, researcher_email=auth_email, researcher_organization=organization
FROM public.person WHERE public.person.id = researcher_id AND researcher_name IS NULL;