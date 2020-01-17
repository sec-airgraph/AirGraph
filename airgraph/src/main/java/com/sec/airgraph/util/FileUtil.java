package com.sec.airgraph.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * ファイル制御Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class FileUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * ファイルが存在するかを判定する
	 * 
	 * @param file
	 * @return
	 */
	public static boolean exists(File file) {
		if (file != null && file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * ファイルが存在するかを判定する
	 * 
	 * @param path
	 * @return
	 */
	public static boolean exists(String path) {
		File file = new File(path);
		return exists(file);
	}

	/**
	 * ファイルが存在しないかを判定する
	 * 
	 * @param file
	 * @return
	 */
	public static boolean notExists(File file) {
		if (file != null && !file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * ファイルが存在しないかを判定する
	 * 
	 * @param file
	 * @return
	 */
	public static boolean notExists(String path) {
		File file = new File(path);
		return notExists(file);
	}

	/**
	 * 文字列を連結しファイルクラスを生成する
	 * 
	 * @param strings
	 * @return
	 */
	public static File concatenateFilePath(String... strings) {
		String filePath = concatenateFilePathStr(strings);
		if (StringUtil.isNotEmpty(filePath)) {
			return new File(filePath);
		}
		return null;
	}

	/**
	 * 文字列を連結しファイルパスを生成する
	 * 
	 * @param strings
	 * @return
	 */
	public static String concatenateFilePathStr(String... strings) {
		String filePath = StringUtil.concatenate(File.separator, strings);
		if (StringUtil.isNotEmpty(filePath)) {
			return filePath;
		}
		return null;
	}

	/**
	 * ディレクトリを再帰的にコピーする
	 * 
	 * @param dirFrom
	 * @param dirTo
	 * @return
	 */
	public static Boolean directoryCopy(File dirFrom, File dirTo) {
		logger.debug("directoryCopy. dirFrom[" + dirFrom.getPath() + "]dirTo[" + dirTo.getPath() + "]");
		return directoryCopy(dirFrom, dirTo, dirFrom.getName());
	}

	/**
	 * コピー先のディレクトリ名を指定して、ディレクトリを再帰的にコピーする
	 * 
	 * @param dirFrom
	 * @param dirTo
	 * @param dirName
	 * @return
	 */
	public static Boolean directoryCopy(File dirFrom, File dirTo, String dirName) {
		logger.debug("directoryCopy. dirFrom[" + dirFrom.getPath() + "]dirTo[" + dirTo.getPath() + "]dirName[" + dirName + "]");
		File[] fromFile = dirFrom.listFiles();
		dirTo = new File(dirTo.getPath() + File.separator + dirName);

		dirTo.mkdir();

		if (fromFile != null) {

			for (File f : fromFile) {
				if (f.isFile()) {
					File copyFile = concatenateFilePath(dirTo.getPath(), f.getName());
					if (!fileCopy(f, copyFile)) {
						return false;
					}
				} else {
					if (!directoryCopy(f, dirTo)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * ディレクトリを作成する
	 * 
	 * @param dir
	 * @return
	 */
	public static Boolean createDirectory(File dir) {
		logger.debug("createDirectory. dir[" + dir.getPath() + "]");
		if (notExists(dir)) {
			return dir.mkdir();
		}
		return true;
	}

	/**
	 * ディレクトリを作成する
	 * 
	 * @param path
	 * @return
	 */
	public static Boolean createDirectory(String path) {
		File dir = new File(path);
		return createDirectory(dir);
	}

	/**
	 * ファイルをコピーする
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public static Boolean fileCopy(File srcFile, File destFile) {
		logger.debug("fileCopy. file[" + srcFile.getPath() + "]dir[" + destFile.getPath() + "]");
		FileInputStream is = null;
		FileOutputStream os = null;
		FileChannel channelFrom = null;
		FileChannel channelTo = null;

		try {
			destFile.createNewFile();
			is = new FileInputStream(srcFile);
			os = new FileOutputStream(destFile);
			channelFrom = is.getChannel();
			channelTo = os.getChannel();

			channelFrom.transferTo(0, channelFrom.size(), channelTo);

			return true;
		} catch (IOException e) {
			logger.error("例外発生:", e);
			return false;
		} finally {
			try {
				if (channelFrom != null) {
					channelFrom.close();
				}
				if (channelTo != null) {
					channelTo.close();
				}
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				// 更新日付もコピー
				destFile.setLastModified(srcFile.lastModified());
			} catch (IOException e) {
				logger.error("例外発生:", e);
				return false;
			}
		}
	}

	/**
	 * Uploadしたファイルを保存する
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public static void saveUploadFile(MultipartFile srcFile, File destFile) {
		logger.debug("File Upload. src[" + srcFile.getName() + "]dest[" + destFile.getPath() + "]");
		try {
			BufferedInputStream in = new BufferedInputStream(srcFile.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile.getPath()));
			FileCopyUtils.copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException("Error uploading file.", e);
		}
	}

	/**
	 * InputStreamを保存する
	 * 
	 * @param inputStream
	 * @param destFile
	 * @return
	 */
	public static void saveInputStream(InputStream inputStream, File destFile) {
		logger.debug("File Upload. dest[" + destFile.getPath() + "]");
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile.getPath()));
			FileCopyUtils.copy(inputStream, out);
		} catch (IOException e) {
			throw new RuntimeException("Error uploading file.", e);
		}
	}

	/**
	 * ディレクトリの中身も含めて全て削除する
	 * 
	 * @param dir
	 */
	public static void deleteDirectory(File dir) {
		if (notExists(dir)) {
			return;
		}
		if (dir.isFile()) {
			dir.delete();
		}
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDirectory(files[i]);
			}
			dir.delete();
		}
	}

	/**
	 * ファイルを削除する
	 * 
	 * @param path
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		if (notExists(file) || !file.isFile()) {
			return;
		}
		file.delete();
	}

	/**
	 * 指定されたPathのファイルをすべて読み込む
	 * 
	 * @param path
	 * @return
	 */
	public static String readAll(String path) {
		File file = new File(path);
		if (exists(file)) {
			try {
				byte[] fileContentBytes = Files.readAllBytes(Paths.get(file.getPath()));
				if (CollectionUtil.isNotEmpty(fileContentBytes)) {
					return new String(fileContentBytes, StandardCharsets.UTF_8);
				}
			} catch (Exception e) {
				logger.error("例外発生:", e);
			}
		}
		return null;
	}

	/**
	 * 指定されたPathのファイルを読み込み、指定された文字列の行をGrepする
	 * 
	 * @param path
	 * @param searchStr
	 * @return
	 */
	public static List<String> readAndSearchStr(String path, String searchStr) {
		List<String> strList = new ArrayList<String>();

		FileReader fr = null;
		BufferedReader br = null;
		try {
			// ファイルを読み込む
			fr = new FileReader(path);
			br = new BufferedReader(fr);

			// 条件にあう行を取得する
			String line;
			while ((line = br.readLine()) != null) {
				Pattern p = Pattern.compile(searchStr);
				Matcher m = p.matcher(line);

				if (m.find()) {
					strList.add(line);
				}
			}
		} catch (Exception e) {
			logger.error("例外発生:", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (Exception e) {
				logger.error("例外発生:", e);
			}
		}
		return strList;
	}

	/**
	 * 指定されたパスに指定されたデータをすべて書き込む
	 * 
	 * @param path
	 * @param data
	 */
	public static void writeAll(String path, String data) {
		File file = new File(path);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (notExists(file)) {
				// ファイル新規作成
				file.createNewFile();
			}
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(data);

		} catch (Exception e) {
			logger.error("例外発生:", e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (Exception e) {
				logger.error("例外発生:", e);
			}
		}
	}

	/**
	 * 指定されたファイルのバックアップを作成する<br>
	 * バックアップファイルはファイル名yyyymmddhhmmss
	 * 
	 * @param filePath
	 */
	public static void createBackup(String filePath) {

		// バックアップ用の文字列を生成する
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String suffix = sdf.format(c.getTime());

		// コピーする
		File fileFrom = new File(filePath);
		File fileTo = new File(filePath + suffix);
		if (fileFrom.isFile()) {
			fileCopy(fileFrom, fileTo);
		}
	}

	/**
	 * Zipファイルを解凍し、指定されたディレクトリ以下に展開する
	 * 
	 * @param filePath
	 * @param targetDirPath
	 * @return
	 */
	public static boolean unzipFile(String filePath, String targetDirPath) {
		try {
			File file = new File(filePath);
			ZipFile zip = new ZipFile(file);
			for (Enumeration<?> e = zip.getEntries(); e.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if (entry.isDirectory()) {
					new File(targetDirPath + "/" + entry.getName()).mkdirs();
				} else {
					File parent = new File(targetDirPath + "/" + entry.getName()).getParentFile();
					if (parent != null) {
						parent.mkdirs();
					}
					FileOutputStream out = new FileOutputStream(targetDirPath + "/" + entry.getName());
					InputStream in = zip.getInputStream(entry);
					byte[] buf = new byte[1024];
					int size = 0;
					while ((size = in.read(buf)) != -1) {
						out.write(buf, 0, size);
					}
					out.close();
					in.close();

				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 指定されたディレクトリ内のファイルを ZIP アーカイブし、指定されたパスに作成する。
	 *
	 * @param fullPath  圧縮後の出力ファイル名をフルパスで指定 ( 例: C:/sample.zip )
	 * @param directory 圧縮するディレクトリ ( 例; C:/sample )
	 * @return 処理結果 true:圧縮成功 false:圧縮失敗
	 */
	public static boolean compressDirectory(String filePath, String directory) {
		File baseFile = new File(filePath);
		File file = new File(directory);

		if (notExists(file)) {
			logger.warn("Not Exists directory. directory[" + directory + "]");
			return false;
		} else if (CollectionUtil.isEmpty(file.listFiles())) {
			// ディレクトリ内にデータが存在しない場合はダミーファイルを作成
			ZipOutputStream outZip = null;
			try {
				// ZIPファイル出力オブジェクト作成
				outZip = new ZipOutputStream(new FileOutputStream(baseFile));
				outZip.putNextEntry(new ZipEntry(
						file.getAbsolutePath().replace(file.getParent() + File.separator, "") + File.separator));
				outZip.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(file.getParent() + File.separator, "")
						+ File.separator + "dummy"));
				// ZIPエントリクローズ
				outZip.closeEntry();
			} catch (Exception e) {
				// ZIP圧縮失敗
				return false;
			} finally {
				// ZIPエントリクローズ
				if (outZip != null) {
					try {
						outZip.closeEntry();
					} catch (Exception e) {
					}
					try {
						outZip.flush();
					} catch (Exception e) {
					}
					try {
						outZip.close();
					} catch (Exception e) {
					}
				}
			}
		} else {
			ZipOutputStream outZip = null;
			try {
				// ZIPファイル出力オブジェクト作成
				outZip = new ZipOutputStream(new FileOutputStream(baseFile));
				archive(outZip, baseFile, file);
			} catch (Exception e) {
				// ZIP圧縮失敗
				return false;
			} finally {
				// ZIPエントリクローズ
				if (outZip != null) {
					try {
						outZip.closeEntry();
					} catch (Exception e) {
					}
					try {
						outZip.flush();
					} catch (Exception e) {
					}
					try {
						outZip.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return true;
	}

	/**
	 * ディレクトリ圧縮のための再帰処理
	 *
	 * @param outZip   ZipOutputStream
	 * @param baseFile File 保存先ファイル
	 * @param file     File 圧縮したいファイル
	 */
	private static void archive(ZipOutputStream outZip, File baseFile, File targetFile) {
		if (targetFile.isDirectory()) {
			File[] files = targetFile.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					archive(outZip, baseFile, f);
				} else {
					if (!f.getAbsoluteFile().equals(baseFile)) {
						// 圧縮処理
						archive(outZip, baseFile, f,
								f.getAbsolutePath().replace(targetFile.getParent(), "").substring(1), "UTF-8");
					}
				}
			}
		}
	}

	/**
	 * 圧縮処理
	 *
	 * @param outZip     ZipOutputStream
	 * @param baseFile   File 保存先ファイル
	 * @param targetFile File 圧縮したいファイル
	 * @parma entryName 保存ファイル名
	 * @param enc 文字コード
	 */
	private static boolean archive(ZipOutputStream outZip, File baseFile, File targetFile, String entryName,
			String enc) {
		// 圧縮レベル設定
		outZip.setLevel(5);

		// 文字コードを指定
		outZip.setEncoding(enc);
		try {

			// ZIPエントリ作成
			outZip.putNextEntry(new ZipEntry(entryName));

			// 圧縮ファイル読み込みストリーム取得
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(targetFile));

			// 圧縮ファイルをZIPファイルに出力
			int readSize = 0;
			byte buffer[] = new byte[1024]; // 読み込みバッファ
			while ((readSize = in.read(buffer, 0, buffer.length)) != -1) {
				outZip.write(buffer, 0, readSize);
			}
			// クローズ処理
			in.close();
			// ZIPエントリクローズ
			outZip.closeEntry();
		} catch (Exception e) {
			// ZIP圧縮失敗
			return false;
		}
		return true;
	}

	/**
	 * ZIPファイルの先頭ディレクトリ名を取得する
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getRootDirNameZipFile(String filePath) {
		String[] result = new String[1];
		java.util.zip.ZipFile zip = null;
		try {
			zip = new java.util.zip.ZipFile(filePath);
			zip.stream().forEach(entry -> {
				if (entry.getName().contains(File.separator)) {
					result[0] = entry.getName().split(File.separator)[0];
				}
			});
		} catch (Exception e) {
			logger.error("Failed to get zip root directory. path[" + filePath + "]");
		} finally {
			try {
				zip.close();
			} catch (Exception e) {
			}
		}
		return result[0];
	}

	/**
	 * ファイル名から拡張子を取得する
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (StringUtil.isEmpty(fileName)) {
			return null;
		}
		int point = fileName.lastIndexOf(".");
		if (point >= 0) {
			return fileName.substring(point + 1);
		}
		return fileName;
	}

	/**
	 * 対象のディレクトリにある一番更新時刻が新しい指定拡張子のファイルを取得する
	 * 
	 * @param dirPath
	 * @param extensions
	 * @return
	 */
	public static File getLatestUpdateFile(String dirPath, List<String> extensions) {
		File result = null;
		// 対象のディレクトリのファイル一覧を取得
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (CollectionUtil.isNotEmpty(files)) {
			// 更新時刻の降順でソートする
			List<File> fileList = CollectionUtil.filter(CollectionUtil.toList(files),
					file -> extensions.contains(getFileExtension(file.getName())));
			if (CollectionUtil.isNotEmpty(fileList)) {
				List<File> sortedList = CollectionUtil.reversedSort(fileList, File::lastModified);
				result = sortedList.get(0);
			}
		}
		return result;
	}

	/**
	 * サブディレクトリも含めて指定されたファイルを検索し取得する
	 * 
	 * @param dirPath
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	public static File searchFileWithSubDir(String dirPath, String fileName, String suffix) {
		File result = null;

		File dir = new File(dirPath);
		// 後方一致で"idl"
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(suffix);
		// サブディレクトリも検索する（しない場合はnull）
		IOFileFilter dirFilter = FileFilterUtils.trueFileFilter();
		// 検索開始
		Collection<File> OpenRtmList = FileUtils.listFiles(dir, fileFilter, dirFilter);
		if (CollectionUtil.isNotEmpty(OpenRtmList)) {
			for (File file : OpenRtmList) {
				if (file.getName().equals(fileName)) {
					// 一致
					result = file;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * サブディレクトリも含めて指定された拡張子に該当するファイルを検索し取得する
	 * 
	 * @param dirPath
	 * @param suffix
	 * @return
	 */
	public static List<File> searchFileListWithSubDir(String dirPath, String suffix) {
		List<File> result = new ArrayList<>();

		File dir = new File(dirPath);
		// 後方一致で"idl"
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(suffix);
		// サブディレクトリも検索する（しない場合はnull）
		IOFileFilter dirFilter = FileFilterUtils.trueFileFilter();
		// 検索開始
		Collection<File> OpenRtmList = FileUtils.listFiles(dir, fileFilter, dirFilter);
		if (CollectionUtil.isNotEmpty(OpenRtmList)) {
			for (File file : OpenRtmList) {
				result.add(file);
			}
		}
		return result;
	}

	/**
	 * 指定されたディレクトリ内のすべてのファイル名・ファイル内容を置換する
	 * 
	 * @param targetDirPath
	 * @param oldName
	 * @param newName
	 */
	public static void renameAllFiles(String targetDirPath, String oldName, String newName) {
		// スクリプトパス
		String scriptPath = PropUtil.getValue("rtcs.scripts.directory.path") + "rename_all_under_component.sh";

		ProcessUtil.startProcessNoReturnWithWorkingDerectory(targetDirPath, "bash", scriptPath, targetDirPath, oldName,
				newName);
	}

	/**
	 * 指定されたディレクトリ内のすべてのファイル内容を置換する
	 * 
	 * @param targetDirPath
	 * @param oldName
	 * @param newName
	 */
	public static void renameAllFilesContent(String targetDirPath, String oldName, String newName) {
		// スクリプトパス
		String scriptPath = PropUtil.getValue("rtcs.scripts.directory.path") + "rename_contents.sh";

		ProcessUtil.startProcessNoReturnWithWorkingDerectory(targetDirPath, "bash", scriptPath, targetDirPath, oldName,
				newName);
	}

	/**
	 * 指定されたディレクトリ内のすべてのファイル内容を空に置換する
	 * 
	 * @param targetDirPath
	 * @param oldName
	 */
	public static void renameAllFilesContentToEmpty(String targetDirPath, String oldName) {
		// スクリプトパス
		String scriptPath = PropUtil.getValue("rtcs.scripts.directory.path") + "rename_contents_to_empty.sh";

		ProcessUtil.startProcessNoReturnWithWorkingDerectory(targetDirPath, "bash", scriptPath, targetDirPath, oldName);
	}

	/**
	 * 指定されたファイルの名称を変更する
	 * 
	 * @param targetDirPath
	 * @param oldName
	 * @param newName
	 */
	public static void renameFileName(String targetDirPath, String oldName, String newName) {
		// スクリプトパス
		String scriptPath = PropUtil.getValue("rtcs.scripts.directory.path") + "rename_file.sh";

		ProcessUtil.startProcessNoReturnWithWorkingDerectory(targetDirPath, "bash", scriptPath, targetDirPath, oldName,
				newName);
	}

	/**
	 * データセットのシンボリックリンクを作成する
	 * 
	 * @param modelDirPath
	 * @param datasetName
	 */
	public static void createDatasetLink(String modelDirPath, String datasetName) {

		// シンボリックリンクを作成するパス
		String linkedDatasetDirPath = modelDirPath + "/dataset";
		File linkedDatasetDir = new File(linkedDatasetDirPath);

		// リンク先のデータセットパス
		String datasetDirPath = PropUtil.getValue("dataset.directory.path") + datasetName;
		File datasetDir = new File(datasetDirPath);

		// 現在のデータセット設定
		String targetDatasetDirPath = "";
		if (linkedDatasetDir.exists()) {
			if (!Files.isSymbolicLink(linkedDatasetDir.toPath())) {
				logger.error("Can't craete symbolicLink. dataset dir is exist.");
				return;
			} else {
				try {
					targetDatasetDirPath = Files.readSymbolicLink(linkedDatasetDir.toPath()).toString();
				} catch (IOException e) {
					logger.error("exception handled.", e);
					return;
				}
			}
		}

		if (StringUtil.isNotEmpty(datasetName) && !linkedDatasetDir.exists()) {
			// データセットリンク新規作成
			try {
				Files.createSymbolicLink(linkedDatasetDir.toPath(), datasetDir.toPath());
			} catch (IOException e) {
				logger.error("exception handled.", e);
				return;
			}
		} else if (StringUtil.isNotEmpty(datasetName) && linkedDatasetDir.exists()
				&& !datasetDirPath.equals(targetDatasetDirPath)) {
			// データセットリンク変更
			try {
				Files.delete(linkedDatasetDir.toPath());
				Files.createSymbolicLink(linkedDatasetDir.toPath(), datasetDir.toPath());
			} catch (IOException e) {
				logger.error("exception handled.", e);
				return;
			}
		} else if (StringUtil.isEmpty(datasetName) && linkedDatasetDir.exists()) {
			// データ・セットリンク削除
			try {
				Files.delete(linkedDatasetDir.toPath());
			} catch (IOException e) {
				logger.error("exception handled.", e);
				return;
			}
		}
	}

	/**
	 * データセットのシンボリックリンクからデータセット名を取得する
	 * 
	 * @param modelDirPath
	 * @return
	 */
	public static String getDatasetLink(String modelDirPath) {
		String targetDatasetDirPath = "";

		// シンボリックリンクが作成されているパス
		String linkedDatasetDirPath = modelDirPath + "/dataset";
		File linkedDatasetDir = new File(linkedDatasetDirPath);

		if (linkedDatasetDir.exists()) {
			if (!Files.isSymbolicLink(linkedDatasetDir.toPath())) {
				logger.error("Can't craete symbolicLink. dataset dir is exist.");
			} else {
				try {
					targetDatasetDirPath = Files.readSymbolicLink(linkedDatasetDir.toPath()).getFileName().toString();
				} catch (IOException e) {
					logger.error("exception handled.", e);
				}
			}
		}

		return targetDatasetDirPath;
	}
}
