ALTER TABLE public.person
    ADD COLUMN synced_directory BOOLEAN NOT NULL DEFAULT FALSE;