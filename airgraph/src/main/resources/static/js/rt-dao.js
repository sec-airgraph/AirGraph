/*************************************************************************
 * 作業領域・Package関連
 *************************************************************************/

/**
 * 作業領域を再取得し展開する
 * 
 * @returns
 */
function reloadAllWorkspace() {
  // 一旦画面からすべてを削除
  deleteAllComponentsViewObject();
  // 作業領域再読み込み
  loadAllWorkspace();
}

/**
 * コンポーネント情報を取得し展開する
 */
function loadAllComponentArea() {
  // コンポーネント領域を空にする
  componentAreaData = new Object();
  
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'GET',
    url: getUrlLoadComponentArea()
  }).done(function(resData){
    if(resData) {
      // コンポーネント領域取得
      componentAreaData = resData;
      // 表示
      setComponentAreaInfo();
      // 作業領域も合わせて取得する
      loadAllWorkspace();
    }
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}


/**
 * 作業領域を取得し展開する
 * @returns
 */
function loadAllWorkspace() {
  // 作業領域を空にする
  mainRtsMap = new Object();
  
  // 画面をロック
  lockScreen();
  
  $.ajax({
    type: 'GET',
    url: getUrlLoadAllWorkspace()
  }).done(function(resData){
    if(resData && resData.length > 0) {
      workspaceCounter = 0;
      loadAllPackagesWorkspace(resData);
    } else {
      // 1件も存在しない
      curWorkspaceName = 0;
    }
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * 作業領域の数を取り直す
 * @returns
 */
function updateWorkspaceCount() {
  $.ajax({
    type: 'GET',
    url: getUrlLoadAllWorkspace()
  }).done(function(resData){
    if(resData && resData.length > 0) {
      workspaceCounter = resData.length;
    }
  });
}

/**
 * Packageを追加して読み込み直す
 * 
 * @param x
 * @param y
 * @param offset
 * @param target
 * @param modelId
 * @returns
 */
function addPackage(modelId, id, sabstract, version, remoteUrl) {
  // 画面をロック
  lockScreen();
  
  // サーバ上で作業領域に展開する
  $.ajax({
    type: 'POST',
    url: getUrlDropRts(),
    data: {
      'workPackageName' : curWorkspaceName, // idは先頭にrts_が含まれている
      'dropedRtsName' : modelId,
      'newId' : id,
      'newSAbstruct' : sabstract,
      'newVersion' : version,
      'newRemoteUrl' : remoteUrl,
    } 
  }).done(function(resData){
    if(resData) {
      // 作業領域再読み込み
      reloadAllWorkspace();
    } else {
      // NOP
    }
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * 編集したRTSの情報をサーバに通知する
 * 
 * @returns
 */
function updatePackage(isReload) {
  // 位置情報を保存する
  updateLocation();

  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'POST',
    url: getUrlUpdatePackage(),
    data: JSON.stringify(mainRtsMap[curWorkspaceName]),
    contentType: 'application/json',
    dataType: 'json',
    scriptCharset: 'utf-8',
  }).done(function() {
    // 編集情報を削除する
    mainRtsMap[curWorkspaceName].editSourceCode = new Object();
    
    if(isReload && isReload === true) {
      // 作業領域再読み込み
      reloadAllWorkspace();
    }
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * 指定したPackageを削除する
 * @param id
 * @returns
 */
function deletePackage(id) {
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'POST',
    url: getUrlDeletePackage(),
    data: { 'rtsName' : id } // idは先頭にrts_が含まれている
  }).done(function(){
    // 作業領域設定
    curWorkspaceName = null;
    // 作業領域再読み込み
    reloadAllWorkspace();
    
    if (Object.keys(mainRtsMap).length == 0) {
        // 作業領域選択コンボ設定
        setWorkspaceSelectMenu();
        // コンポーネント領域再描画
        setComponentAreaInfo();
    }
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/*************************************************************************
 * RTC・コンポーネント関連
 *************************************************************************/
/**
 * コンポーネントを追加する
 * 
 * @param modelId
 * @returns
 */
function addComponent(modelId) {
  // 画面をロック
  lockScreen();
  
  // 一度保存する
  updatePackage(false);

  // コンポーネント名称とGitのリポジトリ名称を取得する
  var componentName = getComponentNameInComponents(modelId);
  var gitName = getRepositryNameInComponents(modelId);
  var clonedDirectory = getClonedDirectory(modelId);
  
  $.ajax({
    type: 'POST',
    url: getUrlAddComponent(),
    data: {
      'workPackageName' : curWorkspaceName,
      'componentName' : componentName,
      'gitName' : gitName,
      'clonedDirectory' : clonedDirectory,
    }
  }).done(function() {
    // 作業領域再読み込み
    reloadAllWorkspace();
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}
/**
 * 新規コンポーネントを追加する
 * 
 * @param componentData
 * @returns
 */
function createNewComponentAjax(componentData) {
  // 画面をロック
  lockScreen();
  
  // 一度保存する
  updatePackage(false);
  
  $.ajax({
    type: 'POST',
    url: getUrlCreateNewComponent(),
    data: JSON.stringify(componentData),
    contentType: 'application/json',
    dataType: 'json',
    scriptCharset: 'utf-8'
  }).done(function() {
    // 作業領域再読み込み
    reloadAllWorkspace();
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * コンポーネントを削除する
 * 
 * @param componentId
 * @param modelId
 * @returns
 */
function deleteComponent(componentId, modelId) {
  // 画面をロック
  lockScreen();
  
  // 一度保存する
  updatePackage(false);
  
  // コンポーネント名称を取得する
  var rtcomponent = null;
  if(mainRtsMap[curWorkspaceName].rtcs) {
    for(var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++){
      if(mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        rtcomponent = mainRtsMap[curWorkspaceName].rtcs[i];
        break;
      }
    }
  }
  
  var componentName = rtcomponent.modelProfile.modelName;
  
  $.ajax({
    type: 'POST',
    url: getUrlDeleteComponent(),
    data: {
      'workPackageName' : curWorkspaceName,
      'id' : componentId,
      'componentName' : componentName
    }
  }).done(function() {
    // 作業領域再読み込み
    reloadAllWorkspace();
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * ロガーを追加する
 * 
 * @param id
 * @param modelId
 * @param portName
 * @param dataType
 * @param logging
 * @param loggerVisible
 * @returns
 */
function addLogger(componentId, modelId, portName, dataType) {
  // 画面をロック
  lockScreen();
  
  // 一度保存する
  updatePackage(false);
  
  $.ajax({
    type: 'POST',
    url: getUrlUpdatePackage(),
    data: JSON.stringify(mainRtsMap[curWorkspaceName]),
    contentType: 'application/json',
    dataType: 'json',
    scriptCharset: 'utf-8'
  }).done(function(){
    // pathUriを取得
    var pathUri = getPathUriInPackage(componentId);
    
    $.ajax({
      type: 'POST',
      url: getUrlAddLogger(),
      data: {
        'workPackageName' : curWorkspaceName,
        'id' : componentId,
        'instanceName' : modelId,
        'portName' : portName,
        'pathUri': pathUri,
        'dataType' : dataType
      }
    }).done(function() {
      // 作業領域再読み込み
      reloadAllWorkspace();
    }).always(function() {
      // 画面ロック解除
      unlockScreen();
    });
  });
}

/**
 * データ型がロギング可能な型かを調べる<br>
 * 同期処理なので注意
 * 
 * @param dataType
 * @returns
 */
function canLogging(dataType) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlCanLogging(),
    data: {
      'dataType' : dataType
    },
    async : false
  }).responseText;
  return (result === 'OK');
}

/**
 * パッケージ名が使用可能かを調べる
 * 
 * @param name
 * @returns
 */
function isAvailablePackageName(name) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlAvailablePackageName(),
    data: {
      'name' : name
    },
    async : false
  }).responseText;
  return (result === 'OK');
}

/**
 * コンポーネント名が使用可能かを調べる
 * 
 * @param componentName
 * @returns
 */
function isAvailableComponentName(componentName) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlAvailableComponentName(),
    data: {
      'workPackageName' : curWorkspaceName,
      'componentName' : componentName
    },
    async : false
  }).responseText;
  return (result === 'OK');
}

/**
 * IDLファイルの一覧を取得する
 * 
 * @param componentName
 * @returns
 */
function getIdlFileChoices(componentName) {
  var result = $.ajax({
    type: 'POST',
    url: getURLIdlFileChoices(),
    data: {
      'workPackageName' : curWorkspaceName,
      'componentName' : componentName
    },
    async : false
  }).responseJSON;
  return result;
}

/**
 * DataType型の一覧を取得する
 * 
 * @param componentName
 * @returns
 */
function getDataTypeChoices(componentName) {
  var result = $.ajax({
    type : 'POST',
    url : getURLDataTypeChoices(),
    data : {
      'workPackageName' : curWorkspaceName,
      'componentName' : componentName
    },
    async : false
  }).responseJSON;
  return result;
}

/**
 * インタフェース型の一覧を取得する
 * 
 * @param componentName
 * @param idlFileName
 * @returns
 */
function getInterfaceTypeChoices(componentName, idlFileName) {
  var result = $.ajax({
    type : 'POST',
    url : getURLInterfaceTypeChoices(),
    data : {
      'workPackageName' : curWorkspaceName,
      'componentName' : componentName,
      'idlFileName' : idlFileName
    },
    async : false
  }).responseJSON;
  return result;
}

/**
 * IDLファイルをアップロードする
 * 
 * @returns
 */
function uploadIdlFile() {
  var formData = new FormData(
    $('#idlUploadForm').get()[0]
  );
  
  $.ajax({
    url:getUrlIdlUpload(),
    method:'post',
    data:formData,
    processData:false,
    contentType:false,
    cache: false
  }).done(function(data, status, jqxhr) {
    
  }).fail(function(data, status, jqxhr) {
    
  }); 
}

/**
 * Kerasモデルの一覧を取得する
 * 
 * @returns
 */
function getKerasModelChoices() {
  var result = $.ajax({
    type: 'POST',
    url: getUrlKerasModelChoices(),
    async : false
  }).responseJSON;
  return result;
}

/**
 * データセットの一覧を取得する
 * 
 * @returns
 */
function getDatasetChoices() {
  var result = $.ajax({
    type: 'POST',
    url: getUrlDatasetChoices(),
    async : false
  }).responseJSON;
  return result;
}

/*************************************************************************
 * DNN関連
 *************************************************************************/

/**
 * 編集したRTSの情報をサーバに通知する
 * 
 * @returns
 */
function updateDnnModels(dnnModelName, isReload) {
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'GET',
    url: getUrlUpdateDnnModels(),
    data: { 'dnnModelName' : dnnModelName }
  }).done(function(resData){
    if(resData && isReload && isReload === true) {
      // 作業領域再読み込み
      reloadAllWorkspace();
    }
  }).always(function() {
    // 画面ロック解除
    unlockScreen();
  });
}

/*************************************************************************
 * Git関連
 *************************************************************************/

/**
 * パッケージをCommitする
 * 
 * @param commitMessage
 * @returns
 */
function commitPackage(commitMessage) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlCommitPackage(),
    data: {
      'workPackageName' : curWorkspaceName,
      'commitMessage' : commitMessage
    },
    async : false
  }).responseText;
  return result;
}

/**
 * パッケージをPushする
 * 
 * @param user
 * @param pass
 * @param commitMessage
 * @returns
 */
function pushPackage(user, pass, commitMessage) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlPushPackage(),
    data: {
      'workPackageName' : curWorkspaceName,
      'commitMessage' : commitMessage,
      'userName' : user,
      'password' : pass
    },
    async : false
  }).responseText;
  return result;
}

