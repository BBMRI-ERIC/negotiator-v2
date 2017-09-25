ALTER TABLE public.query ADD negotiation_started BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN "query"."negotiation_started" IS 'describes if the negotiation has started for the query or not.';
