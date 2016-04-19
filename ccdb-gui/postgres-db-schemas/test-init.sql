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

SELECT setval('public.hibernate_sequence', 650, true);

-- !
-- ! data insertion
-- !

INSERT INTO config VALUES (48, 'schema_version', '1', 0);

-- !
-- ! authorization data
-- !

INSERT INTO ccdb_user VALUES ('admin', NULL, NULL, 'admin', 0);
INSERT INTO ccdb_user VALUES ('editor', NULL, NULL, 'editor', 0);

INSERT INTO role VALUES ('admin', 'test role', 0);
INSERT INTO role VALUES ('editor_role', 'test editor role', 0);

INSERT INTO user_role VALUES (1, true, NULL, '2014-12-17 10:37:59.598', true, '2014-12-17 10:37:59.598', 0, 'admin', 'admin');
INSERT INTO user_role VALUES (2, true, NULL, '2014-12-17 10:37:59.598', false, '2014-12-17 10:37:59.598', 0, 'editor_role', 'editor');

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

INSERT INTO privilege VALUES (40, 'CREATE', 'DEVICE', 'editor_role');
INSERT INTO privilege VALUES (41, 'UPDATE', 'DEVICE', 'editor_role');
INSERT INTO privilege VALUES (42, 'DELETE', 'DEVICE', 'editor_role');
INSERT INTO privilege VALUES (43, 'RENAME', 'DEVICE', 'editor_role');
INSERT INTO privilege VALUES (44, 'CREATE', 'INSTALLATION_RECORD', 'editor_role');
INSERT INTO privilege VALUES (45, 'UPDATE', 'INSTALLATION_RECORD', 'editor_role');
INSERT INTO privilege VALUES (46, 'RENAME', 'INSTALLATION_RECORD', 'editor_role');
INSERT INTO privilege VALUES (47, 'DELETE', 'INSTALLATION_RECORD', 'editor_role');
INSERT INTO privilege VALUES (48, 'CREATE', 'SLOT', 'editor_role');
INSERT INTO privilege VALUES (49, 'UPDATE', 'SLOT', 'editor_role');
INSERT INTO privilege VALUES (50, 'RENAME', 'SLOT', 'editor_role');
INSERT INTO privilege VALUES (51, 'DELETE', 'SLOT', 'editor_role');
INSERT INTO privilege VALUES (52, 'UPDATE', 'PROPERTY', 'editor_role');

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

