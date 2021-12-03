-- T_CONTACT_BOOK

CREATE TABLE public.T_CONTACT_BOOK (
owner character varying(255) NOT NULL,
contacts jsonb NOT NULL,
version bigint NOT NULL
);

ALTER TABLE public.T_CONTACT_BOOK OWNER TO postgresql;

ALTER TABLE ONLY public.T_CONTACT_BOOK
ADD CONSTRAINT T_CONTACT_BOOK_pkey PRIMARY KEY (owner);

ALTER TABLE ONLY public.T_CONTACT_BOOK
ADD CONSTRAINT T_CONTACT_BOOK_owner_version_constraint UNIQUE (owner, version);

CREATE OR REPLACE FUNCTION contactbook_check_version_on_update()
  RETURNS trigger
  LANGUAGE PLPGSQL
  AS $$
DECLARE
  expected_version bigint := OLD.version + 1;
BEGIN
  IF NEW.owner = OLD.owner AND NEW.version != expected_version THEN
    Raise Exception 'ContactBook version unexpected on update for owner % - current version % - expected version %', NEW.owner, NEW.version, expected_version;
  END IF;
  RETURN NEW;
END;
$$;

CREATE TRIGGER contactbook_check_version_on_update_trigger
  BEFORE UPDATE
  ON public.T_CONTACT_BOOK
  FOR EACH ROW
  EXECUTE PROCEDURE contactbook_check_version_on_update();

-- T_CONVERSATION

CREATE TABLE public.T_CONVERSATION (
conversationidentifier character varying(255) NOT NULL,
participantidentifiers jsonb NOT NULL,
events jsonb NOT NULL,
version bigint NOT NULL
);

ALTER TABLE public.T_CONVERSATION OWNER TO postgresql;

ALTER TABLE ONLY public.T_CONVERSATION
ADD CONSTRAINT T_CONVERSATION_pkey PRIMARY KEY (conversationidentifier);

ALTER TABLE ONLY public.T_CONVERSATION
ADD CONSTRAINT T_CONVERSATION_conversationidentifier_version_constraint UNIQUE (conversationidentifier, version);

CREATE OR REPLACE FUNCTION conversation_check_version_on_update()
RETURNS trigger
LANGUAGE PLPGSQL
AS $$
DECLARE
expected_version bigint := OLD.version + 1;
BEGIN
IF NEW.conversationidentifier = OLD.conversationidentifier AND NEW.version != expected_version THEN
Raise Exception 'Conversation version unexpected on update for owner % - current version % - expected version %', NEW.conversationidentifier, NEW.version, expected_version;
END IF;
RETURN NEW;
END;
$$;

CREATE TRIGGER conversation_check_version_on_update_trigger
BEFORE UPDATE
  ON public.T_CONVERSATION
FOR EACH ROW
EXECUTE PROCEDURE conversation_check_version_on_update();

-- outboxevent

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