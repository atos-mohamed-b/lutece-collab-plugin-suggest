--
-- Add a new colum for a consultation's workflow's ID
--
ALTER TABLE suggest_suggest ADD id_workflow INT DEFAULT NULL AFTER workgroup;