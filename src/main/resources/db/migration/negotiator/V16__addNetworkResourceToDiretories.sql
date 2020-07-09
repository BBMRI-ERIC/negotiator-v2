ALTER TABLE public.list_of_directories
    ADD COLUMN resource_networks character varying(255);

ALTER TABLE public.network
    ADD COLUMN list_of_directories_id integer;