INSERT INTO location(id, directory_id, name, description) VALUES (1, '1', 'Biobank Hamburg', 'The biobank for sample from the DKTK in Hamburg managed by the HU');
INSERT INTO location(id, directory_id, name, description) VALUES (2, '2', 'Biobank Berlin', 'The biobank for tissue samples in Berlin managed by the Charite');
INSERT INTO location(id, directory_id, name, description) VALUES (3, '3', 'Biobank München', 'The biobank for blood samples in München managed by the TU München');
INSERT INTO location(id, directory_id, name, description) VALUES (4, '4', 'Biobank Münster', 'The C* biobank in Münstermanaged by the UKM Pathology');
INSERT INTO location(id, directory_id, name, description) VALUES (5, '5', 'Biobank Mainz', 'The C40-49 biobank in Mainz managed by the JGU MedInfo');
INSERT INTO location(id, directory_id, name, description) VALUES (6, '6', 'Biobank Heidelberg', 'The C95 biobank in Heidelberg managed by Zeis');
SELECT pg_catalog.setval('location_id_seq', 6, true);

INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (1, 'user1', 'Dr.med. Harald Researcher', 'test1@test1.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (2, 'user2', 'Goerge Biobanker, M.D.', 'test2@test2.org', NULL, 4);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (3, 'user3', 'Dr.med. Sean Researchington', 'test3@test3.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (4, 'user4', 'Travis Research, M.D.', 'test4@test4.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (5, 'https://auth-dev.mitro.dkfz.de/users/7', 'BBMRI Biobank Owner', 'owner.bbmri@bbmri.org', NULL, 5);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (6, 'https://auth-dev.mitro.dkfz.de/users/8', 'BBMRI Researcher', 'researcher.bbmri@bbmri.org', NULL, NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (7, 'https://auth-dev.mitro.dkfz.de/users/2', 'Max Ataian', 'm.ataian@dkfz.de', NULL, 6);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (8, 'https://auth-dev.mitro.dkfz.de/users/19', 'Saher Maqsood', 's.maqsood@dkfz.de', NULL, 6);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, location_id) VALUES (9, 'https://auth-dev.mitro.dkfz.de/users/24', 'Polina Litvak', 'p.litvak@dkfz.de', NULL, 6);
SELECT pg_catalog.setval('person_id_seq', 9, true);

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, negotiator_token) VALUES(1, 'Cancer', 'Are there biobanks with liver cancer samples that include the age at primary diagnosis and pharmacotherapy information?', '2015-01-02 00:00:00', 1,
'{
  "filters":{
    "humanReadable":"name: ‘WHATEVER’, materials: ‘DNA’ or ’Plasma’"
  },
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ]
}', 'token-1');

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, negotiator_token) VALUES(2, 'Colon cancer', 'Do you have 50 samples on samples of Colorectal cancer as a primary diagnosis (C18.1 to C18.7)?', '2014-05-01 00:00:00', 6,
'{
  "filters":{
    "humanReadable":"name: ‘Netherlands’, materials: ’Plasma’"
  },
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTCH",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ]
}', 'token-2');

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, negotiator_token) VALUES(3, 'Lung cancer', 'For my research, I need information on the response to therapy on patients with lung cancer.', '2016-07-21 17:55:52.70183', 6,
'{
  "filters":{
    "humanReadable":"name: ‘Germany’, materials: ‘DNA’"
  },
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ]
}', 'token-3');

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, negotiator_token) VALUES(4, 'Skin cancer', 'Are there biobanks with skin cancer research data.', '2016-07-21 17:55:52.70183', 8,
'{
  "filters":{
    "humanReadable":"name: ‘Europe’, materials: ‘DNA’ or ’Plasma’"
  },
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ]
}', 'token-4');

SELECT pg_catalog.setval('query_id_seq', 4, true);

