/*************************************************************************
 * データ・セット関連
 *************************************************************************/
/**
 * データ・セットモニタを表示する
 */
function openDatasetListViewer() {
  // すでに表示している場合は何もしない
  if ($(document.getElementById('div-datasetlistviewer')).length > 0) {
    return;
  }

  // データセット名の一覧を取得
  var datasets = getDatasetChoices();
  var selectedDataset = '';
  var codeDirectory = '';

  // 本体
  var div = $('<div>');
  div.attr('id', 'div-datasetlistviewer').attr('title', 'Dataset Viewer');
  div.css('width', '800px').css('height', '300px');
  $('body').append(div);

  // パネルを表示
  var pstyle = 'border: 1px solid #dfdfdf; padding: 0px;';
  $('#div-datasetlistviewer').w2layout({
    name: 'layout-panel-dataset-listviewer',
    padding: 0,
    panels: [
      { type: 'top', size: 55, resizable: false, style: pstyle, content: $().w2form(createDatasetMonitorTop(datasets, selectedDataset)) },
      { type: 'left', size: 200, resizable: false, style: pstyle, content: $().w2sidebar(createDatasetMonitorLeft(selectedDataset, codeDirectory)) },
      { type: 'main', style: pstyle, content: $().w2grid(createDatasetMonitorMain()) },
      { type: 'right', size: 310, resizable: false, style: pstyle, content: "<div id = 'dataset-monitor-right'>" },
      { type: 'bottom', size: 150, resizable: false, style: pstyle, content: '' }
    ]
  });

  // 本体をモーダル表示
  $(div).dialog({
    resizable: false,
    height: 560,
    width: 1100,
    modal: false,
    close: function () {
      // 追加したデータセットモニタを削除
      if (w2ui['dataset-monitor-sidebar']) {
        w2ui['dataset-monitor-sidebar'].destroy();
      }
      if (w2ui['dataset-data-grid']) {
        w2ui['dataset-data-grid'].destroy();
      }
      w2ui['dataset-monitor-top'].destroy();
      w2ui['layout-panel-dataset-listviewer'].destroy();
      $(document.getElementById('div-datasetlistviewer')).remove();
    }
  });
}
/**
 * データセット名選択部分を表示
 * 
 * @param datasets
 * @param selectedDataset
 */
function createDatasetMonitorTop(datasets, selectedDataset) {
  // データセット選択部分を生成
  var form = {
    name: 'dataset-monitor-top',
    padding: 0,
    record: {
      dataset_name: selectedDataset,
    },
    fields: [
      { name: 'dataset_name', type: 'list', required: true, html: { caption: 'Dataset Name', attr: 'style="width:200px"' }, options: { items: datasets } },
    ],
    onChange: function (event) {
      if ($('#dataset_name').val()) {
        // サイドバーを表示
        updateDatasetMonitorLeft($('#dataset_name').val());
        // グリッド・画像も表示
        setDatasetMonitorGridData(null);
      }
    }
  };
  return form;
}

/**
 * ディレクトリ一覧部分を表示
 * 
 * @param  selectedDataset 
 * @param  codeDirectory 
 */
function createDatasetMonitorLeft(selectedDataset, codeDirectory) {
  // ツリーを生成する
  var rootNode = createMonitorSideBar(selectedDataset, codeDirectory, '');
  if (rootNode) {
    rootNode['group'] = true;
    // ツリーからサイドバーを生成する
    var sidebarData = {
      name: 'dataset-monitor-sidebar',
      nodes: [rootNode],
      onClick: function (event) {
        if (event.object) {
          var selectIds = event.object.id.replace(selectedDataset + '-AG-', '').split('-AG-');
          setDatasetMonitorGridData(selectIds);
        }
      }
    };
    return sidebarData;
  }
  return '';
}

/**
 * ディレクトリ一覧部分を更新
 * 
 * @param  selectedDataset 
 */
