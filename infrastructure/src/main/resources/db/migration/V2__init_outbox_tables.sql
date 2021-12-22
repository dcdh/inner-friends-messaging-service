CREATE TABLE public.outboxevent (
  id uuid NOT NULL,
  aggregatetype character varying(255) NOT NULL,
aggregateid character varying(255) NOT NULL,
type character varying(255) NOT NULL,
"timestamp" timestamp without time zone NOT NULL,
payload character varying(8000),
tracingspancontext character varying(256)
);

ALTER TABLE public.outboxevent OWNER TO postgresql;

ALTER TABLE ONLY public.outboxevent
ADD CONSTRAINT outboxevent_pkey PRIMARY KEY (id);