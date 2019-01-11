package com.sec.airgraph.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * KerasIDE関連Utility
 * 
 * @author Ryuiciro Kodama
 *
 */
public class KerasEditorUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(KerasEditorUtil.class);

	/**
	 * 学習を実行する
	 * 
	 * @param modelName
	 * @param modelDirPath
	 */
	public static void learn(String modelName, String modelDirPath) {
		// TensorBoardが参照しに行くディレクトリのパス
		String tensorBoardDirPath = PropUtil.getValue("keras_ide_util.tensorboard.directory.path");
		String modelJsonFilePath = modelDirPath + "/" + modelName + ".json";
		String dataMakerPath = modelDirPath + "/data_maker.py";
		String resultPath = modelDirPath + "/" + modelName + ".hdf5";
		String datasetDirPath = modelDirPath + "/dataset";
		// Tensorboard用のディレクトリがない場合は作成
		File tensorBoardDir = new File(tensorBoardDirPath);
		tensorBoardDir.mkdirs();
		
		// ログファイル
		String logFilePath = PropUtil.getValue("workspace.local.keras_ide_util.logfile.path");
		File logFile = new File(logFilePath);
		
		// data_makerがない場合は実行せずメッセージだけ表示
		File dataMakerFile = new File(dataMakerPath);
		if(dataMakerFile.exists() == false) {
			ProcessUtil.startProcessNoReturnWithWorkingDerectoryAndLog(
					modelDirPath, 
					logFile,
					"echo",
					"You must create a data_maker.py for this model.");
			return;
		}

		// keras.fitを実行する
		ProcessUtil.startProcessNoReturnWithWorkingDerectoryAndLog(
				modelDirPath, 
				logFile,
				"python",
				dataMakerPath,
				modelJsonFilePath,
				resultPath,
				tensorBoardDirPath,
				datasetDirPath);
	}

	/**
	 * アップロードしたdata_makerファイルを保存する
	 * @param wprkspaceModelName
	 * @param dataMakerFile
	 */
	public static void saveDataMakerFile(String workspaceModelName, MultipartFile dataMakerFile) {
		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.keras.directory.path");
		// モデルディレクトリパスの取得
		String dirPath = workspaceDirPath + workspaceModelName;

		File dataDir = FileUtil.concatenateFilePath(dirPath, "data");
		dataDir.mkdirs();

		// 指定されたフォルダからdata_makerのファイルを探す
		File orginalFile = new File(dataMakerFile.getOriginalFilename());
		File savedFile = FileUtil.concatenateFilePath(dataDir.getPath(), orginalFile.getName());
		
		FileUtil.saveUploadFile(dataMakerFile, savedFile);

		// Zipファイル展開
		FileUtil.unzipFile(savedFile.getPath(), dataDir.getPath());
		// 展開後Zipファイルは削除
		savedFile.delete();
		logger.info("DataMaker Zip File is unzipped and the original is deleted.(" + savedFile.getName() + ")");
	}

}
