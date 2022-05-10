package com.life888888.lab;

import java.io.File;

/**
 * 
 * 錄放音貼紙 對應表 播音貼紙：X色 (綠色, 黃色, 橘色, 藍色, 桃紅色) 綠色(1-1000) = REC20493 - REC21492
 * 20493 ~ 21492 黃色(1-1000) = REC21493 - REC22492 21493 ~ 22492 橘色(1-1000) =
 * REC22493 - REC23492 22493 ~ 23492 藍色(1-1000) = REC23493 - REC24492 23493 ~
 * 24492 桃紅色(1-1000) = REC24493 - REC25492 24493 ~ 25492 FOLDER -> 20480
 * 
 * @author life888888
 */
public class StarQPenRecStickerMapper {
	final static String[] colors = { "綠", "黃", "橘", "藍", "桃紅" };
	final static int[][] colorNum = { { 20492, 20493, 21492 }, { 21492, 21493, 22492 }, { 22492, 22493, 23492 },
			{ 23492, 23493, 24492 }, { 24492, 24493, 25492 } };
	final static int baseDirNum = 20480;
	final static String pr1 = "RP";
	final static String pr2 = "978_starq_sticker";
	final static String rec = "REC";
	final static String recWav = "Rec";

	// /media/demo/9C33-9999
	// baseDir
	// RP/978_starq_sticker/REC24576/REC25088/REC25328/REC25332.mp3
	// RP/978_starq_sticker/REC24576/REC25088/REC25328/REC25332.wav
	// pr1 / pr2 / dir1 / dir2 / dir3 / tagName / . / extType

	/**
	 * 轉換錄放音貼紙顏色編號後的點讀筆對應音檔路徑
	 * 
	 * @param color   貼紙顏色
	 * @param sn      貼紙編號
	 * @param baseDir 點讀筆位置 目錄
	 * @param extType 音檔格式 ( mp3 or wav)
	 * @return 點讀筆位置的對應音檔的檔案路徑
	 */
	static String mapStickerToFPath(String color, int sn, String baseDir, String extType) {
		// DONE : color = "綠" , "黃" , "橘" , "藍" ,"桃紅"
		if ("綠黃橘藍桃紅".indexOf(color) < 0) {
			return null;
		}
		// DONE : sn >=1 , sn <= 1000
		if (sn < 1 || sn > 1000) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		Integer dir1No = null;
		Integer dir2No = null;
		Integer dir3No = null;
		Integer tagNo = null;

		Integer c1 = null;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i].equals(color)) {
				c1 = i;
				break;
			}
		} // for
		if (c1 != null) {
			int index1 = c1.intValue();
			int z1 = colorNum[index1][0];
			int min = colorNum[index1][1];
			int max = colorNum[index1][2];
			int tagN = z1 + sn;
			if (tagN >= min && tagN <= max) {
				tagNo = tagN;
				int iNo = tagNo.intValue();

				int p3 = ((iNo - baseDirNum) % 16);
				int m3 = ((iNo - p3) - baseDirNum) / 16;
				dir3No = baseDirNum + m3 * 16;

				int p2 = ((iNo - baseDirNum) % 256);
				int m2 = ((iNo - p2) - baseDirNum) / 256;
				dir2No = baseDirNum + m2 * 256;

				int p1 = ((iNo - baseDirNum) % 4096);
				int m1 = ((iNo - p1) - baseDirNum) / 4096;
				dir1No = baseDirNum + m1 * 4096;

				sb.append(baseDir);
				sb.append(File.separator);
				sb.append(pr1);
				sb.append(File.separator);
				sb.append(pr2);
				sb.append(File.separator);
				sb.append(rec);
				sb.append(dir1No);
				sb.append(File.separator);
				sb.append(rec);
				sb.append(dir2No);
				sb.append(File.separator);
				sb.append(rec);
				sb.append(dir3No);
				sb.append(File.separator);
				if (extType.equals("mp3")) {
					sb.append(rec);
				} else if (extType.equals("wav")) {
					sb.append(recWav);
				}
				sb.append(tagNo);
				sb.append(".");
				sb.append(extType);
			} // if
		}
		return sb.toString();
	}
}
