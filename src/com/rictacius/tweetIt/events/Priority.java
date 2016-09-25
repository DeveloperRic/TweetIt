package com.rictacius.tweetIt.events;

public enum Priority {
	MONITOR(0), LOW(1), NORMAL(2), HIGH(3);

	private int lvl;

	private Priority(int lvl) {
		this.lvl = lvl;
	}

	public int getLevel() {
		return lvl;
	}
}
