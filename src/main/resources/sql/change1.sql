ALTER TABLE public.query ADD query_xml TEXT NULL;

COMMENT ON COLUMN "query"."query_xml" IS 'XML version of the query';
