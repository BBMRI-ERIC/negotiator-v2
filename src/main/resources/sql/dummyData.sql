INSERT INTO "location"("name")
VALUES ('Augmented Reality');
 
INSERT INTO "location"("name")
VALUES ('Space');
 
INSERT INTO "location"("name")
VALUES ('DB Bahn');
 
INSERT INTO "location"("name")
VALUES ('Cloud 9');
 
INSERT INTO "location"("name")
VALUES ('Disney land');

INSERT INTO "location"("name")
VALUES ('Nirvana');

INSERT INTO "person"(person_type, auth_subject, auth_name, auth_email, person_image)
VALUES ('RESEARCHER', 'https://auth.samply.de/users/55', 'Terminator', 'test1@test1.org', null);
		
INSERT INTO "person"(person_type, auth_subject, auth_name, auth_email, person_image, location_id)
VALUES ('OWNER', 'https://auth.samply.de/users/17', 'The Joker', 'test2@test2.org', null, 4);

INSERT INTO "person"(person_type, auth_subject, auth_name, auth_email, person_image)
VALUES ('RESEARCHER', 'https://auth.samply.de/users/007', 'James Bond', 'test3@test3.org', null);

INSERT INTO "person"(person_type, auth_subject, auth_name, auth_email, person_image)
VALUES ('RESEARCHER', 'https://auth.samply.de/users/99', 'Travis Bickle', 'test4@test4.org', null);

INSERT INTO "person"(person_type, auth_subject, auth_name, auth_email, person_image, location_id)
VALUES ('OWNER', 'https://auth-dev.mitro.dkfz.de/users/7', 'Jack Sparrow', 'test5@test5.org', null, 5);

INSERT INTO "person"(person_type, auth_subject, auth_name, auth_email, person_image)
VALUES ('RESEARCHER', 'https://auth-dev.mitro.dkfz.de/users/8', 'Don Vito Corleone', 'test6@test6.org', null);

INSERT INTO "query"("title", "text", query_creation_time, researcher_id)
VALUES ('cancer','Data on all sorts of cancers', '2015-01-02 00:00:00', 1);

INSERT INTO "query"("title", "text", query_creation_time, researcher_id)
VALUES ('colon cancer','latest papers on colon cancer', '2015-01-01 00:00:00', 6);

INSERT INTO "query"("title", "text", query_creation_time, researcher_id)
VALUES ('lung cancer','No smoking data', '2014-05-01 00:00:00', 6);

INSERT INTO "query"("title", "text", query_creation_time, researcher_id)
VALUES ('lung cancer','Loads of smoking data', '2014-06-01 00:00:00', 6);

INSERT INTO "query"("title", "text", query_creation_time, researcher_id)
VALUES ('lung cancer','Second hand smoking data', '2015-06-01 00:00:00', 6);

INSERT INTO "tag"(query_id, "text")
VALUES (2,'Colon' );

INSERT INTO "tag"(query_id, "text")
VALUES (3,'Lung');

INSERT INTO "tagged_query"(query_id, tag_id)
VALUES (1, 2);

INSERT INTO "tagged_query"(query_id, tag_id)
VALUES (2, 1);

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (1, 5, '2016-01-01 00:00:00', 'I think the query is too general');

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (1, 2, '2016-01-01 00:00:00', 'I agree with you on that');

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (1, 2, '2016-01-01 00:00:00', 'Please explain more');

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (3, 2, '2015-05-01 00:00:00', 'What is your deal with lung cancer');

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (3, 6, '2015-05-01 00:00:00', 'They have been lying to you all the way. Smoking is actually good for your health');

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (3, 5, '2015-05-01 00:00:00', 'You talk like you represent the tobacco industry');

INSERT INTO "comment"(query_id, person_id, comment_time, "text")
VALUES (1, 5, '2016-02-02 00:00:00', 'I can share the data with you');

INSERT INTO "flagged_query"(query_id, person_id, "flag")
VALUES (1, 5, 's');

INSERT INTO "flagged_query"(query_id, person_id, "flag")
VALUES (2, 5, 'i');

INSERT INTO "flagged_query"(query_id, person_id, "flag")
VALUES (3, 5, 'a');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (1, 2, '2016-07-01 00:00:00');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (3, 2, '2016-07-02 00:00:00');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (2, 5, '2016-07-02 00:00:00');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (1, 5, '2016-07-01 00:00:00');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (3, 5, '2016-07-01 00:00:00');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (4, 5, '2016-08-01 00:00:00');

INSERT INTO "query_person"(query_id, person_id, query_leaving_time) 
VALUES (5, 5, '2015-08-01 00:00:00');
