package me.daniel.draw;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import me.daniel.draw.objects.Color;

@SuppressWarnings("serial")
public class Paint extends Canvas implements Runnable
{
	private int[] pixels;
	private int[] hudpix;
	public static int width, height, scale;
	
	private int debugx = 10, debugy = 10, posx=0, posy=0;
	private boolean debug = false;

	public Color color = Color.getColorByName("red"), secondcolor = Color.getColorByName("white");

	public void paint(Graphics g) {
		updateHud();
		g.drawImage(createImage(new MemoryImageSource(width, height, pixels, 0, width)), 0, 0, Main.WIDTH, Main.HEIGHT - 100, null);
		g.drawImage(createImage(new MemoryImageSource(width, 100, hudpix, 0, width)), 0, Main.HEIGHT - 100, Main.WIDTH, Main.HEIGHT - 100, null);
		if(debug) {
			String debugstrn = "(" + posx + "," + posy + ")"; 
			g.setColor(java.awt.Color.white);
			g.drawString("PCol: " + color.name, 5, 10);
			g.drawString("SCol: " + secondcolor.name, 5, 20);
			g.drawString("Last pos: (" + debugx + "," + debugy + ")", 5, 30);
			g.drawString(debugstrn, posx, posy);
			g.drawLine(posx, 0, posx, Main.HEIGHT-100);
			g.drawLine(0, posy, Main.WIDTH, posy);
			
		}
	}

	private void updateHud() {
		for (int i = 0; i < hudpix.length; i++) {
			if(i < width) {
				hudpix[i] = 255 << 24 | 255 << 16 | 255 << 8 | 0;
			} else {
				int temp = (i % width) + i / width;
				int offset = 5;
				if(temp >= width / 2 + offset && temp <= width / 2 + (offset + 1))
					hudpix[i] = 255 << 24 | 0;
				else if(temp > width / 2 + (offset + 1))
					hudpix[i] = secondcolor.getHex();
				else
					hudpix[i] = color.getHex();
			}
		}
	}

	public Paint(int width, int height, int scale)
	{
		Paint.width = width / scale;
		Paint.height = (height - 100) / scale;
		pixels = new int[Paint.width * Paint.height];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 255 << 24 | 0;
		}
		hudpix = new int[Paint.width * 100];

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == 's') {
					Color temp = secondcolor;
					secondcolor = color;
					color = temp;
				}
				if(e.getKeyChar() == 'a') { // Previous color
					color = color.prev();
				}
				if(e.getKeyChar() == 'A') { // Previous second color
					secondcolor = secondcolor.prev();
				}
				if(e.getKeyChar() == 'd') { // Next color
					color = color.next();
				}
				if(e.getKeyChar() == 'D') { // Next second color
					secondcolor = secondcolor.next();
				}
				if(e.getKeyChar() == 'G') {
					debug = !debug;
				}
				if(e.getKeyChar() == 'c') { // Clear
					if(!e.isShiftDown()) {
						for (int i = 0; i < pixels.length; i++) {
							pixels[i] = 255 << 24 | 0;
						}
					}
				}
				if(e.getKeyChar() == 'o' || e.getKeyChar() == 'O') { // Open
																		// color
																		// view
					java.awt.Color col = JColorChooser.showDialog(null, "Select a color", java.awt.Color.BLACK);
					if(col != null) {
						String name = "Custom " + Color.counter;
						Color.addColor(new Color(Color.counter, name, col.getRed(), col.getGreen(), col.getBlue()));
						if(e.getKeyChar() == 'o')
							color = Color.getColorByName(name);
						else
							secondcolor = Color.getColorByName(name);
					}
				}
				if(e.getKeyChar() == '?') { // Show keys
					JOptionPane.showMessageDialog(null, "Note: Shift+Any listed key might behave on the second color.\na/d - Previous/next color\n? - Help\ne - export image\nc - clear\no - choose a custom color\ns - swap primary and secondary colors", "Help", JOptionPane.INFORMATION_MESSAGE);
				}
				if(e.getKeyChar() == 'e') { // Export image
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Select the file to save to");
					if(jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						File saveTo = jfc.getSelectedFile();
						BufferedImage bif = new BufferedImage(Paint.width, Paint.height, BufferedImage.TYPE_INT_RGB);
						Image img = createImage(new MemoryImageSource(Paint.width, Paint.height, pixels, 0, Paint.width));
						Graphics g = bif.getGraphics();
						g.drawImage(img, 0, 0, null);
						try {
							ImageIO.write((RenderedImage) bif, "png", saveTo);
						} catch (IOException ex) {
							System.err.println("Could not save to file.");
						}
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				posx = e.getX();
				posy = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX() / scale;
				int y = e.getY() / scale;
				
				debugx = x * scale;
				debugy = y * scale;
				
				posx = e.getX();
				posy = e.getY();
				
				if(x >= width / scale || x < 0) return;
				if(y >= height / scale || y < 0) return;
				try {
					Color touse = e.isShiftDown()? secondcolor : color;
					if(!e.isControlDown()) {
						pixels[y * Paint.width + x] = touse.getHex();
					} else {
						pixels[y * Paint.width + x] = 255 << 24 | (255 - color.red) << 16 | (255 - color.green) << 8 | (255 - color.blue);
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
				}
			}
		});

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() / scale;
				int y = e.getY() / scale;
				
				debugx = x * scale;
				debugy = y * scale;
				
				if(!e.isControlDown()) {
					pixels[y * Paint.width + x] = color.getHex();
				} else {
					pixels[y * Paint.width + x] = 255 << 24 | (255 - color.red) << 16 | (255 - color.green) << 8 | (255 - color.blue);
				}
			}
		});

		new Thread(this).start();
	}

	public void run() {
		while (true) {
			repaint();
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
			}
		}
	}

	public void update(Graphics g) {
		Graphics offgc;
		Image offscreen = null;
		offscreen = createImage(getWidth(), getHeight());
		offgc = offscreen.getGraphics();
		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, getWidth(), getHeight());
		offgc.setColor(getForeground());
		paint(offgc);
		g.drawImage(offscreen, 0, 0, this);
	}
}