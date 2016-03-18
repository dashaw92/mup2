package me.daniel.draw.objects;

import java.util.ArrayList;
import java.util.List;

public class Color
{
	private static List<Color> colors = new ArrayList<>();
	public static int counter;

	public static void addColor(Color c) {
		colors.add(c);
		counter++;
	}
	
	public static int getNumColors() {
		return colors.size()-1;
	}
	
	public static void init() {
		addColor(new Color(0, "RED", 255, 0, 0));
		addColor(new Color(1, "GREEN", 0, 255, 0));
		addColor(new Color(2, "BLUE", 0, 0, 255));
		addColor(new Color(3, "BLACK", 0, 0, 0));
		addColor(new Color(4, "WHITE", 255, 255, 255));
	}

	public int id, red, green, blue;
	public String name;

	public Color(int id, String name, int red, int green, int blue)
	{
		this.id = id;
		this.name = name;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static Color getColorById(int id) {
		for (Color c : colors) {
			if(c.id == id) return c;
		}
		return null;
	}
	
	public static Color getColorByName(String name) {
		for(Color c: colors) {
			if(name.equalsIgnoreCase(c.name)) {
				return c;
			}
		}
		return null;
	}

	public int getHex() {
		return 255 << 24 | red << 16 | green << 8 | blue;
	}

	public Color next() {
		return getColorById(id + 1) == null ? colors.get(0) : getColorById(id + 1);
	}

	public Color prev() {
		return getColorById(id - 1) == null ? colors.get(colors.size() - 1) : getColorById(id - 1);
	}
}
