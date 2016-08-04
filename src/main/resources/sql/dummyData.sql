INSERT INTO location(id, name) VALUES (1, 'Biobank Hamburg');
INSERT INTO location(id, name) VALUES (2, 'Biobank Berlin');
INSERT INTO location(id, name) VALUES (3, 'Biobank München');
INSERT INTO location(id, name) VALUES (4, 'Biobank Münster');
INSERT INTO location(id, name) VALUES (5, 'Biobank Mainz');
INSERT INTO location(id, name) VALUES (6, 'Biobank Heidelberg');
SELECT pg_catalog.setval('location_id_seq', 6, true);

INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (1, 'user1', 'Dr.med. Harald Researcher', 'test1@test1.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (2, 'user2', 'Goerge Biobanker, M.D.', 'test2@test2.org', NULL, 4);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (3, 'user3', 'Dr.med. Sean Researchington', 'test3@test3.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (4, 'user4', 'Travis Research, M.D.', 'test4@test4.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (5, 'https://auth-dev.mitro.dkfz.de/users/7', 'BBMRI Biobank Owner', 'owner.bbmri@bbmri.org', NULL, 5);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (6, 'https://auth-dev.mitro.dkfz.de/users/8', 'BBMRI Researcher', 'researcher.bbmri@bbmri.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (7, 'https://auth-dev.mitro.dkfz.de/users/2', 'Max Ataian', 'm.ataian@dkfz.de', NULL, 6);
SELECT pg_catalog.setval('person_id_seq', 7, true);

INSERT INTO query(id, title, text, query_creation_time, researcher_id) VALUES (1, 'Cancer', 'Are there biobanks with liver cancer samples that include the age at primary diagnosis and pharmacotherapy information?', '2015-01-02 00:00:00', 1);
INSERT INTO query(id, title, text, query_creation_time, researcher_id) VALUES (2, 'Colon cancer', 'Do you have 50 samples on samples of Colorectal cancer as a primary diagnosis (C18.1 to C18.7)?', '2014-05-01 00:00:00', 6);
INSERT INTO query(id, title, text, query_creation_time, researcher_id) VALUES (3, 'Lung cancer', 'For my research, I need information on the response to therapy on patients with lung cancer.', '2016-07-21 17:55:52.70183', 6);
SELECT pg_catalog.setval('query_id_seq', 3, true);

INSERT INTO query_person(query_id, person_id, query_leaving_time) VALUES (1, 2, '2016-07-01 00:00:00');
INSERT INTO query_person(query_id, person_id, query_leaving_time) VALUES (2, 2, '2016-07-02 00:00:00');
INSERT INTO query_person(query_id, person_id, query_leaving_time) VALUES (1, 5, '2016-07-01 00:00:00');
INSERT INTO query_person(query_id, person_id, query_leaving_time) VALUES (2, 5, '2016-07-02 00:00:00');
INSERT INTO query_person(query_id, person_id, query_leaving_time) VALUES (3, 5, '2016-07-01 00:00:00');
INSERT INTO query_person(query_id, person_id, query_leaving_time) VALUES (3, 2, '2016-07-02 00:00:00');

INSERT INTO tag(id, query_id, text) VALUES (1, 2, 'Lung');
SELECT pg_catalog.setval('tag_id_seq', 1, true);

INSERT INTO tagged_query(query_id, tag_id) VALUES (1, 1);

INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (1, 1, 5, '2016-01-01 00:00:00', 'I think the query is too general');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (2, 1, 2, '2016-01-01 15:00:00', 'I agree with you on that');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (4, 2, 2, '2015-05-01 00:00:00', 'Yes, we have it. Additionally I would recommend you to ask also for C19, C20 codes.');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (5, 2, 6, '2015-05-01 01:11:00', 'Do you also have available FFPE – surgical material on those samples?');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (6, 2, 5, '2015-05-01 01:00:00', 'We have about 400 samples on colorectal cancer as a primary diagnosis');
SELECT pg_catalog.setval('comment_id_seq', 6, true);

INSERT INTO flagged_query(query_id, person_id, flag) VALUES (1, 5, 'STARRED');
INSERT INTO flagged_query(query_id, person_id, flag) VALUES (2, 5, 'ARCHIVED');
