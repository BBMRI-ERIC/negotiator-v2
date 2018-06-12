ALTER TABLE public.query_collection ADD result_received_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL;

COMMENT ON COLUMN "query_collection"."result_received_time" IS 'the time when the result is received from the connector.';
