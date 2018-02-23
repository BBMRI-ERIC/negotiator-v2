ALTER TABLE offer DROP CONSTRAINT offer_offer_from_fkey;
ALTER TABLE public.offer
  ADD CONSTRAINT offer_offer_from_fkey FOREIGN KEY (offer_from)
      REFERENCES public.biobank (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE;

