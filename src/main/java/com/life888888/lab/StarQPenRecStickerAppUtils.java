package com.life888888.lab;

/**
 * 點讀筆 自己動手做 錄放音貼紙 輔助類別
 * 
 * @author life888888
 *
 */
public class StarQPenRecStickerAppUtils {
	/*
	 * user.name IEUser demo user.home C:\Users\IEUser /home/demo user.dir
	 * C:\Users\IEUser\Desktop /home/demo/Desktop java.io.tmpdir
	 * C:\Users\IEUser\AppData\Local\Temp\ /tmp line.separator file.separator \ /
	 * path.separator ; :
	 * 
	 * os.name Windows 10 Linux sun.arch.data.model 64 64 os.arch amd64 amd64
	 * 
	 * user.language zh zh user.country TW TW file.encoding Cp1252 UTF-8
	 * sun.jnu.encoding Cp1252 UTF-8
	 * 
	 */
	public final static String _APP_NAME = ".starq-pen-app";
	public final static String _OS_NAME = System.getProperty("os.name"); // Windows 10 Linux
	public final static String _USER_NAME = System.getProperty("user.name"); // IEUser demo
	public final static String _USER_HOME = System.getProperty("user.home"); // C:\Users\IEUser /home/demo
	public final static String _USER_DIR = System.getProperty("user.dir"); // C:\Users\IEUser\Desktop /home/demo/Desktop
	public final static String _TMP_DIR = System.getProperty("java.io.tmpdir"); // C:\Users\IEUser\AppData\Local\Temp\
																				// /tmp
	public final static String _NEW_LINE = System.getProperty("line.separator");
	public final static String _FILE_PATH_CHAR = System.getProperty("file.separator"); // \ /
	public final static String _FILE_ENCODING = System.getProperty("file.encoding"); // Cp1252 UTF-8
	public final static String _JNU_ENCODING = System.getProperty("sun.jnu.encoding"); // Cp1252 UTF-8

	/**
	 * 使用者音樂目錄 Windows C:\Users\IEUser\Music Linux /home/demo/Music
	 */
	public final static String _USER_MUSIC = _USER_HOME + _FILE_PATH_CHAR + "Music";
	/**
	 * 使用者外部媒體目錄 (For Linux , MacOS) TODO Windows 應該是要列出 root 路徑 (之下才是 C: D: Z:)
	 */
	public final static String _USER_MEDIA = "/media" + _FILE_PATH_CHAR + _USER_NAME; // /media/demo

}
