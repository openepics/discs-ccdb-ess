-- ALTER SEQUENCE privilege_privilege_id_seq RESTART WITH 1;
-- ALTER SEQUENCE user_role_user_role_id_seq RESTART WITH 1;
-- ALTER SEQUENCE unit_unit_id_seq RESTART WITH 1;
INSERT INTO role (role_id, description, version) VALUES ('admin', 'admin', 0);
INSERT INTO "user" (user_id, comment, email, name, version) VALUES ('admin', 'admin', 'admin@admin.com', 'admin', 0);
INSERT INTO user_role (candelegate, comment, endtime, isrolemanager, starttime, version, role, "user") VALUES ( true, 'admin', '2050-01-08 04:05:06', true, '2001-01-08 04:05:06', 0, 'admin', 'admin');
INSERT INTO privilege (oper, resource, role) VALUES ( 'UPDATE', 'INSTALLATION_RECORD', 'admin'),( 'AUTHORIZED', 'ALIGNMENT_RECORD', 'admin'),( 'LOGOUT', 'ALIGNMENT_RECORD', 'admin'),( 'LOGIN', 'ALIGNMENT_RECORD', 'admin'),( 'DELETE', 'ALIGNMENT_RECORD', 'admin'),( 'CREATE', 'ALIGNMENT_RECORD', 'admin'),( 'UPDATE', 'ALIGNMENT_RECORD', 'admin'),( 'AUTHORIZED', 'DEVICE', 'admin'),( 'AUTHORIZED', 'SLOT', 'admin'),( 'AUTHORIZED', 'COMPONENT_TYPE', 'admin'),( 'AUTHORIZED', 'USER', 'admin'),( 'AUTHORIZED', 'INSTALLATION_RECORD', 'admin'),( 'AUTHORIZED', 'MENU', 'admin'),( 'UPDATE', 'DEVICE', 'admin'),( 'LOGOUT', 'DEVICE', 'admin'),( 'LOGIN', 'DEVICE', 'admin'),( 'DELETE', 'DEVICE', 'admin'),( 'CREATE', 'DEVICE', 'admin'),( 'LOGOUT', 'SLOT', 'admin'),( 'LOGIN', 'SLOT', 'admin'),( 'DELETE', 'SLOT', 'admin'),( 'CREATE', 'SLOT', 'admin'),( 'UPDATE', 'SLOT', 'admin'),( 'LOGOUT', 'COMPONENT_TYPE', 'admin'),( 'LOGIN', 'COMPONENT_TYPE', 'admin'),( 'DELETE', 'COMPONENT_TYPE', 'admin'),( 'CREATE', 'COMPONENT_TYPE', 'admin'),( 'UPDATE', 'COMPONENT_TYPE', 'admin'),( 'LOGOUT', 'USER', 'admin'),( 'LOGIN', 'USER', 'admin'),( 'DELETE', 'USER', 'admin'),( 'CREATE', 'USER', 'admin'),( 'UPDATE', 'USER', 'admin'),( 'LOGOUT', 'INSTALLATION_RECORD', 'admin'),( 'LOGIN', 'INSTALLATION_RECORD', 'admin'),( 'DELETE', 'INSTALLATION_RECORD', 'admin'),( 'CREATE', 'INSTALLATION_RECORD', 'admin');



