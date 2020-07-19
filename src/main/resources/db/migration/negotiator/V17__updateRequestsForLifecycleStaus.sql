INSERT INTO public.request_status(
    query_id, status, status_type, status_date, status_user_id, status_json)
SELECT id, 'created', 'created', query_creation_time, researcher_id, ''
FROM public.query;

INSERT INTO public.request_status(
    query_id, status, status_type, status_date, status_user_id, status_json)
SELECT id, 'under_review', 'review', query_creation_time + interval '1 minute', researcher_id, ''
FROM public.query;

INSERT INTO public.request_status(
    query_id, status, status_type, status_date, status_user_id, status_json)
SELECT id, 'approved', 'review', query_creation_time + interval '2 minute', researcher_id, '{"statusApprovedText":""}'
FROM public.query;

INSERT INTO public.request_status(
    query_id, status, status_type, status_date, status_user_id, status_json)
SELECT id, 'waitingstart', 'start', query_creation_time + interval '3 minute', researcher_id, ''
FROM public.query;

INSERT INTO public.request_status(
    query_id, status, status_type, status_date, status_user_id, status_json)
SELECT id, 'started', 'start', negotiation_started_time, researcher_id, ''
FROM public.query WHERE negotiation_started_time IS NOT NULL;

INSERT INTO public.query_lifecycle_collection(
    query_id, status_user_id, collection_id, status, status_date, status_type, status_json)
SELECT q.id, researcher_id, qc.collection_id, 'contacted', negotiation_started_time + interval '1 minute', 'contact', ''
FROM public.query q JOIN public.query_collection qc ON q.id = qc.query_id
WHERE negotiation_started_time IS NOT NULL;