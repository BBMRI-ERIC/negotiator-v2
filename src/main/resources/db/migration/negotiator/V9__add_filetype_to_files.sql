ALTER TABLE query_attachment
    ADD COLUMN file_type character varying DEFAULT 'other';

ALTER TABLE query_attachment_private
    ADD COLUMN file_type character varying DEFAULT 'other';

ALTER TABLE query_attachment
    ADD COLUMN comment_id INTEGER DEFAULT 0;

ALTER TABLE query_attachment_private
    ADD COLUMN offer_id INTEGER DEFAULT 0;

ALTER TABLE offer
    ADD COLUMN attachment boolean DEFAULT true;

ALTER TABLE comment
    ADD COLUMN attachment boolean DEFAULT true;