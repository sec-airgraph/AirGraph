package com.sec.airgraph.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sec.rtc.entity.field.ComponentTabInfo;
import com.sec.rtc.entity.rtc.Rtc;
import com.sec.rtc.entity.rts.Rts;
import com.sec.airgraph.util.PropUtil;
import com.sec.airgraph.util.Const.COMPONENT_FIELD.TAB_NAME;
import com.sec.airgraph.util.Const.RT_COMPONENT.LANGUAGE_KIND;

/**
 * コンポーネント領域管理サービス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Service
public class FieldManagementService {

	/**
	 * RTC管理サービス
	 */
	@Autowired
	private RtcManagementService rtcManagementService;

	/************************************************************
	 * RTC,RTS関連
	 ************************************************************/
	
	/**
	 * 新規作成タブ領域生成処理
	 * 
	 * @return
	 */
	public ComponentTabInfo createNewRtcComponentTab() {

		// タブ生成
		ComponentTabInfo tab = new ComponentTabInfo();
		tab.setTabName(TAB_NAME.NEW);
		tab.setSrvPath("");

		// 新規RTS作成
		List<Rts> rtss = new ArrayList<Rts>();
		rtss.add(rtcManagementService.createNewRtsProfile());
		tab.setRtss(rtss);
		
		// 新規RTC作成
		List<Rtc> rtcs = new ArrayList<Rtc>();
		rtcs.add(rtcManagementService.createNewRtcProfile(LANGUAGE_KIND.CPP));
		// rtcs.add(rtcManagementService.createNewRtcProfile(LANGUAGE_KIND.JAVA));
		rtcs.add(rtcManagementService.createNewRtcProfile(LANGUAGE_KIND.PYTHON));
		tab.setRtcs(rtcs);

		return tab;
	}

	/**
	 * Packageタブ領域生成処理
	 * @return
	 */
	public ComponentTabInfo createRtsPackageComponentTab() {
		// Packagesローカルリポジトリ格納先
		String packagesLocalDirPath = PropUtil.getValue("packages.local.directory.path");
		
		// タブ生成
		ComponentTabInfo tab = new ComponentTabInfo();
		tab.setTabName(TAB_NAME.PACKAGE);
		tab.setSrvPath(packagesLocalDirPath);
		
		// Packageの一覧を取得する
		List<Rts> rtss = rtcManagementService.loadRtsProfiles(packagesLocalDirPath, false);
		tab.setRtss(rtss);
		
		return tab;
	}
	
	/**
	 * Rtcタブ領域生成処理
	 * @return
	 */
	public ComponentTabInfo createRtcComponentTab() {
		// Rtcsローカルリポジトリ格納先
		String rtcsLocalDirPath = PropUtil.getValue("rtcs.local.directory.path");
		File rtcsLocalDir = new File(rtcsLocalDirPath);
		
		// タブ生成
		ComponentTabInfo tab = new ComponentTabInfo();
		tab.setTabName(TAB_NAME.RTC);
		tab.setSrvPath(rtcsLocalDirPath);
		
		// Packageの一覧を取得する
		List<Rtc> rtcs = rtcManagementService.loadRtcProfiles(rtcsLocalDir, "rtc_", false);
		tab.setRtcs(rtcs);
		
		return tab;
	}

	/**
	 * 履歴タブ領域生成処理
	 * @return
	 */
	public ComponentTabInfo createRecentRtcComponentTab() {

		// タブ生成
		ComponentTabInfo tab = new ComponentTabInfo();
		tab.setTabName(TAB_NAME.RECENT);
		tab.setSrvPath("");

		return tab;
	}

	/************************************************************
	 * Keras関連
	 ************************************************************/
}
