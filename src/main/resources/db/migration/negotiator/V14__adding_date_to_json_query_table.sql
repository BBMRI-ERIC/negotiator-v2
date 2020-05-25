ALTER TABLE public.json_query ADD COLUMN query_create_time timestamp with time zone DEFAULT now();
ALTER TABLE public.json_query ADD COLUMN query_id integer DEFAULT 0;

UPDATE public.json_query jq SET query_create_time = q.query_creation_time  FROM public.query q WHERE (q.json_text::json ->> 'URL') = (jq.json_text::json ->> 'URL');
UPDATE public.json_query jq SET query_create_time = qq.query_creation_time  FROM (SELECT json_array_elements((q.json_text::jsonb -> 'searchQueries')::json)::json ->> 'URL' urldata , query_creation_time FROM query q) qq WHERE qq.urldata = (jq.json_text::json ->> 'URL');