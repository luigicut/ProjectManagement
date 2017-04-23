package progettoBasiDati;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;





public class Database {
	private static Connection con;
	private static DatabaseMetaData db;
	private static Statement st;
	static Scanner in=new Scanner(System.in);
	
/***metodo di connessione al database***/
	public static boolean ConnectionDB(){
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://localhost:3306/projectman?autoReconnect=true&useSSL=false";
	        con = DriverManager.getConnection(url,"root","luigi");
			db = con.getMetaData();
			st = con.createStatement();
			System.out.println("Connessione al Database "+db.getDatabaseProductName()+" riuscita.\n"); 
			return true;
		}catch(Exception e){
			System.out.println("Connessione Fallita...\n"+e);
			return false;
		}
	}
/***metodo di disconnessione dal database***/
	public static void closeConnectionDB(){
		if(con != null){
			try{ st.close(); 
				System.out.println("Connessione al Database chiusa.\n"); 
			}
			catch(SQLException e){
				System.out.println(e);
			}
		}
	}
	
/***Op1: Calcolare il tempo speso totale da un membro***/
	public static void op1(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			
			//stampa la lista dei membri in database
			String query="SELECT CFmembro, nome, cognome FROM membro";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}
			
			//SELECT dell'attributo richiesto
			query = "SELECT tempo_speso_totale, CFmembro FROM membro AS M WHERE M.CFmembro = ?";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci il codice fiscale del membro interessato: ");
			ps.setString(1, in.nextLine());
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
/***Op2 Crea una task non assegnata in progresso***/
	public static void op2(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			int comp = 0;
			
			//stampa la lista dei progetti esistenti
			String query="SELECT IDprogetto FROM progetto";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
			
			//query di creazione TASK
			query = "INSERT INTO task (IDprogetto, nome, descrizione, assegnata, completata, Pcompletamento, tempo_stimato, priorità, commenti)"
				  + " VALUES (?,?,?,?,?,?,?,?,?)";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci l'ID del progetto tra quelli disponibili: ");
			String idp = in.nextLine();
			ps.setString(1, idp);
			System.out.println("Inserisci il nome della task: ");
			ps.setString(2, in.nextLine());
			System.out.println("Inserisci una descrizione alla task: ");
			ps.setString(3, in.nextLine());
			ps.setInt(4, 1);//non assegnata
			ps.setInt(5, comp);			
			int perc =0;
			while(true){//controllo della percentuale
				System.out.println("Inserisci la percentuale di completamento attuale della task: ");
				perc = in.nextInt();
					if(perc<100) break;
					System.out.println("Il numero deve essere compreso tra 0 e 99");			
			}
			ps.setInt(6, perc);
			System.out.println("Inserisci il tempo stimato: ");
			ps.setDouble(7, in.nextDouble());
			in.nextLine();
			System.out.println("Inserisci la priorità della task (A - E): ");
			String ch = "";
			while(true){//controllo sulla lunghezza
				ch = in.nextLine();
				if(ch.length()==1) break;
				System.out.println("inserire una sola lettera compresa tra A ed E");
			}
			ps.setString(8, ch);
			System.out.println("Inserisci un eventuale commento alla task");
			ps.setString(9, in.nextLine());
			ps.executeUpdate();
			
			//memorizziamo l'id della task creata in una variabile
			query="SELECT max(taskID) FROM task";
			ps = con.prepareStatement(query);
			re = ps.executeQuery();
			re.next();
			int tid=re.getInt(1);
			
			//memorizziamo l'id bacheca associato alla task creata in una variabile
			query="SELECT IDbacheca FROM bacheca WHERE IDprogetto=? AND interna= '1'";
			ps = con.prepareStatement(query);
			ps.setString(1, idp);
			re = ps.executeQuery();
			re.next();
			int idb=re.getInt(1);
			re.close();
			
			//creiamo la relazione visualizza tra task e bacheca
			query="INSERT INTO visualizza(IDbacheca, IDprogetto_b, taskID, IDprogetto_t) VALUES(?,?,?,?)";
			ps = con.prepareStatement(query);
			ps.setInt(1, idb);
			ps.setString(2, idp);
			ps.setInt(3, tid);
			ps.setString(4, idp);
			ps.executeUpdate();
			
			//verifica di associazione Progetto-Team
			query="SELECT nome_team FROM lavora_su_progetto WHERE IDprogetto=?";
			ps = con.prepareStatement(query);
			ps.setString(1, idp);
			re = ps.executeQuery();
			
			if(re.next()){
				
				//se esiste un nome team crea la relazione tra uno o più amministratori del team associato al progetto e la task creata
				System.out.println("Il progetto selezionato e associato ad un team; scegliere uno o più amministratori da associare alla task, tra quelli disponibili, inserendo il CF: ");
				query="SELECT M.nome, M.cognome, C.CFmembro, C.nome_team FROM composto_da AS C, membro AS M WHERE C.CFmembro=M.CFmembro AND C.nome_team=? AND M.impiegato='0'";
				ps = con.prepareStatement(query);
				ps.setString(1, re.getString(1));
				re = ps.executeQuery();
				if(re != null){
					resultTable(re);
					System.out.println();
					re.close();
				}
				
				while(true){
					//memorizziamo il CODamministratore in una variabile
					String cfa=in.nextLine();
					query="SELECT CODamministratore FROM amministratore WHERE CFmembro=?";
					ps = con.prepareStatement(query);
					ps.setString(1, cfa);
					re = ps.executeQuery();
					re.next();
					int cda = re.getInt(1);
					
					//creiamo la relazione associato_a tra amministratore e task
					query="INSERT INTO associato_a(CFmembro, CODamministratore, taskID, IDprogetto) VALUES(?,?,?,?)";
					ps = con.prepareStatement(query);
					ps.setString(1, cfa);
					ps.setInt(2, cda);
					ps.setInt(3, tid);
					ps.setString(4, idp);
					ps.executeUpdate();
					
					System.out.println("Vuoi associare altri amministratori? (SI/NO): ");
					if(in.nextLine().equals("NO")) break;
					else System.out.println("inserire un CF differente: ");
				}
				
				
			}
			else
				re.close();
			
			
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}	
	}
