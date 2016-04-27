-- !
-- ! delete all data
-- !

truncate 
	"alignment_artifact", 
	"alignment_property_value", 
	"alignment_record", 
	"alignment_tag", 
	"artifacts",
	"audit_record",
	"ccdb_user",
	"component_type",
	"comptype_artifact", 
	"comptype_asm", 
	"comptype_property_value", 
	"comptype_tag",
	"config",
	"data_type",
	"device",
	"device_artifact", 
	"device_property_value", 
	"device_tag",
	"filter_by_type",
	"installation_artifact",
	"installation_record",
	"privilege",
	"property",
	"report",
	"report_action",
	"role",
	"slot",
	"slot_artifact",
	"slot_pair",
	"slot_property_value",
	"slot_relation",
	"slot_tag",
	"tag",
	"unit",
	"user_role";

-- !
-- ! update internal data
-- !

vacuum full "alignment_artifact";
vacuum full "alignment_property_value";
vacuum full "alignment_record";
vacuum full "alignment_tag";
vacuum full "artifacts";
vacuum full "audit_record";
vacuum full "ccdb_user";
vacuum full "component_type";
vacuum full "comptype_artifact";
vacuum full "comptype_asm";
vacuum full "comptype_property_value";
vacuum full "comptype_tag";
vacuum full "config";
vacuum full "data_type";
vacuum full "device";
vacuum full "device_artifact";
vacuum full "device_property_value";
vacuum full "device_tag";
vacuum full "filter_by_type";
vacuum full "installation_artifact";
vacuum full "installation_record";
vacuum full "privilege";
vacuum full "property";
vacuum full "report";
vacuum full "report_action";
vacuum full "role";
vacuum full "slot";
vacuum full "slot_artifact";
vacuum full "slot_pair";
vacuum full "slot_property_value";
vacuum full "slot_relation";
vacuum full "slot_tag";
vacuum full "tag";
vacuum full "unit";
vacuum full "user_role";

SELECT setval('public.hibernate_sequence', 128, true);

-- !
-- ! data insertion
-- !

INSERT INTO config VALUES (48, 'schema_version', '1', 0);

-- !
-- ! authorization data
-- !

INSERT INTO ccdb_user VALUES ('admin', NULL, NULL, 'admin', 0);

INSERT INTO role VALUES ('admin', 'test role', 0);

INSERT INTO user_role VALUES (1, true, NULL, '2014-12-17 10:37:59.598', true, '2014-12-17 10:37:59.598', 0, 'admin', 'admin');

INSERT INTO privilege VALUES (2, 'CREATE', 'COMPONENT_TYPE', 'admin');
INSERT INTO privilege VALUES (3, 'UPDATE', 'COMPONENT_TYPE', 'admin');
INSERT INTO privilege VALUES (4, 'RENAME', 'COMPONENT_TYPE', 'admin');
INSERT INTO privilege VALUES (5, 'DELETE', 'COMPONENT_TYPE', 'admin');
INSERT INTO privilege VALUES (6, 'CREATE', 'UNIT', 'admin');
INSERT INTO privilege VALUES (7, 'UPDATE', 'UNIT', 'admin');
INSERT INTO privilege VALUES (8, 'RENAME', 'UNIT', 'admin');
INSERT INTO privilege VALUES (9, 'DELETE', 'UNIT', 'admin');
INSERT INTO privilege VALUES (10, 'CREATE', 'PROPERTY', 'admin');
INSERT INTO privilege VALUES (11, 'UPDATE', 'PROPERTY', 'admin');
INSERT INTO privilege VALUES (12, 'RENAME', 'PROPERTY', 'admin');
INSERT INTO privilege VALUES (13, 'DELETE', 'PROPERTY', 'admin');
INSERT INTO privilege VALUES (14, 'CREATE', 'SLOT', 'admin');
INSERT INTO privilege VALUES (15, 'UPDATE', 'SLOT', 'admin');
INSERT INTO privilege VALUES (16, 'RENAME', 'SLOT', 'admin');
INSERT INTO privilege VALUES (17, 'DELETE', 'SLOT', 'admin');
INSERT INTO privilege VALUES (18, 'CREATE', 'DEVICE', 'admin');
INSERT INTO privilege VALUES (19, 'UPDATE', 'DEVICE', 'admin');
INSERT INTO privilege VALUES (20, 'DELETE', 'DEVICE', 'admin');
INSERT INTO privilege VALUES (21, 'CREATE', 'ALIGNMENT_RECORD', 'admin');
INSERT INTO privilege VALUES (22, 'UPDATE', 'ALIGNMENT_RECORD', 'admin');
INSERT INTO privilege VALUES (23, 'RENAME', 'ALIGNMENT_RECORD', 'admin');
INSERT INTO privilege VALUES (24, 'DELETE', 'ALIGNMENT_RECORD', 'admin');
INSERT INTO privilege VALUES (25, 'CREATE', 'DATA_TYPE', 'admin');
INSERT INTO privilege VALUES (26, 'UPDATE', 'DATA_TYPE', 'admin');
INSERT INTO privilege VALUES (27, 'RENAME', 'DATA_TYPE', 'admin');
INSERT INTO privilege VALUES (28, 'DELETE', 'DATA_TYPE', 'admin');
INSERT INTO privilege VALUES (29, 'CREATE', 'INSTALLATION_RECORD', 'admin');
INSERT INTO privilege VALUES (30, 'UPDATE', 'INSTALLATION_RECORD', 'admin');
INSERT INTO privilege VALUES (31, 'RENAME', 'INSTALLATION_RECORD', 'admin');
INSERT INTO privilege VALUES (32, 'DELETE', 'INSTALLATION_RECORD', 'admin');

