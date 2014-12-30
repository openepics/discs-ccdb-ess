select setval('public.hibernate_sequence', GREATEST (10000,(SELECT MAX(id)+1 FROM audit_record)), false);
