INSERT INTO biobank(id, directory_id, name, description) VALUES (1, '1', 'Biobank Hamburg', 'The biobank for sample from the DKTK in Hamburg managed by the HU');
INSERT INTO biobank(id, directory_id, name, description) VALUES (2, '2', 'Biobank Berlin', 'The biobank for tissue samples in Berlin managed by the Charite');
INSERT INTO biobank(id, directory_id, name, description) VALUES (3, '3', 'Biobank München', 'The biobank for blood samples in München managed by the TU München');
INSERT INTO biobank(id, directory_id, name, description) VALUES (4, '4', 'Biobank Münster', 'The C* biobank in Münstermanaged by the UKM Pathology');
INSERT INTO biobank(id, directory_id, name, description) VALUES (5, '5', 'Biobank Mainz', 'The C40-49 biobank in Mainz managed by the JGU MedInfo');
INSERT INTO biobank(id, directory_id, name, description) VALUES (6, '6', 'Biobank Heidelberg', 'The C95 biobank in Heidelberg managed by Zeis');
INSERT INTO biobank(id, directory_id, name, description) VALUES (7, '7', 'Usertest Biobank 1', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description) VALUES (8, '8', 'Usertest Biobank 2', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description) VALUES (9, '9', 'Usertest Biobank 3', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description) VALUES (10, '10', 'Usertest Biobank 4', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description) VALUES (11, '11', 'Usertest Biobank 5', 'A biobank for testing only');
ALTER SEQUENCE biobank_id_seq RESTART WITH 12;

INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (1, '1', 'Hamburg Blood Samples', 1);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (2, '2', 'Berlin Blood Samples', 2);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (3, '3', 'München Blood Samples', 3);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (4, '4', 'Münster Blood Samples', 4);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (5, '5', 'Mainz Blood Samples', 5);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (6, '6', 'Heidelberg Blood Samples', 6);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (7, '7', 'Usertest Collection 1', 7);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (8, '8', 'Usertest Collection 2', 7);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (9, '9', 'Usertest Collection 3', 7);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (10, '10', 'Usertest Collection 4', 8);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (11, '11', 'Usertest Collection 5', 8);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (12, '12', 'Usertest Collection 6', 8);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (13, '13', 'Usertest Collection 7', 9);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (14, '14', 'Usertest Collection 8', 9);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (15, '15', 'Usertest Collection 9', 9);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (16, '16', 'Usertest Collection 10', 10);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (17, '17', 'Usertest Collection 11', 10);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (18, '18', 'Usertest Collection 12', 10);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (19, '19', 'Usertest Collection 13', 11);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (20, '20', 'Usertest Collection 14', 11);
INSERT INTO collection(id, directory_id, name, biobank_id) VALUES (21, '21', 'Usertest Collection 15', 11);
ALTER SEQUENCE collection_id_seq RESTART WITH 22;

INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (1, 'user1', 'Dr.med. Harald Researcher', 'test1@test1.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (2, 'user2', 'Goerge Biobanker, M.D.', 'test2@test2.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (3, 'user3', 'Dr.med. Sean Researchington', 'test3@test3.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (4, 'user4', 'Travis Research, M.D.', 'test4@test4.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (5, 'https://auth-dev.mitro.dkfz.de/users/7', 'BBMRI Biobank Owner', 'owner.bbmri@bbmri.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (6, 'https://auth-dev.mitro.dkfz.de/users/8', 'BBMRI Researcher', 'researcher.bbmri@bbmri.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (7, 'https://auth-dev.mitro.dkfz.de/users/2', 'Max Ataian', 'm.ataian@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (8, 'https://auth-dev.mitro.dkfz.de/users/19', 'Saher Maqsood', 's.maqsood@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (9, 'https://auth-dev.mitro.dkfz.de/users/24', 'Polina Litvak', 'p.litvak@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (10, 'biobanker-000', 'Biobank test user 1', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (11, 'biobanker-001', 'Biobank test user 2', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (12, 'biobanker-002', 'Biobank test user 3', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (13, 'biobanker-003', 'Biobank test user 4', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (14, 'biobanker-004', 'Biobank test user 5', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image) VALUES (15, 'researcher-001', 'Researcher test user 1', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, is_admin) VALUES (20, 'admin-001','BBMRI ADMIN', 'admin@negotiator', NULL, TRUE);
INSERT INTO person(auth_subject, auth_name, auth_email, is_admin, organization) VALUES ('national-node-001', 'National Node', 'nn@localhost', false, 'BBMRI.eu');

ALTER SEQUENCE person_id_seq RESTART WITH 21;

INSERT INTO person_collection (person_id, collection_id) VALUES (1, 1);
INSERT INTO person_collection (person_id, collection_id) VALUES (2, 2);
INSERT INTO person_collection (person_id, collection_id) VALUES (3, 3);
INSERT INTO person_collection (person_id, collection_id) VALUES (5, 5);
INSERT INTO person_collection (person_id, collection_id) VALUES (5, 2);
INSERT INTO person_collection (person_id, collection_id) VALUES (5, 3);
INSERT INTO person_collection (person_id, collection_id) VALUES (10, 7);
INSERT INTO person_collection (person_id, collection_id) VALUES (10, 8);
INSERT INTO person_collection (person_id, collection_id) VALUES (10, 9);
INSERT INTO person_collection (person_id, collection_id) VALUES (11, 10);
INSERT INTO person_collection (person_id, collection_id) VALUES (11, 11);
INSERT INTO person_collection (person_id, collection_id) VALUES (11, 12);
INSERT INTO person_collection (person_id, collection_id) VALUES (12, 13);
INSERT INTO person_collection (person_id, collection_id) VALUES (12, 14);
INSERT INTO person_collection (person_id, collection_id) VALUES (12, 15);
INSERT INTO person_collection (person_id, collection_id) VALUES (13, 16);
INSERT INTO person_collection (person_id, collection_id) VALUES (13, 17);
INSERT INTO person_collection (person_id, collection_id) VALUES (13, 18);
INSERT INTO person_collection (person_id, collection_id) VALUES (14, 19);
INSERT INTO person_collection (person_id, collection_id) VALUES (14, 20);
INSERT INTO person_collection (person_id, collection_id) VALUES (14, 21);

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token, valid_query)
VALUES(1, 'Cancer', 'Are there biobanks with liver cancer samples that include the age at primary diagnosis and pharmacotherapy information?', '2015-01-02 00:00:00', 1,
'{
    "humanReadable":"name: ‘Germany’, materials: ‘DNA’ or ’Plasma’",
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ], "URL": "https://does-not-exist.com"
}', 0, 'token-1', TRUE);

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token, valid_query) VALUES(2, 'Colon cancer', 'Do you have 50 samples on samples of Colorectal cancer as a primary diagnosis (C18.1 to C18.7)?', '2014-05-01 00:00:00', 6,
'{
    "humanReadable":"name: ‘Netherlands’, materials: ’Plasma’",
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTCH",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ], "URL": "https://does-not-exist.com"
}', 0, 'token-2', TRUE);

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token, valid_query) VALUES(3, 'Lung cancer', 'For my research, I need information on the response to therapy on patients with lung cancer.', '2016-07-21 17:55:52.70183', 6,
'{
    "humanReadable":"name: ‘Germany’, materials: ‘DNA’",
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ], "URL": "https://does-not-exist.com"
}', 0, 'token-3', TRUE);

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token, valid_query) VALUES(4, 'Skin cancer', 'Are there biobanks with skin cancer research data.', '2016-07-21 17:55:52.70183', 8,
'{
    "humanReadable":"name: ‘Europe’, materials: ‘DNA’ or ’Plasma’",
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ], "URL": "https://does-not-exist.com"
}', 0, 'token-4', TRUE);

ALTER SEQUENCE query_id_seq RESTART WITH 5;

INSERT INTO query_collection(query_id, collection_id) VALUES (1, 3);
INSERT INTO query_collection(query_id, collection_id) VALUES (2, 3);
INSERT INTO query_collection(query_id, collection_id) VALUES (3, 3);

INSERT INTO query_collection(query_id, collection_id) VALUES (1, 5);
INSERT INTO query_collection(query_id, collection_id) VALUES (2, 5);
INSERT INTO query_collection(query_id, collection_id) VALUES (3, 5);

INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (1, 1, 5, '2016-01-01 00:00:00', 'I think the query is too general');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (2, 1, 2, '2016-01-01 15:00:00', 'I agree with you on that');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (3, 2, 2, '2015-05-01 00:00:00', 'Yes, we have it. Additionally I would recommend you to ask also for C19, C20 codes.');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (4, 2, 6, '2015-05-01 01:11:00', 'Do you also have available FFPE – surgical material on those samples?');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (5, 2, 5, '2015-05-01 01:00:00', 'We have about 400 samples on colorectal cancer as a primary diagnosis');
ALTER SEQUENCE comment_id_seq RESTART WITH 6;

INSERT INTO flagged_query(query_id, person_id, flag) VALUES (1, 5, 'STARRED');
INSERT INTO flagged_query(query_id, person_id, flag) VALUES (2, 5, 'ARCHIVED');

INSERT INTO json_query (json_text) VALUES (
'{
    "humanReadable":"name: ‘Europe’, materials: ‘DNA’ or ’Plasma’",
  "collections":[
    {
      "collectionID":"5",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_HBC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ], "URL": "https://does-not-exist.com"
}'
);

INSERT INTO json_query (json_text) VALUES (
  '{
      "humanReadable":"name: ‘Russia’",
    "collections":[
      {
        "collectionID":"5",
        "biobankID":"bbmri-eric:biobankID:BE_B0383"
      },
      {
        "collectionID":"bbmri-eric:collectionID:CZ_CUNI_HK:collection:all_samples",
        "biobankID":"bbmri-eric:biobankID:BE_B0383"
      }
    ], "URL": "https://does-not-exist.com"
  }'
);