function updateDatasetMonitorLeft(selectedDataset) {
  // 一度削除
  if (w2ui['dataset-monitor-sidebar']) {
    w2ui['dataset-monitor-sidebar'].destroy();
  }

  // データセットのデータ一覧を取得
  var codeDirectory = JSON.parse(getDatasetDataList(selectedDataset));

  // ツリーを生成する
  var rootNode = createMonitorSideBar(selectedDataset, codeDirectory, '');
  rootNode['group'] = true;

  // ツリーからサイドバーを生成する
  var sidebarData = {
    name: 'dataset-monitor-sidebar',
    nodes: [rootNode],
    codeDirectory: codeDirectory,
    onClick: function (event) {
      if (event.object) {
        var selectIds = event.object.id.replace(selectedDataset + '-AG-', '').split('-AG-');
        setDatasetMonitorGridData(selectIds);
      }
    }
  };
  // 再設定
  if (w2ui['layout-panel-dataset-listviewer']) {
    w2ui['layout-panel-dataset-listviewer'].content('left', $().w2sidebar(sidebarData));
  }
}

/**
 * ディレクトリ一覧のサイドバーを作成する
 * 
 * @param selectedDataset 
 * @param codeDirectory 
 * @param prefix 
 */
function createMonitorSideBar(selectedDataset, codeDirectory, prefix) {
  if (codeDirectory) {
    // ルートディレクトリ
    var id = prefix + codeDirectory.curDirName;
    var node = { selectedDataset: selectedDataset, id: id, text: codeDirectory.curDirName, img: 'icon-folder', expanded: true, isCode: false, nodes: [] };
    if (codeDirectory.directoryMap) {
      // ディレクトリ名と配下の構成情報のMAP
      for (key in codeDirectory.directoryMap) {
        // 再帰的に呼び出す
        var nodeDir = createMonitorSideBar(selectedDataset, codeDirectory.directoryMap[key], id + '-AG-');
        if (nodeDir) {
          node.nodes.push(nodeDir);
        }
      }
    }
    return node;
  }
  return null;
}

/**
 * データ一覧部分表示
 */
function createDatasetMonitorMain() {
  var grid = {
    name: 'dataset-data-grid',
    columns: [
      { field: 'name', caption: 'file name', size: '70%', editable: false, sortable: true },
      { field: 'date', caption: 'update date', size: '30%', editable: false, sortable: true }
    ],
    onClick: function (event) {
      $('#dataset-monitor-right').empty();
      if (event.recid) {
        var filePath = w2ui['dataset-data-grid'].codePathMap[event.recid];
        if (filePath) {
          // ファイルが存在する
          $('#dataset-monitor-right').append("<img id='img-datasetmonitor' style='width:300px; height:300px'></img>");
          getDatasetImage($(document.getElementById('img-datasetmonitor')), filePath);
        }
      }
    }
  };
  return grid;
}

/**
 * グリッド・モニタ部分にデータを設定する
 * 
 * @param  selectedIds 
 */
function setDatasetMonitorGridData(selectedIds) {
  var dataArray = [];

  var codeDirectory = w2ui['dataset-monitor-sidebar'].codeDirectory;
  var directoryMap = codeDirectory.directoryMap;
  if (selectedIds) {
    for (let i = 0; i < selectedIds.length; i++) {
      codeDirectory = directoryMap[selectedIds[i]];
      directoryMap = codeDirectory.directoryMap;
    }
  }
  var codePathMap = codeDirectory.codePathMap;
  var lastModifiedMap = codeDirectory.lastModifiedMap;

  if (Object.keys(codePathMap).length > 0) {
    for (key in codePathMap) {
      var record = { recid: key, name: key, date: lastModifiedMap[key] };
      dataArray.push(record);
    }
  }
  w2ui['dataset-monitor-sidebar'].dirPath = codeDirectory.dirPath;

  if (w2ui['dataset-data-grid']) {
    // グリッドの場合
    // 一度クリアする
    w2ui['dataset-data-grid'].clear();

    w2ui['dataset-data-grid']['codePathMap'] = null;
    if (dataArray.length > 0) {
      // Gridに追加する
      w2ui['dataset-data-grid'].add(dataArray);
      w2ui['dataset-data-grid']['codePathMap'] = codePathMap;
    }
  }
}



