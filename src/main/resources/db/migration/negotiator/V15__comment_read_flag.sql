CREATE TABLE public.person_comment
(
    person_id integer NOT NULL,
    comment_id integer NOT NULL,
    read BOOLEAN,
    date_read timestamp without time zone
);

CREATE TABLE public.person_offer
(
    person_id integer NOT NULL,
    offer_id integer NOT NULL,
    read BOOLEAN,
    date_read timestamp without time zone
);
