DELIMITER //
  
CREATE PROCEDURE clean_burnable_proce()
BEGIN
UPDATE topics SET burned=TRUE WHERE burn_topic=TRUE AND burned=FALSE AND deleted=FALSE AND DATE_ADD(creation_time, INTERVAL '10' MINUTE)<NOW();
UPDATE topic_comments SET burned=TRUE WHERE is_burnable=TRUE AND burned=FALSE AND deleted=FALSE AND DATE_ADD(creation_time, INTERVAL '10' MINUTE)<NOW();
COMMIT ;
END//
DELIMITER ;

CREATE EVENT clean_burnable_event
ON SCHEDULE EVERY 1 SECOND
ON COMPLETION PRESERVE DISABLE
DO CALL clean_burnable_proce();
 
ALTER EVENT clean_burnable_event ON COMPLETION PRESERVE ENABLE;
#alter event clean_burnable_event on completion preserve disable;
#SELECT event_name,event_definition,interval_value,interval_field,status FROM information_schema.EVENTS;
#
#
#
DELIMITER //
CREATE TRIGGER update_comments_check AFTER UPDATE ON topic_comments
FOR EACH ROW
  BEGIN
    IF NEW.burned IS TRUE THEN
      UPDATE topics t SET t.comments_num=(SELECT COUNT(*) FROM topic_comments c WHERE c.deleted IS FALSE AND c.burned IS FALSE AND c.topic_id=NEW.topic_id) WHERE t.id=NEW.topic_id;
    END IF;
  END;//
DELIMITER ;

