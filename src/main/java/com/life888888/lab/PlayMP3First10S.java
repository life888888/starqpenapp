package com.life888888.lab;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 * 播放前10秒MP3
 * 
 * @author life888888
 */
public class PlayMP3First10S extends Thread {
	String mp3Path = null;

	public PlayMP3First10S() {
		super();
	}

	public PlayMP3First10S(String mp3Path) {
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
			AdvancedPlayer player = new AdvancedPlayer(fis);
			// 播放前10秒MP3
			player.play(0, 350);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
}
