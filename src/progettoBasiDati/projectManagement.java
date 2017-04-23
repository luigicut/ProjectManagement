/*package progettoBasiDati;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class projectManagement  {
	
	
	
	
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		JFrame frame = new JFrame();
		frame.setSize(670,500);
		frame.setTitle("Piattaforma di Project Management");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pan = new JPanel();
		int i= 0;
		frame.setLayout(new BorderLayout());
		JLabel txt = new JLabel();
		frame.add(txt, BorderLayout.NORTH);
		pan.setLayout(new GridLayout(14,1));
		txt = new JLabel(" (1) Calcolare il tempo speso totale da un membro");
		pan.add(txt);
		txt = new JLabel(" (2) Crea una task in progresso");
		pan.add(txt);
		txt = new JLabel(" (3) Stampa le task completate");
		pan.add(txt);
		txt = new JLabel(" (4) Aggiungi un nuovo membro");
		pan.add(txt);
		txt = new JLabel(" (5) Leggi il numero di task presenti in una bacheca esterna");
		pan.add(txt);
		txt = new JLabel(" (6) Crea un progetto");
		pan.add(txt);
		txt = new JLabel(" (7) Crea un team");
		pan.add(txt);
		txt = new JLabel(" (8) Leggere quanti progetti sono associati a un committente");
		pan.add(txt);
		txt = new JLabel(" (9) 	Trovare nome e cognome dei membri impiegati che hanno lavorato su almeno 2 task per più di 20 ore ognuna");
		pan.add(txt);
		txt = new JLabel(" (10) Stampare i membri di un team");
		pan.add(txt);
		txt = new JLabel(" (11) trovare nome e cognome dei membri che hanno lavorano su 2 progetti assegnati dallo stesso committente");
		pan.add(txt);
		txt = new JLabel(" (12) elimina un numero di telefono ad un committente");
		pan.add(txt);
		txt = new JLabel(" (13) modifica un attributo di un membro");
		pan.add(txt);
	
	
		
		
		pan.setBackground(new Color(224, 255, 255));
		frame.getContentPane().add(pan, BorderLayout.CENTER);
		
				
		frame.setVisible(true);
		
		
				System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):");
				i = in.nextInt();
			while((i>=0 && i<14)){
				
				
				
				if(i==1)		{Database.op1();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==2)	{Database.op2();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
			    else if(i==3)   {Database.op3();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==4)   {Database.op4();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==5)   {Database.op5();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==6)   {Database.op6();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==7)   {Database.op7();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==8)   {Database.op8();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
			    else if(i==9)   {Database.op9();  System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if(i==10)  {Database.op10(); System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
			    else if(i==11)  {Database.op11(); System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
			    else if(i==12)  {Database.op12(); System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
			    else if(i==13)  {Database.op13(); System.out.println("Inserisci il Numero dell'Operazione da Eseguire (0 per terminare):"); i = in.nextInt(); }
				else if (i==0)   { System.out.println("programma terminato"); 
								   System.exit(0);
				}
				else{};
			
			}
		in.close();			
	}
		
}

*/

