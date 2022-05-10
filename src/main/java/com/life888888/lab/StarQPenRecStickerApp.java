package com.life888888.lab;

import static com.life888888.lab.StarQPenRecStickerAppUtils._USER_MEDIA;
import static com.life888888.lab.StarQPenRecStickerAppUtils._USER_MUSIC;

import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

//import java.awt.Checkbox;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

//import picocli.CommandLine;
//import picocli.CommandLine.IVersionProvider;

/**
 * StarQ 點讀筆 自己動手做錄放音貼紙 - 匯入檔案小幫手
 * 
 * @author life888888
 */
public class StarQPenRecStickerApp {
	static String _MSG_NOT_SELECT_StarQPenFolder = "目前未選擇 StarQ 點讀筆位置";
	static String appTitle = "StarQ 點讀筆 自己動手做錄放音貼紙 - 匯入檔案小幫手 ";
	HashMap<String, Image> imageMap = new HashMap<String, Image>();
	boolean showNoteOnce = false;
	boolean skipVerify = false;
	Integer lastSN = null;
	String mediaPath = null;
	String lastMusicSrcPath = null;
	Integer selectIndex = null;// 匯入檔案清單選擇項目
	String starQPenFolder = null;
	GridLayout gridLayout;
	GridData gridData;
	Display display;
	Shell shell;
	Label lblPenFolder; // ①
	Text txtPenFolder; // ②
	Button btnSelectPenFolder; // ③
	Label lblStickerColor; // ④
	Combo cboColor; // ⑤
	Label lblLeftSize; // ⑥
	Label lblValLeftSize; // ⑦
	Label lblStickerStartNo; // ⑧
	Text txtStickerStartNo; // ⑨
	Label lblFileCounts; // ⑩
	Label lblValFileCounts; // ⑪
	Label lblSourceFile; // ⑫
	Button btnSelectSourceFile; // ⑬
	Label lblFileSize; // ⑭
	Label lblValFileSize; // ⑮
	Button btnClearList; // ⑯
	Button btnImportFileToStarQPen; // ⑱
	Table tblImportList; // ⑲
	TableColumn tblclmnSrcFSize;
	TableColumn tblclmnSrcFName;
	TableColumn tblclmnSrcFPath;
	TableColumn tblclmnStSN;
	TableColumn tblclmnTagFName;
	TableColumn tblclmnTagFPath;
	TableColumn tblclmnSrcFType;
	TableColumn tblclmnTagFExist;
	TableColumn tblclmnFileItemBaseDir;
	TableColumn tblclmnFileItemSrcPath;
	TableColumn tblclmnFileItemStickerColor;
	TableColumn tblclmnFileItemStickerSN;

	Group grpFunctionButton;
	Button btnPlayFirst10SAudio; // ⑳
	Button btnPlayAudio; // ㉑
	Button btnMoveUp; // ㉒
	Button btnMoveDown; // ㉓
	Button btnRemove; // ㉔
	Label lblAppMessage; // ㉕
	Boolean sortOrder = false;

	// 重新計算 點讀筆位置 的剩餘空間容量
	void reCalcFreeSpace() {
		File file = new File(txtPenFolder.getText().trim());
		long freeSpace = file.getFreeSpace() / (1024 * 1024);
		lblValLeftSize.setText(freeSpace + " MB");
	}

	void reCalcStickerSN() {
		// 重新計算及設定 Table 裡面的每一條目的貼紙編號
		Integer tSN = null;
		String tColor = null;
		String tBaseDir = null;
		// 第一個號碼, 要從 txtStickerStartNo 欄位取得
		try {
			tSN = Integer.parseInt(txtStickerStartNo.getText());
		} catch (Exception e) {
			return;
		}
		for (TableItem titem : tblImportList.getItems()) {
			FileItem fItem = getTableItemData(titem);
			if (tColor == null)
				tColor = fItem.getStickerColor();
			if (tBaseDir == null)
				tBaseDir = fItem.getBaseDir();
			FileItem fItemN = new FileItem(tBaseDir, fItem.getSrcPath().getAbsolutePath(), tColor, tSN.intValue());
			titem.setText(fItemN.getStringArray());
			tSN = tSN + 1;
		}
		tblImportList.layout();
	}

