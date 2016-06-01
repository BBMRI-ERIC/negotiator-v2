
--
-- Name: tag; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "tag" (
    id bigint NOT NULL,
    query_id bigint NOT NULL,
    text character varying(255) NOT NULL
);

--
-- Name: TABLE "tag"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE "tag" IS 'Table that contains tags for queries';


--
-- Name: COLUMN "tag".id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "tag".id IS 'Primary key';


--
-- Name: COLUMN "tag".query_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "tag".query_id IS 'Foreign key which exists in the query table as primary key';


--
-- Name: COLUMN "tag".text; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "tag".text IS 'Text for the given tag id';


CREATE TABLE comment (
    id bigint NOT NULL,
    query_id bigint NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    person_id bigint NOT NULL,
    text text NOT NULL
);

--
-- Name: TABLE comment; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE comment IS 'table to store comments on a query';


--
-- Name: COLUMN comment.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN comment.id IS 'Primary key';


--
-- Name: COLUMN comment.query_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN comment.query_id IS 'Foreign key which exists as primary key in the query table. ';


--
-- Name: COLUMN comment."timestamp"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN comment."timestamp" IS 'timestamp of when the comment was made.';


--
-- Name: COLUMN comment.person_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN comment.person_id IS 'Foreign key which exists as primary key in the person table. describes the person who made the comment.';


--
-- Name: COLUMN comment.text; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN comment.text IS 'Text of the comment.';


--
-- Name: flagged_queries; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE flagged_queries (
    query_id bigint NOT NULL,
    owner_id bigint NOT NULL,
    flag character(1)
);

--
-- Name: TABLE flagged_queries; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE flagged_queries IS 'Table for queries that are flagged/bookmarked. bookmark options are starred, archived and ignored.';


--
-- Name: COLUMN flagged_queries.query_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN flagged_queries.query_id IS 'This column along with owner_id will make the primary key. Its also a foreign key here, taken from query table';


--
-- Name: COLUMN flagged_queries.owner_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN flagged_queries.owner_id IS 'This column along with the id column will make the primary key. Its also a foreign key here, taken from owner(which itself takes it from person) table';


--
-- Name: COLUMN flagged_queries.flag; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN flagged_queries.flag IS 'The flag of the comment. One character letter. (A) archived, (I) ignored, (S) starred';


--
-- Name: flagged_queries_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE flagged_queries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: flagged_queries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE flagged_queries_id_seq OWNED BY flagged_queries.query_id;



CREATE TABLE location (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);

--
-- Name: TABLE location; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE location IS 'Table to store locations of researchers';


--
-- Name: COLUMN location.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN location.id IS 'primary key';


--
-- Name: COLUMN location.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN location.name IS 'location name';



--
-- Name: owner; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE owner (
    id bigint NOT NULL,
    location_id bigint NOT NULL
);

--
-- Name: TABLE owner; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE owner IS 'Owner table is one of the child of person table  ';


--
-- Name: COLUMN owner.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN owner.id IS 'This foreign key is the primary key of person table';


--
-- Name: COLUMN owner.location_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN owner.location_id IS 'This foreign key is the primary key of person table';


--
-- Name: person; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE person (
    id bigint NOT NULL,
    auth_data character varying NOT NULL,
    person_type character(1) NOT NULL,
    person_image bytea
);

--
-- Name: TABLE person; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE person IS 'person table which is parent of researcher and owner';


--
-- Name: COLUMN person.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.id IS 'primary key';


--
-- Name: COLUMN person.auth_data; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.auth_data IS 'authentication string that comes from ''samply auth''';


--
-- Name: COLUMN person.person_type; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.person_type IS 'describes wether the person is researcher or owner - one of the the two child classes. ';


--
-- Name: COLUMN person.person_image; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.person_image IS 'image/avatar of the person';


--
-- Name: query; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query (
    id bigint NOT NULL,
    title character varying(255) NOT NULL,
    text character varying,
    date_time timestamp without time zone,
    researcher_id bigint NOT NULL
);

--
-- Name: TABLE query; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE query IS 'query table to contain all  queries';


--
-- Name: COLUMN query.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN query.id IS 'primary key';


--
-- Name: COLUMN query.title; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN query.title IS 'title of query';


--
-- Name: COLUMN query.text; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN query.text IS 'text of query';


--
-- Name: COLUMN query.date_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN query.date_time IS 'date and time of query with out time zone';


--
-- Name: COLUMN query.researcher_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN query.researcher_id IS 'Foreign key. Exists as primary key in the researcher table(which takes it in turn from the person table)';


--
-- Name: researcher; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE researcher (
    id bigint NOT NULL
);

--
-- Name: TABLE researcher; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE researcher IS 'researcher table - one of the child''s of the person table ';


--
-- Name: COLUMN researcher.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN researcher.id IS 'This id is the primary key of person table ';


--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "tag"
    ADD CONSTRAINT "tag_pkey" PRIMARY KEY (id);


--
-- Name: comment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);


--
-- Name: flagged_queries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY flagged_queries
    ADD CONSTRAINT flagged_queries_pkey PRIMARY KEY (query_id, owner_id);




--
-- Name: location_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY location
    ADD CONSTRAINT location_id_key UNIQUE (id);


--
-- Name: location_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY location
    ADD CONSTRAINT location_pkey PRIMARY KEY (id);



--
-- Name: owner_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY owner
    ADD CONSTRAINT owner_id_key UNIQUE (id);


--
-- Name: person_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_id_key UNIQUE (id);


--
-- Name: person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: query_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query
    ADD CONSTRAINT query_id_key UNIQUE (id);


--
-- Name: query_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query
    ADD CONSTRAINT query_pkey PRIMARY KEY (id);


--
-- Name: researcher_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY researcher
    ADD CONSTRAINT researcher_id_key UNIQUE (id);



--
-- Name: tag_query_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "tag"
    ADD CONSTRAINT "tag_query_id_fkey" FOREIGN KEY (query_id) REFERENCES query(id) ON UPDATE CASCADE ON DELETE CASCADE;




--
-- Name: comment_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_person_id_fkey FOREIGN KEY (person_id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: comment_query_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id) ON UPDATE CASCADE ON DELETE CASCADE;



--
-- Name: flagged_queries_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY flagged_queries
    ADD CONSTRAINT flagged_queries_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES owner(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: flagged_queries_query_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY flagged_queries
    ADD CONSTRAINT flagged_queries_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id) ON UPDATE CASCADE ON DELETE CASCADE;




--
-- Name: owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY owner
    ADD CONSTRAINT owner_id_fkey FOREIGN KEY (id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: owner_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY owner
    ADD CONSTRAINT owner_location_id_fkey FOREIGN KEY (location_id) REFERENCES location(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: person_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY researcher
    ADD CONSTRAINT person_id FOREIGN KEY (id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: query_researcher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query
    ADD CONSTRAINT query_researcher_id_fkey FOREIGN KEY (researcher_id) REFERENCES researcher(id) ON UPDATE CASCADE ON DELETE CASCADE;



--
-- PostgreSQL database dump complete
--

