ALTER TABLE public.list_of_directories
    ADD COLUMN resource_networks character varying(255);

ALTER TABLE public.list_of_directories
    ADD COLUMN bbmri_eric_national_nodes boolean;

ALTER TABLE public.network
    ADD COLUMN list_of_directories_id integer;

CREATE TABLE public.person_network
(
    person_id integer,
    network_id integer,
    CONSTRAINT person_network_pk PRIMARY KEY (person_id, network_id)
);

ALTER TABLE public.network_biobank_link
    ADD CONSTRAINT network_biobank_link_pk PRIMARY KEY (biobank_id, network_id);

ALTER TABLE public.network_collection_link
    ADD CONSTRAINT network_collection_link_pk PRIMARY KEY (collection_id, network_id);