-- !
-- ! basic data
-- !

INSERT INTO data_type VALUES (33, '2014-12-17 10:37:59.609', 'system', 0, NULL, 'Integer number', 'Integer', true);
INSERT INTO data_type VALUES (34, '2014-12-17 10:37:59.609', 'system', 0, NULL, 'Double precision floating point', 'Double', true);
INSERT INTO data_type VALUES (35, '2014-12-17 10:37:59.609', 'system', 0, NULL, 'String of characters (text)', 'String', true);
INSERT INTO data_type VALUES (36, '2014-12-17 10:37:59.61', 'system', 0, NULL, 'Date and time', 'Timestamp', true);
INSERT INTO data_type VALUES (37, '2014-12-17 10:37:59.61', 'system', 0, NULL, 'Vector of integer numbers (1D array)', 'Integers Vector', false);
INSERT INTO data_type VALUES (38, '2014-12-17 10:37:59.61', 'system', 0, NULL, 'Vector of double precision numbers (1D array)', 'Doubles Vector', false);
INSERT INTO data_type VALUES (39, '2014-12-17 10:37:59.611', 'system', 0, NULL, 'List of strings (1D array)', 'Strings List', false);
INSERT INTO data_type VALUES (40, '2014-12-17 10:37:59.611', 'system', 0, NULL, 'Table of double precision numbers (2D array)', 'Doubles Table', false);
INSERT INTO data_type VALUES (41, '2014-12-17 10:37:59.622', 'system', 0, '{"meta":{"type":"SedsEnum","protocol":"SEDSv1","version":"1.0.0"},"data":{"selected":"TEST1"},"type":{"elements":["TEST1","TEST2","TEST3","TEST4"]}}', 'Testing of enums', 'Test enums', false);

INSERT INTO component_type VALUES (45, '2014-12-17 10:37:59.625', 'system', 0, '_ROOT', '_ROOT', NULL);
INSERT INTO component_type VALUES (46, '2014-12-17 10:37:59.626', 'system', 0, '_GRP', '_GRP', NULL);

INSERT INTO slot_relation VALUES (42, '2014-12-17 10:37:59.624', 'system', 0, NULL, 'contained in', 'CONTAINS');
INSERT INTO slot_relation VALUES (43, '2014-12-17 10:37:59.625', 'system', 0, NULL, 'powered by', 'POWERS');
INSERT INTO slot_relation VALUES (44, '2014-12-17 10:37:59.625', 'system', 0, NULL, 'controlled by', 'CONTROLS');

-- !
-- ! update internal data
-- !

vacuum full "alignment_artifact";
vacuum full "alignment_property_value";
vacuum full "alignment_record";
vacuum full "alignment_tag";
vacuum full "artifacts";
vacuum full "audit_record";
vacuum full "ccdb_user";
vacuum full "component_type";
vacuum full "comptype_artifact";
vacuum full "comptype_asm";
vacuum full "comptype_property_value";
vacuum full "comptype_tag";
vacuum full "config";
vacuum full "data_type";
vacuum full "device";
vacuum full "device_artifact";
vacuum full "device_property_value";
vacuum full "device_tag";
vacuum full "filter_by_type";
vacuum full "installation_artifact";
vacuum full "installation_record";
vacuum full "privilege";
vacuum full "property";
vacuum full "report";
vacuum full "report_action";
vacuum full "role";
vacuum full "slot";
vacuum full "slot_artifact";
vacuum full "slot_pair";
vacuum full "slot_property_value";
vacuum full "slot_relation";
vacuum full "slot_tag";
vacuum full "tag";
vacuum full "unit";
vacuum full "user_role";
