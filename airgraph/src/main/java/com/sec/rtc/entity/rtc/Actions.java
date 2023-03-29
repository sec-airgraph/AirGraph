package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * アクティビティ設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Actions {

	/**
	 * 初期化処理
	 */
	@XmlElement(name = "OnInitialize", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onInitialize = new ActionStatusDoc();

	/**
	 * 終了処理
	 */
	@XmlElement(name = "OnFinalize", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onFinalize = new ActionStatusDoc();

	/**
	 * 実行開始処理
	 */
	@XmlElement(name = "OnStartup", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onStartup = new ActionStatusDoc();

	/**
	 * 実行終了処理
	 */
	@XmlElement(name = "OnShutdown", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onShutdown = new ActionStatusDoc();

	/**
	 * アクティブ化処理
	 */
	@XmlElement(name = "OnActivated", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onActivated = new ActionStatusDoc();

	/**
	 * 非アクティブ化処理
	 */
	@XmlElement(name = "OnDeactivated", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onDeactivated = new ActionStatusDoc();

	/**
	 * エラー開始処理
	 */
	@XmlElement(name = "OnAborting", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onAborting = new ActionStatusDoc();

	/**
	 * エラー中処理
	 */
	@XmlElement(name = "OnError", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onError = new ActionStatusDoc();

	/**
	 * エラー終了処理
	 */
	@XmlElement(name = "OnReset", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onReset = new ActionStatusDoc();

	/**
	 * 周期処理
	 */
	@XmlElement(name = "OnExecute", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onExecute = new ActionStatusDoc();

	/**
	 * onExecute後に呼ばれる処理
	 */
	@XmlElement(name = "OnStateUpdate", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onStateUpdate = new ActionStatusDoc();

	/**
	 * ExecutionContextのRate変更時の処理
	 */
	@XmlElement(name = "OnRateChanged", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onRateChanged = new ActionStatusDoc();

	/**
	 * 対応する状態に応じた処理
	 */
	@XmlElement(name = "OnAction", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onAction = new ActionStatusDoc();

	/**
	 * モード変更時処理
	 */
	@XmlElement(name = "OnModeChanged", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ActionStatusDoc onModeChanged = new ActionStatusDoc();

	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	
	/**
	 * Actionsが全て一致するかを判定する.
	 *
	 * @param target target
	 * @return Actionsが全て一致するか
	 */
	public boolean equalsActions(Actions target) {
		boolean result = true;
		result &= onInitialize.getImplemented().equals(target.getOnInitialize().getImplemented());
		result &= onFinalize.getImplemented().equals(target.getOnFinalize().getImplemented());
		result &= onStartup.getImplemented().equals(target.getOnStartup().getImplemented());
		result &= onShutdown.getImplemented().equals(target.getOnShutdown().getImplemented());
		result &= onActivated.getImplemented().equals(target.getOnActivated().getImplemented());
		result &= onDeactivated.getImplemented().equals(target.getOnDeactivated().getImplemented());
		result &= onAborting.getImplemented().equals(target.getOnAborting().getImplemented());
		result &= onError.getImplemented().equals(target.getOnError().getImplemented());
		result &= onReset.getImplemented().equals(target.getOnReset().getImplemented());
		result &= onExecute.getImplemented().equals(target.getOnExecute().getImplemented());
		result &= onStateUpdate.getImplemented().equals(target.getOnStateUpdate().getImplemented());
		result &= onRateChanged.getImplemented().equals(target.getOnRateChanged().getImplemented());
		result &= onAction.getImplemented().equals(target.getOnAction().getImplemented());
		result &= onModeChanged.getImplemented().equals(target.getOnModeChanged().getImplemented());
		return result;
	}
}
