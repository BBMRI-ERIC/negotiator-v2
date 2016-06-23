INSERT INTO "person"("personType", "authSubject", "authName", "authEmail", "personImage")
VALUES ('RESEARCHER', 'https://auth.samply.de/users/55', 'Terminator', 'test1@test1.org', null);
		
INSERT INTO "person"("personType", "authSubject", "authName", "authEmail", "personImage")
VALUES ('RESEARCHER', 'https://auth.samply.de/users/17', 'The Jocker', 'test2@test2.org', null);

INSERT INTO "person"("personType", "authSubject", "authName", "authEmail", "personImage")
VALUES ('RESEARCHER', 'https://auth.samply.de/users/007', 'James Bond', 'test3@test3.org', null);

INSERT INTO "person"("personType", "authSubject", "authName", "authEmail", "personImage")
VALUES ('RESEARCHER', 'https://auth.samply.de/users/99', 'Travis Bickle', 'test4@test4.org', null);

INSERT INTO "person"("personType", "authSubject", "authName", "authEmail", "personImage", "locationId")
VALUES ('OWNER', 'https://auth-dev.mitro.dkfz.de/users/7', 'Jack Sparrow', 'test5@test5.org', null, 5);

INSERT INTO "person"("personType", "authSubject", "authName", "authEmail", "personImage")
VALUES ('RESEARCHER', 'https://auth-dev.mitro.dkfz.de/users/8', 'Don Vito Corleone', 'test6@test6.org', null);

INSERT INTO "location"(name)
VALUES ('Augmented Reality');
 
INSERT INTO "location"(name)
VALUES ('Space');
 
INSERT INTO "location"(name)
VALUES ('DB Bahn');
 
INSERT INTO "location"(name)
VALUES ('Cloud 9');
 
INSERT INTO "location"(name)
VALUES ('Disney land');

INSERT INTO "query"(title, text, "researcherId")
VALUES ('cancer','Data on all sorts of cancers', 2);

INSERT INTO "query"(title, text, "researcherId")
VALUES ('colon cancer','latest papers on colon cancer', 9);

INSERT INTO "query"(title, text, "researcherId")
VALUES ('lung cancer','NO smoking data', 11);

INSERT INTO "tag"("queryId", text)
VALUES (2,'Colon' );

INSERT INTO "tag"("queryId", text)
VALUES (3,'Lung');

INSERT INTO "taggedQuery"("queryId", "tagId")
VALUES (1, 2);

INSERT INTO "taggedQuery"("queryId", "tagId")
VALUES (2, 1);

INSERT INTO "comment"("queryId", "personId", "timeStamp", text)
VALUES (1, 2, '2016-01-01 00:00:00', 'I think the query is too general');

INSERT INTO "comment"("queryId", "personId", "timeStamp", text)
VALUES (3, 9, '2010-05-01 00:00:00', 'They have been lying to you all the way. Smoking is actually good for your health');

INSERT INTO "comment"("queryId", "personId", "timeStamp", text)
VALUES (1, 2, '2020-01-01 00:00:00', 'I can share the data with you');

INSERT INTO "flaggedQuery"("queryId", "personId", flag)
VALUES (1, 2, 's');

INSERT INTO "flaggedQuery"("queryId", "personId", flag)
VALUES (2, 9, 'i');

INSERT INTO "flaggedQuery"("queryId", "personId", flag)
VALUES (3, 11, 'a');



