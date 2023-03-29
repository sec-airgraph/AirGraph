/**
 * ajaxでエラーが帰ってきた場合、alertを表示する.
 */
$(document).ajaxError( function (eo, jqXHR, settings){
	//alert(jqXHR.responseJSON.message);
});

/*************************************************************************
 * 作業領域・Package関連
 *************************************************************************/

/**
 * 作業領域を再取得し展開する
 * 
 * @returns {undefined}
 */
function reloadAllWorkspace() {
  // 一旦画面からすべてを削除
  deleteAllComponentsViewObject();
  
  resetPropertyAreaHostProfile();
  // 作業領域再読み込み
  loadAllWorkspace();
}

/**
 * コンポーネント情報を取得し展開する
 * 
 * @returns {undefined}
 */
function loadAllComponentArea() {
  // コンポーネント領域を空にする
  componentAreaData = new Object();

  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'GET',
    url: getUrlLoadComponentArea(),
    data: {
    'hostId': 'local',
  }
  }).done(function (resData) {
    if (resData) {
      // コンポーネント領域取得
      componentAreaData = resData;
      // 表示
      setComponentAreaInfo();
      // 作業領域も合わせて取得する
      loadAllWorkspace();
    }
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}


/**
 * 作業領域を取得し展開する
 *
 * @returns {undefined}
 */
function loadAllWorkspace() {
  // 作業領域を空にする
  mainRtsMap = new Object();

  // 画面をロック
  lockScreen();

  $.ajax({
    type: 'GET',
    url: getUrlLoadAllWorkspace()
  }).done(function (resData) {
    if (resData && resData.length > 0) {
      workspaceCounter = 0;
      loadAllPackagesWorkspace(resData);
    } else {
      // 1件も存在しない
      curWorkspaceName = 0;
    }
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * 作業領域の数を取り直す
 * 
 * @returns {undefined}
 */
function updateWorkspaceCount() {
  $.ajax({
    type: 'GET',
    url: getUrlLoadAllWorkspace()
  }).done(function (resData) {
    if (resData && resData.length > 0) {
      workspaceCounter = resData.length;
    }
  });
}

/**
 * Packageを追加して読み込み直す
 * 
 * @param {*} modelId modelId
 * @param {*} id id
 * @param {*} sabstract sabstract
 * @param {*} version version
 * @param {*} remoteUrl remoteUrl
 * @param {*} packageName packageName
 * @param {*} hostId hostId
 * @returns {undefined}
 */
function addPackage(modelId, id, sabstract, version, remoteUrl, packageName, hostId) {
  // 画面をロック
  lockScreen();

  // サーバ上で作業領域に展開する
  $.ajax({
    type: 'POST',
    url: getUrlDropRts(),
    data: {
      workPackageName: curWorkspaceName, // idは先頭にrts_が含まれている
      dropedRtsName: modelId,
      newId: id,
      newSAbstruct: sabstract,
      newVersion: version,
      newRemoteUrl: remoteUrl,
      newPackageName: packageName,
      hostId: hostId
    }
  }).done(function (resData) {
    if (resData) {
      // 作業領域再読み込み
      reloadAllWorkspace();
    } else {
      // NOP
    }
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * 編集したRTSの情報をサーバに通知する
 * 
 * @param {*} isReload isReload
 * @returns {undefined}
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
    scriptCharset: 'utf-8',
  }).done(function () {
    // 編集情報を削除する
    mainRtsMap[curWorkspaceName].editSourceCode = new Object();

    if (isReload && isReload === true) {
      // 作業領域再読み込み
      reloadAllWorkspace();
    }
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * 指定したPackageを削除する
 *
 * @param {*} id id
 * @returns {undefined}
 */
function deletePackage(id) {
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'DELETE',
    url: getUrlDeletePackage(),
    data: { 'rtsName': id } // idは先頭にrts_が含まれている
  }).done(function () {
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
  }).always(function () {
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
 * @param {*} modelId modelId
 * @returns {undefined}
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
      'workPackageName': curWorkspaceName,
      'componentName': componentName,
      'gitName': gitName,
      'clonedDirectory': clonedDirectory,
    }
  }).done(function () {
    // 作業領域再読み込み
    reloadAllWorkspace();
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}
/**
 * 新規コンポーネントを追加する
 * 
 * @param {*} componentData componentData
 * @returns {undefined}
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
    scriptCharset: 'utf-8'
  }).done(function () {
    // 作業領域再読み込み
    reloadAllWorkspace();
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * コンポーネントを削除する
 * 
 * @param {*} componentId componentId
 * @param {*} modelId modelId
 * @returns {undefined}
 */
function deleteComponent(componentId) {
  // 画面をロック
  lockScreen();

  // 一度保存する
  updatePackage(false);

  // コンポーネント名称を取得する
  var rtcomponent = null;
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
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
      'workPackageName': curWorkspaceName,
      'id': componentId,
      'componentName': componentName
    }
  }).done(function () {
    // 作業領域再読み込み
    reloadAllWorkspace();
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * ロガーを追加する
 * 
 * @param {*} componentId componentId
 * @param {*} modelId modelId
 * @param {*} portName portName
 * @param {*} dataType dataType
 * @returns {undefined}
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
  }).done(function () {
    // pathUriを取得
    var pathUri = getPathUriInPackage(componentId);

    $.ajax({
      type: 'POST',
      url: getUrlAddLogger(),
      data: {
        'workPackageName': curWorkspaceName,
        'id': componentId,
        'instanceName': modelId,
        'portName': portName,
        'pathUri': pathUri,
        'dataType': dataType
      }
    }).done(function () {
      // 作業領域再読み込み
      reloadAllWorkspace();
    }).always(function () {
      // 画面ロック解除
      unlockScreen();
    });
  });
}

/**
 * データ型がロギング可能な型かを調べる<br>
 * 同期処理なので注意
 * 
 * @param {*} dataType dataType
 * @returns {undefined}
 */
function canLogging(dataType) {
  return $.ajax({
    type: 'POST',
    url: getUrlCanLogging(),
    data: {
      'dataType': dataType
    },
    async: false
  }).responseText;
}

/**
 * パッケージ名が使用可能かを調べる
 * 
 * @param {*} name name
 * @returns {boolean} result
 */
function isAvailablePackageName(name) {
  return $.ajax({
    type: 'POST',
    url: getUrlAvailablePackageName(),
    data: {
      name: name,
      hostId: 'local'
    },
    async: false
  });
}

/**
 * コンポーネント名が使用可能かを調べる
 * 
 * @param {*} componentName componentName
 * @returns {boolean} result
 */
function isAvailableComponentName(componentName) {
  return $.ajax({
    type: 'POST',
    url: getUrlAvailableComponentName(),
    data: {
      workPackageName: curWorkspaceName,
      componentName: componentName,
      hostId: 'local'
    },
    async: false
  });
}

/**
 * IDLファイルの一覧を取得する
 * 
 * @param {*} componentName componentName
 * @returns {*} IDLファイルの一覧
 */
function getIdlFileChoices(componentName) {
  return $.ajax({
    type: 'POST',
    url: getURLIdlFileChoices(),
    data: {
      'workPackageName': curWorkspaceName,
      'componentName': componentName
    },
    async: false
  }).responseJSON;
}

/**
 * DataType型の一覧を取得する
 * 
 * @param {*} componentName componentName
 * @returns {*} DataType型の一覧
 */
function getDataTypeChoices(componentName) {
  return $.ajax({
    type: 'POST',
    url: getURLDataTypeChoices(),
    data: {
      'workPackageName': curWorkspaceName,
      'componentName': componentName
    },
    async: false
  }).responseJSON;
}

/**
 * インタフェース型の一覧を取得する
 * 
 * @param {*} componentName componentName
 * @param {*} idlFileName idlFileName
 * @returns {*} インタフェース型の一覧
 */
function getInterfaceTypeChoices(componentName, idlFileName) {
  return $.ajax({
    type: 'POST',
    url: getURLInterfaceTypeChoices(),
    data: {
      'workPackageName': curWorkspaceName,
      'componentName': componentName,
      'idlFileName': idlFileName
    },
    async: false
  }).responseJSON;
}

/**
 * IDLファイルをアップロードする
 * 
 * @returns {undefined}
 */
function uploadIdlFile() {
  var formData = new FormData(
    $('#idlUploadForm').get()[0]
  );

  $.ajax({
    url: getUrlIdlUpload(),
    method: 'post',
    data: formData,
    processData: false,
    contentType: false,
    cache: false
  }).done(function () {

  }).fail(function () {

  });
}

/**
 * Kerasモデルの一覧を取得する
 * 
 * @returns {*} Kerasモデルの一覧
 */
function getKerasModelChoices() {
  return $.ajax({
    type: 'POST',
    url: getUrlKerasModelChoices(),
    async: false
  }).responseJSON;
}

/**
 * データセットの一覧を取得する
 * 
 * @returns {*} データセットの一覧
 */
function getDatasetChoices() {
  return $.ajax({
    type: 'POST',
    url: getUrlDatasetChoices(),
    async: false
  }).responseJSON;
}

/*************************************************************************
 * DNN関連
 *************************************************************************/

/**
 * 編集したRTSの情報をサーバに通知する
 * 
 * @param {*} dnnModelName dnnModelName
 * @param {*} isReload isReload
 * @param {*} pathuri pathUri
 * @returns {undefined}
 */
function updateDnnModels(dnnModelName, isReload, pathuri) {
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'GET',
    url: getUrlUpdateDnnModels(),
    data: { 'dnnModelName': dnnModelName,
            'pathUri': pathuri 
          }
  }).done(function (resData) {
    if (resData && isReload && isReload === true) {
      // 作業領域再読み込み
      reloadAllWorkspace();
    }
  }).always(function () {
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
 * @param {*} commitMessage commitMessage
 * @returns {string} コミットの結果
 */
function commitPackage(commitMessage) {
  return $.ajax({
    type: 'POST',
    url: getUrlCommitPackage(),
    data: {
      'workPackageName': curWorkspaceName,
      'commitMessage': commitMessage
    },
    async: false
  }).responseText;
}

/**
 * パッケージをPushする
 * 
 * @param {*} user user
 * @param {*} pass pass
 * @param {*} commitMessage commitMessage
 * @returns {string} push結果
 */
function pushPackage(user, pass, commitMessage) {
  return $.ajax({
    type: 'POST',
    url: getUrlPushPackage(),
    data: {
      'workPackageName': curWorkspaceName,
      'commitMessage': commitMessage,
      'userName': user,
      'password': pass
    },
    async: false
  }).responseText;
}

/**
 * パッケージをPullする
 * 
 * @param {*} user user
 * @param {*} pass pass
 * @returns {string} pull結果
 */
function pullPackage(user, pass) {
  return $.ajax({
    type: 'POST',
    url: getUrlPullPackage(),
    data: {
      'workPackageName': curWorkspaceName,
      'userName': user,
      'password': pass
    },
    async: false
  }).responseText;
}

/**
 * コンポーネントをCommitする
 * 
 * @param {*} componentId componentId
 * @param {*} commitMessage commitMessage
 * @returns {string} commit結果
 */
function commitComponent(componentId, commitMessage) {
  // コンポーネント名称とGitのリポジトリ名称を取得する
  var component = getComponentInPackage(componentId);

  return $.ajax({
    type: 'POST',
    url: getUrlCommitComponent(),
    data: {
      'workPackageName': curWorkspaceName,
      'componentName': component.modelProfile.modelName,
      'gitName': component.modelProfile.gitName,
      'commitMessage': commitMessage
    },
    async: false
  }).responseText;
}

/**
 * コンポーネントをPushする
 * 
 * @param {*} componentId componentId
 * @param {*} user user
 * @param {*} pass pass
 * @param {*} commitMessage commitMessage
 * @returns {*} result
 */
function pushComponent(componentId, user, pass, commitMessage) {
  // コンポーネント名称とGitのリポジトリ名称を取得する
  var component = getComponentInPackage(componentId);

  return $.ajax({
    type: 'POST',
    url: getUrlPushComponent(),
    data: {
      'workPackageName': curWorkspaceName,
      'componentName': component.modelProfile.modelName,
      'gitName': component.modelProfile.gitName,
      'commitMessage': commitMessage,
      'userName': user,
      'password': pass
    },
    async: false
  }).responseText;
}

/**
 * コンポーネントをPullする
 * 
 * @param {*} componentId componentId
 * @param {*} user user
 * @param {*} pass pass
 * @returns {*} result
 */
function pullComponent(componentId, user, pass) {
  // コンポーネント名称とGitのリポジトリ名称を取得する
  var component = getComponentInPackage(componentId);

  return $.ajax({
    type: 'POST',
    url: getUrlPullComponent(),
    data: {
      'workPackageName': curWorkspaceName,
      'componentName': component.modelProfile.modelName,
      'gitName': component.modelProfile.gitName,
      'userName': user,
      'password': pass
    },
    async: false
  }).responseText;
}

/**
 * コミットハッシュを取得する
 * 
 * @param {*} packageName packageName
 * @returns {*} result
 */
function getCommitHash(packageName) {

  return $.ajax({
    type: 'GET',
    url: getUrlGetCommitHash(),
    data: {
       'packageName': packageName
    },
    async: false
  }).responseText;
}

/**
 * Airgraphのバージョンを確認する
 * 
 * @returns {*} result
 */
function getAirgraphVersion() {
  
  return $.ajax({
    type: 'GET',
    url: getUrlAitgraphVersion(),
    async: false
  }).responseText;
}

/**
 * Wasanbonのバージョンを確認する
 * 
 * @param {*} hostId hostId
 * @returns {*} result
 */
function getWasanbonVersion(hostId) {
  
  return $.ajax({
    type: 'GET',
    url: getUrlWasanbonVersion(),
    data: {
    hostId: hostId
  },
    async: false
  }).responseText;
}

/**
* packageの状態を確認する
* 
* @param {*} ws ws
* @param {*} rtsName rtsName
* @returns {*} result
*/
function checkPackageStatus(ws, rtsName) {

  return $.ajax({
    type: 'GET',
    url: getUrlCheckPackageStatus(),
    data: {
      'ws': ws,
       'rtsName': rtsName
    },
  })
 }
 
 /**
 * RTCの状態を確認する
 * 
 * @param {*} ws ws
 * @param {*} rtsName rtsName
 * @returns {*} result
 */
function checkRtcsStatus(ws, rtsName) {

  return $.ajax({
    type: 'GET',
    url: getUrlCheckRtcsStatus(),
    data: {
      'ws': ws,
       'rtsName': rtsName
    },
  })
 }

 /**
 * nameserverの状態を確認する
 * 
 * @param {*} hostId hostId
 * @returns {*} result
 */
function checkNameserverStatus(hostId) {

  return $.ajax({
    type: 'GET',
    url: getUrlCheckNameserverStatus(),
    data: {
      hostId: hostId
    },
  })
 }


/*************************************************************************
 * ビルド・実行関連
 *************************************************************************/
 /**
 * 複数のajax通信をする.すべてが終了した段階で、コンソールログを表示する
 * 
 * @param {*} ws ws
 * @param {*} ajaxList ajaxList
 * @returns {undefined}
 */
function multipleAjax(ws, ajaxList) {
  // 非同期処理を実行
  $.when.apply($, ajaxList).done(function () {
  }).always(function () {
    // 編集中に移行
    setState(STATE.EDIT);
    if (ws === 'dev') {
      openLocalConsoleLog();
    } else {
      openRemoteConsoleLog();
    }
    // 画面ロック解除
    unlockScreen();
  });
}

 /**
 * 複数のajax通信をする.すべてが終了した段階で、コンソールログを表示する
 * RTCアイコンの色も変更する
 *
 * @param {*} ws ws
 * @param {*} ajaxList ajaxList
 * @param {*} id id
 * @param {*} assignedHostList assignedHostList
 * @returns {undefined}
 */
function multipleAjaxAndChangeColors(ws, ajaxList, id, assignedHostList) {
  // 非同期処理を実行
  $.when.apply($, ajaxList).done(function () {
  }).always(function () {
    // RTCアイコンの色を変更
    for (let i = 0; i < assignedHostList.length; i++) {
      changeRTCIconColor(isRunningPackage(ws, id, assignedHostList[i]));
    }

    if (ws === 'dev') {
      openLocalConsoleLog();
    } else {
      openRemoteConsoleLog();
    }
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * デプロイする
 *
 * @param {*} assignedHostList assignedHostList
 * @param {*} ws ws
 * @param {*} remoteRepositoryUrl remoteRepositoryUrl
 * @param {*} commitHash commitHash
 * @returns {*} result
 */
function deploy(assignedHostList, ws, remoteRepositoryUrl, commitHash) {
  // 実行中に移行
  setState(STATE.EXEC);
  lockScreen();

  let ajaxList = [];
  for (let i = 0; i < assignedHostList.length; i++) {
    ajaxList.push(
      $.ajax({
        type: 'POST',
        url: getUrlDeploy(),
        data: {
          hostId: assignedHostList[i],
          ws: ws,
          remoteRepositoryUrl: remoteRepositoryUrl,
          commitHash: commitHash
        }
      }));
  }

  // 非同期処理を実行
  $.when.apply($, ajaxList).done(function () {
    openCreateDeployPopup('All RTCs are Deployed.');
  }).fail(function (jqXHR, textStatus, errorThrown) {
    let msg = 'status: ' + textStatus + '<br>';
    msg += 'body: ' + jqXHR.responseText + '<br>';
    openCreateDeployErrPopup(msg);
  }).always(function () {
    // 画面ロック解除
    unlockScreen();
    setState(STATE.EDIT);
  });

}

/**
 * Packageをすべてビルドする
 * 
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} assignedHostList assignedHostList
 * @returns {undefined}
 */
function buildPackageAll(ws, id, assignedHostList) {

  // 実行中に移行
  setState(STATE.EXEC);
  lockScreen();

  clearExecuteWasanbonLog();

  let ajaxList = [];
  for (let i = 0; i < assignedHostList.length; i++) {
    ajaxList.push(
      $.ajax({
        type: 'POST',
        url: getUrlBuildAll(),
        data: {
          ws: ws,
          rtsName: id, // idは先頭にrts_が含まれている
          hostId: assignedHostList[i]
        }
      }));
  }
  multipleAjax(ws, ajaxList);
}
/**
 * Packageをすべてクリーンする
 *
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} assignedHostList assignedHostList
 * @returns {undefined}
 */
function cleanPackageAll(ws, id, assignedHostList) {
  // 実行中に移行
  setState(STATE.EXEC);
  lockScreen();

  clearExecuteWasanbonLog();

  let ajaxList = [];
  for (let i = 0; i < assignedHostList.length; i++) {
    ajaxList.push(
      $.ajax({
        type: 'POST',
        url: getUrlCleanAll(),
        data: {
          ws: ws,
          rtsName: id, // idは先頭にrts_が含まれている
          hostId: assignedHostList[i]
        }
      }));
  }
  multipleAjax(ws, ajaxList);
}

/**
 * Packageを実行する
 * 
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} assignedHostList assignedHostList
 * @returns {undefined}
 */
function runSystem(ws, id, assignedHostList) {
  // 実行中に移行
  setState(STATE.EXEC);
  lockScreen();

  clearExecuteWasanbonLog();

  let ajaxList = [];
  for (let i = 0; i < assignedHostList.length; i++) {
    ajaxList.push(
      $.ajax({
        type: 'POST',
        url: getUrlRunSystem(),
        data: {
          ws: ws,
          rtsName: id,// idは先頭にrts_が含まれている
          hostId: assignedHostList[i]
        }
      }));
  }
  multipleAjaxAndChangeColors(ws, ajaxList, id, assignedHostList);
}

/**
 * スタート
 * 
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} assignedHostList assignedHostList
 * @returns {undefined}
 */
function startRtcs(ws, id, assignedHostList) {
  // 実行中に移行
  setState(STATE.EXEC);
  lockScreen();

  clearExecuteWasanbonLog();

  let ajaxList = [];
  for (let i = 0; i < assignedHostList.length; i++) {
    ajaxList.push(
      $.ajax({
        type: 'POST',
        url: getUrlStartRTCs(),
        data: {
          ws: ws,
          rtsName: id,
          hostId: assignedHostList[i]
        }
      }));
  }
  multipleAjaxAndChangeColors(ws, ajaxList, id, assignedHostList);
}

/**
 * コネクトする
 * 
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} hostId hostId
 * @returns {undefined}
 */
function connectPorts(ws, id, hostId) {
  // 実行中に移行
  setState(STATE.EXEC);
  clearExecuteWasanbonLog();

  $.ajax({
    type: 'POST',
    url: getUrlConnectPorts(),
    data: {
      ws: ws,
      rtsName: id,// idは先頭にrts_が含まれている
      hostId: hostId
    },
  }).done(function () {
    reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
    changeRTCIconColor(isRunningPackage(ws, id, hostId));
  });
}

/**
 * アクティベイトかディアクティベイトをする
 * 
 * @param {*} isActivate isActivate
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} hostId hostId
 * @returns {undefined}
 */
function activateOrDeactivateRtcs(isActivate, ws, id, hostId) {
  // 実行中に移行
  setState(STATE.EXEC);
  clearExecuteWasanbonLog();

  $.ajax({
    type: 'POST',
    url: getUrlActivateOrDeactivateRTCs(),
    data: {
      isActivate: isActivate,
      ws: ws,
      rtsName: id,// idは先頭にrts_が含まれている
      hostId: hostId
    },
  }).done(function () {
    reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
    changeRTCIconColor(isRunningPackage(ws, id, hostId));
  });
}

/**
 * Packageを停止する
 * 
 * @param {*} ws ws
 * @param {*} id id
 * @param {*} assignedHostList assignedHostList
 * @returns {undefined}
 */
function terminateSystem(ws, id, assignedHostList) {
  // 編集中に移行
  setState(STATE.EDIT);
  lockScreen();

  clearExecuteWasanbonLog();

  let ajaxList = [];
  for (let i = 0; i < assignedHostList.length; i++) {
    ajaxList.push(
      $.ajax({
        type: 'POST',
        url: getUrlTerminateSystem(),
        data: {
          ws: ws,
          rtsName: id,// idは先頭にrts_が含まれている
          hostId: assignedHostList[i]
        },
      }));
  }
  multipleAjaxAndChangeColors(ws, ajaxList, id, assignedHostList);
}

/**
 * Packageの実行状況を確認する
 * 
 * @param {*} ws ws
 * @param {*} curWorkspaceName curWorkspaceName
 * @param {*} hostId hostId
 * @returns {*} result
 */
function isRunningPackage(ws, curWorkspaceName, hostId) {
  return $.ajax({
    type: 'POST',
    url: getUrlIsRunningPackage(),
    data: { 
    ws: ws,
    rtsName: curWorkspaceName,// idは先頭にrts_が含まれている
    hostId: hostId
     }, 
    async: false
  }).responseJSON;
}

/*************************************************************************
 * ログ監視・モニタ監視
 *************************************************************************/
/**
 * ログ監視開始(keras用)
 * 
 * @returns {undefined}
 */
function startTailLog() {
  tailTimerID = setInterval(tailLog, 1000);
}

/**
 * ログ監視開始(local rt-system用)
 * 
 * @returns {undefined}
 */
function startLocalTailLog() {
  tailTimerID = setInterval(localTailLog, 1000);
}

/**
 * ログ監視終了(keras用)
 * 
 * @returns {undefined}
 */
function stopTailLog() {
  clearInterval(tailTimerID);
}

/**
 * ログ監視終了(local rt-system用)
 * 
 * @returns {undefined}
 */
function stopLocalTailLog() {
  clearInterval(tailTimerID);
}

/**
 * 最下部までスクロール
 * 
 * @param {*} obj obj
 * @returns {undefined}
 */
function scrollBottom(obj) {
  if (obj[0]) {
    obj.scrollTop(obj[0].scrollHeight);
  }
}

/**
 * ローカルログ監視
 * 
 * @returns {undefined}
 */
function localTailLog() {
  if ($('[name=console-scroll-check]').prop("checked") === true) {
    // 実行時のwasanbon.logを表示
    getExecuteWasanbonLog(function (content) {
      wasanbonLogViewer.setValue(content.replace(/[\x00-\x09\x0b-\x1f\x7f-\x9f]/g, ''));
      wasanbonLogViewer.setScrollTop(wasanbonLogViewer.getScrollHeight())
    });

    var url = getUrlTailLog();
    var xhr = new XMLHttpRequest();
    var param = 'ws=dev&hostId=local&workPackageName=' + curWorkspaceName;
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.responseType = 'blob';
    xhr.onload = function () {
      if (this.status == 200) {
        var blob = this.response;//レスポンス
        unzipFiles(blob, function (unzipped) {
          var importFileList = unzipped.getFilenames();

          for (var i in importFileList) {
            var logFileContent = new TextDecoder().decode(unzipped.decompress(importFileList[i]));
            if (importFileList[i] == 'rtc_py.log') {
              pythonLogViewer.setValue(logFileContent);
            }
          }
          pythonLogViewer.setScrollTop(pythonLogViewer.getScrollHeight(), logFileContent)
        });
      }
    };
    xhr.send(param);
  }
}

/**
 * リモートログ監視
 * 
 * @param {*} hostId hostId
 * @returns {undefined}
 */
function remoteTailLog(hostId) {
  var url = getUrlTailLog();
  var xhr = new XMLHttpRequest();
  var param = 'ws=exec&hostId=' + hostId + '&workPackageName=' + curWorkspaceName;
  xhr.open('POST', url, true);
  xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
  xhr.responseType = 'blob';
  xhr.onload = function () {
    if (this.status == 200) {
      var blob = this.response;//レスポンス
      unzipFiles(blob, function (unzipped) {
        var importFileList = unzipped.getFilenames();

        for (var i in importFileList) {
          var logFileContent = new TextDecoder().decode(unzipped.decompress(importFileList[i]));
          // ログファイルの中身が空でなければ、ダイアログに表示
          if (logFileContent != '') {
            addRemoteLogTab(hostId, importFileList[i], logFileContent);
          }
        }
      });
    }
  };
  xhr.send(param);
}
 
/**
 * 実行時のwasanbon.logを取得する
 * 
 * @returns {*} result
 */
function getExecuteWasanbonLog(callback) {
  $.ajax({
    type: 'GET',
    url: getUrlGetExecuteWasanbonLog(),
    async: false,
  }).done(function (content) {
    if (content) {
      callback(content);
    }
  });
}

/**
 * 実行時のwasanbon.logをクリアする
 *
 * @returns {*} result
 */
function clearExecuteWasanbonLog() {
  $.ajax({
    type: 'POST',
    url: getUrlClearExecuteWasanbonLog(),
    async: false,
  });
}


/*************************************************************************
 * ホスト関連
 *************************************************************************/
/**
 * ホスト名、または(IP, Port)が重複しないか判定する
 * 
 * @param {*} isWasanbon isWasanbon
 * @param {*} hostname hostname
 * @param {*} ip ip
 * @param {*} nsport nsport
 * @returns {*} result
 */
 function isHostNameUnique(isWasanbon, hostname, ip, nsport) {
  return $.ajax({
    type: 'POST',
    url: getUrlIsHostNameUnique(),
    data: { 
            isWasanbon: isWasanbon,
            'hostname': hostname,
            'ip': ip,
            'nsport': nsport,
          },
    async: false,
  }).responseText;
}


/**
 *  ホストIDが重複しないか判定する
 * 
 * @param {*} id id
 * @returns {boolean} ホストIDが重複しないか
 */
 function isHostIdUnique(id) {
  return $.ajax({
    type: 'POST',
    url: getUrlIsHostIdUnique(),
    data: { id: id },
    async: false,
  }).responseText;
}

/**
 * ホスト定義ファイルに新規ホストを追加する
 * 
 * @param {*} hostname hostname
 * @param {*} ip ip
 * @param {*} nsport nsport
 * @param {*} wwport wwport
 * @param {*} id id
 * @param {*} password password
 * @returns {*} ホストファイルに書き込みできたかどうか
 */
function registerHostToConfigFile(hostname, ip, nsport, wwport, id, password) {

  return $.ajax({
    type: 'POST',
    url: getUrlregisterHostToConfigFile(),
    data: { 'hostname': hostname,
            'IP': ip,
            'nsport': nsport,
            'wwport': wwport,
            'ID': id,
            'password': password
          },
    async: false,

  }).responseText;

}

/**
 * AirGraph用ホストを追加する
 * 
 * @param {*} hostname hostname
 * @param {*} ip ip
 * @param {*} port port
 * @returns {*} ホストファイルに書き込みできたかどうか
 */
function addAirGraphHost(hostname, ip, port) {

  return $.ajax({
    type: 'POST',
    url: getUrlAddAirGraphHost(),
    data: { 'hostname': hostname,
            'ip': ip,
            'port': port,
          },
    async: false,
  }).responseText;

}
/**
 * ホスト定義ファイルを更新する
 * 
 @param {*} requestBody requestBody
 @returns {boolean} 正常終了できたかどうか
 */
function updateHostConfigFile(requestBody) {

  return $.ajax({
    type: 'POST',
    url: getUrlUpdateHostConfigFile(),
    data: JSON.stringify(requestBody),
    contentType: 'application/json',
    dataType: 'json',
    scriptCharset: 'utf-8',
    async: false,
  }).responseText;

}

/**
 * ホスト定義ファイルを読み込む
 * 
 * @returns {*} result
 */
function loadHostList() {

  return $.ajax({
    type: 'GET',
    url: getUrlloadHostList(),
    async: false,
  }).responseJSON;

}

/**
 * GitHub設定ファイルを読み込む
 * 
 * @returns {*} result
 */
function getGitHubConfigFile(){

  return $.ajax({
    type: 'GET',
    url: getUrlGetGitHubConfigFile(),
    async: false,

  }).responseJSON;

}

/**
 * nameserverを起動する
 * 
 * @param {*} hostId hostId
 * @returns {*} result
 */
function startNameserver(hostId){

  return $.ajax({
    type: 'POST',
    url: getUrlStartNameserver(),
    data: {
    hostId: hostId,
  },

  }).responseJSON; 

}

/*************************************************************************
 * Binder関連
 *************************************************************************/
/**
 * Binderを作成する
 * 
 * @param {*} username username
 * @param {*} token token
 * @returns {*} result
 */
function createBinder(username, token){

  return $.ajax({
    type: 'POST',
    url: getUrlCreateBinder(),
    data: {
    username: username,
    token: token
  },
    async: false,

  }).responseJSON;

}

/**
 * Binderを更新する
 * 
 * @returns {undefined}
 */
function updateBinder() {
  $.ajax({
    type: 'POST',
    url: getUrlUpdateBinder(),
    async: false,
  });
}

/**
 * Binderにパッケージを追加する
 * @param {*} packageName packageName
 * @param {*} binderName binderName
 * @returns {*} result
 */
function addPackageToBinder(packageName, binderName) {

  return $.ajax({
    type: 'POST',
    url: getUrlAddPackageToBinder(),
    data: { packageName: packageName,
         binderName: binderName,
         hostId: 'local'
          },
    async: false,

  }).responseText;

}

/**
 * Binderのパッケージを更新する
 * @param {*} ws ws
 * @param {*} packageName packageName
 * @param {*} binderName binderName
 * @returns {*} result
 */
function updatePackageToBinder(ws, packageName, binderName) {

  return $.ajax({
    type: 'PUT',
    url: getUrlUpdatePackageToBinder(),
    data: {ws: ws, 
      packageName: packageName,
         binderName: binderName
          },
    async: false,

  }).responseText;

}

/**
 * BinderにRTCを追加する
 * 
 * @param {*} ws ws
 * @param {*} packageName packageName
 * @param {*} rtcName rtcName
 * @param {*} binderName binderName
 * @returns {*} result
 */
function addRtcToBinder(ws, packageName, rtcName, binderName) {

  return $.ajax({
    type: 'POST',
    url: getUrlAddRTCToBinder(),
    data: {ws: ws,  
        packageName: packageName,
      rtcName: rtcName,
       binderName: binderName  
          },
    async: false,

  }).responseText;

}

/**
 * BinderのRTCを更新する
 * 
 * @param {*} ws ws
 * @param {*} packageName packageName
 * @param {*} rtcName rtcName
 * @param {*} binderName binderName
 * @returns {*} result
 */
function updateRtcToBinder(ws, packageName, rtcName, binderName) {

  return $.ajax({
    type: 'PUT',
    url: getUrlUpdateRTCToBinder(),
    data: { ws: ws, 
      packageName: packageName,
         rtcName: rtcName,
         binderName: binderName
          },
    async: false,

  }).responseText;

}

/**
 * Binderをcommitする
 * 
 * @param {*} binderName binderName
 * @param {*} comment comment
 * @returns {*} result
 */
function commitBinder(binderName, comment) {

  return $.ajax({
    type: 'POST',
    url: getUrlCommitBinder(),
    data: {
    binderName: binderName,
    comment: comment
          },
    async: false,

  }).responseText;

}

/**
 * BinderをPushする
 * 
 * @param {*} binderName binderName
 * @param {*} comment comment
 * @returns {*} result
 */
function pushBinder(binderName, comment) {

  return $.ajax({
    type: 'POST',
    url: getUrlPushBinder(),
    data: {
    binderName: binderName,
    comment: comment
          },
    async: false,

  }).responseText;

}