/***Op3 Stampa le task completate***/
	public static void op3(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			String query = "SELECT * FROM task AS T WHERE T.completata = '1'";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
/***Op4 Aggiungi un nuovo membro***/
	public static void op4(){
		String cf=null;
		PreparedStatement ps = null;
		PreparedStatement pa = null;
		ResultSet re;
		try{
		Database.ConnectionDB();
		
		String query = "INSERT INTO membro (CFmembro, nome, cognome, impiegato, tempo_speso_totale, indirizzo, telefono) VALUES (?, ?, ?, ?, ?, ?, ?)";
		ps = con.prepareStatement(query);
		System.out.println("Inserisci il codice fiscale: ");
		cf=in.nextLine();
		ps.setString(1, cf);
		System.out.println("Inserisci il nome: ");
		ps.setString(2, in.nextLine());
		System.out.println("Inserisci il cognome: ");
		ps.setString(3, in.nextLine());
		System.out.println("Inserisci 1 se impiegato, 0 altrimenti: ");
		int amm=in.nextInt();
		in.nextLine();	
		ps.setInt(4, amm);
		ps.setDouble(5, 0);
		System.out.println("Inserisci l'indirizzo: ");
		ps.setString(6, in.nextLine());
		System.out.println("inserisci telefono: ");
		ps.setString(7, in.nextLine());
		ps.executeUpdate();
		if(amm==0){//controllo su attributo impiegato
			System.out.println("il membro creato è un amministratore, assegnare un codice amministratore");
			//creo entità amministratore
			String query1="INSERT INTO amministratore (CODamministratore, CFmembro) VALUES (?,?) ";
			pa= con.prepareStatement(query1);
			pa.setInt(1, in.nextInt());
			in.nextLine();
			pa.setString(2, cf);
			pa.executeUpdate();
		}
			while(true){//aggiungo email
				query="INSERT INTO email (indirizzoemail, CFmembro) VALUES (?,'"+cf+"')";
				ps=con.prepareStatement(query);
				System.out.println("inserisci l'indirizzo e-mail (0 per terminare)");
				String email=in.nextLine();
				if(email.equals("0")){
					break;
				}
				ps.setString(1, email);
				ps.executeUpdate();	
			}//associare ad un team o aggiungere un nuovo team
		System.out.println("vuoi 'creare' un nuovo team o 'aggiungere' ad un team esistente?");
		String dec= in.nextLine();
		String te;
		while(true){
			if(dec.equals("creare")){
				query = "INSERT INTO team (nome) VALUES (?)";
				ps = con.prepareStatement(query);
				System.out.println("Inserisci il nome del team: ");
				te= in.nextLine();
				ps.setString(1, te);
				ps.executeUpdate();
				//creo relazione tra membro e team
				query = "INSERT INTO composto_da (CFmembro,nome_team) VALUES(?,?);";
				ps = con.prepareStatement(query);
				ps.setString(1, cf);
				ps.setString(2, te);
				ps.executeUpdate();
				break;		
			} //seleziono un team preesistente
			if(dec.equals("aggiungere")){
				query = "SELECT * FROM team";
				ps = con.prepareStatement(query);
				re=ps.executeQuery();
				if(re != null){
					resultTable(re);
					System.out.println();
				}	
				System.out.println("Scegliere il team");
				te=in.nextLine();
				query = "INSERT INTO composto_da (CFmembro,nome_team) VALUES(?,?);";
				ps = con.prepareStatement(query);
				ps.setString(1, cf);
				ps.setString(2, te);
				ps.executeUpdate();		
				break;		
			}//controllo errore
			System.out.println("ERRORE. Inserire 'creare' o 'aggiungere'");
			dec=in.nextLine();
		}   //stampa del risultato dell'operazione
		
			//controlla se il team aggiunto è associato ad un team
			query="SELECT * FROM lavora_su_progetto WHERE nome_team=?";
			ps = con.prepareStatement(query);
			ps.setString(1, te);
			re= ps.executeQuery();	
			int i=0;
			String prog="";
			ResultSet ri;
			while(re.next()){
				if(i==0){System.out.println("Il Team è associato ad un progetto; effettuare un log work su una delle task del progetto");}
				 prog= re.getString(2);
				 //stampa le taskID di quel progetto
				 query="SELECT taskID, nome FROM task WHERE IDprogetto=?";
				 ps = con.prepareStatement(query);
				 ps.setString(1, prog);
				 ri=ps.executeQuery();
				if(ri != null){
					resultTable(ri);
					System.out.println();
				}
				
				i=1;
			}	
			if(!prog.equals("")){
				System.out.println("Seleziona l'ID della task sulla quale si vuole effettuare il log work: ");
				//crea la relazione tra membro e la task selezionata
				query="INSERT INTO lavora_su_task (CFmembro, taskID, IDprogetto, tempo_speso) VALUES(?,?,?,?)";
				ps = con.prepareStatement(query);
				ps.setString(1, cf);
				ps.setInt(2, in.nextInt());
				in.nextLine();
				ps.setString(3, prog);
				System.out.println("INserisci il tempo speso sulla task: ");
				int time=in.nextInt();
				in.nextLine();
				ps.setInt(4, time);
				ps.executeUpdate();
				
				//effettua l'update del tempo_speso_totale sul menbro 
				query = "UPDATE membro SET tempo_speso_totale = ? WHERE CFmembro=?";
				ps = con.prepareStatement(query);
				ps.setInt(1, time);
				ps.setString(2, cf);
				ps.executeUpdate();
			}	
			
			//stampa del risultato
			query = "SELECT M.CFmembro, M.nome, M.cognome, M.impiegato, M.tempo_speso_totale, M.indirizzo, M.telefono, E.indirizzoemail, C.nome_team "
				+ "FROM membro AS M, email AS E, composto_da AS C "
				+ "WHERE M.CFmembro = E.CFmembro AND M.CFmembro = C.CFmembro AND M.CFmembro=?";
			ps = con.prepareStatement(query);
				ps.setString(1, cf);
				re = ps.executeQuery();	
				if(re != null){
					resultTable(re);
					System.out.println();
					
				}
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}	
	}
	
/***Op5 Leggi il numero di task presenti in una bacheca esterna***/
	public static void op5(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();//stampa la lista delle bacheche esterne
			String query="SELECT * FROM bacheca_esterna";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}		//query dell'operazione
			query = "SELECT COUNT(*) FROM mostra as M WHERE  M.CODcommittente = ? AND M.IDprogetto_b = ? AND M.IDbacheca = ?";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci il codice committente: ");
			ps.setInt(1, in.nextInt());
			in.nextLine();
			System.out.println("Inserisci l'IDprogetto: ");
			ps.setString(2, in.nextLine());
			System.out.println("Inserisci l'IDbacheca: ");
			ps.setInt(3, in.nextInt());
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
/***Op6  Crea un progetto***/
	public static void op6(){
		PreparedStatement ps = null;
		ResultSet ro =null;
		try{
			Database.ConnectionDB();	
			String query = "INSERT INTO progetto (IDprogetto, nome) VALUES (?, ?)";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci l'ID del progetto (10 caratteri): ");
			String pro= in.nextLine();
			ps.setString(1, pro);
			System.out.println("Inserisci il nome del progetto: ");
			ps.setString(2, in.nextLine());
			ps.executeUpdate();	
			System.out.println("questo progetto appartiene ad un committente già registrato? si/no");
			String risp= in.nextLine();
			String cfc;
			while(true){
				if(risp.equals("no")){//creiamo un nuovo committente
					System.out.println("Registrare un nuovo committente. Inserisci il CF del committente:  ");
					query = "INSERT INTO committente (CFcommittente,nome,cognome,email) VALUES (?,?,?,?)";
					ps = con.prepareStatement(query);
					
					cfc= in.nextLine();
					ps.setString(1, cfc);
					System.out.println("Inserisci il nome del committente: ");
					ps.setString(2, in.nextLine());
					System.out.println("Inserisci il cognome del committente: ");
					ps.setString(3, in.nextLine());
					System.out.println("Inserisci l'email del committente: ");
					ps.setString(4, in.nextLine());
					ps.executeUpdate();
					while(true){//aggiungiamo numeri di telefono
						query="INSERT INTO telefono (telefono, CFcommittente) VALUES (?,'"+cfc+"')";
						ps=con.prepareStatement(query);
						System.out.println("inserisci il numero di telefono per il committente (0 per terminare)");
						String ntel=in.nextLine();
						if(ntel.equals("0")){
							break;
						}
						ps.setString(1, ntel);
						ps.executeUpdate();	
					}
					//creiamo la relazione commissionato_da tra Committente e Progetto creato
					query = "INSERT INTO commissionato_da (CFcommittente, IDprogetto) VALUES(?,?);";
					ps = con.prepareStatement(query);
					ps.setString(1, cfc);
					ps.setString(2, pro);
					ps.executeUpdate();
					break;		
					
				}
				//committente già creato
				if(risp.equals("si")){
					query = "SELECT * FROM committente";
					ps = con.prepareStatement(query);
					ro=ps.executeQuery();
					if(ro != null){
						resultTable(ro);
						System.out.println();
					}	
					System.out.println("Scegliere il committente tramite codice fiscale");
					cfc=in.nextLine();
					query = "INSERT INTO commissionato_da (CFcommittente, IDprogetto) VALUES(?,?);";
					ps = con.prepareStatement(query);
					ps.setString(1, cfc);
					ps.setString(2, pro);
					ps.executeUpdate();
					break;
				}	
					System.out.println("ERRORE. Rispondere 'si' oppure 'no'");
					risp=in.nextLine();
				
					
				
			}//aggiungo bacheca interna
			query="INSERT INTO bacheca(IDprogetto,interna) Values('"+pro+"',?)";
			ps=con.prepareStatement(query);
			ps.setInt(1, 1);
			ps.executeUpdate();
			
			//aggiungo bacheca esterna
			query="INSERT INTO bacheca(IDprogetto,interna) Values('"+pro+"',?)";
			ps=con.prepareStatement(query);
			ps.setInt(1, 0);
			ps.executeUpdate();
			
			//salvo l'id della bacheca interna
			query="SELECT IDbacheca FROM bacheca WHERE IDprogetto=? AND interna=?";
			ps=con.prepareStatement(query);
			ps.setString(1, pro);
			ps.setInt(2, 0);
			ro=ps.executeQuery();
			ro.next();
			int idbe=ro.getInt(1);
			
			//salvo l'id della bacheca esterna
			query="SELECT IDbacheca FROM bacheca WHERE IDprogetto=? AND interna=?";
			ps=con.prepareStatement(query);
			ps.setString(1, pro);
			ps.setInt(2, 1);
			ro=ps.executeQuery();
			ro.next();
			int idb=ro.getInt(1);
			
			
			//creo la bacheca esterna 
			query="INSERT INTO bacheca_esterna (CODcommittente, IDbacheca, IDprogetto) VALUES (?,?,?)";
			ps=con.prepareStatement(query);
			System.out.println("Scegli un codice committente per la nuova bacheca esterna");
			int codc=in.nextInt();
			in.nextLine();
			ps.setInt(1, codc);
			ps.setInt(2, idbe);
			ps.setString(3, pro);
			ps.executeUpdate();
			
			//creiamo la relazione accede_a tra Committente e bacheca ESTERNA
			query="INSERT INTO accede_a (CFcommittente, CODcommittente, IDbacheca, IDprogetto) VALUES(?,?,?,?)";
			ps=con.prepareStatement(query);
			ps.setString(1, cfc);
			ps.setInt(2, codc);
			ps.setInt(3, idbe);
			ps.setString(4, pro);
			ps.executeUpdate();
			
		
			ResultSet ri;
			int i=1;
			System.out.printf("Aggiungere almeno una task al progetto.");
			while(true){
				
				//creiamo task per il progetto
			    query = "INSERT INTO task (IDprogetto, nome, descrizione, assegnata, completata, Pcompletamento, tempo_stimato, priorità, commenti)"
							 + " VALUES (?,?,?,?,?,?,?,?,?)";
				ps = con.prepareStatement(query);
				ps.setString(1, pro);
				System.out.println("Inserisci il nome della task: ");
				ps.setString(2, in.nextLine());
				System.out.println("Inserisci una descrizione alla task: ");
				ps.setString(3, in.nextLine());
				System.out.println("Assegnata: 0 \nNon Assegnata: 1");
				ps.setInt(4, in.nextInt());
				System.out.println("inserisci 0 per in progresso, 1 per completata");
				int comp=in.nextInt();
				ps.setInt(5, comp);
				if(comp==0){
					int perc =0;
					while(true){//controllo 
						System.out.println("Inserisci la percentuale di completamento attuale della task: ");
						perc = in.nextInt();
						in.nextLine();
						if(perc<100) break;
						System.out.println("Il numero deve essere compreso tra 0 e 99");			
					}
					ps.setInt(6, perc);
				}	
				if(comp==1){
					ps.setInt(6, 100);
				}
				System.out.println("Inserisci il tempo stimato: ");
				ps.setDouble(7, in.nextDouble());
				in.nextLine();
				System.out.println("Inserisci la priorità della task (A - E): ");
				String ch = "";
				while(true){
					ch = in.nextLine();
					if(ch.length()==1) break;
					System.out.println("inserire una sola lettera compresa tra A ed E");
				}
				ps.setString(8, ch);
				System.out.println("Inserisci un eventuale commento alla task");
				ps.setString(9, in.nextLine());
			    ps.executeUpdate();
			    query="SELECT taskID FROM task WHERE IDprogetto=?";
			    ps = con.prepareStatement(query);
			    ps.setString(1, pro);
			    ri=ps.executeQuery();
			    
			    //prendiamo l'id della task appena creata
			    ri.last();
			    int tid = ri.getInt(1);
			    
			    //se completata = 0 creiamo la relazione visualizza tra bacheca e task
			    if(comp==0){
			    	query="INSERT INTO visualizza(IDprogetto_b, IDbacheca,taskID,IDprogetto_t) VALUES(?,?,?,?)";
			    	ps = con.prepareStatement(query);
			    	ps.setString(1, pro);
			    	ps.setInt(2, idb);
			    	ps.setInt(3, tid);
			    	ps.setString(4, pro);
			    	ps.executeUpdate();
			    	
			    }
			    //se completata = 1 creiamo la relazione mostra tra bacheca esterna e task
			    //e anche a relazione visualizza tra bacheca e task
			    if(comp==1){
			    	query="INSERT INTO mostra(IDprogetto_b, IDbacheca,CODcommittente, taskID,IDprogetto_t) VALUES(?,?,?,?,?)";
			    	ps = con.prepareStatement(query);
			    	ps.setString(1, pro);
			    	ps.setInt(2, idbe);
			    	ps.setInt(3, codc);
			    	ps.setInt(4, tid);
			    	ps.setString(5, pro);
			    	ps.executeUpdate();
			    	
			    	
			    	query="INSERT INTO visualizza(IDprogetto_b, IDbacheca,taskID,IDprogetto_t) VALUES(?,?,?,?)";
			    	ps = con.prepareStatement(query);
			    	ps.setString(1, pro);
			    	ps.setInt(2, idb);
			    	ps.setInt(3, tid);
			    	ps.setString(4, pro);
			    	ps.executeUpdate();
			    }
			    System.out.println("Vuoi inserire ancora task? 1 per continuare, 0 per terminare: ");
			    i=in.nextInt();
			    in.nextLine();
			    if(i==0){break;}
			    
			    
			}			
		ri.close();
		ro.close();
		
		//stampa del risultato
		query="SELECT C.nome AS nome_committente , C.cognome AS cognome_committente, R.IDprogetto, T.taskID, T.nome AS nome_task "
				+ "FROM commissionato_da AS R, committente AS C, task AS T "
				+ "WHERE R.CFcommittente = C.CFcommittente AND R.IDprogetto = T.IDprogetto AND R.IDprogetto=?";
		ps = con.prepareStatement(query);
		ps.setString(1, pro);
		ri = ps.executeQuery();
		if(ri != null){
			resultTable(ri);
			System.out.println();
			ri.close();
		}	
		
		
		
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}	
	}
/***Op7 Crea un Team***/
	public static void op7(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();	//creazione team
			String query = "INSERT INTO team (nome) VALUES (?)";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci il nome del team: ");
			String team = in.nextLine();
			ps.setString(1, team);
			ps.executeUpdate();
			
			
			System.out.printf("Aggiungere almeno una membro al team.");
			int i=0;
			while(true){//inserimento nuovo membro - tutti i membri devono afferire ad un team e un team deve avere almeno un membro -  
			    query = "INSERT INTO membro (CFmembro, nome, cognome, impiegato, tempo_speso_totale, indirizzo, telefono) VALUES (?, ?, ?, ?, ?, ?, ?)";
				ps = con.prepareStatement(query);
				System.out.println("Inserisci il codice fiscale: ");
				String cf=in.nextLine();
				ps.setString(1, cf);
				System.out.println("Inserisci il nome: ");
				ps.setString(2, in.nextLine());
				System.out.println("Inserisci il cognome: ");
				ps.setString(3, in.nextLine());
				System.out.println("Inserisci 1 se impiegato, 0 se amministratore: ");
				int amm=in.nextInt();
				ps.setInt(4, amm);
				ps.setDouble(5, 0);	
				System.out.println("Inserisci l'indirizzo: ");
				ps.setString(6, in.nextLine());
				System.out.println("inserisci telefono: ");
				ps.setString(7, in.nextLine());
				ps.executeUpdate();
				if(amm==0){//controllo sul tipo di impiegato - se amministratore aggiungere anche all'entità amministratore
					System.out.println("il membro creato è un amministratore, assegnare un codice amministratore");
					String query1="INSERT INTO amministratore (CODamministratore, CFmembro) VALUES (?,?) ";
					ps= con.prepareStatement(query1);
					ps.setInt(1, in.nextInt());
					in.nextLine();
					ps.setString(2, cf);
					ps.executeUpdate();		
				}
				while(true){
					query="INSERT INTO email (indirizzoemail, CFmembro) VALUES (?,'"+cf+"')";
					ps=con.prepareStatement(query);
					System.out.println("inserisci l'indirizzo e-mail (0 per terminare)");
					String email=in.nextLine();
					if(email.equals("0")){break;}
					ps.setString(1, email);
					ps.executeUpdate();	
				}
				//creiamo la relazione "composto_da" tra membro e team
				query= "INSERT INTO composto_da(CFmembro, nome_team) VALUES (?,?)";	
				ps=con.prepareStatement(query);
				ps.setString(1, cf);
				ps.setString(2, team);
				ps.executeUpdate();
				
				System.out.println("Vuoi inserire ancora membri al team? 1 per continuare, 0 per terminare: ");
			    i=in.nextInt();
			    in.nextLine();
			    if(i==0){break;}
			}
			//stampa risultato
			query="SELECT C.nome_team, M.nome AS nome_membro, M.cognome AS cognome_membro, impiegato=0 AS amministratore "
					+ "FROM composto_da AS C, membro AS M "
					+ "WHERE C.CFmembro = M.CFmembro AND C.nome_team=?";
			ps = con.prepareStatement(query);
			ps.setString(1, team);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}	
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}	
	}
	
	
