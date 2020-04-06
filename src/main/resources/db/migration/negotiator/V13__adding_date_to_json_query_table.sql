ALTER TABLE public.json_query ADD COLUMN query_create_time timestamp with time zone DEFAULT now();
ALTER TABLE public.json_query ADD COLUMN query_id integer DEFAULT 0;