	// 計算清單的檔案總數以及所有檔案大小
	void updateTableFileCountAndSize() {
		int fCounts = tblImportList.getItemCount();
		long fKBSize = 0;
		for (TableItem titem : tblImportList.getItems()) {
			FileItem fItem = getTableItemData(titem);
			fKBSize = fKBSize + fItem.getSrcFKBSize();
		}
		lblValFileCounts.setText("" + fCounts);
		lblValFileCounts.pack();
		lblValFileSize.setText("" + fKBSize + " KB");
		lblValFileSize.pack();
		shell.pack();
	}

	FileItem getTableItemData(TableItem titem) {
		FileItem f = new FileItem(titem.getText(8), titem.getText(9), titem.getText(10),
				Integer.parseInt(titem.getText(11)));
		return f;
	}

	void showMessageBox(String title, String message, Integer... styleICON) {
		int style = SWT.ICON_WARNING | SWT.OK; // Default MessageBox Icon Style
		if (styleICON != null) {
			for (Integer s1 : styleICON) {
				style = style | s1.intValue();
			}
		} // if

		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	void initDisable() {
		lblAppMessage.setText(_MSG_NOT_SELECT_StarQPenFolder);
		lblValLeftSize.setText("　　　　　　");
		lblValLeftSize.pack();
		disableImportList();
		disableStickerFunctionButton();
		disableTableItemButton();
	}

	void disableStickerFunctionButton() {
		cboColor.setEnabled(false);
		txtStickerStartNo.setEnabled(false);
		txtStickerStartNo.setText("");
		btnSelectSourceFile.setEnabled(false);
	}

	void enableStickerFunctionButton() {
		cboColor.setEnabled(true);
		txtStickerStartNo.setEnabled(true);
		txtStickerStartNo.setText("");
		btnSelectSourceFile.setEnabled(true);
	}

	void disableListFunctionButton() {
		disableTableItemButton();
		btnClearList.setEnabled(false);
		btnImportFileToStarQPen.setEnabled(false);
	}

	void enableListFunctionButtton() {
		btnClearList.setEnabled(true);
		btnImportFileToStarQPen.setEnabled(true);
	}

	void enableTableItemButton() {
		btnPlayFirst10SAudio.setEnabled(true);
		btnPlayAudio.setEnabled(true);
		btnMoveUp.setEnabled(true);
		btnMoveDown.setEnabled(true);
		btnRemove.setEnabled(true);
	}

	void disableTableItemButton() {
		btnPlayFirst10SAudio.setEnabled(false);
		btnPlayAudio.setEnabled(false);
		btnMoveUp.setEnabled(false);
		btnMoveDown.setEnabled(false);
		btnRemove.setEnabled(false);

	}

	void disableImportList() {
		initImportList();
		tblImportList.setEnabled(false);
		disableListFunctionButton();
	}

	void enableImportList() {
		initImportList();
		tblImportList.setEnabled(true);
	}

	void initImportList() {
		tblImportList.clearAll();
		lblValFileCounts.setText("　　　　　");
		lblValFileCounts.pack();
		lblValFileSize.setText("　　　　　");
		lblValFileSize.pack();
	}

	// 可以分多次選擇檔案加入清單
	void addFilesToTable(String[] selectedFiles, String path) {
		String baseDir = txtPenFolder.getText();
		String stickerColor = cboColor.getText();
		int stickerSN = 0;
		if (selectedFiles.length > 0) { // if2
			// 如果表格是空的 , 取 txtStickerStartNo 的值為起始序號
			if (tblImportList.getItemCount() == 0) {
				if (!"".equals(txtStickerStartNo.getText().trim())) { // if3
					stickerSN = Integer.parseInt(txtStickerStartNo.getText());
				} // if3
			} else {
				// 如果表格不是空的 , 取 表格中最後一列的值為起始序號
				TableItem t1 = tblImportList.getItem(tblImportList.getItemCount() - 1);
				FileItem f1 = getTableItemData(t1);
				stickerSN = f1.getStickerSN() + 1;
			}
			for (String s : selectedFiles) {
				if (stickerSN > 1000) { // if4
					lblAppMessage.setText("貼紙序號已經超過1000。無法新增檔案到清單中。請先完成　匯入檔案到點讀筆　後，點選　清除以下清單。再重新 選擇來源檔案 加入檔案到清單。");
					// txtStickerStartNo.setText("");
					showMessageBox("警告", "貼紙序號已經超過1000。無法新增檔案到清單中。請先完成　匯入檔案到點讀筆　後，點選　清除以下清單。再重新 選擇來源檔案 加入檔案到清單。",
							SWT.ICON_WARNING | SWT.OK);
					break;
				} // if4
					// System.out.println("["+s+"]");
				String srcPath = path + File.separator + s;
				FileItem r1 = new FileItem(baseDir, srcPath, stickerColor, stickerSN);
				// fList.add(r1);
				stickerSN = stickerSN + 1;
				TableItem titem = new TableItem(tblImportList, SWT.NONE);
				titem.setText(r1.getStringArray());
				tblImportList.layout();// refresh?
			} // for
			if (stickerSN <= 1000) {
				lastSN = stickerSN;
			} else {
				lastSN = null;
			}
		} // if2

		tblclmnSrcFSize.setWidth(0);
		tblclmnTagFExist.setWidth(0);
		tblclmnFileItemBaseDir.setWidth(0);
		tblclmnFileItemSrcPath.setWidth(0);
		tblclmnFileItemStickerColor.setWidth(0);
		tblclmnFileItemStickerSN.setWidth(0);
		tblclmnSrcFType.setWidth(0);
		tblclmnSrcFName.pack();
		tblclmnSrcFPath.pack();
		tblclmnStSN.pack();
		tblclmnTagFName.pack();
		tblclmnTagFPath.pack();

		enableListFunctionButtton();
		if (tblImportList.getItemCount() > 0) {
			// 將貼紙起始編號上鎖 禁止編輯修改
			txtStickerStartNo.setEditable(false);
			txtStickerStartNo.setToolTipText("清單已有檔案時,不能修改貼紙起始編號");
			// 將顏色貼紙上鎖
			cboColor.setToolTipText("清單已有檔案時,不能變更選擇貼紙顏色");
			cboColor.setEnabled(false);
			// 變更點讀筆目錄 也要上鎖
			btnSelectPenFolder.setToolTipText("清單已有檔案時,不能變更點讀筆位置");
			txtPenFolder.setToolTipText("清單已有檔案時,不能變更點讀筆位置");
			btnSelectPenFolder.setEnabled(false);
		}
		updateTableFileCountAndSize();
		tblImportList.layout();// refresh?
		shell.pack();
	}

	void setupImageMap() {
		imageMap.put("folder",
				new Image(display, StarQPenRecStickerApp.class.getResourceAsStream("/images/folder.png")));
		imageMap.put("moveUp",
				new Image(display, StarQPenRecStickerApp.class.getResourceAsStream("/images/moveUp.png")));
		imageMap.put("moveDown",
				new Image(display, StarQPenRecStickerApp.class.getResourceAsStream("/images/moveDown.png")));
		imageMap.put("remove",
				new Image(display, StarQPenRecStickerApp.class.getResourceAsStream("/images/remove.png")));
		imageMap.put("music", new Image(display, StarQPenRecStickerApp.class.getResourceAsStream("/images/music.png")));
		imageMap.put("play", new Image(display, StarQPenRecStickerApp.class.getResourceAsStream("/images/play.png")));
	}

	SelectionAdapter createBtnSelectPenFolderSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				if (txtPenFolder.getText().trim().length() == 0) {
					// 使用預設媒體個人路徑
					dialog.setFilterPath(mediaPath);
				} else {
					// 使用之前設定的目錄為起始目錄
					dialog.setFilterPath(txtPenFolder.getText().trim());
				} // if
				String selPath = dialog.open();
				if (selPath != null) {
					txtPenFolder.setText(selPath);
					reCalcFreeSpace();
					enableImportList();
					enableStickerFunctionButton();
					lblAppMessage.setText("選取點讀筆目錄為 " + selPath);
				} else {
					// DO NOTHING 沒有選擇目錄,維持原來設定
				}
			}
		};
	}

	VerifyListener crateTxtStickerStartNoVerifyListener() {
		return new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				boolean isValid = true;
				// 加入之後 用程式直接修改 .setText 會失敗沒有反應
				// 加入 skipVerify 跳過檢查
				if (skipVerify == true) {
					e.doit = isValid;
				} else {
					// 以下寫法 僅可適用於一次一個字元輸入 ,
					// .setText("456");會導致 e.Text 會抓到 "456" 而不是單一字元 ,
					// 在下面做
					// "0123456789".indexOf(e.text)>=0 會永遠為 false

					String oldS = null;
					String newS = null;
					oldS = txtStickerStartNo.getText();
					e.doit = "0123456789".indexOf(e.text) >= 0;
					if (e.doit == true)
						newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
					try {
						if (!"".equals(newS)) {
							int num = Integer.parseInt(newS);
							if (num > 1000 || num < 1)
								isValid = false;
						}
					} catch (NumberFormatException ex) {
						isValid = false;
					}
					e.doit = isValid;
				}
			}
		};
	}

	SelectionAdapter creatBtnSelectSourceFileSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lblAppMessage.setText("選擇來源檔案");
				// 檢查 貼紙顏色是否設定
				String stickerColor = cboColor.getText();
				if ("".equals(stickerColor.trim())) {
					lblAppMessage.setText("請先下拉選擇點讀筆錄放音貼紙顏色");
					showMessageBox("警告", "請先下拉選擇點讀筆錄放音貼紙顏色", SWT.ICON_WARNING | SWT.OK);
					return;
				}
				// 檢查 貼紙起始編號是否設定
				int tmpStartSN = 0;
				try {
					tmpStartSN = Integer.parseInt(txtStickerStartNo.getText().trim());
				} catch (Exception e0) {
				}
				if (tmpStartSN <= 0) {
					lblAppMessage.setText("請先輸入點讀筆錄放音貼紙起始編號(請看著你現在貼紙的編號,每個貼色顏色可用編號為 1 ~ 1000)");
					showMessageBox("警告", "請先輸入點讀筆錄放音貼紙起始編號(請看著你現在貼紙的編號,每個貼色顏色可用編號為 1 ~ 1000)",
							SWT.ICON_WARNING | SWT.OK);
					return;
				}
				FileDialog dialog = new FileDialog(shell, SWT.MULTI);// 多選檔案
				// 只提供 MP3 選擇
				dialog.setFilterExtensions(new String[] { "*.mp3" });
				dialog.setFilterNames(new String[] { "MP3 Files" });
				// ToDo : 各平台的預設音樂目錄 , 或是 上一次使用的目錄
				// dialog.setFilterPath("/home/demo/Music");
				if (lastMusicSrcPath != null) {
					dialog.setFilterPath(lastMusicSrcPath);
				} else {
					dialog.setFilterPath(_USER_MUSIC);
				}
				String firstFile = dialog.open();
				if (firstFile != null) { // if1
					String[] selectedFiles = dialog.getFileNames();
					String path = dialog.getFilterPath();
					if (path != null)
						lastMusicSrcPath = path;
					addFilesToTable(selectedFiles, path);
					lblAppMessage.setText("完成選擇檔案");
					if (showNoteOnce == false && tblImportList.getItemCount() > 0) {
						showMessageBox("資訊",
								"已加入檔案到清單,貼紙顏色及起始編號都無法修改。您可以繼續點擊　加入來源檔案　單選或多選檔案。也可以執行　匯入檔案到點讀筆。也可以點擊　清除以下清單　後，重新設定貼紙顏色及貼紙起始編號。",
								SWT.ICON_INFORMATION | SWT.OK);
						showNoteOnce = true;
					}
				} else {
					lblAppMessage.setText("未選擇任何檔案");
				}
			}
		};
	} // creatBtnSelectSourceFileSelectionListener

	SelectionAdapter createBtnClearListSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// System.out.println("清除以下清單");
				// tblImportList.clearAll();
				disableListFunctionButton();
				tblImportList.removeAll();
				skipVerify = true;
				txtStickerStartNo.setText("　　　　");
				txtStickerStartNo.pack();
				txtStickerStartNo.setText("");
				txtStickerStartNo.setEditable(true);
				txtStickerStartNo.setToolTipText("每種貼紙顏色可用編號為 1 ~ 1000");
				skipVerify = false;
				cboColor.setToolTipText("選擇 StarQ 自己動手做錄放音貼紙 的貼紙顏色");
				cboColor.setEnabled(true);
				btnSelectPenFolder.setEnabled(true);
				btnSelectPenFolder.setToolTipText("請點擊本按鈕來選擇點讀筆位置");
				txtPenFolder.setToolTipText("點讀筆位置");
				updateTableFileCountAndSize();
				lblAppMessage.setText("清單已清除，您可以重新選擇貼紙顏色及貼紙起始編號");
			}
		};
	} // createBtnClearListSelectionListener

	SelectionAdapter creatBtnImportFileToStarQPenSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// System.out.println("匯入檔案到點讀筆");
				String ErrMsg = "";
				int listCount = tblImportList.getItemCount();
				int copyCount = 0;
				for (TableItem titem : tblImportList.getItems()) {
					FileItem fitem = getTableItemData(titem);
					fitem.getTagPath().getParentFile().mkdirs();
					try {
						Files.copy(fitem.getSrcPath().toPath(), fitem.getTagPath().toPath(),
								StandardCopyOption.REPLACE_EXISTING);
						copyCount = copyCount + 1;
					} catch (IOException e1) {
						e1.printStackTrace();
						ErrMsg = ErrMsg + "\n" + e1.getMessage();
					}
				} // for tableitem
					// 比對 listCount 跟 copyCount 有沒有一致
					// 沒有一致 表示有部份檔案複製失敗
				if (listCount == copyCount) {
					showMessageBox("匯入檔案結果", "檔案已經匯入完成!", SWT.ICON_INFORMATION | SWT.OK);
					lblAppMessage.setText("匯入檔案結果: 檔案已經匯入完成! 匯入檔案總數 :" + copyCount);
				} else {
					showMessageBox("匯入檔案結果", "檔案有部份匯入失敗!" + "\n" + "錯誤訊息為" + ErrMsg, SWT.ICON_ERROR | SWT.OK);
					lblAppMessage.setText("匯入檔案結果: 檔案有部份匯入失敗! 匯入檔案總數 :" + copyCount + "\n錯誤訊息為 " + ErrMsg);
				}
				reCalcFreeSpace();// 重新計算 點讀筆位置 的剩餘空間容量
				shell.pack();
			}
		};
	} // creatBtnImportFileToStarQPenSelectionListener

	SelectionAdapter createTblImportListSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TableItem[] itemList = tblImportList.getSelection();
				int indexOf = tblImportList.indexOf(itemList[0]);
				tblImportList.setSelection(indexOf);
				tblImportList.forceFocus();
				selectIndex = Integer.valueOf(indexOf);
				enableTableItemButton();
			}
		};
	} // createTblImportListSelectionListener

	SelectionAdapter createBtnPlayFirst10SAudioSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectIndex = tblImportList.getSelectionIndex();
				if (selectIndex == null) {
					return;
				}
				TableItem titem = tblImportList.getItem(selectIndex);
				FileItem f = getTableItemData(titem);
				PlayMP3First10S player = new PlayMP3First10S(f.getSrcPath().getAbsolutePath());
				player.start();
			}
		};
	} // createBtnPlayFirst10SAudioSelectionListener

	SelectionAdapter createBtnPlayAudioSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectIndex = tblImportList.getSelectionIndex();
				if (selectIndex == null) {
					return;
				}
				TableItem titem = tblImportList.getItem(selectIndex);
				FileItem f = getTableItemData(titem);
				PlayMP3Full player = new PlayMP3Full(f.getSrcPath().getAbsolutePath());
				player.start();
			}
		};
	} // createBtnPlayAudioSelectionListener

	SelectionAdapter createBtnMoveUpSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectIndex = tblImportList.getSelectionIndex();
				if (selectIndex == null) {
					return;
				}
				// 檢查是不是第一個,是第一個 不做任何事離開
				if (selectIndex == 0) {
					return;
				} else {
					// 不是第一個
					// 取得自身 tItem(x) , 取得 fItem(x)
					TableItem titem1 = tblImportList.getItem(selectIndex);
					FileItem f1 = getTableItemData(titem1);
					// 取得往上一個 tItem(x-1) , 取得 fItem(x-1)
					TableItem titem0 = tblImportList.getItem(selectIndex - 1);
					FileItem f0 = getTableItemData(titem0);
					titem1.setText(f0.getStringArray());
					titem0.setText(f1.getStringArray());
					// >> 重新計算 貼紙編號
					reCalcStickerSN();
					tblImportList.setSelection(tblImportList.getSelectionIndex() - 1);
					selectIndex = tblImportList.getSelectionIndex();
				}
			}
		};
	} // createBtnMoveUpSelectionListener

	SelectionAdapter createBtnMoveDownSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectIndex = tblImportList.getSelectionIndex();
				if (selectIndex == null) {
					return;
				}
				// 檢查是不是最後一個,是最後一個 不做任何事離開
				if (selectIndex == tblImportList.getItemCount() - 1) {
					return;
				} else {
					// 不是最後一個
					// 取得自身 tItem(x) , 取得 fItem(x)
					TableItem titem1 = tblImportList.getItem(selectIndex);
					FileItem f1 = getTableItemData(titem1);
					TableItem titem2 = tblImportList.getItem(selectIndex + 1);
					FileItem f2 = getTableItemData(titem2);

					// 取得往上一個 tItem(x+1) , 取得 fItem(x+1)
					titem1.setText(f2.getStringArray());
					titem2.setText(f1.getStringArray());
					// >> 重新計算 貼紙編號
					reCalcStickerSN();
					tblImportList.setSelection(tblImportList.getSelectionIndex() + 1);
					selectIndex = tblImportList.getSelectionIndex();
				}

			}
		};
	} // createBtnMoveDownSelectionListener

	SelectionAdapter createBtnRemoveSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectIndex = tblImportList.getSelectionIndex();
				if (selectIndex == null) {
					return;
				}
				tblImportList.remove(selectIndex);
				if (tblImportList.getItemCount() > 0) {
					selectIndex = null;
					reCalcStickerSN();
					selectIndex = tblImportList.getSelectionIndex();
				} else {
					// table is 0 , clear
					// 與清除清單按鈕幾乎雷同
					disableListFunctionButton();
					skipVerify = true;
					txtStickerStartNo.setText("　　　　");
					txtStickerStartNo.pack();
					txtStickerStartNo.setText("");
					txtStickerStartNo.setEditable(true);
					txtStickerStartNo.setToolTipText("每種貼紙顏色可用編號為 1 ~ 1000");
					skipVerify = false;
					cboColor.setToolTipText("選擇 StarQ 自己動手做錄放音貼紙 的貼紙顏色");
					cboColor.setEnabled(true);
					btnSelectPenFolder.setEnabled(true);
					btnSelectPenFolder.setToolTipText("請點擊本按鈕來選擇點讀筆位置");
					txtPenFolder.setToolTipText("點讀筆位置");
				}
				updateTableFileCountAndSize();
			}
		};
	} // createBtnRemoveSelectionListener

	StarQPenRecStickerApp() {
		mediaPath = _USER_MEDIA;
		display = new Display();
		shell = new Shell(display);
		shell.setText(appTitle);
		setupImageMap();

		gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		shell.setLayout(gridLayout);
		lblPenFolder = new Label(shell, SWT.NULL);
		lblPenFolder.setText("StarQ 點讀筆置於");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		txtPenFolder = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtPenFolder.setLayoutData(gridData);
		txtPenFolder.setToolTipText("點讀筆位置");
		txtPenFolder.setEditable(false);
		btnSelectPenFolder = new Button(shell, SWT.PUSH);
		btnSelectPenFolder.setImage(imageMap.get("folder"));
		btnSelectPenFolder.setToolTipText("請點擊本按鈕來選擇點讀筆位置");
		btnSelectPenFolder.addSelectionListener(createBtnSelectPenFolderSelectionListener());
		lblStickerColor = new Label(shell, SWT.NULL);
		lblStickerColor.setText("貼紙顏色");
		cboColor = new Combo(shell, SWT.READ_ONLY);
		cboColor.setItems(new String[] { "綠", "黃", "橘", "藍", "桃紅" });
		cboColor.setToolTipText("選擇 StarQ 自己動手做錄放音貼紙 的貼紙顏色");

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 3;// 水平跨越兩個單元格
		lblLeftSize = new Label(shell, SWT.NULL);
		lblLeftSize.setText("剩餘容量");
		lblLeftSize.setLayoutData(gridData);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;// 水平跨越兩個單元格
		lblValLeftSize = new Label(shell, SWT.NULL);
		lblValLeftSize.setText("　　　　　　");// EX 1200 MB
		lblValLeftSize.setToolTipText("點讀筆目前剩餘容量");
		lblValLeftSize.setLayoutData(gridData);
		lblValLeftSize.pack();
		lblStickerStartNo = new Label(shell, SWT.NULL);
		lblStickerStartNo.setText("貼紙起始編號");
		txtStickerStartNo = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtStickerStartNo.setToolTipText("每種貼紙顏色可用編號為 1 ~ 1000");

		txtStickerStartNo.addVerifyListener(crateTxtStickerStartNoVerifyListener());

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 3;// 水平跨越兩個單元格
		lblFileCounts = new Label(shell, SWT.NULL);
		lblFileCounts.setText("檔案總數");
		lblFileCounts.setLayoutData(gridData);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;// 水平跨越兩個單元格
		lblValFileCounts = new Label(shell, SWT.NULL);
		lblValFileCounts.setText("");// EX 20
		lblValFileCounts.setToolTipText("目前選取檔案清單檔案總數");
		lblValFileCounts.setLayoutData(gridData);

		lblSourceFile = new Label(shell, SWT.NULL);
		lblSourceFile.setText("選擇來源檔案");
		btnSelectSourceFile = new Button(shell, SWT.PUSH);
		btnSelectSourceFile.addSelectionListener(creatBtnSelectSourceFileSelectionListener());// 選擇來源檔案
		btnSelectSourceFile.setText("選擇來源檔案");

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 3;// 水平跨越兩個單元格
		lblFileSize = new Label(shell, SWT.NULL);
		lblFileSize.setText("清單檔案大小");
		lblFileSize.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;// 水平跨越兩個單元格
		lblValFileSize = new Label(shell, SWT.NULL);
		lblValFileSize.setText(""); // EX 1200000 KB
		lblValFileSize.setToolTipText("目前選取檔案清單檔案大小");
		lblValFileSize.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalSpan = 2;// 水平跨越兩個單元格
		btnClearList = new Button(shell, SWT.PUSH);
		btnClearList.addSelectionListener(createBtnClearListSelectionListener());
		btnClearList.setText("清除以下清單");
		btnClearList.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 4;// 水平跨越兩個單元格
		btnImportFileToStarQPen = new Button(shell, SWT.PUSH);
		btnImportFileToStarQPen.setText("匯入檔案到點讀筆");
		btnImportFileToStarQPen.setLayoutData(gridData);
		btnImportFileToStarQPen.addSelectionListener(creatBtnImportFileToStarQPenSelectionListener());

		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 7);
		gridData.horizontalSpan = 5;// 水平跨越5個單元格
		tblImportList = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tblImportList.setLayoutData(gridData);
		tblImportList.setHeaderVisible(true);
		tblImportList.setLinesVisible(true);
		tblImportList.addSelectionListener(createTblImportListSelectionListener());
		tblclmnSrcFName = new TableColumn(tblImportList, SWT.LEFT);
		tblclmnSrcFName.setWidth(200);
		tblclmnSrcFName.setText("來源檔名");

		tblclmnSrcFPath = new TableColumn(tblImportList, SWT.LEFT);
		tblclmnSrcFPath.setWidth(400);
		tblclmnSrcFPath.setText("來源路徑");

		tblclmnStSN = new TableColumn(tblImportList, SWT.LEFT);
		tblclmnStSN.setWidth(100);
		tblclmnStSN.setText("貼紙編號");

		tblclmnSrcFSize = new TableColumn(tblImportList, SWT.RIGHT);
		tblclmnSrcFSize.setWidth(0);
		tblclmnSrcFSize.setText("");// 檔案大小
		tblclmnSrcFSize.setResizable(false);

		tblclmnTagFName = new TableColumn(tblImportList, SWT.LEFT);
		tblclmnTagFName.setWidth(200);
		tblclmnTagFName.setText("匯入檔名");

		tblclmnTagFPath = new TableColumn(tblImportList, SWT.LEFT);
		tblclmnTagFPath.setWidth(400);
		tblclmnTagFPath.setText("匯入路徑");

		tblclmnSrcFType = new TableColumn(tblImportList, SWT.CENTER);
		// tblclmnSrcFType.setToolTipText("檔案類型是 mp3 或 wav");
		tblclmnSrcFType.setWidth(0);
		tblclmnSrcFType.setText("");// 檔案類型
		tblclmnSrcFType.setResizable(false);

		tblclmnTagFExist = new TableColumn(tblImportList, SWT.CENTER);
		tblclmnTagFExist.setWidth(0);
		tblclmnTagFExist.setText("");// 檔案是否已存在
		tblclmnTagFExist.setResizable(false);
		tblclmnFileItemBaseDir = new TableColumn(tblImportList, SWT.CENTER);
		tblclmnFileItemBaseDir.setWidth(0);
		tblclmnFileItemBaseDir.setText("");
		tblclmnFileItemBaseDir.setResizable(false);
		tblclmnFileItemSrcPath = new TableColumn(tblImportList, SWT.CENTER);
		tblclmnFileItemSrcPath.setWidth(0);
		tblclmnFileItemSrcPath.setText("");
		tblclmnFileItemSrcPath.setResizable(false);
		tblclmnFileItemStickerColor = new TableColumn(tblImportList, SWT.CENTER);
		tblclmnFileItemStickerColor.setWidth(0);
		tblclmnFileItemStickerColor.setText("");
		tblclmnFileItemStickerColor.setResizable(false);
		tblclmnFileItemStickerSN = new TableColumn(tblImportList, SWT.CENTER);
		tblclmnFileItemStickerSN.setWidth(0);
		tblclmnFileItemStickerSN.setText("");
		tblclmnFileItemStickerSN.setResizable(false);
		// 自動調整表格欄位大小 符合資料寬度
		for (int i = 0, n = tblImportList.getColumnCount(); i < n; i++) {
			tblImportList.getColumn(i).pack();
		}

		gridData = new GridData(SWT.FILL);
		gridData.heightHint = 300;
		gridData.minimumHeight = 300;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 7;
		grpFunctionButton = new Group(shell, SWT.SHADOW_ETCHED_IN);
		grpFunctionButton.setLayoutData(gridData);

		btnPlayFirst10SAudio = new Button(grpFunctionButton, SWT.PUSH);
		btnPlayFirst10SAudio.setImage(imageMap.get("music"));
		btnPlayFirst10SAudio.setToolTipText("播放前10秒選取檔案");
		btnPlayFirst10SAudio.pack();
		btnPlayFirst10SAudio.addSelectionListener(createBtnPlayFirst10SAudioSelectionListener());

		btnPlayAudio = new Button(grpFunctionButton, SWT.PUSH);
		btnPlayAudio.setImage(imageMap.get("play"));
		btnPlayAudio.setToolTipText("播放選取檔案");
		btnPlayAudio.setLocation(0, 60);
		btnPlayAudio.pack();
		btnPlayAudio.addSelectionListener(createBtnPlayAudioSelectionListener());

		btnMoveUp = new Button(grpFunctionButton, SWT.PUSH);
		btnMoveUp.setImage(imageMap.get("moveUp"));
		btnMoveUp.setToolTipText("上移選取檔案");
		btnMoveUp.setLocation(0, 120);
		btnMoveUp.pack();
		btnMoveUp.addSelectionListener(createBtnMoveUpSelectionListener());

		btnMoveDown = new Button(grpFunctionButton, SWT.PUSH);
		btnMoveDown.setImage(imageMap.get("moveDown"));
		btnMoveDown.setToolTipText("下移選取檔案");
		btnMoveDown.setLocation(0, 180);
		btnMoveDown.pack();
		btnMoveDown.addSelectionListener(createBtnMoveDownSelectionListener());

		btnRemove = new Button(grpFunctionButton, SWT.PUSH);
		btnRemove.setImage(imageMap.get("remove"));
		btnRemove.setToolTipText("移除選取檔案");
		btnRemove.setLocation(0, 240);
		btnRemove.pack();
		btnRemove.addSelectionListener(createBtnRemoveSelectionListener());

		grpFunctionButton.pack();

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 6;

		lblAppMessage = new Label(shell, SWT.BORDER);
		lblAppMessage.setText(_MSG_NOT_SELECT_StarQPenFolder);
		lblAppMessage.setLayoutData(gridData);

		shell.pack();
		initDisable();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	} // StarQPenRecStickerApp

	static void waitAndCloseSplashScreen() {
		// 特別處理 Java Option Splash Screen 不會自動關閉問題
		try {
			Thread.sleep(1500);
			System.setProperty("java.awt.headless", "false");
			Optional.ofNullable(SplashScreen.getSplashScreen()).ifPresent(SplashScreen::close);
			System.setProperty("java.awt.headless", "true");
		} catch (InterruptedException e) {
			// DO NOTHING
		} catch (Exception e) {
			// DO NOTHING
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		waitAndCloseSplashScreen();

		StarQPenRecStickerApp app = new StarQPenRecStickerApp();

	} // main
}
