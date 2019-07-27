ALTER TABLE public.query_attachment
    ADD COLUMN file_type character varying DEFAULT 'other';

ALTER TABLE public.query_attachment_private
    ADD COLUMN file_type character varying DEFAULT 'other';