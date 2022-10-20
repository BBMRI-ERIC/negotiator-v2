alter table public.comment
    add moderated boolean;

comment on column public.comment.moderated is 'marks the comment as done by user with Moderator role';

