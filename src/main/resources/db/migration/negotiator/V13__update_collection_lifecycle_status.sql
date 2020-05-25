ALTER TABLE public.query_lifecycle_collection
    ADD COLUMN id serial;

ALTER TABLE public.query_lifecycle_collection
    ADD PRIMARY KEY (id);

ALTER TABLE public.query_lifecycle_collection
    RENAME lifecycle_time TO status_date;

ALTER TABLE public.query_lifecycle_collection
    ADD COLUMN status_type character varying;

ALTER TABLE public.query_lifecycle_collection
    ADD COLUMN status_json text;

ALTER TABLE public.query_lifecycle_collection
    RENAME person_id TO status_user_id;