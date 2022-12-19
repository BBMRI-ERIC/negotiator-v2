alter table public.comment
    add moderated boolean default false;

comment on column public.comment.moderated is 'marks the comment as done by user with Moderator role';

alter table public.offer
    add moderated boolean default false;

comment on column public.offer.moderated is 'marks the offer as done by user with Moderator role';

alter table public.person
    add is_moderator boolean default false;

comment on column public.person.is_moderator is 'elevates the user to Moderator role';

