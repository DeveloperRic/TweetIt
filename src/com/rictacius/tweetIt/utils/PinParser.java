package com.rictacius.tweetIt.utils;

import java.util.ArrayList;

import com.rictacius.tweetIt.Main;

public class PinParser {
	private static ArrayList<Character> keys = new ArrayList<Character>();
	private static Main plugin = Main.pl;

	public static void load() {
		keys.add('A');
		keys.add('B');
		keys.add('C');
		keys.add('D');
		keys.add('E');
		keys.add('F');
		keys.add('G');
		keys.add('H');
		keys.add('I');
		keys.add('J');
		keys.add('K');
		keys.add('L');
		keys.add('M');
		keys.add('N');
		keys.add('O');
		keys.add('P');
		keys.add('Q');
		keys.add('R');
		keys.add('S');
		keys.add('T');
		keys.add('U');
		keys.add('V');
		keys.add('W');
		keys.add('X');
		keys.add('Y');
		keys.add('Z');
		if (Boolean.parseBoolean(plugin.getConfig().getString("auth.lowercase-keys"))) {
			keys.add('a');
			keys.add('b');
			keys.add('c');
			keys.add('d');
			keys.add('e');
			keys.add('f');
			keys.add('g');
			keys.add('h');
			keys.add('i');
			keys.add('j');
			keys.add('k');
			keys.add('l');
			keys.add('m');
			keys.add('n');
			keys.add('o');
			keys.add('p');
			keys.add('q');
			keys.add('r');
			keys.add('s');
			keys.add('t');
			keys.add('u');
			keys.add('v');
			keys.add('w');
			keys.add('x');
			keys.add('y');
			keys.add('z');
		}
		if (Boolean.parseBoolean(plugin.getConfig().getString("auth.number-keys"))) {
			keys.add('0');
			keys.add('1');
			keys.add('2');
			keys.add('3');
			keys.add('4');
			keys.add('5');
			keys.add('6');
			keys.add('7');
			keys.add('8');
			keys.add('9');
		}
		if (Boolean.parseBoolean(plugin.getConfig().getString("auth.symbol-keys"))) {
			keys.add('!');
			keys.add('£');
			keys.add('%');
			keys.add('^');
			keys.add('&');
			keys.add('*');
			keys.add('(');
			keys.add(')');
			keys.add('-');
			keys.add('_');
			keys.add('=');
			keys.add('+');
			keys.add('~');
			keys.add('#');
			keys.add('@');
			keys.add('?');
			keys.add('/');
			keys.add('\\');
			keys.add('.');
			keys.add(',');
			keys.add('>');
			keys.add('<');
		}
	}

	/**
	 * @return the keys
	 */
	public static ArrayList<Character> getKeys() {
		return keys;
	}

	/**
	 * <strong>IT IS HIGHLY DISCOURAGED TO MODIFY THE CHARACTER KEYS REFRENCE IF
	 * YOUR SERVER HAS MORE THAN ONE PLUGIN DPENDING ON TweetIT! IF YOU WANT TO
	 * ADD MORE CHARACTER KEYS, SIMPLY ADD THEM DO NOT REARRAGE THIS MAP AS IT
	 * WILL AFFECT OTHER PLUGINS DEPENDING ON TweetIt.</strong>
	 * 
	 * @param keys
	 *            the keys to set
	 */
	public static void setKeys(ArrayList<Character> keys) {
		PinParser.keys = keys;
	}

}