INSERT INTO unit VALUES (49, '2014-12-17 13:17:02.946', 'admin', 0, 'Length in meters', 'meter', 'length', 'm');
INSERT INTO unit VALUES (51, '2014-12-17 13:17:03.095', 'admin', 0, 'Length in inches', 'inch', 'length', 'in');
INSERT INTO unit VALUES (53, '2014-12-17 13:17:03.097', 'admin', 0, 'Volume', 'cubic-meter', 'volume', 'm3');
INSERT INTO unit VALUES (55, '2014-12-17 13:17:03.099', 'admin', 0, 'Area', 'square-meter', 'area', 'm2');
INSERT INTO unit VALUES (57, '2014-12-17 13:17:03.101', 'admin', 0, 'Temperature', 'kelvin', 'temperature', 'K');
INSERT INTO unit VALUES (59, '2014-12-17 13:17:03.103', 'admin', 0, 'Time', 'second', 'time', 's');
INSERT INTO unit VALUES (61, '2014-12-17 13:17:03.105', 'admin', 0, 'Weight', 'kilogram', 'weight', 'kg');
INSERT INTO unit VALUES (63, '2014-12-17 13:17:03.107', 'admin', 0, 'Power', 'watt', 'power', 'W');
INSERT INTO unit VALUES (65, '2014-12-17 13:17:03.109', 'admin', 0, 'Energy', 'electronvolt', 'energy', 'eV');
INSERT INTO unit VALUES (67, '2014-12-17 13:17:03.112', 'admin', 0, 'Magnetic flux density', 'tesla', 'magnetic flux density', 'T');
INSERT INTO unit VALUES (69, '2014-12-17 13:17:03.114', 'admin', 0, 'Electric current', 'ampere', 'electric current', 'A');
INSERT INTO unit VALUES (71, '2014-12-17 13:17:03.115', 'admin', 0, 'Electric potential difference', 'volt', 'electric potential difference', 'V');
INSERT INTO unit VALUES (73, '2014-12-17 13:17:03.117', 'admin', 0, 'Pressure', 'pascal', 'pressure', 'Pa');
INSERT INTO unit VALUES (75, '2014-12-17 13:17:03.119', 'admin', 0, 'Electric resistance', 'ohm', 'electric resistance', 'Ohm');
INSERT INTO unit VALUES (77, '2014-12-17 13:17:03.121', 'admin', 0, 'Angle in degrees', 'degree', 'angle', '°');
INSERT INTO unit VALUES (79, '2014-12-17 13:17:03.124', 'admin', 0, 'Magnetic field strength', 'ampere-per-meter', 'magnetic field strength', 'A/m');
INSERT INTO unit VALUES (81, '2014-12-17 13:17:03.128', 'admin', 0, 'Quadrupole gradient', 'tesla-per-meter', 'gradient', 'T/m');
INSERT INTO unit VALUES (83, '2014-12-17 13:17:03.13', 'admin', 0, 'Sextupole gradient', 'tesla-per-square-meter', 'gradiant', 'T/m2');
INSERT INTO unit VALUES (85, '2014-12-17 13:17:03.131', 'admin', 0, 'Price in Euros', 'Euro', 'price', '€');
INSERT INTO unit VALUES (87, '2014-12-17 13:17:03.133', 'admin', 0, 'Price in Dollars', 'Dollar', 'price', '$');
INSERT INTO unit VALUES (89, '2014-12-17 13:17:03.135', 'admin', 0, 'Price in SEK', 'Swedish Krona', 'price', 'SEK');

INSERT INTO component_type VALUES (45, '2014-12-17 10:37:59.625', 'system', 0, '_ROOT', '_ROOT', NULL);
INSERT INTO component_type VALUES (46, '2014-12-17 10:37:59.626', 'system', 0, '_GRP', '_GRP', NULL);
INSERT INTO component_type VALUES (173, '2014-12-17 13:31:35.88', 'admin', 0, 'Device type 1', 'DEV_TYPE_1', NULL);
INSERT INTO component_type VALUES (175, '2014-12-17 13:31:50.092', 'admin', 0, 'Device type 2', 'DEV_TYPE_2', NULL);
INSERT INTO component_type VALUES (177, '2014-12-17 13:32:05.275', 'admin', 0, 'Device type 3', 'DEV_TYPE_3', NULL);
INSERT INTO component_type VALUES (179, '2014-12-17 13:32:17.669', 'admin', 0, 'Device type 4', 'DEV_TYPE_4', NULL);

INSERT INTO slot_relation VALUES (42, '2014-12-17 10:37:59.624', 'system', 0, NULL, 'contained in', 'CONTAINS');
INSERT INTO slot_relation VALUES (43, '2014-12-17 10:37:59.625', 'system', 0, NULL, 'powered by', 'POWERS');
INSERT INTO slot_relation VALUES (44, '2014-12-17 10:37:59.625', 'system', 0, NULL, 'controlled by', 'CONTROLS');

-- !
-- ! test data; other
-- !

