ALTER TABLE public.json_query ADD COLUMN query_create_time timestamp with time zone DEFAULT now();

ALTER TABLE public.json_query ADD COLUMN query_id integer DEFAULT 0;

UPDATE public.json_query jq SET query_create_time = q.query_creation_time
FROM (SELECT *, json_array_elements(subq.json_text::json#>'{searchQueries}') ->> 'URL' url
      FROM public.query subq WHERE subq.json_text IS NOT NULL AND subq.json_text != '') q
WHERE q.json_text IS NOT NULL AND q.json_text != '' AND
        q.url = (jq.json_text::json ->> 'URL');

UPDATE public.json_query q SET query_create_time = sub_update.new_date FROM
(SELECT qm.id update_id, (qb.query_create_time + (qa.query_create_time - qb.query_create_time)/2) new_date FROM public.json_query qm
JOIN public.json_query qb ON qb.id = (SELECT MAX(subqm.id) FROM public.json_query subqm WHERE subqm.id < qm.id AND subqm.query_create_time != qm.query_create_time)
JOIN public.json_query qa ON qa.id = (SELECT MIN(subqm.id) FROM public.json_query subqm WHERE subqm.id > qm.id AND subqm.query_create_time != qm.query_create_time)
WHERE qm.query_create_time = (SELECT query_create_time
FROM public.json_query GROUP BY query_create_time ORDER BY COUNT(*) DESC LIMIT 1)
ORDER BY qm.id) sub_update
WHERE q.id = sub_update.update_id;

SELECT query_create_time, COUNT(*) FROM public.json_query qm
GROUP BY query_create_time ORDER BY COUNT(*) DESC;