/**
 * パッケージをPullする
 * 
 * @param user
 * @param pass
 * @returns
 */
function pullPackage(user, pass) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlPullPackage(),
    data: {
      'workPackageName' : curWorkspaceName,
      'userName' : user,
      'password' : pass
    },
    async : false
  }).responseText;
  return result;
}

/**
 * コンポーネントをCommitする
 * 
 * @param componentId
 * @param commitMessage
 * @returns
 */
function commitComponent(componentId, commitMessage) {
  // コンポーネント名称とGitのリポジトリ名称を取得する
  var component = getComponentInPackage(componentId);
  
  var result = $.ajax({
    type: 'POST',
    url: getUrlCommitComponent(),
    data: {
      'workPackageName' : curWorkspaceName,
      'componentName' : component.modelProfile.modelName,
      'gitName' : component.modelProfile.gitName,
      'commitMessage' : commitMessage
    },
    async : false
  }).responseText;
  return result;
}

/**
 * コンポーネントをPushする
 * 
 * @param componentId
 * @param user
 * @param pass
 * @param commitMessage
 * @returns
 */
function pushComponent(componentId, user, pass, commitMessage) {
  // コンポーネント名称とGitのリポジトリ名称を取得する
  var component = getComponentInPackage(componentId);
  
  var result = $.ajax({
    type: 'POST',
    url: getUrlPushComponent(),
    data: {
      'workPackageName' : curWorkspaceName,
      'componentName' : component.modelProfile.modelName,
      'gitName' : component.modelProfile.gitName,
      'commitMessage' : commitMessage,
      'userName' : user,
      'password' : pass
    },
    async : false
  }).responseText;
  return result;
}