INSERT INTO property VALUES (1, '2014-12-18 11:41:42.654', 'admin', 0, 'Test property, integer type, assigned to Device type', false, false, false, true, 'ASSGN_INT_TYPE', 33, NULL);
INSERT INTO property VALUES (3, '2014-12-18 11:42:36.037', 'admin', 0, 'Test property, integer type, assigned to Installation slot', false, false, true, false, 'ASSGN_INT_SLOT', 33, NULL);
INSERT INTO property VALUES (5, '2014-12-18 11:43:22.341', 'admin', 0, 'Test property, integer type, assigned to Device instance', false, true, false, false, 'ASSGN_INT_INSTANCE', 33, NULL);
INSERT INTO property VALUES (91, '2014-12-17 13:21:00.205', 'admin', 0, 'Accumulated Center Position', false, false, true, false, 'ACENPOS', 34, 49);
INSERT INTO property VALUES (93, '2014-12-17 13:21:00.215', 'admin', 0, 'Accumulated End Position', false, false, true, false, 'AENDPOS', 34, 49);
INSERT INTO property VALUES (95, '2014-12-17 13:21:00.219', 'admin', 0, 'Another Name', true, true, true, true, 'ALIAS', 35, NULL);
INSERT INTO property VALUES (97, '2014-12-17 13:21:00.224', 'admin', 0, 'Radius Aperture', false, true, false, true, 'APERTURE', 34, 49);
INSERT INTO property VALUES (99, '2014-12-17 13:21:00.23', 'admin', 0, 'Bending Angle', false, true, false, true, 'BANGLE', 34, 77);
INSERT INTO property VALUES (101, '2014-12-17 13:21:00.235', 'admin', 0, 'B-pole', true, true, true, true, 'BPOLE', 34, 67);
INSERT INTO property VALUES (103, '2014-12-17 13:21:00.248', 'admin', 0, 'Bending Radius', true, true, true, true, 'BRADIUS', 34, 49);
INSERT INTO property VALUES (105, '2014-12-17 13:21:00.259', 'admin', 0, 'Conductor Size', false, false, false, true, 'CDTRSIZE', 39, 55);
INSERT INTO property VALUES (107, '2014-12-17 13:21:00.275', 'admin', 0, 'Current', false, false, false, true, 'CURRENT', 34, 69);
INSERT INTO property VALUES (109, '2014-12-17 13:21:00.291', 'admin', 0, 'Distance from Artemis Source', false, false, false, true, 'DFAS', 34, 49);
INSERT INTO property VALUES (111, '2014-12-17 13:21:00.303', 'admin', 0, 'Edge Angle', false, false, false, true, 'EANGLE', 34, 77);
INSERT INTO property VALUES (113, '2014-12-17 13:21:00.309', 'admin', 0, 'Effective Flange to flange length', false, false, true, false, 'EF2FLEN', 34, 49);
INSERT INTO property VALUES (115, '2014-12-17 13:21:00.315', 'admin', 0, 'Effective Length', false, true, false, true, 'EFFLENGTH', 34, 49);
INSERT INTO property VALUES (117, '2014-12-17 13:21:00.321', 'admin', 0, 'Length', false, false, false, true, 'LENGTH', 34, 49);
INSERT INTO property VALUES (119, '2014-12-17 13:21:00.326', 'admin', 0, 'Maximum Field', false, false, false, true, 'MAXFIELD', 34, 67);
INSERT INTO property VALUES (121, '2014-12-17 13:21:00.332', 'admin', 0, 'Minimum Beampipe Inner Diameter', false, false, true, false, 'MINBPID', 34, 49);
INSERT INTO property VALUES (123, '2014-12-17 13:21:00.339', 'admin', 0, 'Number Required', false, false, false, true, 'NUMREQ', 33, NULL);
INSERT INTO property VALUES (125, '2014-12-17 13:21:00.345', 'admin', 0, 'Power', false, false, false, true, 'POWER', 34, 63);
INSERT INTO property VALUES (127, '2014-12-17 13:21:00.351', 'admin', 0, 'Resistance', false, false, false, true, 'RESISTNC', 34, 75);
INSERT INTO property VALUES (129, '2014-12-17 13:21:00.356', 'admin', 0, 'Temprature Rise', false, false, false, true, 'TEMPRISE', 34, 57);
INSERT INTO property VALUES (131, '2014-12-17 13:21:00.364', 'admin', 0, 'Vaccum Pipe O.D.', false, false, false, true, 'VACPOD', 34, 51);
INSERT INTO property VALUES (133, '2014-12-17 13:21:00.371', 'admin', 0, 'Voltage', false, false, false, true, 'VOLTAGE', 34, 71);
INSERT INTO property VALUES (135, '2014-12-17 13:21:00.377', 'admin', 0, 'Quad Gradient', true, true, true, true, 'QUADGRAD', 34, 81);
INSERT INTO property VALUES (137, '2014-12-17 13:21:00.384', 'admin', 0, 'Quad Bpole', true, true, true, true, 'QUADBPOL', 34, 67);
INSERT INTO property VALUES (139, '2014-12-17 13:21:00.39', 'admin', 0, 'Sext Gradient', true, true, true, true, 'SEXTGRAD', 34, 83);
INSERT INTO property VALUES (141, '2014-12-17 13:21:00.397', 'admin', 0, 'Sext Bpole', true, true, true, true, 'SEXTBPOL', 34, 67);
INSERT INTO property VALUES (143, '2014-12-17 13:21:00.403', 'admin', 0, 'Dipole B', true, true, true, true, 'DIPOLEB', 34, 67);
INSERT INTO property VALUES (145, '2014-12-17 13:21:00.407', 'admin', 0, 'Field Measurement Polynomia', true, true, true, true, 'FIELDPOLY', 35, NULL);
INSERT INTO property VALUES (147, '2014-12-17 13:21:00.411', 'admin', 0, 'External Document', true, true, true, true, 'DOC01', 35, NULL);
INSERT INTO property VALUES (149, '2014-12-17 13:21:00.418', 'admin', 0, 'Distance to center from L-Line Extension Origin', false, false, true, false, 'DCFLEO', 34, 49);
INSERT INTO property VALUES (151, '2014-12-17 13:21:00.424', 'admin', 0, 'Length from Element before (c2c) ', false, false, true, false, 'LFELB', 34, 49);
INSERT INTO property VALUES (153, '2014-12-17 13:22:43.002', 'admin', 1, 'Test integer property associated with all entities', true, true, true, true, 'TEST_INT_ALL', 33, NULL);
INSERT INTO property VALUES (156, '2014-12-17 13:23:54.935', 'admin', 1, 'Test double type property associated with installation slot', false, false, true, false, 'TEST_DBL_SLOT', 34, NULL);
INSERT INTO property VALUES (159, '2014-12-17 13:24:31.316', 'admin', 0, 'Test string property associated with a slot', false, false, true, false, 'TEST_STR_SLOT', 35, NULL);
INSERT INTO property VALUES (161, '2014-12-17 13:25:39.903', 'admin', 0, 'Test time stamp property associated with a device instance.', false, true, false, false, 'TEST_TIME_INSTANCE', 36, NULL);
INSERT INTO property VALUES (163, '2014-12-17 13:26:16.535', 'admin', 0, 'Test integer vector property associated with device type.', false, false, false, true, 'TEST_INTVEC_TYPE', 37, NULL);
INSERT INTO property VALUES (165, '2014-12-17 13:27:25.783', 'admin', 0, 'Test double vector property associated with a device type.', false, false, false, true, 'TEST_DBLVEC_TYPE', 38, NULL);
INSERT INTO property VALUES (167, '2014-12-17 13:28:05.914', 'admin', 0, 'TEST string list property associated with all entities.', true, true, true, true, 'TEST_STRLIST_ALL', 39, NULL);
INSERT INTO property VALUES (169, '2014-12-17 13:28:43.472', 'admin', 0, 'Test double table property associated wit all entities.', true, true, true, true, 'TEST_TBL_ALL', 40, NULL);
INSERT INTO property VALUES (171, '2014-12-17 13:29:14.405', 'admin', 0, 'Test enumeration property associated with all entities.', false, true, true, true, 'TEST_ENUM_ALL', 41, NULL);
INSERT INTO property VALUES (516, '2014-12-19 12:32:36.097', 'admin', 0, 'Installation slot property', false, false, true, false, 'INST_SLOT_PROP', 35, NULL);
INSERT INTO property VALUES (599, '2014-12-19 13:10:49.449', 'admin', 0, 'Device instance property', false, true, false, false, 'DEV_INST_PROP', 35, NULL);

