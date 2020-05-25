CREATE TABLE public.notification
(
    notification_id serial NOT NULL,
    query_id integer NOT NULL,
    comment_id integer DEFAULT NULL,
    person_id integer NOT NULL,
    notification_type character varying(250),
    create_date timestamp without time zone,
    CONSTRAINT notification_pk PRIMARY KEY (notification_id)
);

CREATE TABLE public.mail_notification
(
    mail_notification_id serial NOT NULL,
    notification_id integer NOT NULL,
    person_id integer NOT NULL,
    email_address character varying(250),
    status character varying(250),
    create_date timestamp without time zone,
    send_date timestamp without time zone,
    subject character varying(500),
    body text,
    CONSTRAINT mail_notification_pk PRIMARY KEY (mail_notification_id)
);

CREATE TABLE public.notification_setting
(
    notification_setting_id serial NOT NULL,
    person_id integer NOT NULL,
    notification_type character varying(250),
    send_settings character varying(250),
    CONSTRAINT notification_setting_pk PRIMARY KEY (notification_setting_id)
)

