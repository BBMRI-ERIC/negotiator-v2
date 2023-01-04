INSERT INTO biobank(id, directory_id, name, description)
VALUES (1, '1', 'Biobank Hamburg', 'The biobank for sample from the DKTK in Hamburg managed by the HU');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (2, '2', 'Biobank Berlin', 'The biobank for tissue samples in Berlin managed by the Charite');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (3, '3', 'Biobank München', 'The biobank for blood samples in München managed by the TU München');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (4, '4', 'Biobank Münster', 'The C* biobank in Münstermanaged by the UKM Pathology');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (5, '5', 'Biobank Mainz', 'The C40-49 biobank in Mainz managed by the JGU MedInfo');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (6, '6', 'Biobank Heidelberg', 'The C95 biobank in Heidelberg managed by Zeis');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (7, '7', 'Usertest Biobank 1', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (8, '8', 'Usertest Biobank 2', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (9, '9', 'Usertest Biobank 3', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (10, '10', 'Usertest Biobank 4', 'A biobank for testing only');
INSERT INTO biobank(id, directory_id, name, description)
VALUES (11, '11', 'Usertest Biobank 5', 'A biobank for testing only');
ALTER SEQUENCE biobank_id_seq RESTART WITH 12;

INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (1, '1', 'Hamburg Blood Samples', 1);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (2, '2', 'Berlin Blood Samples', 2);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (3, '3', 'München Blood Samples', 3);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (4, '4', 'Münster Blood Samples', 4);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (5, '5', 'Mainz Blood Samples', 5);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (6, '6', 'Heidelberg Blood Samples', 6);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (7, '7', 'Usertest Collection 1', 7);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (8, '8', 'Usertest Collection 2', 7);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (9, '9', 'Usertest Collection 3', 7);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (10, '10', 'Usertest Collection 4', 8);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (11, '11', 'Usertest Collection 5', 8);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (12, '12', 'Usertest Collection 6', 8);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (13, '13', 'Usertest Collection 7', 9);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (14, '14', 'Usertest Collection 8', 9);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (15, '15', 'Usertest Collection 9', 9);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (16, '16', 'Usertest Collection 10', 10);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (17, '17', 'Usertest Collection 11', 10);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (18, '18', 'Usertest Collection 12', 10);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (19, '19', 'Usertest Collection 13', 11);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (20, '20', 'Usertest Collection 14', 11);
INSERT INTO collection(id, directory_id, name, biobank_id)
VALUES (21, '21', 'Usertest Collection 15', 11);
ALTER SEQUENCE collection_id_seq RESTART WITH 22;

INSERT INTO public.list_of_directories (id, name, url, rest_url, username, password, api_username, api_password, resource_biobanks, resource_collections, description, sync_active, directory_prefix, resource_networks, bbmri_eric_national_nodes, api_type) VALUES (3, 'Test Directory', 'https://test.eu', 'https://test.eu', '', '', '', '', 'eu_bbmri_eric_biobanks', 'eu_bbmri_eric_collections', 'Test Directory', true, 'Test Directory', null, null, 'Molgenis');


INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (1, 'user1', 'Dr.med. Harald Researcher', 'test1@test1.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (2, 'user2', 'Goerge Biobanker, M.D.', 'test2@test2.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (3, 'user3', 'Dr.med. Sean Researchington', 'test3@test3.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (4, 'user4', 'Travis Research, M.D.', 'test4@test4.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (5, 'https://auth-dev.mitro.dkfz.de/users/7', 'BBMRI Biobank Owner', 'owner.bbmri@bbmri.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (6, 'https://auth-dev.mitro.dkfz.de/users/8', 'BBMRI Researcher', 'researcher.bbmri@bbmri.org', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (7, 'https://auth-dev.mitro.dkfz.de/users/2', 'Max Ataian', 'm.ataian@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (8, 'https://auth-dev.mitro.dkfz.de/users/19', 'Saher Maqsood', 's.maqsood@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (9, 'https://auth-dev.mitro.dkfz.de/users/24', 'Polina Litvak', 'p.litvak@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (10, 'biobanker-000', 'Biobank test user 1', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (11, 'biobanker-001', 'Biobank test user 2', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (12, 'biobanker-002', 'Biobank test user 3', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (13, 'biobanker-003', 'Biobank test user 4', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (14, 'biobanker-004', 'Biobank test user 5', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image)
VALUES (15, 'researcher-001', 'Researcher test user 1', 'r.proynova@dkfz.de', NULL);
INSERT INTO person(id, auth_subject, auth_name, auth_email, person_image, is_admin)
VALUES (20, 'admin-001', 'BBMRI ADMIN', 'admin@negotiator', NULL, TRUE);
INSERT INTO person(id, auth_subject, auth_name, auth_email, is_admin, organization)
VALUES (19, 'national-node-001', 'National Node', 'nn@localhost', false, 'BBMRI.eu');
ALTER SEQUENCE person_id_seq RESTART WITH 21;

INSERT INTO person_collection (person_id, collection_id)
VALUES (1, 1);
INSERT INTO person_collection (person_id, collection_id)
VALUES (2, 2);
INSERT INTO person_collection (person_id, collection_id)
VALUES (3, 3);
INSERT INTO person_collection (person_id, collection_id)
VALUES (5, 5);
INSERT INTO person_collection (person_id, collection_id)
VALUES (5, 2);
INSERT INTO person_collection (person_id, collection_id)
VALUES (5, 3);
INSERT INTO person_collection (person_id, collection_id)
VALUES (10, 7);
INSERT INTO person_collection (person_id, collection_id)
VALUES (10, 8);
INSERT INTO person_collection (person_id, collection_id)
VALUES (10, 9);
INSERT INTO person_collection (person_id, collection_id)
VALUES (11, 10);
INSERT INTO person_collection (person_id, collection_id)
VALUES (11, 11);
INSERT INTO person_collection (person_id, collection_id)
VALUES (11, 12);
INSERT INTO person_collection (person_id, collection_id)
VALUES (12, 13);
INSERT INTO person_collection (person_id, collection_id)
VALUES (12, 14);
INSERT INTO person_collection (person_id, collection_id)
VALUES (12, 15);
INSERT INTO person_collection (person_id, collection_id)
VALUES (13, 16);
INSERT INTO person_collection (person_id, collection_id)
VALUES (13, 17);
INSERT INTO person_collection (person_id, collection_id)
VALUES (13, 18);
INSERT INTO person_collection (person_id, collection_id)
VALUES (14, 19);
INSERT INTO person_collection (person_id, collection_id)
VALUES (14, 20);
INSERT INTO person_collection (person_id, collection_id)
VALUES (14, 21);

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token,
                  valid_query)
VALUES (1, 'Cancer',
        'Are there biobanks with liver cancer samples that include the age at primary diagnosis and pharmacotherapy information?',
        '2015-01-02 00:00:00', 1,
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

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token,
                  valid_query)
VALUES (2, 'Colon cancer',
        'Do you have 50 samples on samples of Colorectal cancer as a primary diagnosis (C18.1 to C18.7)?',
        '2014-05-01 00:00:00', 6,
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

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token,
                  valid_query)
VALUES (3, 'Lung cancer',
        'For my research, I need information on the response to therapy on patients with lung cancer.',
        '2016-07-21 17:55:52.70183', 6,
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

INSERT INTO query(id, title, text, query_creation_time, researcher_id, json_text, num_attachments, negotiator_token,
                  valid_query)
VALUES (4, 'Skin cancer', 'Are there biobanks with skin cancer research data.', '2016-07-21 17:55:52.70183', 8,
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

INSERT INTO query (id, title, text, query_creation_time, researcher_id, json_text, num_attachments,
                          negotiator_token, valid_query, request_description, query_xml, ethics_vote,
                          negotiation_started_time, researcher_name, researcher_email, researcher_organization,
                          test_request)
VALUES (784, 'Whole Genome and/or Whole Exome (WGS/WES) sequencing data availability for CRC-Cohort',
        'EHDS2 Pilot project is aiming to pilot deployment of European Health Data Spaces for research and policy making (EHDS2), where BBMRI-ERIC should act as a research infrastructure based EHDS2 Node. As a part of the pilot, several use cases are going to be supported and one in which BBMRI-ERIC participates is about genomic data linked to health data, with a focus on cancer.',
        '2022-09-15 15:16:04.841000', 1,
        '{"searchQueries":[{"nToken":"6213999f-7041-4572-9077-181c132ed4a4__search__d9cb6947-0a19-42ba-bb5e-1c06ba141d29","collections":[{"biobankId":"bbmri-eric:ID:AT_MUG","collectionId":"bbmri-eric:ID:AT_MUG:collection:ColorectalCancerCollection"},{"biobankId":"bbmri-eric:ID:BE_71030031000","collectionId":"bbmri-eric:ID:BE_71030031000:collection:001"},{"biobankId":"bbmri-eric:ID:BE_BBRU1","collectionId":"bbmri-eric:ID:BE_BBRU1:collection:all_samples"},{"biobankId":"bbmri-eric:ID:BE_BERA1","collectionId":"bbmri-eric:ID:BE_BERA1:collection:all_samples"},{"biobankId":"bbmri-eric:ID:BE_CMGO1","collectionId":"bbmri-eric:ID:BE_CMGO1:collection:all_samples"},{"biobankId":"bbmri-eric:ID:BE_LCHU1","collectionId":"bbmri-eric:ID:BE_LCHU1:collection:all_samples"},{"biobankId":"bbmri-eric:ID:CH_UniversityOfBern","collectionId":"bbmri-eric:ID:CH_UniversityOfBern:collection:CH_TissueBiobankBern"},{"biobankId":"bbmri-eric:ID:CZ_MMCI","collectionId":"bbmri-eric:ID:CZ_MMCI:collection:LTS"},{"biobankId":"bbmri-eric:ID:DE_ICBL","collectionId":"bbmri-eric:ID:DE_ICBL:collection:ICBL"},{"biobankId":"bbmri-eric:ID:DE_ZeBanC","collectionId":"bbmri-eric:ID:DE_ZeBanC:collection:Onoloy"},{"biobankId":"bbmri-eric:ID:DE_ibdw","collectionId":"bbmri-eric:ID:DE_ibdw:collection:bc"},{"biobankId":"bbmri-eric:ID:FI_001","collectionId":"bbmri-eric:ID:FI_001:collection:Auria_FFPE"},{"biobankId":"bbmri-eric:ID:FI_005","collectionId":"bbmri-eric:ID:FI_005:collection:HBB"},{"biobankId":"bbmri-eric:ID:IT_1382686156197551","collectionId":"bbmri-eric:ID:IT_1382686156197551:collection:145484867305069"},{"biobankId":"bbmri-eric:ID:IT_1383824434985378","collectionId":"bbmri-eric:ID:IT_1383824434985378:collection:1444717533914953"},{"biobankId":"bbmri-eric:ID:IT_1384936945873731","collectionId":"bbmri-eric:ID:IT_1384936945873731:collection:1444717710166120"},{"biobankId":"bbmri-eric:ID:IT_1504858990324590","collectionId":"bbmri-eric:ID:IT_1504858990324590:collection:1606752281669494"},{"biobankId":"bbmri-eric:ID:MT_DwarnaBio","collectionId":"bbmri-eric:ID:MT_DwarnaBio:collection:all_samples"},{"biobankId":"bbmri-eric:ID:PL_3THFK","collectionId":"bbmri-eric:ID:PL_3THFK:collection:PL_3THFK_D3271"},{"biobankId":"bbmri-eric:ID:SE_827","collectionId":"bbmri-eric:ID:SE_827:collection:UCAN_Uppsala"},{"biobankId":"bbmri-eric:ID:UK_GBR-1-14","collectionId":"bbmri-eric:ID:UK_GBR-1-14:collection:1"},{"biobankId":"bbmri-eric:ID:UK_GBR-1-22","collectionId":"bbmri-eric:ID:UK_GBR-1-22:collection:1"}],"humanReadable":"#1: Disease type(s): [ C18 ] - Malignant neoplasm of colon and Text search is graz\r\n#2: Disease type(s): [ C18 ] - Malignant neoplasm of colon and Text search is Antwerpen\r\n#3: Disease type(s): [ C18 ] - Malignant neoplasm of colon and Text search is Brugmann\r\n#4: Disease type(s): [ C18 ] - Malignant neoplasm of colon and Text search is Erasme\r\n#5: Disease type(s): [ C18 ] - Malignant neoplasm of colon and Text search is Namur\r\n#6: Disease type(s): [ C18 ] - Malignant neoplasm of colon and Text search is Liège\r\n#7: Text search is Bern\r\n#8: Text search is Luebeck and Disease type(s): [ C18 ] - Malignant neoplasm of colon\r\n#9: Text search is Charité and Disease type(s): [ C18 ] - Malignant neoplasm of colon\r\n#10: Text search is Wuerzburg\r\n#11: Text search is auria\r\n#12: Text search is Helsinki\r\n#13: Text search is martino\r\n#14: Text search is Humanitas\r\n#15: Text search is ire\r\n#16: Text search is malta\r\n#17: Text search is Fahrenheit\r\n#18: Text search is Uppsala\r\n#19: Text search is wales\r\n#20: Text search is Nottingham\r\n#21: Text search is bern\r\n#22: Text search is INT\r\n#23: Text search is Masaryk\r\n#24: Text search is ire","URL":"https:\/\/directory.bbmri-eric.eu\/#\/?search=ire&cart=YmJtcmktZXJpYzpJRDpBVF9NVUc6Y29sbGVjdGlvbjpDb2xvcmVjdGFsQ2FuY2VyQ29sbGVjdGlvbixiYm1yaS1lcmljOklEOkJFXzcxMDMwMDMxMDAwOmNvbGxlY3Rpb246MDAxLGJibXJpLWVyaWM6SUQ6QkVfQkJSVTE6Y29sbGVjdGlvbjphbGxfc2FtcGxlcyxiYm1yaS1lcmljOklEOkJFX0JFUkExOmNvbGxlY3Rpb246YWxsX3NhbXBsZXMsYmJtcmktZXJpYzpJRDpCRV9DTUdPMTpjb2xsZWN0aW9uOmFsbF9zYW1wbGVzLGJibXJpLWVyaWM6SUQ6QkVfTENIVTE6Y29sbGVjdGlvbjphbGxfc2FtcGxlcyxiYm1yaS1lcmljOklEOkNIX1VuaXZlcnNpdHlPZkJlcm46Y29sbGVjdGlvbjpDSF9UaXNzdWVCaW9iYW5rQmVybixiYm1yaS1lcmljOklEOkRFX0lDQkw6Y29sbGVjdGlvbjpJQ0JMLGJibXJpLWVyaWM6SUQ6REVfWmVCYW5DOmNvbGxlY3Rpb246T25vbG95LGJibXJpLWVyaWM6SUQ6REVfaWJkdzpjb2xsZWN0aW9uOmJjLGJibXJpLWVyaWM6SUQ6RklfMDAxOmNvbGxlY3Rpb246QXVyaWFfRkZQRSxiYm1yaS1lcmljOklEOkZJXzAwNTpjb2xsZWN0aW9uOkhCQixiYm1yaS1lcmljOklEOklUXzEzODI2ODYxNTYxOTc1NTE6Y29sbGVjdGlvbjoxNDU0ODQ4NjczMDUwNjksYmJtcmktZXJpYzpJRDpJVF8xMzg0OTM2OTQ1ODczNzMxOmNvbGxlY3Rpb246MTQ0NDcxNzcxMDE2NjEyMCxiYm1yaS1lcmljOklEOk1UX0R3YXJuYUJpbzpjb2xsZWN0aW9uOmFsbF9zYW1wbGVzLGJibXJpLWVyaWM6SUQ6UExfM1RIRks6Y29sbGVjdGlvbjpQTF8zVEhGS19EMzI3MSxiYm1yaS1lcmljOklEOlNFXzgyNzpjb2xsZWN0aW9uOlVDQU5fVXBwc2FsYSxiYm1yaS1lcmljOklEOlVLX0dCUi0xLTE0OmNvbGxlY3Rpb246MSxiYm1yaS1lcmljOklEOlVLX0dCUi0xLTIyOmNvbGxlY3Rpb246MSxiYm1yaS1lcmljOklEOklUXzEzODM4MjQ0MzQ5ODUzNzg6Y29sbGVjdGlvbjoxNDQ0NzE3NTMzOTE0OTUzLGJibXJpLWVyaWM6SUQ6Q1pfTU1DSTpjb2xsZWN0aW9uOkxUUyxiYm1yaS1lcmljOklEOklUXzE1MDQ4NTg5OTAzMjQ1OTA6Y29sbGVjdGlvbjoxNjA2NzUyMjgxNjY5NDk0"}]}',
        0, '6213999f-7041-4572-9077-181c132ed4a4', true,
        'We inquire which biobanks contributing to the CRC-Cohort would be able to provide WGS or WES data, ideally for existing CRC-Cohort patients.',
        null, 'Original CRC-Cohort', '2022-09-16 06:38:41.789000', 'Dr.med. Harald Researcher',
        'idk@idk.com', null, false);


ALTER SEQUENCE query_id_seq RESTART WITH 5;

INSERT INTO query_collection(query_id, collection_id)
VALUES (1, 3);
INSERT INTO query_collection(query_id, collection_id)
VALUES (2, 3);
INSERT INTO query_collection(query_id, collection_id)
VALUES (3, 3);

INSERT INTO query_collection(query_id, collection_id)
VALUES (1, 5);
INSERT INTO query_collection(query_id, collection_id)
VALUES (2, 5);
INSERT INTO query_collection(query_id, collection_id)
VALUES (3, 5);
INSERT INTO query_collection(query_id, collection_id)
VALUES (784, 3);
INSERT INTO query_collection(query_id, collection_id)
VALUES (784, 5);

INSERT INTO comment(id, query_id, person_id, comment_time, text)
VALUES (1, 1, 5, '2016-01-01 00:00:00', 'I think the query is too general');
INSERT INTO comment(id, query_id, person_id, comment_time, text)
VALUES (2, 1, 2, '2016-01-01 15:00:00', 'I agree with you on that');
INSERT INTO comment(id, query_id, person_id, comment_time, text)
VALUES (3, 2, 2, '2015-05-01 00:00:00',
        'Yes, we have it. Additionally I would recommend you to ask also for C19, C20 codes.');
INSERT INTO comment(id, query_id, person_id, comment_time, text)
VALUES (4, 2, 6, '2015-05-01 01:11:00', 'Do you also have available FFPE – surgical material on those samples?');
INSERT INTO comment(id, query_id, person_id, comment_time, text)
VALUES (5, 2, 5, '2015-05-01 01:00:00', 'We have about 400 samples on colorectal cancer as a primary diagnosis');
INSERT INTO public.comment (id, query_id, person_id, comment_time, text, attachment, status, moderated) VALUES (1184, 784, 1, '2022-09-16 12:57:05.938000', e'

unfortunately, we are not able to provide WGS or WES data for the CRC-Cohort.
', false, 'published', false);
INSERT INTO public.comment (id, query_id, person_id, comment_time, text, attachment, status, moderated) VALUES (1189, 784, 20, '2022-10-21 13:24:57.961000', 'idk test', false, 'published', false);

ALTER SEQUENCE comment_id_seq RESTART WITH 6;

INSERT INTO flagged_query(query_id, person_id, flag)
VALUES (1, 5, 'STARRED');
INSERT INTO flagged_query(query_id, person_id, flag)
VALUES (2, 5, 'ARCHIVED');

INSERT INTO json_query (json_text)
VALUES ('{
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
}');

INSERT INTO json_query (json_text)
VALUES ('{
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
  }');
INSERT INTO public.request_status (id, query_id, status, status_type, status_date, status_user_id, status_json) VALUES (3141, 784, 'created', 'created', '2022-09-15 15:16:05.055000', 20, null);
INSERT INTO public.request_status (id, query_id, status, status_type, status_date, status_user_id, status_json) VALUES (3142, 784, 'under_review', 'review', '2022-09-15 15:16:05.057000', 20, null);
INSERT INTO public.request_status (id, query_id, status, status_type, status_date, status_user_id, status_json) VALUES (3143, 784, 'approved', 'review', '2022-09-16 06:38:41.778000', 20, '{"statusApprovedText":""}');
INSERT INTO public.request_status (id, query_id, status, status_type, status_date, status_user_id, status_json) VALUES (3144, 784, 'waitingstart', 'start', '2022-09-16 06:38:41.784000', 20, null);
INSERT INTO public.request_status (id, query_id, status, status_type, status_date, status_user_id, status_json) VALUES (3145, 784, 'started', 'start', '2022-09-16 06:38:41.791000', 20, null);
