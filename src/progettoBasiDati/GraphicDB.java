package progettoBasiDati;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GraphicDB {
	
	

	public static void main(String[] args){
		JButton op1 = new JButton("Op1");
		JButton op2 = new JButton("Op2");
		JButton op3 = new JButton("Op3");
		JButton op4 = new JButton("Op4");
		JButton op5 = new JButton("Op5");
		JButton op6 = new JButton("Op6");
		JButton op7 = new JButton("Op7");
		JButton op8 = new JButton("Op8");
		JButton op9 = new JButton("Op9");
		JButton op10 = new JButton("Op10");
		JButton op11 = new JButton("Op11");
		JButton op12 = new JButton("Op12");
		JButton op13 = new JButton("Op13");
		JButton exit = new JButton("exit");
		
		
		class myListener implements ActionListener{

			
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==op1)
					Database.op1();
				if(e.getSource()==op2)
					Database.op2(); 
				if(e.getSource()==op3)
					Database.op3();
				if(e.getSource()==op4)
					Database.op4();
				if(e.getSource()==op5)
					Database.op5();
				if(e.getSource()==op6)
					Database.op6();
				if(e.getSource()==op7)
					Database.op7();
				if(e.getSource()==op8)
					Database.op8();
				if(e.getSource()==op9)
					Database.op9();
				if(e.getSource()==op10)
					Database.op10();
				if(e.getSource()==op11)
					Database.op11();
				if(e.getSource()==op12)
					Database.op12();
				if(e.getSource()==op13)
					Database.op13();
				if(e.getSource()==exit)
					System.exit(0);
				
			}
			
		}
		
		op1.addActionListener(new myListener());
		op2.addActionListener(new myListener());
		op3.addActionListener(new myListener());
		op4.addActionListener(new myListener());
		op5.addActionListener(new myListener());
		op6.addActionListener(new myListener());
		op7.addActionListener(new myListener());
		op8.addActionListener(new myListener());
		op9.addActionListener(new myListener());
		op10.addActionListener(new myListener());
		op11.addActionListener(new myListener());
		op12.addActionListener(new myListener());
		op13.addActionListener(new myListener());
		exit.addActionListener(new myListener());
		
		
	
		JFrame frame = new JFrame();
		frame.setSize(670,500);
		frame.setTitle("Piattaforma di Project Management");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pan = new JPanel();
		JPanel buttonPan = new JPanel();
		frame.setLayout(new BorderLayout());
		JLabel txt = new JLabel();
		frame.add(txt, BorderLayout.NORTH);
		buttonPan.setLayout(new GridLayout(14,1));
		pan.setLayout(new GridLayout(14,1));
		txt = new JLabel(" (1) Calcolare il tempo speso totale da un membro");
		pan.add(txt);
		buttonPan.add(op1);
		
		txt = new JLabel(" (2) Crea una task in progresso");
		pan.add(txt);
		buttonPan.add(op2);
		
		txt = new JLabel(" (3) Stampa le task completate");
		pan.add(txt);
		buttonPan.add(op3);
		
		txt = new JLabel(" (4) Aggiungi un nuovo membro");
		pan.add(txt);
		buttonPan.add(op4);
		
		txt = new JLabel(" (5) Leggi il numero di task presenti in una bacheca esterna");
		pan.add(txt);
		buttonPan.add(op5);
		
		txt = new JLabel(" (6) Crea un progetto");
		pan.add(txt);
		buttonPan.add(op6);
		
		txt = new JLabel(" (7) Crea un team");
		pan.add(txt);
		buttonPan.add(op7);
		
		txt = new JLabel(" (8) Leggere quanti progetti sono associati a un committente");
		pan.add(txt);
		buttonPan.add(op8);
		
		txt = new JLabel(" (9) 	Trovare nome e cognome dei membri impiegati che hanno lavorato su almeno 2 task per più di 20 ore ognuna");
		pan.add(txt);
		buttonPan.add(op9);
		
		txt = new JLabel(" (10) Stampare i membri di un team");
		pan.add(txt);
		buttonPan.add(op10);
		
		txt = new JLabel(" (11) Trovare nome e cognome dei membri che hanno lavorano su 2 progetti assegnati dallo stesso committente");
		pan.add(txt);
		buttonPan.add(op11);
		
		txt = new JLabel(" (12) Elimina un numero di telefono ad un committente");
		pan.add(txt);
		buttonPan.add(op12);
		
		txt = new JLabel(" (13) Modifica un attributo di un membro");
		pan.add(txt);
		buttonPan.add(op13);
		
		JPanel chiudi = new JPanel();
		frame.add(chiudi, BorderLayout.SOUTH);
		chiudi.add(exit);
		chiudi.setBackground(new Color(224, 255, 255));
		buttonPan.setBackground(new Color(224, 255, 255));
		pan.setBackground(new Color(224, 255, 255));
		frame.getContentPane().add(pan, BorderLayout.CENTER);
		frame.add(buttonPan, BorderLayout.WEST);
				
		frame.setVisible(true);
				
			
			
	}

	
	

}
