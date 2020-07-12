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

INSERT INTO public.network(name, description, acronym, directory_id, list_of_directories_id)
SELECT 'BBMRI.' || substring(directory_id, 15, 2) AS name, substring(directory_id, 15, 2) || ' National Node' description,
       'BBMRI.' || substring(directory_id, 15, 2) AS acronym,
       'bbmri-eric:networkID:BBMRI.' || substring(directory_id, 15, 2) directory_id, MAX(list_of_directories_id) list_of_directories_id
FROM public.biobank
WHERE directory_id ILIKE 'bbmri-eric%' AND list_of_directories_id = 1
  AND substring(directory_id, 15, 2) != 'FI' AND substring(directory_id, 15, 2) != 'DE'
  AND substring(directory_id, 15, 2) != 'NO' AND substring(directory_id, 15, 2) != 'PL'
  AND substring(directory_id, 15, 2) != 'CH'
GROUP BY substring(directory_id, 15, 2);