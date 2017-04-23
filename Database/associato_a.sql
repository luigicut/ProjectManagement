INSERT INTO associato_a (CFmembro, CODamministratore, taskID, IDprogetto)
VALUES ('FRNDNL88M02H703J', '111', '3', 'PP04012017');

/*Error Code: 1452. Cannot add or update a child row: 
a foreign key constraint fails (`projectman`.`associato_a`, CONSTRAINT `associato_a_ibfk_2` 
FOREIGN KEY (`taskID`, `IDprogetto`) REFERENCES `task` (`taskID`, `IDprogetto`) ON DELETE CASCADE)*/
