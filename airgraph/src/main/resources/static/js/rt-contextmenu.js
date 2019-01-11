/*************************************************************************
 * 右クリックメニュー設定
 *************************************************************************/
/**
 * 作業領域右クリックメニュー作成
 */
function createContextMenuMainPaper() {
  // 作業領域全体の右クリックメニュー設定
  setMainAreaContextMenu();
  // コンポーネント右クリックメニュー設定
  setComponentContextMenu();
  // データ入力ポートメニュー設定
  setDataInPortContextMenu();
  // データ出力ポートメニュー設定
  setDataOutPortContextMenu();
  // 接続時入力メニュー設定
  setDataportConnectionContextMenu();
  // サービスポートメニュー設定
  setServicePortContextMenu();
}

/**
 * 作業領域の右クリックメニュー設定
 */
function setMainAreaContextMenu() {
  $.contextMenu({
    selector: '#main-joint-area',
    callback: function(key, options) {
      if(key === 'save') {
        // 保存
        updatePackage(true);
      } else if(key === 'git') {
        // Git連携
        openPackageGitCommitPushPopup();
      } else if(key === 'edit') {
        // Package設定
        openPackageProfileSetting();
      } else if(key === 'delete') {
        // Package削除
        w2confirm('表示中のPackageを削除します。<br/>よろしいですか？', function (btn) {
          if(btn === 'Yes') {
            deletePackage(curWorkspaceName);
          }
        });
      }
    },
    items: {
      'save':    {name: 'Save All',             icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'sep1':    '---------',
      'edit':    {name: 'Edit Package Setting', icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'sep2':    '---------',
      'git':     {name: 'Git Repository Link',  icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'sep3':    '---------',
      'delete':  {name: 'Remove Package',       icon: 'delete', disabled: function(key, opt) { return curState !== STATE.EDIT } }
    }
  });
}

/*************************************************************************
 * コンポーネント右クリックメニュー設定
 *************************************************************************/
/**
 * コンポーネントの右クリックメニュー設定
 */
function setComponentContextMenu() {
  $.contextMenu({
    selector: '.rtc-context-menu',
    callback: function(key, options) {
      // RtcID
      var componentId = $(this).attr('component-id');
      var modelId = $(this).parent().parent().attr('model-id');

      if(key === 'addIn') {
        // 入力ポート追加
        openNewDataPortSettingPopup(componentId, true);
      } else if(key === 'addOut') {
        // 出力ポート追加
        openNewDataPortSettingPopup(componentId, false);
      } else if(key === 'addService') {
        // サービスポート追加
        openNewServicePortSettingPopup(componentId);
      } else if(key === 'edit') {
        // RTC設定画面表示
        openEditRtcProfileSettingPopup(componentId);
      } else if(key === 'configuration') {
        // コンフィギュレーション設定画面表示
        openConfigurationPopup(componentId);
      } else if(key === 'delete') {
        // RTC削除
        w2confirm('選択したコンポーネントを削除します。<br/>よろしいですか？', function (btn) {
          if(btn === 'Yes') {
            deleteComponent(componentId, modelId);
          }
        });
      } else if(key === 'git') {
        // Git設定
        openRtcGitCommitPushPopup(componentId);
      } else if(key === 'source') {
        // ソースコード編集
        openSourceCodePopup(componentId);
      }
    },
    items: {
      'addIn':         {name: 'Add Input Port',                icon: 'add',    disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'addOut':        {name: 'Add Output Port',               icon: 'add',    disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'addService':    {name: 'Add Service Port',              icon: 'add',    disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'sep1':          '---------',
      'edit':          {name: 'Edit Component Setting',        icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'configuration': {name: 'Edit Configuration Parameters', icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'source':        {name: 'Edit Source Code',              icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'sep2':          '---------',
      'git':           {name: 'Git Repository Link',           icon: 'edit',   disabled: function(key, opt) { return curState !== STATE.EDIT } },
      'sep3':          '---------',
      'delete':        {name: 'Remove Component',              icon: 'delete', disabled: function(key, opt) { return curState !== STATE.EDIT } },
    }
  });
}
/*************************************************************************
 * データポート右クリックメニュー設定
 *************************************************************************/
/**
 * データ入力ポートの右クリックメニュー設定
 */
function setDataInPortContextMenu() {
  $.contextMenu({
    selector: '.rtc-data-inport-menu',
    callback: function(key, options) {
      if(key === 'editPort') {
        // ポート編集
        openEditDataPortSettingPopup(this.data());
        options.isRemove = false;
      } else if(key === 'deletePort') {
        // ポート削除
        deleteDataPort(this.data().id, this.data().index);
        // 作業領域再読み込み
        // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
        options.isRemove = true;
        updatePackage(true);
      }
    },
    items: {
      // ポート編集
      editPort:    { name: 'edit Port', icon: 'edit', disabled: function(key, opt) { return curState !== STATE.EDIT } },
      // ポート削除
      deletePort:    { name: 'remove Port', icon: 'delete', disabled: function(key, opt) { return isPortConnected(this.data().id, this.data().modelId, this.data().portName) || curState !== STATE.EDIT } },
    }, 
    events: {
      show: function(opt) {
        var $this = this;
        $.contextMenu.setInputValues(opt, $this.data());
      }, 
      hide: function(opt) {
        if (opt.isRemove && opt.isRemove === true) {
          // NOP
        } else {
          updateDataportChanged(opt, this);
        }
      }
    }
  });
}

/**
 * データ出力ポートの右クリックメニュー設定
 */
function setDataOutPortContextMenu() {
  $.contextMenu({
    selector: '.rtc-data-outport-menu',
    callback: function(key, options) {
      if(key === 'editPort') {
        // ポート編集
        openEditDataPortSettingPopup(this.data());
        options.isRemove = false;
      } else if(key === 'deletePort') {
        // ポート削除
        deleteDataPort(this.data().id, this.data().index);
        // 作業領域再読み込み
        // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
        options.isRemove = true;
        updatePackage(true);
      } else if(key === 'openMonitor') {
        // モニタ表示
        var $this = this;
        openMonitorImage($this.data().id, $this.data().portName);
      }
    },
    items: {
      // ポート編集
      editPort:    { name: 'edit Port', icon: 'edit', disabled: function(key, opt) { return curState !== STATE.EDIT } },
      // ポート削除
      deletePort:    { name: 'remove Port', icon: 'delete', disabled: function(key, opt) { return isPortConnected(this.data().id, this.data().modelId, this.data().portName) || curState !== STATE.EDIT } },
      // 'sep1':        '---------',
      // editLog: { name: 'setting Logs', icon: 'fa-pencil-square-o', items: {
      //  // ログON/OFF
      //  logging:       { name: 'logging', type: 'checkbox', disabled: function(key, opt) { return !canLogging(this.data('dataType')) || curState !== STATE.EDIT } },
      //  // ロガー表示
      //  loggerVisible: { name: 'show Logger', type: 'checkbox', disabled: function(key, opt) { return !this.data('logging') || curState !== STATE.EDIT } },
      // }},
      // // モニタ表示
      // openMonitor:   { name: 'open Monitor', icon: 'fa-desktop', disabled: function(key, opt) { return !canLogging(this.data('dataType')) }}
    }, 
    events: {
      show: function(opt) {
        var $this = this;
        $.contextMenu.setInputValues(opt, $this.data());
      }, 
      hide: function(opt) {
        if (opt.isRemove && opt.isRemove === true) {
          // NOP
        } else {
          updateDataportChanged(opt, this);
        }
      }
    }
  });
}

/**
 * データポートの接続情報の右クリックメニュー設定
 * @returns
 */
function setDataportConnectionContextMenu() {
  $.contextMenu({
    selector: '.rtc-link-menu',
    items: {
      // Name
      name:             { name: 'Name:',              type: 'text', value: '' },
      // DataType
      dataType:         { name: 'Data Type:',         type: 'text', disabled: true },
      // InterfaceType
      interfaceType:    { name: 'Interface Type:',    type: 'select', options: getConnectInterfaceTypeChoices(), disabled: false },
      // DataflowType
      dataflowType:     { name: 'Dataflow Type:',     type: 'select', options: getConnectDataflowTypeChoices(), disabled: false },
      // SubscriptionType
      subscriptionType: { name: 'Subscription Type:', type: 'select', options: getConnectSubscriptionTypeChoices(), disabled: false },
    }, 
    events: {
      show: function(opt) {
          var $this = this;
          $.contextMenu.setInputValues(opt, $this.data());
      }, 
      hide: function(opt) {
          var $this = this;
          var oldInfo = new Object();
          oldInfo.interfaceType = $this.data().interfaceType;
          oldInfo.dataflowType = $this.data().dataflowType;
          oldInfo.subscriptionType = $this.data().subscriptionType;
          $.contextMenu.getInputValues(opt, $this.data());
          updateDataportConnectorInfo(oldInfo, $this.data())
      }
    }
  });
}

/**
 * データポートの変更を反映する
 * 
 * @param elm
 * @returns
 */
function updateDataportChanged(opt, elm) {
  var $this = elm;
  // 変更前・変更後ポート名称
  var componentId = $this.data().id;
  var oldLogging = $this.data().logging;
  var oldLoggerVisible = $this.data().loggerVisible;
  $.contextMenu.getInputValues(opt, $this.data());
  var newLogging = $this.data().logging;
  var newLoggerVisible = $this.data().loggerVisible;
  
  var rtc = getComponentInPackage(componentId);
  var isIn = $this.data().isIn;
  
  var changeFlg = false;
  if(oldLogging === false && newLogging === true) {
    // ログ追加
    changeFlg = true;
    addLogger($this.data().id, $this.data().modelId, $this.data().portName, $this.data().dataType);
  }
  if(oldLogging === true && newLogging === false) {
    // ログ削除
    changeFlg = true;
    removeLogger($this.data().id, $this.data().modelId, $this.data().portName);
  }
  
  if(oldLoggerVisible !== newLoggerVisible) {
    // ログ表示・非表示
    changeLoggerDisplay($this.data().id, $this.data().modelId, $this.data().portName, newLoggerVisible);
  }
  
  if(changeFlg === true) {
    // 接続情報再構築
    updateDataPortConnectionInfo(rtc, oldPortName, newPortName, isIn);
    // 作業領域再読み込み
    // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
    updatePackage(true);
  }
}

/**
 * データポートの接続情報を変更する
 * 
 * @param oldInfo
 * @param newInfo
 * @returns
 */
function updateDataportConnectorInfo(oldInfo, newInfo) {
  if (oldInfo.interfaceType !== newInfo.interfaceType
    || oldInfo.dataflowType !== newInfo.dataflowType
    || oldInfo.subscriptionType !== newInfo.subscriptionType) {
    
    var dataPortConnectors = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
    if(dataPortConnectors && dataPortConnectors.length > 0) {
      for (var i = 0; i < dataPortConnectors.length; i++) {
        var dataPortConnector = dataPortConnectors[i];
        if (dataPortConnector.connectorId === newInfo.connectorId) {
          mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].interfaceType = newInfo.interfaceType;
          mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].dataflowType = newInfo.dataflowType;
          mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].subscriptionType = newInfo.subscriptionType;
          
          if (dataPortConnector.properties.length > 0) {
            for (var j = 0; j < dataPortConnector.properties.length; j++) {
              if (dataPortConnector.properties[j].name === 'dataport.dataflow_type') {
                mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].properties[j].value = newInfo.dataflowType;
              } else if (dataPortConnector.properties[j].name === 'dataport.interface_type') {
                mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].properties[j].value = newInfo.interfaceType;
              } else if (dataPortConnector.properties[j].name === 'dataport.subscription_type') {
                mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].properties[j].value = newInfo.subscriptionType;
              }
            }
          }
          
          break;
        }
      }
    }
  }
}

/*************************************************************************
 * サービスポート右クリックメニュー設定
 *************************************************************************/

/**
 * サービスポートの右クリックメニュー設定
 */
function setServicePortContextMenu() {
  $.contextMenu({
    selector: '.rtc-serviceport-menu',
    callback: function(key, options) {
      if(key === 'editPort') {
        // ポート編集
        openEditServicePortSettingPopup(this.data());
      } else if(key === 'deletePort') {
        // ポート削除
        deleteServicePort(this.data().id, this.data().index);
        // 作業領域再読み込み
        // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
        updatePackage(true);
      }
    },
    items: {
      // ポート編集
      editPort:    { name: 'edit Port', icon: 'edit', disabled: function(key, opt) { return curState !== STATE.EDIT } },
      // ポート削除
      deletePort:    { name: 'remove Port', icon: 'delete', disabled: function(key, opt) { return curState !== STATE.EDIT } }
    }, 
    events: {
      show: function(opt) {
        var $this = this;
        $.contextMenu.setInputValues(opt, $this.data());
      }, 
      hide: function(opt) {
      }
    }
  });
}
/*************************************************************************
 * ソースコードエディタ右クリックメニュー設定
 *************************************************************************/

/**
 * ファイル追加用右クリックメニュー
 * 
 */
function setSourceEditorFolderContextMenu() {
  $.contextMenu({
    selector: '.w2ui-sidebar-div',
    callback: function(key, options) {
      if (key === 'add') {
        // ID
        var componentId = w2ui['soruce-editor-sidebar'].componentId;
        // 入力値
        var className = options.inputs["name"].$input[0].value;
        if (className) {
          // ファイル追加
          addClassFile(componentId, className);
          // ツリー再表示
          updateSidebarData(componentId);
        }
      }
    },
    items: {
      // Name
      'name': { name: 'class Name:', type: 'text', disabled: function(key, opt) { return curState !== STATE.EDIT }, value: '' },
      'add' : {name: 'add Class',    icon: 'add',  disabled: function(key, opt) { return curState !== STATE.EDIT } }
    }
  });
}

function setSourceEdtiorFileContextMenu() {
  $.contextMenu({
    selector: '.rtc-source-file-menu',
    callback: function(key, options) {
      if (key === 'delete') {
        // ID
        var componentId = w2ui['soruce-editor-sidebar'].componentId;
        // ファイル名
        var filePath = sourceEditor['sourcePath'];
        if (filePath) {
          w2confirm('選択したファイルを削除します。<br/>よろしいですか？', function (btn) {
            if(btn === 'Yes') {
              // ファイル削除
              deleteFile(filePath);
              // ツリー再表示
              updateSidebarData(componentId);
            }
          });
        }
      }
    },
    items: {
      'delete': {name: 'delete File', icon: 'delete', disabled: function(key, opt) { return curState !== STATE.EDIT } }
    }
  });
}
