package com.life888888.lab;

import java.io.File;

/**
 * 檔案項目 用對對應 原始來源的檔案 配對 貼紙顏色 貼紙編號 轉換到 點讀筆目錄 對應的檔案路徑名稱
 * 
 * @author life888888
 */
public class FileItem implements Comparable<FileItem> {
	private String baseDir; // 匯入的根基路徑
	private File srcPath; // 來源檔案路徑 OK
	private File tagPath; // 匯入檔案路徑
	private Boolean isTagPathExist; // 匯入檔案是否存在
	private String srcFName; // 來源檔案名稱 OK
	private String srcFExtType; // 來源檔案類型 OK
	private String tagFName; // 匯入檔案名稱
	private Long srcFSize; // 來源檔案大小 OK
	private String stickerColor; // 貼紙顏色 OK
	private int stickerSN; // 貼紙編號 OK

	public FileItem(String baseDir, String srcPath, String stickerColor, int stickerSN) {
		super();
		this.baseDir = baseDir;
		this.stickerColor = stickerColor;
		this.stickerSN = stickerSN;
		this.srcPath = new File(srcPath);
		this.srcFName = this.srcPath.getName();
		this.srcFExtType = srcFName.substring(srcFName.lastIndexOf('.') + 1);
		this.srcFSize = this.srcPath.length();

		String tPath = StarQPenRecStickerMapper.mapStickerToFPath(this.stickerColor, this.stickerSN, this.baseDir,
				this.srcFExtType);
		this.tagPath = new File(tPath);
		this.tagFName = this.tagPath.getName();
		this.isTagPathExist = this.tagPath.exists();
	}

	public String[] getStringArray() {
		return new String[] { this.getSrcFName(), this.getSrcPath().getAbsolutePath(), this.getStickerColorNo(),
				this.getSrcFKBSize() + " KB", this.getTagFName(), this.getTagPath().getAbsolutePath(),
				this.getFExtType(), String.valueOf(this.getIsTagPathExist()), this.baseDir,
				this.srcPath.getAbsolutePath(), this.stickerColor, "" + this.stickerSN };
	}

	public String getStringOfFileItem() {
		return ("baseDir=" + baseDir + ", srcPath=" + srcPath + ", stickerColor=" + stickerColor + ", stickerSN="
				+ stickerSN);
	}

	public File getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(File srcPath) {
		this.srcPath = srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = new File(srcPath);
		this.srcFName = this.srcPath.getName();
		String ext = null;
		int pos = srcFName.lastIndexOf(".");
		if (pos >= 0)
			ext = srcFName.substring(pos + 1).toLowerCase();
		this.srcFExtType = ext;
		this.srcFSize = this.srcPath.length();
	}

	public File getTagPath() {
		return tagPath;
	}

	public void setTagPath(File tagPath) {
		this.tagPath = tagPath;
	}

	public void setTagPath(String tagPath) {
		this.tagPath = new File(tagPath);
		this.tagFName = this.tagPath.getName();
	}

	public Boolean getIsTagPathExist() {
		return isTagPathExist;
	}

	public int getStickerSN() {
		return stickerSN;
	}

	public void setStickerSN(int stickerSN) {
		this.stickerSN = stickerSN;
		String tPath = StarQPenRecStickerMapper.mapStickerToFPath(this.stickerColor, this.stickerSN, baseDir,
				this.srcFExtType);
		this.tagPath = new File(tPath);
		this.tagFName = this.tagPath.getName();
		this.isTagPathExist = this.tagPath.exists();

	}

	public String getSrcFName() {
		return srcFName;
	}

	public String getTagFName() {
		return tagFName;
	}

	public String getStickerColor() {
		return stickerColor;
	}

	public void setStickerColorAndSN(String stickerColor, int stickerSN) {
		this.stickerColor = stickerColor;
		this.stickerSN = stickerSN;

		String tPath = StarQPenRecStickerMapper.mapStickerToFPath(this.stickerColor, this.stickerSN, baseDir,
				this.srcFExtType);
		this.tagPath = new File(tPath);
		this.tagFName = this.tagPath.getName();
		this.isTagPathExist = this.tagPath.exists();

	}

	public void setStickerColor(String stickerColor) {
		this.stickerColor = stickerColor;
		String tPath = StarQPenRecStickerMapper.mapStickerToFPath(this.stickerColor, this.stickerSN, baseDir,
				this.srcFExtType);
		this.tagPath = new File(tPath);
		this.tagFName = this.tagPath.getName();
		this.isTagPathExist = this.tagPath.exists();
	}

	public String getStickerColorNo() {
		return stickerColor + " - " + this.stickerSN;
	}

	public Long getSrcFSize() {
		return srcFSize;
	}

	public Long getSrcFKBSize() {
		return srcFSize / 1024;
	}

	public String getFExtType() {
		return this.srcFExtType;
	}

	@Override
	public int compareTo(FileItem arg0) {
		FileItem item = arg0;
		int srcFNameComp = this.srcFName.compareTo(item.getSrcFName());
		return srcFNameComp;
	}

	public String getBaseDir() {
		return baseDir;
	}

} // class
