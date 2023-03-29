/*************************************************************************
 * 作業領域・Package関連
 *************************************************************************/
/**
 * モデル・レイヤー情報を取得し展開する.
 *
 * @returns {undefined}
 */
function loadAllNetworkArea() {
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'GET',
    url: getUrlLoadNetworkArea()
  }).done(function (resData) {
    if (resData) {
      // ネットワーク領域取得
      setNetworkAreaInfo(resData);
      // プロパティ情報取得
      loadAllLayerPropertyTemplates();
      // 作業領域も合わせて取得する
      loadAllWorkspace();
      // TODO:
      if (curWorkspaceName == null || modelMap[curWorkspaceName] == null) {
        unlockScreen();
        return;
      }
      // モデルを更新
      var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
      loadPackageWorkspace(model);

      // ワークスペースセレクタを更新
      setWorkspaceSelectMenu();
      setWorkingAccordionInfo();

      // プロパティ表示の更新
      setPropertyAreaForModel();
    }
    // 画面ロック解除
    unlockScreen();
  });
}

/**
 * レイヤープロパティ設定情報を取得し展開する.
 *
 * @returns {undefined}
 */
function loadAllLayerPropertyTemplates() {
  // 画面をロック
  lockScreen();
  $.ajax({
    type: 'GET',
    url: getUrlLoadAllLayerPropertyTemplates()
  }).done(function (resData) {
    if (resData) {
      resData.forEach(function (propJson) {
        var prop = JSON.parse(propJson);
        templateLayerPropertyMap[prop.class_name] = prop;
      });
    }
    // 画面ロック解除
    unlockScreen();
  });
}


/**
 * 作業領域を取得し展開する.
 *
 * @returns {undefined}
 */
function loadAllWorkspace() {
  $.ajax({
    type: 'GET',
    url: getUrlLoadAllWorkspace()
  }).done(function (resData) {
    if (resData && resData.length > 0) {
      workspaceCounter = 1;
      while (templateModelMap[WORKSPACE_PREFIX + workspaceCounter] != null) {
        workspaceCounter++;
      }
      loadAllPackagesWorkspace(resData);
    } else {
      // 失敗した場合は中止
      curWorkspaceName = null;
      return;
    }
  });
}

/**
 * 現在編集中のモデルを保存する.
 *
 * @returns {undefined}
 */
function saveModel() {
  $.ajax({
    type: 'POST',
    url: getUrlSaveModel(),
    data: { 'dirName': modelMap[curWorkspaceName].dirName, 'json': JSON.stringify(modelMap[curWorkspaceName]) },
  }).done(function () {
    // 再読み込み
    loadAllNetworkArea();
  });
}

/**
 * 現在編集中のモデルを削除する.
 *
 * @returns {undefined}
 */
function deleteModel() {
  $.ajax({
    type: 'POST',
    url: getUrlDeleteModel(),
    data: { 'modelName': modelMap[curWorkspaceName].modelName },
  }).done(function () {
    // 保存したモデルをワークエリアから削除し、モデルを切り替え
    delete modelMap[curWorkspaceName];
    if (Object.keys(modelMap).length != 0) {
      curWorkspaceName = Object.keys(modelMap)[0];
    } else {
      curWorkspaceName = null;
    }
    // 再読み込み
    loadAllNetworkArea();
  });
}

/**
 * データセットの一覧を取得する.
 * 
 * @returns {json} レスポンス
 */
function getDatasetChoices() {
  return $.ajax({
    type: 'POST',
    url: getUrlDatasetChoices(),
    async: false
  }).responseJSON;
}

/**
 * AirGraphホストの一覧を取得する.
 *
 * @returns {json} レスポンス
 */
function getAirGraphHostChoices() {
  return $.ajax({
    type: 'POST',
    url: getUrlAirGraphHostChoices(),
    async: false
  }).responseJSON;
}

/**
 * 指定されたロボットのデータセットの一覧を取得する.
 *
 * @param {*} robotHostName ホスト名
 * @returns {json} レスポンス
 */
function getRobotDatasetChoices(robotHostName) {
  return $.ajax({
    type: 'POST',
    url: getUrlRobotDatasetChoices(),
    data: {"robotHostName" : robotHostName},
    async: false
  }).responseJSON;
}

/**
 * 指定されたロボットのデータセットを取得する.
 *
 * @param {*} robotHostName ホスト名
 * @param {*} datasetName データセット名
 * @returns {undefined}
 */
function getRobotDatasets(robotHostName, datasetName) {
  lockScreen();
  $.ajax({
    type: 'POST',
    url: getUrlRobotDatasets(),
    data: {
      "robotHostName" : robotHostName,
      "datasetName" : datasetName
    }
  }).done(function () {
    // データセット名の一覧を取得
    var datasets = getDatasetChoices();
    w2ui['dataset-monitor-top'].fields[0].options.items = datasets;
    w2ui['dataset-monitor-top'].refresh();

    unlockScreen();
  });
}

/*************************************************************************
 * Keras実行
 *************************************************************************/
/**
 * Kerasで学習を実行する.
 *
 * @returns {undefined}
 */
function runKerasFit() {
  openConsoleLog();
  $.ajax({
    type: 'POST',
    url: getUrlRunKerasFit(),
    data: JSON.stringify(modelMap[curWorkspaceName]),
    contentType: 'application/json',
    dataType: 'json',
    scriptCharset: 'utf-8'
  }).done(function () {
  });
}

/**
 * DataMakerファイルをアップロードする.
 *
 * @returns {undefined}
 */
function uploadDataMakerFile() {
  var formData = new FormData(
    $('#dataMakerUploadForm').get()[0]
  );

  $.ajax({
    url: getUrlDataMakerUpload(),
    method: 'post',
    data: formData,
    processData: false,
    contentType: false,
    cache: false
  }).done(function (data, status, jqxhr) {

  }).fail(function (data, status, jqxhr) {

  });
}

/**
 * Datasetをアップロードする.
 *
 * @returns {undefined}
 */
function uploadDataset() {
  var formData = new FormData(
    $('#datasetUploadForm').get()[0]
  );

  $.ajax({
    url: getUrlDatasetUpload(),
    method: 'post',
    data: formData,
    processData: false,
    contentType: false,
    cache: false
  }).done(function (data, status, jqxhr) {

  }).fail(function (data, status, jqxhr) {

  });
}

/*************************************************************************
 * ログ監視・モニタ監視
 *************************************************************************/
/**
 * ログ監視開始.
 *
 * @returns {undefined}
 */
function startTailLog() {
  tailTimerID = setInterval(tailLog, 1000);
}

/**
 * ログ監視終了.
 *
 * @returns {undefined}
 */
function stopTailLog() {
  clearInterval(tailTimerID);
}

/**
 * 最下部までスクロール.
 *
 * @param {*} obj object
 * @returns {undefined}
 */
function scrollBottom(obj) {
  if (obj[0]) {
    obj.scrollTop(obj[0].scrollHeight);
  }
}

/**
 * ログ監視.
 * TODO: keras用に改変.
 *
 * @returns {undefined}
 */
function tailLog() {
  if ($('[name=console-scroll-check]').prop("checked") === true) {
    $.ajax({
      type: 'GET',
      url: getUrlTailLog(),
      data: { 'workPackageName': curWorkspaceName }
    }).done(function (log) {
      if (log) {
        $('#console-text').html(log['keras'].replace(/[\x00-\x09\x0b-\x1f\x7f-\x9f]/g, ' '));
        scrollBottom($('#console-text'));
      }
    });
  }
}