/**
 * データセットデータ一覧取得
 * 
 * @param datasetName 
 */
function getDatasetDataList(datasetName) {
  var result = $.ajax({
    type: 'POST',
    url: getUrlGetDatasetDataList(),
    data: { 'datasetName': datasetName },
    async: false
  }).done(function () {
  }).responseText;
  return result;
}

/**
 * データセットモニタを表示する
 * @returns
 */
function openDatasetMonitor() {
  // 対象のコネクタのモニタが存在する場合は何もしない
  if ($(document.getElementById('div-datasetmonitor')).length > 0) {
    return;
  }

  // データセット名の一覧を取得
  var datasets = getDatasetChoices();
  var selectedDataset = '';
  var codeDirectory = '';

  // 本体
  var div = $('<div>');
  div.attr('id', 'div-datasetmonitor').attr('title', 'Dataset Viewer');
  div.css('width', '400px').css('height', '300px');
  $('body').append(div);

  // パネルを表示
  var pstyle = 'border: 1px solid #dfdfdf; padding: 0px;';
  $('#div-datasetmonitor').w2layout({
    name: 'layout-panel-dataset-listviewer',
    padding: 0,
    panels: [
      { type: 'top', size: 55, resizable: false, style: pstyle, content: $().w2form(createDatasetMonitorTop(datasets, selectedDataset)) },
      { type: 'left', size: 200, resizable: false, style: pstyle, content: $().w2sidebar(createDatasetMonitorLeft(selectedDataset, codeDirectory)) },
      {
        type: 'right', size: 310, resizable: false, style: pstyle,
        content: "<div id = 'dataset-monitor-right'><img id='img-datasetmonitor' style='width:300px; height:300px'></img></div>"
      },
    ]
  });

  // 画像取得開始
  var datasetTimerID = startTailDataset($(document.getElementById('img-datasetmonitor')));

  $(div).dialog({
    resizable: false,
    height: 410,
    width: 540,
    modal: false,
    close: function () {
      // 追加したデータセットモニタを削除
      if (w2ui['dataset-monitor-sidebar']) {
        w2ui['dataset-monitor-sidebar'].destroy();
      }
      if (w2ui['dataset-data-grid']) {
        w2ui['dataset-data-grid'].destroy();
      }
      w2ui['dataset-monitor-top'].destroy();
      w2ui['layout-panel-dataset-listviewer'].destroy();
      $(document.getElementById('div-datasetmonitor')).remove();
      stopTailDataset(datasetTimerID);
    }
  });
}

/**
   * データセット監視開始
   * @param elm
   * @returns
   */
function startTailDataset(elm) {
  return setInterval(function () { tailDataset(elm) }, 1000);
}

/**
 * データ・セット監視終了
 * @param id
 * @returns
 */
function stopTailDataset(id) {
  clearInterval(id);
}

/**
 * データセット監視
 * @param elm
 * @returns
 */
function tailDataset(elm) {
  if (w2ui['dataset-monitor-sidebar']) {
    var dirPath = w2ui['dataset-monitor-sidebar'].dirPath;
    var url = getUrlTailImage() + '?imageDirectoryPath=' + dirPath + '&date=' + new Date().getTime();
    $(elm).attr('src', url);
  }
}

/**
 * データセットの画像取得
 * @param elm
 * @returns
 */
function getDatasetImage(elm, filePath) {
  if (w2ui['dataset-monitor-sidebar']) {
    var url = getUrlGetImage() + '?imageFilePath=' + filePath;
    $(elm).attr('src', url);
  }
}