/**
 * コンポーネントをPullする
 * 
 * @param componentId
 * @param user
 * @param pass
 * @returns
 */
function pullComponent(componentId, user, pass) {
  // コンポーネント名称とGitのリポジトリ名称を取得する
  var component = getComponentInPackage(componentId);
  
  var result = $.ajax({
    type: 'POST',
    url: getUrlPullComponent(),
    data: {
      'workPackageName' : curWorkspaceName,
      'componentName' : component.modelProfile.modelName,
      'gitName' : component.modelProfile.gitName,
      'userName' : user,
      'password' : pass
    },
    async : false
  }).responseText;
  return result;
}

/*************************************************************************
 * ビルド・実行関連
 *************************************************************************/
/**
 * Packageをすべてビルドする
 * @param id
 * @returns
 */
function buildPackageAll(id) {
  // // 一度保存する
  // updatePackage(false);
  // 実行中に移行
  setState(STATE.EXEC);
  $.ajax({
    type: 'POST',
    url: getUrlBuildAll(),
    data: { 'rtsName' : id } // idは先頭にrts_が含まれている
  }).done(function(){
    // 編集中に移行
    setState(STATE.EDIT);
  });
}
/**
 * Packageをすべてクリーンする
 * @param id
 * @returns
 */
function cleanPackageAll(id) {
  // // 一度保存する
  // updatePackage(false);
  // 実行中に移行
  setState(STATE.EXEC);
  $.ajax({
    type: 'POST',
    url: getUrlCleanAll(),
    data: { 'rtsName' : id } // idは先頭にrts_が含まれている
  }).done(function(){
    // 編集中に移行
    setState(STATE.EDIT);
  });
}

