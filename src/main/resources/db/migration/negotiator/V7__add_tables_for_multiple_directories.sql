CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE list_of_directories (
    "id" SERIAL NOT NULL,
    "name" CHARACTER VARYING(255) NOT NULL,
    "url" CHARACTER VARYING(255) NOT NULL,
    "rest_url" CHARACTER VARYING(255) NOT NULL,
    "username" CHARACTER VARYING(255) NOT NULL,
    "password" CHARACTER VARYING(255) NOT NULL,
    "api_username" CHARACTER VARYING(255) NOT NULL,
    "api_password" CHARACTER VARYING(255) NOT NULL,
    "resource_biobanks" CHARACTER VARYING(255) NOT NULL,
    "resource_collections" CHARACTER VARYING(255) NOT NULL,
    "description" TEXT,
    "sync_active" BOOLEAN,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE list_of_directories IS 'Table to store directories';
COMMENT ON COLUMN list_of_directories."id" IS 'primary key';
COMMENT ON COLUMN list_of_directories."name" IS 'The directory name';
COMMENT ON COLUMN list_of_directories."url" IS 'The directory url';
COMMENT ON COLUMN list_of_directories."rest_url" IS 'The directory API url';
COMMENT ON COLUMN list_of_directories."username" IS 'The directories username';
COMMENT ON COLUMN list_of_directories."password" IS 'The directoryíes password';
COMMENT ON COLUMN list_of_directories."api_username" IS 'The directories API username';
COMMENT ON COLUMN list_of_directories."api_password" IS 'The directories API password';
COMMENT ON COLUMN list_of_directories."resource_biobanks" IS 'The directories biobank model';
COMMENT ON COLUMN list_of_directories."resource_collections" IS 'The directories collection model';
COMMENT ON COLUMN list_of_directories."description" IS 'The description for this directory';

ALTER TABLE biobank ADD COLUMN list_of_directories_id INTEGER;
COMMENT ON COLUMN biobank."list_of_directories_id" IS 'The directorie Id where the biobank belongs to';
ALTER TABLE collection ADD COLUMN list_of_directories_id INTEGER;
COMMENT ON COLUMN collection."list_of_directories_id" IS 'The directorie Id where the biobank belongs to';

UPDATE public.query SET json_text = '{"token":"' || uuid_generate_v4() || '",' || substring(json_text, 2) WHERE json_text ILIKE '%token%';
UPDATE public.query SET json_text = '{"searchQueries":[' || json_text || '],"negotiatorToken":"' || uuid_generate_v4() || '"}';