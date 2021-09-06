package flappy;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Game extends JFrame {
	
	
	JPanelWithBackground background;
	public Game() {
		background = new JPanelWithBackground("flappy (1).png", "sunnyDay.jpg", "pillarDown.png", "pillarUp.png");
		
		setTitle("Flappy bird");
		setBounds(100,0,400,400);
		this.getContentPane().add(background);
		setResizable(false);
		
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	

 public static void main(String[] args) {
		new Game();
	}
}
