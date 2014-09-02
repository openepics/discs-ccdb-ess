select setval('hibernate_sequence', (SELECT MAX(id)+1 FROM audit_record), false);
