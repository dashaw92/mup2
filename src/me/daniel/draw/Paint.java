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
	public Color color = Color.getColorByName("blue");

	public void paint(Graphics g) {
		updateHud();
		g.drawImage(createImage(new MemoryImageSource(width, height, pixels, 0, width)), 0, 0, Main.WIDTH, Main.HEIGHT - 100, null);
		g.drawImage(createImage(new MemoryImageSource(width, 100, hudpix, 0, width)), 0, Main.HEIGHT - 100, Main.WIDTH, Main.HEIGHT - 100, null);
	}

	private void updateHud() {
		for (int i = 0; i < hudpix.length; i++) {
			if(i < width) {
				hudpix[i] = 255 << 24 | 255 << 16 | 255 << 8 | 0;
			} else {
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
		for (int i = 0; i < hudpix.length; i++) {
			hudpix[i] = color.getHex();
		}

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == 'a') { // Previous color
					color = color.prev();
				}
				if(e.getKeyChar() == 'd') { // Next color
					color = color.next();
				}
				if(e.getKeyChar() == 'c') { // Clear
					if(!e.isShiftDown()) {
						for (int i = 0; i < pixels.length; i++) {
							pixels[i] = 255 << 24 | 0;
						}
					}
				}
				if(e.getKeyChar() == 'o') { // Open color view
					java.awt.Color col = JColorChooser.showDialog(null, "Select a color", java.awt.Color.BLACK);
					if(col != null) {
						color = new Color(Color.counter, "Custom " + Color.getNumColors(), col.getRed(), col.getGreen(), col.getBlue());
						Color.addColor(color);
					}
				}
				if(e.getKeyChar() == '?') { // Show keys
					JOptionPane.showMessageDialog(null, "a/d - Previous/next color\n? - Help\ne - export image\nc - clear\no - choose a custom color", "Help", JOptionPane.INFORMATION_MESSAGE);
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
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX() / scale;
				int y = e.getY() / scale;
				if(x>=width/scale || x<0) return;
				if(y>=height/scale || y<0) return;
				try {
					if(!e.isControlDown()) {
						pixels[y * Paint.width + x] = color.getHex();
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