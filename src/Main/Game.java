package Main;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Game {
	
	public Game() {
		
		ImageIcon ii = new ImageIcon("Resources/Player40x40.png");
		Image image = ii.getImage();
		
		JFrame frame = new JFrame("Tales of Awesomeness");
		frame.setContentPane(new GamePanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(400, 200);							// under construction
		frame.setIconImage(image);
		frame.pack();
		frame.setBackground(Color.white);
		frame.setVisible(true);
		
	}
	
	
	
	public static void main(String[] args) {
		new Game();
	}

}
