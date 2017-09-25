ALTER TABLE public.query ADD negotiation_started_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL;
ALTER TABLE public.connector_log ADD last_negotiation_time TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL;

ALTER TABLE public.connector_log ALTER COLUMN last_query_time DROP NOT NULL;
ALTER TABLE public.connector_log ALTER COLUMN last_query_time SET DEFAULT NULL;

COMMENT ON COLUMN "query"."negotiation_started_time" IS 'Time when the researcher started the negotiation for the query.';
COMMENT ON COLUMN "connector_log"."last_negotiation_time" IS 'Time when the connector last fetched for new negotiations.';