/**
 * Packageを実行する
 * @param id
 * @returns
 */
function runPackage(id) {
  // 実行中に移行
  setState(STATE.EXEC);
  $.ajax({
    type: 'POST',
    url: getUrlRunPackage(),
    data: { 'rtsName' : id } // idは先頭にrts_が含まれている
  }).done(function(){
    // NOP
  });

  // 再描画する
  reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
}

/**
 * Packageを停止する
 * @param id
 * @returns
 */
function terminatePackage(id) {
  // 編集中に移行
  setState(STATE.EDIT);
  $.ajax({
    type: 'POST',
    url: getUrlTerminatePackage(),
    data: { 'rtsName' : id } // idは先頭にrts_が含まれている
  }).done(function(result){
    // NOP
  });

  // 再描画する
  reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
}

/**
 * Packageの実行状況を確認する
 * @param id
 * @returns
 */
function isRunningPackage(id) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlIsRunningPackage(),
    data: { 'rtsName' : id }, // idは先頭にrts_が含まれている
    async : false
  }).responseText;
  return 'true' === result;
}

/*************************************************************************
 * ログ監視・モニタ監視
 *************************************************************************/
/**
 * ログ監視開始
 */
function startTailLog() {
  tailTimerID = setInterval(tailLog, 1000);
}

/**
 * ログ監視終了
 * @returns
 */
function stopTailLog() {
  clearInterval(tailTimerID);
}

/**
 * 最下部までスクロール
 * @param obj
 * @returns
 */
function scrollBottom(obj){
	if(obj[0]){
		obj.scrollTop(obj[0].scrollHeight);
	}
}

/**
 * ログ監視
 */
function tailLog() {
  if($('[name=console-scroll-check]').prop("checked") === true) {
    $.ajax({
      type: 'GET',
      url: getUrlTailLog(),
      data: { 'workPackageName' : curWorkspaceName }
    }).done(function(log){
      if(log) {
        if (log['wasanbon']) {
          wasanbonLogViewer.setValue(log['wasanbon'].replace(/[\x00-\x09\x0b-\x1f\x7f-\x9f]/g, ''));
        }
        if (log['python']) {
          pythonLogViewer.setValue(log['python']);
        }
        
        wasanbonLogViewer.setScrollTop(wasanbonLogViewer.getScrollHeight())
        pythonLogViewer.setScrollTop(pythonLogViewer.getScrollHeight())
      }
    });
  }
}