INSERT INTO query_person(query_id, person_id) VALUES (1, 2);
INSERT INTO query_person(query_id, person_id) VALUES (2, 2);
INSERT INTO query_person(query_id, person_id) VALUES (1, 5);
INSERT INTO query_person(query_id, person_id) VALUES (2, 5);
INSERT INTO query_person(query_id, person_id) VALUES (3, 5);
INSERT INTO query_person(query_id, person_id) VALUES (3, 2);
INSERT INTO query_person(query_id, person_id) VALUES (1, 8);
INSERT INTO query_person(query_id, person_id) VALUES (2, 8);
INSERT INTO query_person(query_id, person_id) VALUES (3, 8);
INSERT INTO query_person(query_id, person_id) VALUES (4, 8);
INSERT INTO query_person(query_id, person_id) VALUES (1, 9);
INSERT INTO query_person(query_id, person_id) VALUES (2, 9);
INSERT INTO query_person(query_id, person_id) VALUES (3, 9);
INSERT INTO query_person(query_id, person_id) VALUES (4, 9);

INSERT INTO tag(id, query_id, text) VALUES (1, 2, 'Lung');
SELECT pg_catalog.setval('tag_id_seq', 1, true);

INSERT INTO tagged_query(query_id, tag_id) VALUES (1, 1);

INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (1, 1, 5, '2016-01-01 00:00:00', 'I think the query is too general');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (2, 1, 2, '2016-01-01 15:00:00', 'I agree with you on that');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (3, 2, 2, '2015-05-01 00:00:00', 'Yes, we have it. Additionally I would recommend you to ask also for C19, C20 codes.');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (4, 2, 6, '2015-05-01 01:11:00', 'Do you also have available FFPE – surgical material on those samples?');
INSERT INTO comment(id, query_id, person_id, comment_time, text) VALUES (5, 2, 5, '2015-05-01 01:00:00', 'We have about 400 samples on colorectal cancer as a primary diagnosis');
SELECT pg_catalog.setval('comment_id_seq', 5, true);

INSERT INTO flagged_query(query_id, person_id, flag) VALUES (1, 5, 'STARRED');
INSERT INTO flagged_query(query_id, person_id, flag) VALUES (2, 5, 'ARCHIVED');

INSERT INTO role(role_type, person_id) VALUES ('RESEARCHER', 1);
INSERT INTO role(role_type, person_id) VALUES ('OWNER', 2);
INSERT INTO role(role_type, person_id) VALUES ('RESEARCHER', 3);
INSERT INTO role(role_type, person_id) VALUES ('RESEARCHER', 4);
INSERT INTO role(role_type, person_id) VALUES ('OWNER', 5);
INSERT INTO role(role_type, person_id) VALUES ('RESEARCHER', 6);
INSERT INTO role(role_type, person_id) VALUES ('OWNER', 7);
INSERT INTO role(role_type, person_id) VALUES ('OWNER', 8);
INSERT INTO role(role_type, person_id) VALUES ('RESEARCHER', 8);
INSERT INTO role(role_type, person_id) VALUES ('OWNER', 9);
INSERT INTO role(role_type, person_id) VALUES ('RESEARCHER', 9);


INSERT INTO json_query (json_text) VALUES (
'{
  "filters":{
    "humanReadable":"name: ‘Europe’, materials: ‘DNA’ or ’Plasma’"
  },
  "collections":[
    {
      "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    },
    {
      "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
      "biobankID":"bbmri-eric:biobankID:BE_B0383"
    }
  ]
}'
);

INSERT INTO json_query (json_text) VALUES (
  '{
    "filters":{
      "humanReadable":"name: ‘Russia’"
    },
    "collections":[
      {
        "collectionID":"bbmri-eric:collectionID:BE_B0383_LTC",
        "biobankID":"bbmri-eric:biobankID:BE_B0383"
      },
      {
        "collectionID":"bbmri-eric:collectionID:BE_B03843_LTCD",
        "biobankID":"bbmri-eric:biobankID:BE_B0383"
      }
    ]
  }'
);