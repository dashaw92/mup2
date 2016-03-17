package me.daniel.draw;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main
{
	public static final int WIDTH = 800, HEIGHT = 600;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
		JFrame frame = new JFrame("Multi-user Paint | ? for help");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			frame.setIconImage(ImageIO.read(getClass().getResource("icon.png")));
		} catch (IOException e) {}
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(new Paint(WIDTH, HEIGHT, 6));
		frame.setVisible(true);
	}
}