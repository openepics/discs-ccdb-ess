select setval('public.hibernate_sequence', GREATEST (100000,(SELECT MAX(id)+1 FROM audit_record)), false);