/***Op8 Leggere quanti progetti sono associati a un committente***/
	public static void op8(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();//stampiamo la lista dei committenti presenti in database
			String query="SELECT * FROM committente";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}
			query = "SELECT COUNT(*) FROM commissionato_da where CFcommittente= ?";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci il codice fiscale del committente interessato: ");
			ps.setString(1, in.nextLine());
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
	
/***Op9 Trovare nome e cognome dei membri impiegati che hanno lavorato su almeno 2 task per più di 20 ore ognuna***/
	public static void op9(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			String query = "SELECT DISTINCT nome,cognome FROM membro AS M, lavora_su_task as L WHERE M.CFmembro=L.CFmembro AND M.impiegato ='1' "
					     + "AND  L.tempo_speso>20 AND L.CFmembro IN (SELECT CFmembro FROM lavora_su_task as L WHERE L.tempo_speso>20 "
					     + "Group By (CFmembro) HAVING COUNT(*)>1)";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
	
/***Op10 Stampare i membri di un team***/
	public static void op10(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			String query="SELECT * FROM team";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}
			query = "SELECT C.CFmembro, nome, cognome, impiegato=0 AS amministratore FROM membro AS M, composto_da AS C "
						 + "WHERE C.CFmembro = M.CFmembro AND C.nome_team=? ORDER BY M.impiegato ASC";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci il nome del team interessato: ");
			ps.setString(1, in.nextLine());
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
/***Op11 Trovare nome e cognome dei membri che hanno lavorano su 2 progetti assegnati dallo stesso committente***/
	public static void op11(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			String query="SELECT * FROM committente";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}
			   query = "SELECT DISTINCT M.nome, M.cognome FROM membro AS M, team AS T, lavora_su_progetto AS L, commissionato_da as C, "
				     + "composto_da AS CT, progetto AS P WHERE M.CFmembro = CT.CFmembro AND T.nome = CT.nome_team AND T.nome = L.nome_team "
				     + "AND L.IDprogetto = P.IDprogetto AND P.IDprogetto = C.IDprogetto AND C.CFcommittente =? "
				     + "AND M.CFmembro IN(SELECT CFmembro FROM lavora_su_task as L,Commissionato_da as C, progetto as p,task as T "
				     + "WHERE L.taskID=T.taskID AND T.IDprogetto=L.IDprogetto AND T.IDprogetto=P.IDprogetto AND C.IDprogetto=P.IDprogetto "
				     + "GROUP BY(L.CFmembro) HAVING COUNT(*)=2)";
			ps = con.prepareStatement(query);
			System.out.println("Inserisci il codice fiscale del committente interessato: ");
			ps.setString(1, in.nextLine());
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
/***Op12 Elimina un numero di telefono ad un committente***/
	public static void op12(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			//stampiamo la lista dei committenti
			String query="SELECT * FROM committente";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}//stampiamo la lista dei numeri di telefono associati al committente selezionato
			String cf;
			while(true){
				query = "SELECT telefono FROM telefono AS T WHERE T.CFcommittente = ?";
				ps = con.prepareStatement(query);
				System.out.println("Inserisci il codice fiscale del committente: ");
				cf = in.nextLine();
				ps.setString(1, cf);
				re = ps.executeQuery();
				if(re != null){
					resultTable(re);
					System.out.println();
					re.close();
				}	//contiamo quanti numeri di telefono sono memorizzati per quel committente
				query="SELECT COUNT(*) FROM telefono WHERE CFcommittente=?";
				ps = con.prepareStatement(query);
				ps.setString(1, cf);
				re = ps.executeQuery();
				re.next();
				int cont = re.getInt(1);
				if(cont==1){//controllo sul numero di telefoni memorizzati - un committente DEVE avere almeno un numero di telefono
					System.out.println("Il committente selezionato ha solo un numero di telefono, ogni committente deve avere almeno un numero di telefono!");
					System.out.println("Vuoi selezionare un altro committente o uscire?(0 se vuoi uscire, 1 per continuare)");
					int j = in.nextInt();
					in.nextLine();
					if(j==0) {
						break;
					}	
					continue;
				}
					
				
				while(cont>1){
					//effettuiamo la cancellazione del numero di telefono selezionato
					query = "DELETE FROM telefono WHERE CFcommittente= ? AND telefono= ?";
					ps = con.prepareStatement(query);
					System.out.println("Inserisci il numero di telefono che vuoi eliminare: ");
					ps.setString(1, cf);
					ps.setString(2, in.nextLine());
					ps.executeUpdate();
					System.out.println("vuoi cancellare altri numeri di questo committente?(0 per uscire, 1 per continuare): ");
					int i = in.nextInt();
					in.nextLine();
					if(i==0){ 
						
						break;
					}	
					cont--;
					if(cont==1){
						System.out.println("Il committente selezionato ha solo un numero di telefono memorizzato, non puoi cancellarne altri!");
						break;
					}
				}
				break;
			}	
			//stampa lista telefoni aggiornata
			query = "SELECT * FROM telefono";
			ps = con.prepareStatement(query);			
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
				re.close();
			}
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
	
