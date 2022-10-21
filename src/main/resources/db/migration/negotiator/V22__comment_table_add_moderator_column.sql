alter table public.comment
    add moderated boolean;

comment on column public.comment.moderated is 'marks the comment as done by user with Moderator role';

create table public.moderator_network
    (
        person_id integer,
        network_id integer,
        CONSTRAINT moderator_network_pk PRIMARY KEY (person_id, network_id)
);

