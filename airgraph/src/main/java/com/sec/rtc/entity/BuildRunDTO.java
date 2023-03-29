package com.sec.rtc.entity;

import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.Const.COMMON.DIR_NAME;
import com.sec.airgraph.util.Const.COMMON.FILE_NAME;
import java.io.File;


/**
 * Build系・Run系関数DTO
 * 
 * @author Tatsuya Ide
 *
 */
public class BuildRunDTO {

	/**
	 * ws
	 */
	private String ws;
	
	/**
	 * rtsName
	 */
	private String rtsName;
	
	/**
	 * hostId
	 */
	private String hostId;
	
	/**
	 * コンストラクタ
	 */
	public BuildRunDTO(String ws, String rtsName, String hostId){
		this.ws = ws;
		this.rtsName = rtsName;
		this.hostId = hostId;
	}
	
	/**
	 * getter
	 */
	public String getWs() {
		return this.ws;
	}
	
	/**
	 * getter
	 */
	public String getRtsName() {
		return this.rtsName;
	}
	
	/**
	 * getter
	 */
	public String getHostId() {
		return this.hostId;
	}
	
	/**
	 * packageRepositoryNameを取得する
	 */
	public String getPackageDirPath() {

		// 作業領域パス
		String workspaceDirPath = PropUtil.getValue("workspace.local.directory.path");
		// packageName
		String packageRepositoryName = StringUtil.getPackageNameFromModelName(this.rtsName);
		// 対象Packageディレクトリ
		return workspaceDirPath + packageRepositoryName + File.separator;
	}
	
	/**
	 * 実行ログファイルパスを取得する
	 */
	public String getExecuteLogFilePath() {
		return PropUtil.getValue("workspace.execute.wasanbon.logFile.path");
	}
	
	/**
	 * APIに適したパッケージ名を取得する
	 */
	public String getPackageName() {
		// packageNameの取得
		String packageName = this.rtsName.replace("rts_", "");
		if (this.ws.equals("exec")) {
			packageName = "exec_" + packageName;
		}
		return packageName;
	}
}
