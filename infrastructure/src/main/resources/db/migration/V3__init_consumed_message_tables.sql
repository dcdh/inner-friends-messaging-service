CREATE TABLE public.T_CONSUMED_MESSAGE (
  eventid uuid NOT NULL,
  groupid character varying(255) NOT NULL,
timeofreceiving timestamp without time zone NOT NULL,
CONSTRAINT consumedmessage_pkey PRIMARY KEY (eventid, groupid)
);

ALTER TABLE public.T_CONSUMED_MESSAGE OWNER TO postgresql;