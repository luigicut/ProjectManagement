CREATE SCHEMA IF NOT EXISTS projectman;
USE projectman;


CREATE TABLE IF NOT EXISTS membro ( 
  CFmembro CHAR(16) NOT NULL,
  nome VARCHAR(20),
  cognome VARCHAR(20),
  impiegato TINYINT(1),
  tempo_speso_totale DOUBLE,
  indirizzo VARCHAR(90),
  telefono VARCHAR(20),
  PRIMARY KEY (CFmembro)
);


CREATE TABLE IF NOT EXISTS team ( 
  nome VARCHAR(20) NOT NULL,
  PRIMARY KEY (nome)
);


CREATE TABLE IF NOT EXISTS email ( 
  indirizzoemail VARCHAR(40) NOT NULL,
  CFmembro CHAR(16) NOT NULL,
  PRIMARY KEY (indirizzoemail),
  FOREIGN KEY (CFmembro) REFERENCES membro (CFmembro) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS progetto ( 
  IDprogetto CHAR(10) NOT NULL,
  nome VARCHAR(45),
  PRIMARY KEY (IDprogetto)
);


CREATE TABLE IF NOT EXISTS task ( 
  taskID INT NOT NULL AUTO_INCREMENT,
  IDprogetto CHAR(10) NOT NULL,
  commenti VARCHAR(200),
  assegnata TINYINT(1),
  completata TINYINT(1),
  Pcompletamento INT,
  tempo_stimato DOUBLE,
  priorità CHAR(1),
  nome VARCHAR(45),
  descrizione VARCHAR(200),
  PRIMARY KEY (taskID, IDprogetto),
  FOREIGN KEY (IDprogetto) REFERENCES progetto (IDprogetto) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS committente ( 
  CFcommittente CHAR(16) NOT NULL,
  nome VARCHAR(45),
  cognome VARCHAR(45),
  email VARCHAR(45),
  PRIMARY KEY (CFcommittente)
);


CREATE TABLE IF NOT EXISTS telefono ( 
  telefono VARCHAR(20) NOT NULL,
  CFcommittente CHAR(16) NOT NULL,
  PRIMARY KEY (telefono),
    FOREIGN KEY (CFcommittente) REFERENCES committente (CFcommittente) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS bacheca ( 
  IDbacheca INT NOT NULL AUTO_INCREMENT,
  IDprogetto CHAR(10) NOT NULL,
  interna TINYINT(1) NOT NULL,
  PRIMARY KEY (IDbacheca, IDprogetto), /*possiamo dare un nome con constraint alla key*/
  FOREIGN KEY (IDprogetto) REFERENCES progetto (IDprogetto) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS bacheca_esterna ( 
  CODcommittente INT NOT NULL,
  IDbacheca INT NOT NULL,
  IDprogetto CHAR(10) NOT NULL,
  PRIMARY KEY (CODcommittente, IDprogetto, IDbacheca),
  FOREIGN KEY (IDbacheca, IDprogetto) REFERENCES bacheca (IDbacheca, IDprogetto) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS amministratore ( 
  CODamministratore INT NOT NULL,
  CFmembro CHAR(16) NOT NULL,
  PRIMARY KEY (CODamministratore, CFmembro),
  FOREIGN KEY (CFmembro) REFERENCES membro (CFmembro) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS composto_da ( 
  CFmembro CHAR(16) NOT NULL,
  nome_team VARCHAR(20) NOT NULL,
  PRIMARY KEY (CFmembro, nome_team),
   FOREIGN KEY (CFmembro) REFERENCES membro (CFmembro) ON DELETE CASCADE,
    FOREIGN KEY (nome_team) REFERENCES team (nome) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS lavora_su_progetto ( 
  nome_team VARCHAR(20) NOT NULL,
  IDprogetto CHAR(10) NOT NULL,
  PRIMARY KEY (nome_team, IDprogetto),
    FOREIGN KEY (nome_team) REFERENCES team (nome) ON DELETE CASCADE,
    FOREIGN KEY (IDprogetto) REFERENCES progetto (IDprogetto) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS commissionato_da ( 
  IDprogetto CHAR(10) NOT NULL,
  CFcommittente CHAR(16) NOT NULL,
  PRIMARY KEY (IDprogetto, CFcommittente),
    FOREIGN KEY (IDprogetto) REFERENCES progetto (IDprogetto) ON DELETE CASCADE,
    FOREIGN KEY (CFcommittente) REFERENCES committente (CFcommittente) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS lavora_su_task ( 
  CFmembro CHAR(16) NOT NULL,
  taskID INT NOT NULL,
  IDprogetto CHAR(10) NOT NULL,
  tempo_speso DOUBLE NOT NULL,
  PRIMARY KEY (CFmembro, taskID, IDprogetto),
    FOREIGN KEY (CFmembro) REFERENCES membro (CFmembro) ON DELETE CASCADE,
    FOREIGN KEY (taskID, IDprogetto) REFERENCES task (taskID, IDprogetto) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS associato_a ( 
  CFmembro CHAR(16) NOT NULL,
  CODamministratore INT NOT NULL,
  taskID INT NOT NULL,
  IDprogetto CHAR(10) NOT NULL,
  PRIMARY KEY (CFmembro, CODamministratore, taskID, IDprogetto),
    FOREIGN KEY (CFmembro, CODamministratore) REFERENCES amministratore (CFmembro, CODamministratore) ON DELETE CASCADE,
     FOREIGN KEY (taskID, IDprogetto) REFERENCES task (taskID, IDprogetto) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS accede_a ( 
  CFcommittente CHAR(16) NOT NULL,
  CODcommittente INT NOT NULL,
  IDbacheca INT NOT NULL,
  IDprogetto CHAR(10) NOT NULL,
  PRIMARY KEY (CFcommittente, CODcommittente, IDbacheca, IDprogetto),
    FOREIGN KEY (CFcommittente) REFERENCES committente (CFcommittente) ON DELETE CASCADE,
     FOREIGN KEY (IDbacheca, IDprogetto, CODcommittente) REFERENCES bacheca_esterna (IDbacheca, IDprogetto, CODcommittente) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS mostra ( 
  IDprogetto_b CHAR(10) NOT NULL,
  IDbacheca INT NOT NULL,
  CODcommittente INT NOT NULL,
  taskID INT NOT NULL,
  IDprogetto_t CHAR(10) NOT NULL,
  PRIMARY KEY (IDprogetto_b, IDbacheca, CODcommittente, taskID, IDprogetto_t),
    FOREIGN KEY (IDbacheca, IDprogetto_b, CODcommittente) REFERENCES bacheca_esterna (IDbacheca, IDprogetto, CODcommittente) ON DELETE CASCADE,
    FOREIGN KEY (taskID, IDprogetto_t) REFERENCES task (taskID, IDprogetto) ON DELETE CASCADE 
);


CREATE TABLE IF NOT EXISTS visualizza ( 
  IDprogetto_b CHAR(10) NOT NULL,
  IDbacheca INT NOT NULL,
  taskID INT NOT NULL,
  IDprogetto_t CHAR(10) NOT NULL,
  PRIMARY KEY (IDprogetto_b, IDbacheca, taskID, IDprogetto_t),
  FOREIGN KEY (IDbacheca, IDprogetto_b) REFERENCES bacheca (IDbacheca, IDprogetto) ON DELETE CASCADE,
	FOREIGN KEY (taskID, IDprogetto_t) REFERENCES task (taskID, IDprogetto) ON DELETE CASCADE
);


LOAD DATA LOCAL INFILE 'c:/popolazione/membri.txt' INTO TABLE membro FIELDS TERMINATED BY',' LINES terminated BY '\n' (CFmembro, nome, cognome, indirizzo, telefono, tempo_speso_totale, impiegato);
LOAD DATA LOCAL INFILE 'c:/popolazione/email.txt' INTO TABLE email FIELDS TERMINATED BY',' LINES terminated BY '\n' (indirizzoemail, CFmembro);
LOAD DATA LOCAL INFILE 'c:/popolazione/progetti.txt' INTO TABLE progetto FIELDS TERMINATED BY',' LINES terminated BY '\n' (IDprogetto, nome);
LOAD DATA LOCAL INFILE 'c:/popolazione/task.txt' INTO TABLE task FIELDS TERMINATED BY',' LINES terminated BY '\n' (IDprogetto, commenti, assegnata, Pcompletamento, tempo_stimato, priorità, nome ,descrizione, completata);
LOAD DATA LOCAL INFILE 'c:/popolazione/team.txt' INTO TABLE team FIELDS TERMINATED BY',' LINES terminated BY ','(nome);
LOAD DATA LOCAL INFILE 'c:/popolazione/bacheca.txt' INTO TABLE bacheca FIELDS TERMINATED BY',' LINES terminated BY '\n' (interna, IDprogetto);
LOAD DATA LOCAL INFILE 'c:/popolazione/bacheca_esterna.txt' INTO TABLE bacheca_esterna FIELDS TERMINATED BY',' LINES terminated BY '\n'  (CODcommittente, IDbacheca, IDprogetto);
LOAD DATA LOCAL INFILE 'c:/popolazione/committenti.txt' INTO TABLE committente FIELDS TERMINATED BY',' LINES terminated BY '\n'  (CFcommittente, nome, cognome, email);
LOAD DATA LOCAL INFILE 'c:/popolazione/telefono.txt' INTO TABLE telefono FIELDS TERMINATED BY',' LINES terminated BY '\n' (telefono, CFcommittente);
LOAD DATA LOCAL INFILE 'c:/popolazione/amministratore.txt' INTO TABLE amministratore FIELDS TERMINATED BY',' LINES terminated BY '\n' (CODamministratore, CFmembro);
LOAD DATA LOCAL INFILE 'c:/popolazione/composto_da.txt' INTO TABLE composto_da FIELDS TERMINATED BY',' LINES terminated BY '\n' (nome_team, CFmembro);
LOAD DATA LOCAL INFILE 'c:/popolazione/lavora_su_progetto.txt' INTO TABLE lavora_su_progetto FIELDS TERMINATED BY',' LINES terminated BY '\n' (nome_team, IDprogetto);
LOAD DATA LOCAL INFILE 'c:/popolazione/commissionato_da.txt' INTO TABLE commissionato_da FIELDS TERMINATED BY',' LINES terminated BY '\n' (IDprogetto, CFcommittente);
LOAD DATA LOCAL INFILE 'c:/popolazione/lavora_su_task.txt' INTO TABLE lavora_su_task FIELDS TERMINATED BY',' LINES terminated BY '\n' (taskID, IDprogetto, tempo_speso, CFmembro);
LOAD DATA LOCAL INFILE 'c:/popolazione/associato_a.txt' INTO TABLE associato_a FIELDS TERMINATED BY',' LINES terminated BY '\n' (CFmembro, CODamministratore, taskID, IDprogetto);
LOAD DATA LOCAL INFILE 'c:/popolazione/accede_a.txt' INTO TABLE accede_a FIELDS TERMINATED BY',' LINES terminated BY '\n' (CFcommittente, CODcommittente, IDbacheca, IDprogetto);
LOAD DATA LOCAL INFILE 'c:/popolazione/mostra.txt' INTO TABLE mostra FIELDS TERMINATED BY',' LINES terminated BY '\n' (IDprogetto_b, IDbacheca, CODcommittente, taskID, IDprogetto_t);
LOAD DATA LOCAL INFILE 'c:/popolazione/visualizza.txt' INTO TABLE visualizza FIELDS TERMINATED BY',' LINES terminated BY '\n' (IDprogetto_b,IDbacheca,taskID,IDprogetto_t);
