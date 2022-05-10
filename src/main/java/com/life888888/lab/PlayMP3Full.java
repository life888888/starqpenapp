package com.life888888.lab;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * 播放整首 mp3
 * 
 * @author life888888
 */
public class PlayMP3Full extends Thread {
	String mp3Path = null;

	public PlayMP3Full() {
		super();
	}

	public PlayMP3Full(String mp3Path) {
		super();
		this.mp3Path = mp3Path;
	}

	public String getMp3Path() {
		return mp3Path;
	}

	public void setMp3Path(String mp3Path) {
		this.mp3Path = mp3Path;
	}

	public void run() {
		try {
			FileInputStream fis = new FileInputStream(mp3Path);
			Player playMP3 = new Player(fis);
			playMP3.play();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}

}
