-- Dates are relative to the date the demo app starts, so the sample remains usable.
INSERT INTO vessels VALUES (1, 'Atlantic Pioneer', 'DEMO-IMO-001', 'Houston', DATEADD('DAY', 3, CURRENT_DATE));
INSERT INTO vessels VALUES (2, 'Pacific Horizon', 'DEMO-IMO-002', 'Singapore', DATEADD('DAY', 8, CURRENT_DATE));
INSERT INTO vessels VALUES (3, 'Ocean Meridian', 'DEMO-IMO-003', 'Rotterdam', DATEADD('DAY', 5, CURRENT_DATE));
INSERT INTO vessels VALUES (4, 'Northern Star', 'DEMO-IMO-004', 'Antwerp', DATEADD('DAY', 2, CURRENT_DATE));
INSERT INTO certificates VALUES (1,1,'Safety Management Certificate', DATEADD('DAY',12,CURRENT_DATE));
INSERT INTO certificates VALUES (2,1,'Safety Equipment Certificate', DATEADD('DAY',125,CURRENT_DATE));
INSERT INTO certificates VALUES (3,2,'Safety Management Certificate', DATEADD('DAY',120,CURRENT_DATE));
INSERT INTO certificates VALUES (4,3,'Safety Equipment Certificate', DATEADD('DAY',28,CURRENT_DATE));
INSERT INTO certificates VALUES (5,4,'Safety Management Certificate', DATEADD('DAY',1,CURRENT_DATE));
INSERT INTO findings VALUES (1,1,'Fire drill record requires review','OPEN');
INSERT INTO findings VALUES (2,1,'Maintenance documentation follow-up','OPEN');
INSERT INTO findings VALUES (3,3,'Inspection observation pending closure','OPEN');
INSERT INTO findings VALUES (4,4,'Safety equipment corrective action review','OPEN');