INSERT INTO slot VALUES (47, '2014-12-17 10:37:59.626', 'system', 0, NULL, NULL, NULL, NULL, 'Implicit CCDB type.', false, '_ROOT', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 45);
INSERT INTO slot VALUES (193, '2014-12-17 13:36:00.381', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-2', false, 'TEST-1-2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (184, '2014-12-17 13:36:09.807', 'admin', 2, NULL, NULL, NULL, NULL, 'Test-1-1', false, 'TEST-1-1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (181, '2014-12-17 13:36:17.914', 'admin', 2, NULL, NULL, NULL, NULL, 'Test-1', false, 'TEST-1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (198, '2014-12-17 13:36:42.841', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-3', false, 'TEST-1-3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (201, '2014-12-17 13:37:07.072', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-1-1', false, 'TEST-1-1-1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (204, '2014-12-17 13:37:19.817', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-1-2', false, 'TEST-1-1-2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (207, '2014-12-17 13:37:33.184', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-2-1', false, 'TEST-1-2-1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (210, '2014-12-17 13:37:46.368', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-2-2', false, 'TEST-1-2-2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (213, '2014-12-17 13:37:58.368', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-2-3', false, 'TEST-1-2-3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (216, '2014-12-17 13:38:13.527', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-3-1', false, 'TEST-1-3-1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (219, '2014-12-17 13:38:25.316', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-3-2', false, 'TEST-1-3-2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (222, '2014-12-17 13:38:39.767', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-3-3', false, 'TEST-1-3-3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (225, '2014-12-17 13:39:00.48', 'admin', 0, NULL, NULL, NULL, NULL, 'Test-1-3-4', false, 'TEST-1-3-4', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 46);
INSERT INTO slot VALUES (228, '2014-12-17 13:44:05.557', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-1-1_001', true, 'TEST-1-1-1_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (231, '2014-12-17 13:44:28.838', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-1-1_002', true, 'TEST-1-1-1_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (234, '2014-12-17 13:44:47.523', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-1-2_001', true, 'TEST-1-1-2_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (237, '2014-12-17 13:44:58.205', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-1-2_002', true, 'TEST-1-1-2_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (240, '2014-12-17 13:45:15.441', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-1_001', true, 'TEST-1-2-1_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (243, '2014-12-17 13:45:25.475', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-1_002', true, 'TEST-1-2-1_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (246, '2014-12-17 13:45:45.343', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-2_001', true, 'TEST-1-2-2_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (249, '2014-12-17 13:46:14.355', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-2_002', true, 'TEST-1-2-2_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (252, '2014-12-17 13:46:34.152', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-3_001', true, 'TEST-1-2-3_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (255, '2014-12-17 13:46:43.23', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-3_002', true, 'TEST-1-2-3_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (258, '2014-12-17 14:01:40.169', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-3_003', true, 'TEST-1-2-3_003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (261, '2014-12-17 14:01:52.871', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-2-3_004', true, 'TEST-1-2-3_004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (264, '2014-12-17 14:02:10.586', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-1_001', true, 'TEST-1-3-1_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (267, '2014-12-17 14:02:18.758', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-1_002', true, 'TEST-1-3-1_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (270, '2014-12-17 14:02:27.201', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-1_003', true, 'TEST-1-3-1_003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (273, '2014-12-17 14:02:41.926', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-2_001', true, 'TEST-1-3-2_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (276, '2014-12-17 14:02:50.919', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-2_002', true, 'TEST-1-3-2_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (279, '2014-12-17 14:03:01.119', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-2_003', true, 'TEST-1-3-2_003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (282, '2014-12-17 14:03:19.881', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-3_001', true, 'TEST-1-3-3_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (285, '2014-12-17 14:03:30.161', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-3_002', true, 'TEST-1-3-3_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (288, '2014-12-17 14:03:39.816', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-3_003', true, 'TEST-1-3-3_003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 173);
INSERT INTO slot VALUES (291, '2014-12-17 14:03:55.842', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-4_001', true, 'TEST-1-3-4_001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (294, '2014-12-17 14:04:03.268', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-4_002', true, 'TEST-1-3-4_002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (297, '2014-12-17 14:04:12.936', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-4_003', true, 'TEST-1-3-4_003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);
INSERT INTO slot VALUES (300, '2014-12-17 14:04:23.666', 'admin', 0, NULL, NULL, NULL, NULL, 'TEST-1-3-4_004', true, 'TEST-1-3-4_004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 175);

INSERT INTO slot_pair VALUES (182, 1, 0, 181, 47, 42);
INSERT INTO slot_pair VALUES (185, 1, 0, 184, 181, 42);
INSERT INTO slot_pair VALUES (194, 2, 0, 193, 181, 42);
INSERT INTO slot_pair VALUES (199, 3, 0, 198, 181, 42);
INSERT INTO slot_pair VALUES (202, 1, 0, 201, 184, 42);
INSERT INTO slot_pair VALUES (205, 2, 0, 204, 184, 42);
INSERT INTO slot_pair VALUES (208, 1, 0, 207, 193, 42);
INSERT INTO slot_pair VALUES (211, 2, 0, 210, 193, 42);
INSERT INTO slot_pair VALUES (214, 3, 0, 213, 193, 42);
INSERT INTO slot_pair VALUES (217, 1, 0, 216, 198, 42);
INSERT INTO slot_pair VALUES (220, 2, 0, 219, 198, 42);
INSERT INTO slot_pair VALUES (223, 3, 0, 222, 198, 42);
INSERT INTO slot_pair VALUES (226, 4, 0, 225, 198, 42);
INSERT INTO slot_pair VALUES (229, 1, 0, 228, 201, 42);
INSERT INTO slot_pair VALUES (232, 2, 0, 231, 201, 42);
INSERT INTO slot_pair VALUES (235, 1, 0, 234, 204, 42);
INSERT INTO slot_pair VALUES (238, 2, 0, 237, 204, 42);
INSERT INTO slot_pair VALUES (241, 1, 0, 240, 207, 42);
INSERT INTO slot_pair VALUES (244, 2, 0, 243, 207, 42);
INSERT INTO slot_pair VALUES (247, 1, 0, 246, 210, 42);
INSERT INTO slot_pair VALUES (250, 2, 0, 249, 210, 42);
INSERT INTO slot_pair VALUES (253, 1, 0, 252, 213, 42);
INSERT INTO slot_pair VALUES (256, 2, 0, 255, 213, 42);
INSERT INTO slot_pair VALUES (259, 3, 0, 258, 213, 42);
INSERT INTO slot_pair VALUES (262, 4, 0, 261, 213, 42);
INSERT INTO slot_pair VALUES (265, 1, 0, 264, 216, 42);
INSERT INTO slot_pair VALUES (268, 2, 0, 267, 216, 42);
INSERT INTO slot_pair VALUES (271, 3, 0, 270, 216, 42);
INSERT INTO slot_pair VALUES (274, 1, 0, 273, 219, 42);
INSERT INTO slot_pair VALUES (277, 2, 0, 276, 219, 42);
INSERT INTO slot_pair VALUES (280, 3, 0, 279, 219, 42);
INSERT INTO slot_pair VALUES (283, 1, 0, 282, 222, 42);
INSERT INTO slot_pair VALUES (286, 2, 0, 285, 222, 42);
INSERT INTO slot_pair VALUES (289, 3, 0, 288, 222, 42);
INSERT INTO slot_pair VALUES (292, 1, 0, 291, 225, 42);
INSERT INTO slot_pair VALUES (295, 2, 0, 294, 225, 42);
INSERT INTO slot_pair VALUES (298, 3, 0, 297, 225, 42);
INSERT INTO slot_pair VALUES (301, 4, 0, 300, 225, 42);

INSERT INTO device VALUES (601, '2014-12-22 11:39:24.896', 'admin', 3, NULL, NULL, 'Device 001', NULL, NULL, NULL, NULL, NULL, '001', 'READY', NULL, 173);
INSERT INTO device VALUES (604, '2014-12-19 13:13:19.165', 'admin', 1, NULL, NULL, 'Device 002', NULL, NULL, NULL, NULL, NULL, '002', 'DEFINED', NULL, 173);

INSERT INTO comptype_property_value VALUES (7, '2014-12-18 12:10:42.406', 'admin', 0, false, '{"meta":{"type":"SedsScalar_Integer","protocol":"SEDSv1","version":"1.0.0"},"data":{"value":123}}', 1, NULL, false, false, false, 173);
INSERT INTO comptype_property_value VALUES (9, '2014-12-18 12:11:03.477', 'admin', 0, false, NULL, 3, NULL, false, true, true, 173);
INSERT INTO comptype_property_value VALUES (39, '2014-12-18 12:11:11.418', 'admin', 0, false, NULL, 5, NULL, true, false, true, 173);

INSERT INTO slot_property_value VALUES (567, '2014-12-19 13:08:35.832', 'admin', 0, false, NULL, 3, NULL, 231);
INSERT INTO slot_property_value VALUES (569, '2014-12-19 13:08:35.865', 'admin', 0, false, NULL, 3, NULL, 240);
INSERT INTO slot_property_value VALUES (571, '2014-12-19 13:08:35.903', 'admin', 0, false, NULL, 3, NULL, 243);
INSERT INTO slot_property_value VALUES (573, '2014-12-19 13:08:35.937', 'admin', 0, false, NULL, 3, NULL, 252);
INSERT INTO slot_property_value VALUES (575, '2014-12-19 13:08:35.975', 'admin', 0, false, NULL, 3, NULL, 255);
INSERT INTO slot_property_value VALUES (577, '2014-12-19 13:08:36.009', 'admin', 0, false, NULL, 3, NULL, 258);
INSERT INTO slot_property_value VALUES (579, '2014-12-19 13:08:36.051', 'admin', 0, false, NULL, 3, NULL, 261);
INSERT INTO slot_property_value VALUES (581, '2014-12-19 13:08:36.085', 'admin', 0, false, NULL, 3, NULL, 264);
INSERT INTO slot_property_value VALUES (583, '2014-12-19 13:08:36.116', 'admin', 0, false, NULL, 3, NULL, 267);
INSERT INTO slot_property_value VALUES (585, '2014-12-19 13:08:36.149', 'admin', 0, false, NULL, 3, NULL, 270);
INSERT INTO slot_property_value VALUES (587, '2014-12-19 13:08:36.179', 'admin', 0, false, NULL, 3, NULL, 282);
INSERT INTO slot_property_value VALUES (589, '2014-12-19 13:08:36.212', 'admin', 0, false, NULL, 3, NULL, 285);
INSERT INTO slot_property_value VALUES (591, '2014-12-19 13:08:36.244', 'admin', 0, false, NULL, 3, NULL, 288);
INSERT INTO slot_property_value VALUES (593, '2014-12-19 13:09:20.661', 'admin', 1, false, '{"meta":{"type":"SedsScalar_Integer","protocol":"SEDSv1","version":"1.0.0"},"data":{"value":456}}', 3, NULL, 228);

INSERT INTO device_property_value VALUES (605, '2014-12-19 13:13:19.165', 'admin', 0, false, NULL, 5, NULL, 604);
INSERT INTO device_property_value VALUES (602, '2014-12-19 13:13:40.745', 'admin', 1, false, '{"meta":{"type":"SedsScalar_Integer","protocol":"SEDSv1","version":"1.0.0"},"data":{"value":789}}', 5, NULL, 601);

INSERT INTO installation_record VALUES (635, '2014-12-22 10:59:32.093', 'admin', 0, '2014-12-22', NULL, '1419242372085', NULL, 601, 231);

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