/***Op13 Modifica un attributo di un membro***/
	public static void op13(){
		PreparedStatement ps = null;
		try{
			Database.ConnectionDB();
			//stampiamo la lista dei membri in database
			String query = "SELECT CFmembro, nome, cognome, tempo_speso_totale, indirizzo, telefono FROM membro";
			ps = con.prepareStatement(query);
			ResultSet re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}	//seleziona membro
			System.out.println("Inserisci il codice fiscale del membro che vuoi modificare: ");
			String cf = in.nextLine();
			query = "SELECT * FROM membro AS M WHERE M.CFmembro= ?";
			ps = con.prepareStatement(query);
			ps.setString(1, cf);
			re = ps.executeQuery();
			if(re != null){
				resultTable(re);
				System.out.println();
			}
						

			System.out.println("Inserisci l'attributo che vuoi modificare: ");
			String attr = in.nextLine();
			while(true){
				if(attr.equals("nome")){
					query = "UPDATE membro SET nome = ? WHERE CFmembro=?";
					System.out.println("Inserisci il nuovo nome: ");
					ps = con.prepareStatement(query);
					ps.setString(1, in.nextLine());
					ps.setString(2, cf);
					ps.executeUpdate();
					break;
				};
				if(attr.equals("cognome")) {
					query = "UPDATE membro SET cognome = ? WHERE CFmembro=?";
					System.out.println("Inserisci il nuovo cognome: ");
					ps = con.prepareStatement(query);
					ps.setString(1, in.nextLine());
					ps.setString(2, cf);
					ps.executeUpdate();
					break;
				};
				 if(attr.equals("tempo_speso_totale")) {
					 query = "SELECT tempo_speso_totale FROM membro AS M WHERE M.CFmembro = ?";
					 ps = con.prepareStatement(query);
					 ps.setString(1, cf);
					 re = ps.executeQuery();
				     re.next();
				     Double tst = re.getDouble(1);
					 re.close();
					 query = "UPDATE membro SET tempo_speso_totale = ? WHERE CFmembro= ?";
					 System.out.println("Inserisci le ore che vuoi aggiungere al tempo speso totale: ");
					 tst = tst+in.nextDouble();
					 in.nextLine();
					 ps = con.prepareStatement(query);
					 ps.setDouble(1, tst);
					 ps.setString(2, cf);
					 ps.executeUpdate();
					 break; 	  
				  };
				  	
				  if(attr.equals("indirizzo")) {
					 query = "UPDATE membro SET indirizzo = ? WHERE CFmembro=?";
					 System.out.println("Inserisci il nuovo indirizzo: ");
					 ps = con.prepareStatement(query);
					 ps.setString(1, in.nextLine());
					 ps.setString(2, cf);
					 ps.executeUpdate();
					 break;
				  };
				   
				  if(attr.equals("telefono")) {
					 query = "UPDATE membro SET telefono = ? WHERE CFmembro=?";
					 System.out.println("Inserisci il nuovo telefono: ");
					 ps = con.prepareStatement(query);
					 ps.setString(1, in.nextLine());
					 ps.setString(2, cf);
					 ps.executeUpdate();
					 break;
				  };
				  
				  System.out.println("nome attributo non corretto, riprova");
				  attr = in.nextLine();
			}
			
			//stampa risultato
			query="SELECT * FROM membro WHERE CFmembro=?";
			 ps = con.prepareStatement(query);	
			 ps.setString(1, cf);
			 re= ps.executeQuery();
			 if(re != null){
					resultTable(re);
					System.out.println();
				}
			re.close();	
		}
		catch(SQLException e){
			System.out.println(e);
		}finally{	
			Database.closeConnectionDB();
		}
	}
	
	
	
	
	public static void resultTable(ResultSet re){
		ResultSetMetaData md;
		int n=0;
		try{
			md = re.getMetaData();
			for(int i=1; i<=md.getColumnCount(); i++){
				if(n<md.getColumnLabel(i).length())
					n = md.getColumnLabel(i).length();
			}
			while(re.next()){
				for(int i=1; i<=md.getColumnCount(); i++){
					if(n<re.getString(i).length())
						n = re.getString(i).length();
				}
			}
			re.beforeFirst();
			for(int i=0; i<md.getColumnCount()*n+md.getColumnCount(); i++)
				System.out.print("-");
			System.out.println();
			
			System.out.print("|");
			for(int i=1; i<=md.getColumnCount(); i++){
				System.out.print(md.getColumnLabel(i));
				for(int j=0; j<n-md.getColumnLabel(i).length(); j++)
					System.out.print(" ");
				System.out.print("|");
			}
			System.out.println();
			
			for(int i=0; i<md.getColumnCount()*n+md.getColumnCount(); i++)
				System.out.print("-");
			System.out.println();
			
			while(re.next()){
				System.out.print("|");
				for(int i=1; i<=md.getColumnCount(); i++){
					System.out.print(re.getString(i));
					for(int j=0; j<n-re.getString(i).length(); j++)
						System.out.print(" ");
					System.out.print("|");
				}
				System.out.println();
				
				for(int i=0; i<md.getColumnCount()*n+md.getColumnCount(); i++)
					System.out.print("-");
				System.out.println();
			}
		}catch(SQLException e){
			System.out.println(e);
		}
	}
	
}


