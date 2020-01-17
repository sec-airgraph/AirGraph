/*************************************************************************
 * 初期レイアウト処理
 *************************************************************************/
/**
 * 全体初期レイアウト処理
 */
function layoutPanelAll() {
  var pstyle = 'border: 1px solid #dfdfdf; padding: 0px;';
  $('#layout-panel').w2layout({
    name: 'layout-panel',
    padding: 0,
    panels: [
      { type: 'top', size: 30, resizable: false, style: pstyle, content: createToolBarPanel(), overflow: 'visible' },
      { type: 'left', size: 300, resizable: true, style: pstyle, content: createNetworkPanel() },
      { type: 'main', style: pstyle, content: createMainPanel() },
      { type: 'preview', size: '30%', resizable: true, style: pstyle, content: createJsonPanel(), overflow: 'hidden' },
      { type: 'right', size: 380, resizable: true, style: pstyle, content: createPropertyPanel() },
      { type: 'bottom', size: 30, resizable: false, style: pstyle, content: createFooterPanel() }
    ]
  });
}

/**
 * 作業領域作成処理
 */
function createMainPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'main-panel');
  panel.css('position', 'absolute').css('width', '100%').css('height', '100%');

  var jointArea = $('<div>');
  jointArea.attr('id', 'main-joint-area');
  jointArea.css('position', 'absolute').css('width', '100%').css('height', '100%').css('overflow', 'scroll');
  panel.append(jointArea);

  return panel;
}

/**
 * ツールバー領域作成処理
 */
function createToolBarPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'toolbar-panel');
  panel.css('padding-left', '10px');

  return panel;
}

/**
 * ネットワーク領域作成処理
 */
function createNetworkPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'network-panel');

  // 検索窓追加
  var searchDiv = $('<div>');
  $('<input>').attr({
    type: 'text',
    id: 'network-search'
  }).css({
    margin: '5px',
    width: '75%'
  }).appendTo(searchDiv);

  // 検索ボタン追加
  var searchBtn = $('<button>');
  searchBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '3px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-search').appendTo(searchBtn);
  searchDiv.append(searchBtn);
  panel.append(searchDiv);

  return panel;
}

/**
 * プロパティ領域作成処理
 */
function createPropertyPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'property-panel');
  return panel;
}

/**
 * JSON領域作成処理
 */
function createJsonPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'json-panel');
  panel.css('position', 'absolute').css('width', '100%').css('height', '100%');

  return panel;
}

/**
 * フッター領域作成処理
 */
function createFooterPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'footer-panel');

  // ネットワークボタン追加
  var networkBtn = $('<button type="button">');
  networkBtn.attr('id', 'network-button');
  networkBtn.html('Network')
  networkBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '110px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-circlesmall-minus').appendTo(networkBtn);
  $(networkBtn).on('click', function (event) {
    toggleNetworkPanel('network-button', 'left');
  });
  panel.append(networkBtn);

  // プロパティボタン追加
  var propertyBtn = $('<button type="button">');
  propertyBtn.attr('id', 'property-button');
  propertyBtn.html('Property')
  propertyBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '110px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-circlesmall-plus').appendTo(propertyBtn);
  $(propertyBtn).on('click', function (event) {
    toggleNetworkPanel('property-button', 'right');
  });
  panel.append(propertyBtn);

  // Jsonボタン追加
  var jsonBtn = $('<button type="button">');
  jsonBtn.attr('id', 'json-button');
  jsonBtn.html('JSON')
  jsonBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '110px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-circlesmall-minus').appendTo(jsonBtn);
  $(jsonBtn).on('click', function (event) {
    toggleNetworkPanel('json-button', 'preview');
  });
  panel.append(jsonBtn);

  // 作業領域選択
  var workspaceSelect = $('<select>');
  workspaceSelect.attr('id', 'workspace-selector');
  workspaceSelect.css('float', 'right').css('height', '27px');
  $(workspaceSelect).on('change', function () {
    // 作業領域設定
    curWorkspaceName = $(this).val();
    // 一旦画面からすべてを削除
    deleteAllLayersViewObject();
    // 作業領域再読み込み
    var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
    loadPackageWorkspace(model);
    // プロパティ表示を更新
    setPropertyAreaForModel();
  });

  panel.append(workspaceSelect);

  return panel;
}

/*******************************************************************************
 * ツールバー領域
 ******************************************************************************/

/**
 * ツールバーを生成する
 */
function setToolbarComponent() {
  $('#toolbar-panel').w2toolbar({
    name: 'toolbar',
    items: [
      {
        type: 'menu', id: 'file-menu', caption: 'File', icon: 'fa fa-files-o',
        items: [
          { type: 'menu', id: 'save-menu', caption: 'Save This Model', icon: 'fa fa-floppy-o' }
        ]
      },
      {
        type: 'menu', id: 'tool-menu', caption: 'Tools', icon: 'fa fa-tasks',
        items: [
          { type: 'menu', id: 'learn-menu', caption: 'Lean', icon: 'fa fa-refresh' }
        ]
      },
      {
        type: 'menu', id: 'setting-menu', caption: 'Dataset', icon: 'fa fa-book',
        items: [
          { type: 'menu', id: 'dataset-menu', caption: 'Open Dataset Viewer', icon: 'fa fa-television' },
          { type: 'menu', id: 'upload-menu', caption: 'Upload Dataset', icon: 'fa fa-upload' },
          { type: 'menu', id: 'download-menu', caption: 'Download Dataset', icon: 'fa fa-download' }
        ]
      },
    ],
    onClick: function (event) {
      // クリックされた時のイベント
      if (event.subItem) {
        switch (event.subItem.id) {
          case 'save-menu':
            //  保存
            saveModel();
            break;
          case 'learn-menu':
            runKerasFit();
            break;
          case 'dataset-menu':
            // データセットビューワ表示
            openDatasetListViewer();
            break;
          case 'upload-menu':
            w2confirm('ZIPファイルの上位ディレクトリをデータセット名として利用します。</br>すでに存在するデータセットは削除されますがよろしいですか？', function (btn) {
              if (btn === 'Yes') {
                $('#dataset-upload').val("");
                $('#workspace-model-name-dataset').val(curWorkspaceName);
                // ファイルアップロード画面を開く
                $('#dataset-upload').click();
              }
            });
            break;
          case 'download-menu':
            if (!modelMap[curWorkspaceName].dataset) {
              // データセットが設定されていない
              w2alert('データセットが選択されていません');
            } else {
              $('#workspace-model-name-dataset').val(curWorkspaceName);
              $('#datasetUploadForm').attr('action', getUrlDatasetDownload());
              $('#datasetUploadForm').submit();
            }
            break;
          default:
            // NOP
            break;
        }
      }
    }
  })
}

/**
 * 作業領域の選択肢を設定する
 * 
 * @returns
 */
function setWorkspaceSelectMenu() {
  if (curWorkspaceName && modelMap) {
    var options = [];
    for (key in modelMap) {
      var option = $('<option>').html(key);
      if (curWorkspaceName === key) {
        option.attr('selected', 'selected');
      }
      options.push(option);
    }
    $('#workspace-selector').empty().append(options);
  } else {
    $('#workspace-selector').empty();
  }
}
/*******************************************************************************
 * ネットワーク領域
 ******************************************************************************/
/**
 * ネットワーク領域のアコーディオン設定
 * @param networkAreaInfo 領域情報
 * @returns
 */
function setNetworkAreaInfo(networkAreaInfo) {
  // ネットワーク領域アコーディオンを生成する
  var layerTabs = networkAreaInfo.kerasTabs;
  $('#network-panel').empty();
  $('#network-panel').append(createNetworkAccordion(layerTabs, 'graph'));
  // ネットワーク領域アコーディオンを有効化する
  $('.accordion').accordion({
    collapsible: true,
    heightStyle: 'content'
  });
}

/**
 * 作業領域タブを作成する
 * @returns
 */
function setWorkingAccordionInfo() {
  if ($('#network-panel').children(0).length > 1) {
    $('#network-panel').children(0)[0].remove();
  }
  // ネットワーク領域アコーディオンを生成する
  var modelArray = Object.keys(modelMap).map(function (key) { return modelMap[key]; });
  var workingTabs = [{ tabName: 'Work', models: modelArray, layers: null, childTabs: null }];
  $('#network-panel').prepend(createNetworkAccordion(workingTabs, 'graph'));
  // ネットワーク領域アコーディオンを有効化する
  $('.accordion').accordion({
    collapsible: true,
    heightStyle: 'content'
  });
  // 編集対象以外のモデル色を設定
  networkAreaGraph.getCells().forEach(function (cell) {
    cell.attr({
      rect: {
        rx: 2, ry: 2,
        fill: {
          type: 'linearGradient',
          stops: [
            { offset: '0%', color: 'lightblue' },
            { offset: '100%', color: 'deepskyblue' }
          ],
          attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
        }
      }
    });
  });

  // 編集対象のモデル色を設定
  networkAreaGraph.getCell(curWorkspaceName).attr({
    rect: {
      rx: 2, ry: 2,
      fill: {
        type: 'linearGradient',
        stops: [
          { offset: '0%', color: 'deepskyblue' },
          { offset: '100%', color: 'mediumblue' }
        ],
        attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
      }
    }
  });
}


/**
 * ネットワーク領域のアコーディオン作成処理
 * 
 * @param 領域情報
 * @param 親領域名称
 */
function createNetworkAccordion(tabs, parentName) {
  // アコーディオン親
  var cmpAc = $('<div>');
  cmpAc.attr('class', 'accordion');

  if (tabs && tabs.length > 0) {
    for (var i = 0; i < tabs.length; i++) {
      var section = $('<h3>');
      // 表示名
      var tabName = tabs[i].tabName;
      section.html(tabName);

      cmpAc.append(section);

      // 描画領域作成
      var jointArea;
      // 子供領域存在チェック
      var childTabs = tabs[i].childTabs;
      if (childTabs && childTabs.length > 0) {
        // 子供領域が存在する場合 -> 子供領域についてアコーディオン生成
        jointArea = createNetworkAccordion(childTabs, tabName);
      } else {
        // 子供領域が存在しない場合 -> 描画領域生成
        var divName = parentName + '-' + tabName;
        jointArea = $('<div>');
        jointArea.attr('id', divName);
        jointArea.attr('class', 'joint-area');

        // 描画オブジェクト生成
        createNetworkGraphArea(jointArea, tabs[i].models, tabs[i].layers);
      }
      cmpAc.append(jointArea);
    }
  }
  return cmpAc;
}

/**
 * ネットワーク領域上にモデルを描画
 * @param name 作成対象のモデル名
 * @param posX X座標
 * @param posY Y座標
 * @returns
 */
function createModelViewObject(name, posX, posY) {
  var fontSize = 14;
  var textX = 35;
  var textY = 2;
  var textAnchor = 'left';
  var width = 20;
  var height = 20;

  // Model作成
  var model = new joint.shapes.devs.Model({
    id: name,
    type: 'Model',
    position: { x: posX, y: posY },
    size: { width: width, height: height },
    inPorts: [],
    outPorts: [],
    attrs: {
      '.label': {
        text: name,
        'ref-x': textX,
        'ref-y': textY,
        'font-size': fontSize,
        'font-family': 'sans-serif',
        'font-weight': 'normal',
        'text-anchor': textAnchor
      },
      rect: {
        rx: 2, ry: 2,
        fill: {
          type: 'linearGradient',
          stops: [
            { offset: '0%', color: 'aquamarine' },
            { offset: '100%', color: 'seagreen' }
          ],
          attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
        },
        'model-type': 'Model'
      }
    }
  });

  // 影をつける
  model.attr('rect/filter', { name: 'dropShadow', args: { dx: 2, dy: 2, blur: 3 } });

  return model;
}

/**
 * ネットワーク領域・作業領域上にレイヤーを描画
 * @param name 作成対象のレイヤー名
 * @param posX X座標
 * @param posY Y座標
 * @param onWorkspace 対象が作業領域であるかどうか
 * @returns
 */
function createLayerViewObject(name, posX, posY, onWorkspace, color) {
  // ネットワークパネル上と作業領域上で描画レイアウトを切り替え
  var fontSize = 14;
  var textX = 35;
  var textY = 2;
  var textAnchor = 'left';
  var width = 20;
  var height = 20;
  var labelText = name;
  if (color == null || color == undefined) {
    color = ['gold', 'darkorange'];
  }
  if (onWorkspace) {
    textX = 0;
    //textY = - fontSize - 2;
    textY = 2;
    textAnchor = 'left';
    width = 80;
    height = 150;

    //クラス名・必要プロパティの表示
    var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
    if (model.class_name == 'Sequential') {
      labelText = joint.util.breakText(model.config.find(l => l.config.name == name).class_name, { width: 90 });
    } else {
      labelText = joint.util.breakText(model.config.layers.find(l => l.config.name == name).class_name, { width: 90 });
    }
    labelText += '\n\n[name]\n' + joint.util.breakText(name, { width: 90 });
  }

  // Layer作成
  var layer = new joint.shapes.devs.Model({
    id: name + (onWorkspace ? '' : '_template'),
    type: 'Layer',
    position: { x: posX, y: posY },
    size: { width: width, height: height },
    inPorts: [],
    outPorts: [],
    attrs: {
      '.label': {
        text: labelText,
        'ref-x': textX,
        'ref-y': textY,
        'font-size': fontSize,
        'font-family': 'sans-serif',
        'font-weight': 'normal',
        'text-anchor': textAnchor
      },
      rect: {
        rx: 2, ry: 2,
        fill: {
          type: 'linearGradient',
          stops: [
            { offset: '0%', color: color[0] },
            { offset: '100%', color: color[1] },
          ],
          attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
        },
        'model-type': 'Layer'
      }
    }
  });

  // レイヤー移動時にmainPaperのサイズを再計算
  layer.on('change:position', function () {
    mainPaper.fitToContent({ padding: 50 });
  });

  // 影をつける
  layer.attr('rect/filter', { name: 'dropShadow', args: { dx: 2, dy: 2, blur: 3 } });

  return layer;
}

/**
 * ネットワークGraph領域作成処理
 * 
 */
function createNetworkGraphArea(areaElm, models, layers) {
  var height = 35;
  var modelsSize = 0;
  var allSize = 0;
  if (models && models.length > 0) {
    modelsSize = models.length;
    allSize += models.length;
  }
  if (layers && layers.length > 0) {
    allSize += layers.length;
  }
  if (allSize > 0) {
    height = 35 * allSize;
  }

  networkAreaGraph = new joint.dia.Graph;
  var paper = new joint.dia.Paper({
    el: areaElm,
    width: '100%',
    height: height,
    gridSize: 1,
    model: networkAreaGraph,
    interactive: false
  });

  paper.on('cell:pointerdown', function (cellView, e, x, y) {
    dragLayer(cellView, e, x, y);
  });

  if (layers && layers.length > 0) {
    for (var i = 0; i < layers.length; i++) {
      var parsedLayer = JSON.parse(layers[i]);
      // mapに保持しておく
      templateLayerMap[parsedLayer.class_name] = parsedLayer;
      if (parsedLayer.color) {
        // 描画オブジェクト作成
        networkAreaGraph.addCell(createLayerViewObject(
          parsedLayer.class_name,
          10, (i + modelsSize) * 35 + 10,
          false, parsedLayer.color));
      } else {
        // 描画オブジェクト作成
        networkAreaGraph.addCell(createLayerViewObject(
          parsedLayer.class_name,
          10, (i + modelsSize) * 35 + 10,
          false));
      }
    }
  }

  if (models && models.length > 0) {
    for (var i = 0; i < models.length; i++) {
      // mapに保持しておく
      templateModelMap[models[i].modelName] = models[i];
      // 描画オブジェクト作成
      networkAreaGraph.addCell(createModelViewObject(models[i].modelName, 10, i * 35 + 10));
    }
  }
}

/**
 * ネットワーク領域から作業領域へのドラッグ処理
 * 
 * @param cellView
 * @param e
 * @param x
 * @param y
 * @returns
 */
function dragLayer(cellView, e, x, y) {
  // Drag用のオブジェクトを追加する
  $('body').append('<div id="flyPaper" style="position:fixed;z-index:1000;opacity:.7;pointer-event:none;"></div>');
  var flyGraph = new joint.dia.Graph;
  var flyPaper = new joint.dia.Paper({ el: $('#flyPaper'), model: flyGraph, interactive: false, width: 300, height: 35 });
  var flyShape = cellView.model.clone();
  for (var i = 0; i < flyShape.portData.ports.length; i++) {
    flyShape.portData.ports[i].attrs['.port-body'].r = 3;
    flyShape.portData.ports[i].attrs['.port-body'].fill = '#c6c6c6';
    flyShape.portData.ports[i].attrs['.port-label'].text = '';
  }
  var pos = cellView.model.position();
  var offset = { x: x - pos.x, y: y - pos.y };
  var targetId = cellView.model.id;

  flyShape.position(10, 10);
  flyGraph.addCell(flyShape);
  $('#flyPaper').offset({ left: e.pageX - offset.x, top: e.pageY - offset.y });

  // マウス移動イベントを設定
  $('body').on('mousemove.fly', function (e) {
    $('#flyPaper').offset({ left: e.pageX - offset.x, top: e.pageY - offset.y });
  });

  // マウスアップイベントを設定
  $('body').on('mouseup.fly', function (e) {
    var x = e.pageX;
    var y = e.pageY;
    var target = $('#main-joint-area').offset();

    if (x > target.left && x < target.left + $('#main-joint-area').width() && y > target.top && y < target.top + $('#main-panel').height()) {
      // 対象の領域内でマウスが離された場合
      dropLayer(x, y, offset, target, cellView.model.id, cellView.model.attributes.type);
    }
    // イベントを解除
    $('body').off('mousemove.fly').off('mouseup.fly');

    // 追加したオブジェクトを削除
    flyShape.remove();
    $('#flyPaper').remove();
  });
};

/**
 * ネットワーク領域から作業領域へのドロップ処理
 * 
 * @param x
 * @param y
 * @param offset
 * @param target
 * @param modelId
 * @returns
 */
function dropLayer(x, y, offset, target, name, type) {
  if (type == 'Model') {
    // Modelをドロップされた場合

    // 一旦画面からすべてを削除
    deleteAllLayersViewObject();
    selectedCellViews = [];

    if (modelMap[name] == null) {
      // 作業領域を追加する
      curWorkspaceName = WORKSPACE_PREFIX + workspaceCounter;
      modelMap[curWorkspaceName] = $.extend(true, {}, templateModelMap[name]);
      modelMap[curWorkspaceName].modelName = curWorkspaceName;
      var newModel = JSON.parse(modelMap[curWorkspaceName].jsonString);
      if (newModel.class_name == 'Model') {
        // Functional APIの場合はモデル名をJSON側にも適用
        newModel.config.name = curWorkspaceName;
        modelMap[curWorkspaceName].jsonString = JSON.stringify(newModel, null, '\t');
      }
      workspaceCounter++;
      while (templateModelMap[WORKSPACE_PREFIX + workspaceCounter] != null ||
        modelMap[WORKSPACE_PREFIX + workspaceCounter] != null) {
        workspaceCounter++;
      }
    } else {
      curWorkspaceName = name;
    }

    // Packageを追加して読み込み直す
    // 画面をロック
    lockScreen();
    var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
    loadPackageWorkspace(model);

    // モデルのプロパティを表示
    setPropertyAreaForModel();

    // 作業領域の選択肢反映
    setWorkspaceSelectMenu();
    setWorkingAccordionInfo();

    unlockScreen();
  }

  if (type == 'Layer') {
    // モデルにレイヤーを追加
    addLayer(name);
    // レイヤーのプロパティを表示
    setPropertyAreaForLayer();
  }
};

/**
 * 作業領域へのレイヤー追加
 * @param layerName
 * @returns
 */
function addLayer(layerName) {
  // 画面をロック
  lockScreen();

  // 現在のモデルが指定されていない場合は新規モデルを作成
  var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
  var layers = [];
  if (model.class_name == 'Sequential') {
    layers = model.config;
  } else {
    layers = model.config.layers;
  }

  // レイヤー名称からレイヤーを取得し、モデルに追加
  var className = layerName.replace(/_template/g, '');
  var newLayer = templateLayerMap[className];
  var sameClassLayers = layers.filter(l => l.class_name == className);
  var classCount = 1 + sameClassLayers.length;
  // 名前が重複している場合はカウントを上げる
  sameClassLayers.forEach(function (element, index, array) {
    if (element.config.name == className.toLowerCase() + '_' + classCount) {
      classCount++;
    }
  });
  newLayer.config.name = className.toLowerCase() + '_' + classCount;
  if (model.class_name == 'Model') {
    newLayer.name = newLayer.config.name;
    // 入出力レイヤーを指定
    newLayer['inbound_nodes'] = [[]];
    if (layers.length != 0) {
      // 出力レイヤーの１つを入力ノードとして設定
      if (model.config.output_layers.length != 0) {
        newLayer.inbound_nodes[0].push([model.config.output_layers[0][0], 0, 0, {}]);
      }
    }
    // モデルにレイヤーを追加
    model.config.layers.push(newLayer);
  } else {
    // モデルにレイヤーを追加
    model.config.push(newLayer);
  }
  // 入出力レイヤーを再設定
  model = reconfigureInputAndOutputLayers(model);

  // モデルのJSONも更新
  modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');
  loadPackageWorkspace(model);

  unlockScreen();
}

/*******************************************************************************
 * 作業領域
 ******************************************************************************/
/**
 * 作業Graph領域作成処理
 * 
 * @param 親DIVタグ
 */
function createMainGraphArea() {
  mainGraph = new joint.dia.Graph;
  mainPaper = new joint.dia.Paper({
    el: $('#main-joint-area'),
    width: '100%',
    height: '100%',
    gridSize: 1,
    model: mainGraph,
    drawGrid: true,
    clickThreshold: 1,
    defaultLink: new joint.dia.Link({
      // 接続線の形状を設定
      connector: { name: 'smooth' },
      attrs: { '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' } }
    }),
    // 折れ線の大きさ
    snapLinks: { radius: 40 },
    // 接続時の表示有効
    markAvailable: true,
    // ダブルクリックで折れ線作成
    linkView: joint.dia.LinkView.extend({
      pointerdblclick: function (evt, x, y) {
        if (V(evt.target).hasClass('connection') || V(evt.target).hasClass('connection-wrap')) {
          this.addVertex({ x: x, y: y });
        }
      }
    }),
    // シングルクリックで折れ線作成禁止（右クリックメニューのため）
    interactive: function (cellView) {
      if (cellView.model instanceof joint.dia.Link) {
        return { vertexAdd: false };
      }
      return true;
    }
  });

  // サイズ調整
  mainPaper.fitToContent({ padding: 50 });

  // 作業領域イベント関連付
  setEventMainPaper();
}

/**
 * 作業領域イベント関連付
 * 
 * @returns
 */
function setEventMainPaper() {
  // コンポーネントクリック
  mainPaper.off('cell:pointerclick');
  mainPaper.on('cell:pointerclick', function (cellView, e, x, y) {
    // 矢印を引っ張ってきたときの動作
    if (onDragging) {
      if (cellView.model.attributes.type == 'Layer') {
        if (rightClickedCellView != null) {
          var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
          if (model.class_name == 'Model') {
            // レイヤーの接続
            var layer = model.config.layers.find(l => l.config.name == cellView.model.attributes.id);
            var layerIndex = model.config.layers.findIndex(l => l.config.name == cellView.model.attributes.id);
            layer.inbound_nodes[0].push([rightClickedCellView.model.attributes.id, 0, 0, {}]);
            // モデルを更新
            model.config.layers[layerIndex] = layer;
            model = reconfigureInputAndOutputLayers(model);
            modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');
            loadPackageWorkspace(model);
          }
        }
        onDragging = false;
        return;
      } else {
        // 未接続矢印の削除
        mainPaper.model.getCells()
          .filter(c => c.attributes.type == 'link' && c.attributes.source.id == null)
          .forEach(c => c.remove());
      }
    }

    // button要素の場合は処理を実行
    if (cellView.model.attributes.type == 'Button') {
      addInputLayer();
      return;
    }


    if (e.ctrlKey) {
      // Controlキー押下状態
    } else {
      // 全選択解除
      unHighLightAll();
    }
    // クリック情報を保持する
    selectedCellViews.push(cellView);
    // ハイライト表示
    cellView.highlight();

    if (cellView.model.attributes.type == 'link') {
      // リンクのクリック
      // モデルプロパティ設定エリア表示
      setPropertyAreaForModel();
    } else if (cellView.model.attributes.type == 'Layer' || cellView.model.attributes.type == 'InputLayer') {
      // レイヤーのクリック
      // レイヤープロパティ設定エリア表示
      setPropertyAreaForLayer();
    }
  });

  // コンポーネント右クリック
  mainPaper.off('cell:contextmenu');
  mainPaper.on('cell:contextmenu', function (cellView, e, x, y) {
    // ハイライト非表示
    unHighLightAll();
    // クリック情報を保持する
    selectedCellViews.push(cellView);
    // ハイライト表示
    cellView.highlight();

    if (cellView.model.attributes.type == 'link') {
      // リンクのクリック
      // モデルプロパティ設定エリア表示
      setPropertyAreaForModel();
    } else if (cellView.model.attributes.type == 'Layer' || cellView.model.attributes.type == 'InputLayer') {
      // レイヤーのクリック
      // レイヤープロパティ設定エリア表示
      setPropertyAreaForLayer();
      // 右クリックされたオブジェクトを保存
      rightClickedCellView = cellView;
      rightClickInfo = { e: e, x: x, y: y };
    }
  });

  // コンポーネント外クリック
  mainPaper.off('blank:pointerclick');
  mainPaper.on('blank:pointerclick', function (cellView, e, x, y) {
    // ハイライト非表示
    unHighLightAll();
    // プロパティ設定エリア表示
    setPropertyAreaForModel();
  });

  // コンポーネント接続イベント
  mainPaper.off('link:connect');
  mainPaper.on('link:connect', function (linkView, e, connectedToView, magnetElement, type) {
    var sourcePortData = $('text', linkView.sourceMagnet.parentElement).data();
    var targetPortData = $('text', linkView.targetMagnet.parentElement).data();

    // モデルに追加
  });

  // コンポーネント接続解除イベント
  mainPaper.off('link:disconnect');
  mainPaper.on('link:disconnect', function (linkView, e, disconnectedFrom, magnetElement, type) {
    // 画面・モデルから削除
  });
}

/**
 * 作業領域をすべて展開する
 * 
 * @returns
 */
function loadAllPackagesWorkspace(modelList) {
  // 作業領域フォルダにあるモデルをすべて読み込み
  for (var i = 0; i < modelList.length; i++) {
    var modelName = modelList[i].modelName;

    modelMap[modelName] = $.extend(true, {}, modelList[i]);
    if (!curWorkspaceName) {
      // 作業領域が指定されていない場合は指定
      curWorkspaceName = modelName;
    }
  }

  if (!curWorkspaceName) {
    // 作業領域が指定されていない場合は指定
    while (modelMap[WORKSPACE_PREFIX + workspaceCounter] != null) {
      workspaceCounter++;
    }
    curWorkspaceName = WORKSPACE_PREFIX + workspaceCounter;
  }

  if (modelMap[curWorkspaceName] == null) {
    // 作業領域を追加する
    curWorkspaceName = WORKSPACE_PREFIX + workspaceCounter;
    modelMap[curWorkspaceName] = $.extend(true, {}, templateModelMap['Functional_API']);
    modelMap[curWorkspaceName].modelName = curWorkspaceName;
    var newModel = JSON.parse(modelMap[curWorkspaceName].jsonString);
    if (newModel.class_name == 'Model') {
      // Functional APIの場合はモデル名をJSON側にも適用
      newModel.config.name = curWorkspaceName;
      modelMap[curWorkspaceName].jsonString = JSON.stringify(newModel, null, '\t');
    }
    workspaceCounter++;
    while (templateModelMap[WORKSPACE_PREFIX + workspaceCounter] != null ||
      modelMap[WORKSPACE_PREFIX + workspaceCounter] != null) {
      workspaceCounter++;
    }

    // 作業領域の選択肢反映
    setWorkingAccordionInfo();
    setWorkspaceSelectMenu();
  }

  var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
  // 現在の作業領域として展開する
  loadPackageWorkspace(model);

  // 作業領域選択コンボ設定
  setWorkspaceSelectMenu();
  setWorkingAccordionInfo();
  // RtsProfile設定領域表示
  setPropertyAreaForModel();
  // 作業領域の選択肢反映
  setWorkspaceSelectMenu();
  setWorkingAccordionInfo();
}

/**
 * 作業領域を展開する
 * TODO: 整列・重なり回避
 * 
 * @param posXDef
 * @param posYDef
 * @param rtsystem
 * @returns
 */
function loadPackageWorkspace(model) {
  // 作業領域内のレイヤをすべて削除
  deleteAllLayersViewObject();
  // モデルに含まれるレイヤを展開していく
  layers = [];
  if (model.class_name == 'Sequential') {
    layers = model.config;
  } else {
    layers = model.config.layers;
  }
  // 奇麗に表示したい
  if (layers && layers.length > 0) {
    // 描画位置を設定
    var posX = 50;
    var posY = 50;
    var gridWidth = 120;
    var gridHeight = 200;
    counter = 0;
    var inputLayerCounter = 0;

    // レイヤ追加ボタンを追加
    mainGraph.addCell(createInputLayerViewObject('New Input Layer', posX, 0, true));
    setEventMainPaper();

    // 各レイヤの追加
    for (var i = 0; i < layers.length; i++) {
      var isInputLayer = false;
      var layer = layers[i];
      // ドロップされたRTCをコンポーネント領域オブジェクトマップから取得
      // 作業領域用にオブジェクトを再作成する
      if (model.class_name == 'Model') {
        if (model.config.input_layers.length != 0 && model.config.input_layers.find(l => l[0] == layer.config.name)) {
          isInputLayer = true;
        } else {
          if (i > 0) {
            // 親レイヤーがある場合は親レイヤーの右・同じ高さに配置
            if (layer.inbound_nodes[0].length > 0) {
              var parentLayers = layer.inbound_nodes[0].map(function (n) { return mainGraph.getCell(n[0]); });
              // 画面未配置の要素は考慮しない
              parentLayers = parentLayers.filter(p => p != null && p != undefined);
              if (parentLayers.length > 0) {
                var parentX = Math.min.apply(Math, parentLayers.map(function (p) { return p.getBBox().x; }));
                var parentY = parentLayers.find(p => p.getBBox().x == parentX).getBBox().y;
                posX = parentX + gridWidth;
                posY = parentY;
              }
            }
          } else {
            posX += gridWidth;
          }
        }
      } else {
        if (i > 0) {
          posX += gridWidth;
        }
      }
      // 位置がかぶっている場合はY方向にずらす
      while (mainGraph.getCells().find(c =>
        (c.attributes.type == 'Layer' || c.attributes.type == 'InputLayer') &&
        c.getBBox().x == posX && c.getBBox().y == posY)
      ) {
        posY += gridHeight;
      }
      var s;
      if (isInputLayer) {
        // 入力レイヤーの場合は一番左に表示
        s = createInputLayerViewObject(layer.config.name, 50, 50 + gridHeight * inputLayerCounter, false);
        inputLayerCounter++;
      } else {
        if (layer.color) {
          s = createLayerViewObject(layer.config.name, posX, posY, true, layer.color);
        } else {
          // テンプレート側に色指定がある場合はそちらを参照
          if (templateLayerMap[layer.class_name] && templateLayerMap[layer.class_name].color) {
            s = createLayerViewObject(layer.config.name, posX, posY, true, templateLayerMap[layer.class_name].color);
          } else {
            s = createLayerViewObject(layer.config.name, posX, posY, true);
          }
        }
      }

      // 作業領域に追加
      mainGraph.addCell(s);

      // 作業領域イベント関連付
      setEventMainPaper();
    }
  }

  // mainPaperのサイズ調整
  mainPaper.fitToContent({ padding: 50 });

  // JSON表示を更新
  setJsonData();

  // ワークスペースセレクタを更新
  setWorkspaceSelectMenu();
  setWorkingAccordionInfo();

  // プロパティ表示の更新
  setPropertyAreaForModel();

  // 矢印を描画
  drawArrows();

  // レイヤ種類ごとに色を設定
  repaintLayerColor();
}

/**
 * ワークスペース内のレイヤー色変更
 * @returns
 */
function repaintLayerColor() {
  // インプットレイヤーはグレー表示
  mainGraph.getCells()
    .filter(c => c.attributes.type == 'Layer' && c.id.indexOf('input_') != -1)
    .forEach(l => l.attr({
      rect: {
        rx: 2, ry: 2,
        fill: {
          type: 'linearGradient',
          stops: [
            { offset: '0%', color: 'lightgray' },
            { offset: '100%', color: 'gray' }
          ],
          attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
        },
      }
    }));
}

/**
 * すべてのレイヤーを画面から削除する
 * @returns
 */
function deleteAllLayersViewObject() {
  if (mainGraph.attributes.cells.models && mainGraph.attributes.cells.models.length > 0) {
    for (var i = mainGraph.attributes.cells.models.length; i >= 0; i--) {
      mainGraph.attributes.cells.remove(mainGraph.attributes.cells.models[i]);
    }
  }
}

/**
 * 矢印を描画する
 * @returns
 */
function drawArrows() {
  var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
  var layers = [];
  var count = 0;

  if (model.class_name == 'Sequential') {
    layers = model.config;
    for (var i = 0; i < layers.length - 1; i++) {
      var link = new joint.dia.Link({
        id: 'connector' + count,
        source: { id: layers[i].config.name },
        target: { id: layers[i + 1].config.name },
        router: {
          name: 'manhattan',
          args: {
            startDirections: ['right'],
            endDirections: ['left'],
            excludeTypes: ['myNamespace.MyCommentElement']
          }
        },
        connector: { name: 'rounded' },
        //        connector: {name: 'smooth'},
        attrs: {
          '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' }
        }
      });
      mainGraph.addCell(link);
      count++;
    }
  } else {
    layers = model.config.layers;
    layers.forEach(function (layer, index) {
      if (layer.inbound_nodes.length == 0) {
        return;
      }
      layer.inbound_nodes[0].forEach(function (sourceNode) {
        var link = new joint.dia.Link({
          id: 'connector' + count,
          source: { id: sourceNode[0] },
          target: { id: layer.config.name },
          router: {
            name: 'manhattan',
            args: {
              startDirections: ['right'],
              endDirections: ['left'],
              excludeTypes: ['myNamespace.MyCommentElement']
            }
          },
          connector: { name: 'rounded' },
          //          connector: {name: 'smooth'},
          attrs: {
            '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' }
          }
        });
        mainGraph.addCell(link);
        count++;
      });
    });
  }

  // コネクション情報削除のツールを非表示
  $('.link-tools .tool-remove').css('display', 'none');
  $('.marker-arrowhead').css('display', 'none');
}

/**
 * レイヤーの右クリックメニュー設定
 */
function setLayerContextMenu() {
  $.contextMenu({
    selector: '.joint-type-layer, .joint-type-inputlayer',
    callback: function (key, options) {
      if (rightClickedCellView == null) {
        return;
      }
      switch (key) {
        case 'addOut':
          // 出力ポート追加
          var linkView = mainPaper.getDefaultLink()
            .set({
              'source': { x: rightClickInfo.x, y: rightClickInfo.y },
              'target': { x: rightClickInfo.x, y: rightClickInfo.y }
            })
            .addTo(mainPaper.model).findView(mainPaper);
          linkView.startArrowheadMove('target');
          $(document).on({
            'mousemove.addout': onDrag,
            'mouseup.addout': onDragEnd
          }, {
            // shared data between listeners
            view: linkView,
            paper: mainPaper
          });

          function onDrag(evt) {
            // transform client to paper coordinates
            var p = evt.data.paper.snapToGrid({
              x: evt.clientX,
              y: evt.clientY
            });
            // manually execute the linkView mousemove handler
            evt.data.view.pointermove(evt, p.x, p.y);
          }

          function onDragEnd(evt) {
            // manually execute the linkView mouseup handler
            onDragging = true;
            evt.data.view.pointerclick(evt, evt.clientX, evt.clientY);
            $(document).off('.addout');
          }

          break;
        default:
          rightClickedCellView = null;
          rightClickInfo = {};
          break;
      }
    },
    items: {
      'addOut': {
        name: 'Create New Link',
        icon: 'add',
        visible: function (key, opt) {
          var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
          return model.class_name == 'Model';
        }
      },
    }
  });
}

/**
 * 作業領域上に入力レイヤーを描画
 * @param name 作成対象のレイヤー名
 * @param posX X座標
 * @param posY Y座標
 * @param isButton ボタンかどうか
 * @returns
 */
function createInputLayerViewObject(name, posX, posY, isButton) {
  // ネットワークパネル上と作業領域上で描画レイアウトを切り替え
  var fontSize = 14;
  var textX = 0;
  var textY = 2;
  var textAnchor = 'start';
  var width = 80;
  var height = 150;
  var labelText = name;
  var backgroundColor = 'lightsalmon';
  var backgroundColor2 = 'tomato';
  var dasharray = [];
  var type = 'InputLayer';
  var fontFamily = 'sans-serif';

  if (isButton) {
    //    dasharray = [4, 3];
    type = 'Button';
    backgroundColor = 'white';
    backgroundColor2 = 'gray';
    fontFamily = 'FontAwesome';
    fontSize = 18;
    textY = 8;
    height = 30;
    labelText = ' \uf055 Input';
  } else {
    // クラス名・必要プロパティの表示
    var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
    if (model.class_name == 'Sequential') {
      labelText = joint.util.breakText(model.config.find(l => l.config.name == name).class_name, { width: 90 });
    } else {
      labelText = joint.util.breakText(model.config.layers.find(l => l.config.name == name).class_name, { width: 90 });
    }
    labelText += '\n\n[name]\n' + joint.util.breakText(name, { width: 90 });
  }

  // Layer作成
  var layer = new joint.shapes.devs.Model({
    id: name,
    type: type,
    position: { x: posX, y: posY },
    size: { width: width, height: height },
    inPorts: [],
    outPorts: [],
    attrs: {
      '.label': {
        text: labelText,
        'ref-x': textX,
        'ref-y': textY,
        'font-size': fontSize,
        'font-family': fontFamily,
        'font-weight': 'normal',
        'text-anchor': textAnchor
      },
      rect: {
        rx: 2, ry: 2,
        fill: {
          type: 'linearGradient',
          stops: [
            { offset: '0%', color: backgroundColor },
            { offset: '100%', color: backgroundColor2 }
          ],
          attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
        },
        'model-type': type,
        'stroke-dasharray': dasharray
      }
    }
  });

  // レイヤー移動時にmainPaperのサイズを再計算
  layer.on('change:position', function () {
    mainPaper.fitToContent({ padding: 50 });
  });

  if (isButton == false) {
    // 影をつける
    layer.attr('rect/filter', { name: 'dropShadow', args: { dx: 2, dy: 2, blur: 3 } });
  }

  return layer;
}


/*******************************************************************************
 * JSON領域
 ******************************************************************************/

/**
 * JSON領域のレイアウト処理
 * 
 * @returns
 */
function layoutPanelJson() {
  var pstyle = 'border: none; padding: 0px;';
  var jsonPanel = $('#json-panel').w2layout({
    name: 'json-panel',
    padding: 0,
    panels: [
      { type: 'main', style: pstyle, content: createJsonView(), overflow: 'hidden' },
      { type: 'right', size: 150, style: pstyle, content: createJsonButtons() }
    ]
  });

  // Buttonの有効・無効設定
  $('#json-edit-btn').button();
  $('#json-edit-btn').button('enable');
  $('#json-apply-btn').button();
  $('#json-apply-btn').button('disable');

  createJsonEditor();

}

/**
 * JSONエディタ生成処理
 * 
 */
function createJsonEditor() {
  require.config({
    baseUrl: '/mimosa/',
  });

  require(['vs/editor/editor.main'], function () {
    jsonEditor = Monaco.Editor.create(document.getElementById('json-view-jsontext'), {
      value: [].join('\n'),
      mode: 'json',
      lineNumbers: true,
      fontSize: 12,
      automaticLayout: true,
      autoSize: true,
      scrollbar: {
        handleMouseWheel: true
      },
      scrollBeyondLastLine: false,
      lineDecorationWidth: 0,
      renderWhitespace: true,
      readOnly: true,
    });
  });
}

/**
 * JSONパネル表示部分
 * 
 * @returns
 */
function createJsonView() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'json-view');
  panel.css('background-color', '#fff');
  var table = $('<table>');
  table.css('width', '100%').css('height', '100%');

  // モデル名設定
  var trTop = $('<tr>');
  trTop.attr('height', '25px')
  var tdTopLeft = $('<td>');
  tdTopLeft.html('ModelName:');
  tdTopLeft.css('text-align', 'right');
  tdTopLeft.css('width', '120px');
  tdTopLeft.css('font-size', '12px');
  var tdTopRight = $('<td>');
  $('<input>').attr({
    type: 'text',
    id: 'json-view-modelname',
    readonly: 'readonly'
  }).css({
    width: '100%'
  }).appendTo(tdTopRight);

  // JSON表示
  var trMiddle = $('<tr>');
  var tdMiddleLeft = $('<td>');
  var tdMiddleRight = $('<td>');
  tdMiddleLeft.html('Json:');
  tdMiddleLeft.css('width', '120px');
  tdMiddleLeft.css('font-size', '12px');
  tdMiddleLeft.css('vertical-align', 'top');
  tdMiddleLeft.css('text-align', 'right');
  $('<div>').attr({
    id: 'json-view-jsontext'
  }).css({
    width: '100%',
    height: '100%',
    resize: 'vertical'
  }).appendTo(tdMiddleRight);

  trTop.append(tdTopLeft);
  trTop.append(tdTopRight);
  trMiddle.append(tdMiddleLeft);
  trMiddle.append(tdMiddleRight);

  table.append(trTop);
  table.append(trMiddle);

  panel.append(table);

  return panel;
}
/**
 * JSONパネルボタン部分設定
 * 
 * @returns
 */
function createJsonButtons() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.css('background-color', '#fff');

  // 編集ボタン
  var editBtn = $('<button type="button">');
  editBtn.html('Edit JSON')
  editBtn.attr('id', 'json-edit-btn');
  editBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '2px').css('margin-left', '5px').css('margin-right', '5px').css('width', '120px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-pencil').appendTo(editBtn);
  $(editBtn).on('click', function (event) {
    $('#json-edit-btn').button('disable');
    $('#json-apply-btn').button('enable');
    if (JSON.parse(modelMap[curWorkspaceName].jsonString).class_name == 'Sequential') {
      $('#json-view-modelname').attr('readonly', false);
    }
    jsonEditor.updateOptions({ readOnly: false });
  });
  panel.append(editBtn);

  // 適用ボタン
  var applyButton = $('<button type="button">');
  applyButton.html('Apply')
  applyButton.attr('id', 'json-apply-btn');
  applyButton.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '2px').css('margin-left', '5px').css('margin-right', '5px').css('width', '120px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-pencil').appendTo(applyButton);
  $(applyButton).on('click', function (event) {
    applyJsonData();
    $('#json-edit-btn').button('enable');
    $('#json-apply-btn').button('disable');
    if (JSON.parse(modelMap[curWorkspaceName].jsonString).class_name == 'Sequential') {
      $('#json-view-modelname').attr('readonly', true);
    }
    jsonEditor.updateOptions({ readOnly: true });
  });
  panel.append(applyButton);

  return panel;
}

/**
 * JSON表示部で編集したJSONをワークスペースのモデルに反映
 * @returns
 */
function applyJsonData() {
  if (modelMap[curWorkspaceName] == null) {
    modelMap[curWorkspaceName] = new Object();
  }
  modelMap[curWorkspaceName].jsonString = jsonEditor.getValue();
  // TODO: エラー処理
  var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
  if (model.class_name == 'Sequential') {
    modelMap[curWorkspaceName].modelName = $('#json-view-modelname').val();
  } else {
    modelMap[curWorkspaceName].modelName = model.config.name;
  }
  modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');

  loadPackageWorkspace(model);
}

/**
 * ワークスペースのモデルをJSONで表示
 * @returns
 */
function setJsonData() {
  if (jsonEditor == null || jsonEditor == undefined) {
    // TODO: 要解決
    return;
  };
  if (modelMap[curWorkspaceName] != null) {
    $('#json-view-modelname').val(modelMap[curWorkspaceName].modelName);
    jsonEditor.setValue(modelMap[curWorkspaceName].jsonString);
    // TODO: 最終行への移動
    //    jsonEditor.revealLine()
  } else {
    $('#json-view-modelname').val(templateModelMap[curWorkspaceName].modelName);
    jsonEditor.setValue(templateModelMap[curWorkspaceName].jsonString);
    // TODO: 最終行への移動
    //    jsonEditor.revealLine()
  }
}

/**
 * JSON領域の表示情報をクリア
 * @returns
 */
function clearJsonData() {
  $('#json-view-modelname').val(null);
  jsonEditor.setValue('');
}

/*******************************************************************************
 * プロパティ領域
 ******************************************************************************/
/**
 * プロパティ領域にModelの設定Formを表示する
 * 
 * @returns
 */
function setPropertyAreaForModel() {
  if (curWorkspaceName && modelMap[curWorkspaceName]) {
    // RtsProfile設定Formを設定する
    $('#property-panel').w2form(createModelPropertySettingFrom(modelMap[curWorkspaceName]));
    //    // 釦を変更する
    //    $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
  }
}

function createModelPropertySettingFrom(modelBase) {
  destroySettingForm();
  var model = JSON.parse(modelBase.jsonString);

  var form = {
    name: 'model-property-setting',
    padding: 0,
    fields: [
      // モデルパラメータ[tab1]
      { name: 'model_name', type: 'text', required: true, html: { caption: 'Model Name', page: 0, attr: 'style="width:200px"' } },
      { name: 'class_name', type: 'text', required: true, html: { caption: 'Class Name', page: 0, attr: 'style="width:200px" readonly="readonly"' } },
      { name: 'backend', type: 'text', required: true, html: { caption: 'Backend', page: 0, attr: 'style="width:200px" readonly="readonly"' } },
      { name: 'keras_version', type: 'text', required: true, html: { caption: 'Keras Version', page: 0, attr: 'style="width:200px" readonly="readonly"' } },
      { name: 'dataset', type: 'list', required: false, html: { caption: 'Dataset', page: 0, attr: 'style="width:200px"' }, options: { items: getDatasetChoices() } },
      // 学習用(compile)[tab1]
      { name: 'optimizer', type: 'list', required: true, html: { caption: 'Optimizer', page: 0, attr: 'style="width:200px"' } },
      { name: 'loss', type: 'list', required: true, html: { caption: 'Loss', page: 0, attr: 'style="width:200px"' } },
      { name: 'metrics', type: 'enum', required: true, html: { caption: 'Metrics', page: 0, attr: 'style="width:200px"' } },
      { name: 'loss_weights', type: 'text', required: true, html: { caption: 'Loss Weights', page: 0, attr: 'style="width:200px"' } },
      { name: 'sample_weights', type: 'text', required: true, html: { caption: 'Sample Weights', page: 0, attr: 'style="width:200px"' } },
      // 学習用(fit)[tab2]
      { name: 'fit.batch_size', type: 'int', required: true, html: { caption: 'Batch Size', page: 1, attr: 'style="width:50px"' } },
      { name: 'fit.epochs', type: 'int', required: true, html: { caption: 'Epochs', page: 1, attr: 'style="width:50px"' } },
      { name: 'fit.callback', type: 'enum', required: true, html: { caption: 'Callback', page: 1, attr: 'style="width:200px"' } },
      { name: 'fit.validation_split', type: 'checkbox', required: true, html: { caption: 'Validation Split', page: 1, attr: '' } },
      { name: 'fit.shuffle', type: 'checkbox', required: true, html: { caption: 'Shuffle', page: 1, attr: '' } },
      { name: 'fit.x', type: 'text', required: true, html: { caption: 'x', page: 1, attr: 'style="width:200px"' } },
      { name: 'fit.y', type: 'text', required: true, html: { caption: 'y', page: 1, attr: 'style="width:200px"' } },
      { name: 'fit.validation_data', type: 'text', required: true, html: { caption: 'Validation Data', page: 1, attr: 'style="width:200px"' } },
      { name: 'fit.class_weight', type: 'text', required: false, html: { caption: 'Class Weight', page: 1, attr: 'style="width:200px"' } },
      { name: 'fit.sample_weight', type: 'text', required: false, html: { caption: 'Sample Weight', page: 1, attr: 'style="width:200px"' } },
      { name: 'fit.initial_epoch', type: 'int', required: false, html: { caption: 'Initial Epoch', page: 1, attr: 'style="width:50px"' } }
    ],
    tabs: [
      { id: 'model-tab1', caption: 'Model' },
      { id: 'model-tab2', caption: 'Learning' }
    ],
    record: {
      model_name: modelBase.modelName,
      class_name: model.class_name,
      backend: model.backend,
      keras_version: model.keras_version,
      dataset: modelBase.dataset
    },
    actions: {
      // 値の更新
      'Update': function () {
        var oldName = curWorkspaceName;
        var newName = this.record.model_name;
        modelMap[newName] = $.extend(true, null, modelMap[oldName]);
        if (newName != oldName) {
          delete modelMap[oldName];
        }
        curWorkspaceName = newName;
        modelMap[curWorkspaceName].modelName = newName;
        var newModel = JSON.parse(modelMap[curWorkspaceName].jsonString);
        if (newModel.class_name == 'Model') {
          newModel.config.name = curWorkspaceName;
          modelMap[curWorkspaceName].jsonString = JSON.stringify(newModel, null, '\t');
        }
        modelMap[curWorkspaceName].dataset = this.record.dataset;

        var model = JSON.parse(modelMap[curWorkspaceName].jsonString);

        // 学習・予測系プロパティの更新
        var currentFields = this.fields.filter(f => f.page == this.page);
        for (var i in currentFields) {
          var prop = currentFields[i];
          if (prop.name != 'model_name' && prop.name != 'class_name' &&
            prop.name != 'backend' && prop.name != 'keras_version') {
            // 値が設定されない場合はそのプロパティを削除
            if (this.record[prop.name] == null || this.record[prop.name] == '' || this.record[prop.name] == 'null') {
              delete model[prop.name];
              continue;
            }
            // 評価関数・コールバックの場合はリストなので設定方法を変える
            if (prop.name.indexOf('callback') != -1 ||
              prop.name.indexOf('metrics') != -1) {
              model[prop.name] = [];
              this.record[prop.name].forEach(function (val) {
                model[prop.name].push(val.text);
              });
            } else {
              // 数値型以外はすべてテキスト
              if (prop.type == 'int' || prop.type == 'float' || prop.type == 'hex') {
                model[prop.name] = Number(this.record[prop.name]);
              } else {
                model[prop.name] = this.record[prop.name];
              }
            }
          }
        }

        modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');
        loadPackageWorkspace(model);

        // ワークスペースセレクタを更新
        setWorkspaceSelectMenu();
        setWorkingAccordionInfo();

        // プロパティ表示の更新
        setPropertyAreaForModel();
      },
      'Reset': function () {
        this.record.model_name = curWorkspaceName;
        this.refresh();
      }
    }
  };

  // リスト・Enumアイテムの登録
  form.fields.forEach(function (prop, index) {
    if (prop.name != 'model_name' && prop.name != 'class_name' &&
      prop.name != 'backend' && prop.name != 'keras_version') {
      if (prop.name.indexOf('loss') != -1) {
        form.fields[index].options = { items: lossListItems };
        if (model[prop.name] != null) {
          form.record[prop.name] = model[prop.name];
        }
      } else if (prop.name.indexOf('optimizer') != -1) {
        form.fields[index].options = { items: optimizerListItems };
        if (model[prop.name] != null) {
          form.record[prop.name] = model[prop.name];
        }
      } else if (prop.name.indexOf('callback') != -1) {
        form.fields[index].options = { items: callbackListItems };
        if (model[prop.name] != null) {
          form.record[prop.name] = model[prop.name];
        } else {
          form.record[prop.name] = ['TendorBoard'];
        }
      } else if (prop.name.indexOf('metrics') != -1) {
        form.fields[index].options = { items: metricsListItems };
        if (model[prop.name] != null) {
          form.record[prop.name] = model[prop.name];
        } else {
          form.record[prop.name] = ['accuracy'];
        }
      } else {
        // リストでないプロパティを登録
        form.record[prop.name] = model[prop.name];
      }
    }
  });

  return form;
}

/**
 * プロパティ領域にLayerの設定Formを表示する
 * 
 * @returns
 */
function setPropertyAreaForLayer() {
  if (modelMap[curWorkspaceName] != null && selectedCellViews.length != 0) {
    // RtsProfile設定Formを設定する
    $('#property-panel').w2form(createLayerPropertySettingFrom(selectedCellViews[selectedCellViews.length - 1].model.id));
    //    // 釦を変更する
    //    $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
  }
}

/**
 * レイヤーのプロパティの表示・登録画面を作成する
 * @param layerName
 * @returns
 */
function createLayerPropertySettingFrom(layerName) {
  destroySettingForm();

  var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
  var layer = {};
  if (model.class_name == 'Sequential') {
    layer = model.config.find(l => l.config.name == layerName);
  } else {
    layer = model.config.layers.find(l => l.config.name == layerName);
  }

  var form = {
    name: 'layer-property-setting',
    padding: 0,
    fields: [
      { name: 'class_name', type: 'text', required: true, html: { caption: 'Class Name', page: 0, attr: 'style="width:200px" readonly="readonly"' } },
      { name: 'name', type: 'text', required: true, html: { caption: 'Layer Name', page: 0, attr: 'style="width:200px"' } },
      { name: 'trainable', type: 'checkbox', required: true, html: { caption: 'Trainable', page: 0, attr: '' } },
    ],
    tabs: [
      { id: 'layer-tab1', caption: 'Layer Property' }
    ],
    record: {
      class_name: layer.class_name,
    },
    actions: {
      // 値の更新
      'Update': function () {
        var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
        var layerIndex;
        var layer = {};
        if (model.class_name == 'Sequential') {
          layerIndex = model.config.findIndex(l => l.config.name == editingLayerRecord.name);
          layer = model.config[layerIndex];
        } else {
          layerIndex = model.config.layers.findIndex(l => l.config.name == editingLayerRecord.name);
          layer = model.config.layers[layerIndex];
        }

        for (var i in this.fields) {
          var prop = this.fields[i];
          if (model.class_name == 'Model' && prop.name == 'name') {
            // 名前はconfigより上にも登録
            var oldName = layer.config[prop.name];
            var newName = this.record[prop.name];
            layer[prop.name] = newName;
            layer.config[prop.name] = newName;
            // 入出力レイヤーに変更したレイヤーがある場合レイヤー名変更を適用
            model.config.input_layers.filter(l => l[0] == oldName).forEach(l => l[0] = newName);
            model.config.output_layers.filter(l => l[0] == oldName).forEach(l => l[0] = newName);
            // inbound_nodesも変更
            model.config.layers.forEach(function (layer, index) {
              if (layer.inbound_nodes.length == 0) {
                layer.inbound_nodes = [[]];
              }
              layer.inbound_nodes[0].filter(n => n[0] == oldName).forEach(n => n[0] = newName);
            });
          } else if (prop.name != 'class_name') {
            // 値が設定されない場合はそのプロパティを削除
            if (this.record[prop.name] == null || this.record[prop.name] == '' || this.record[prop.name] == 'null') {
              delete layer.config[prop.name];
              continue;
            }

            if (prop.name == 'batch_input_shape' ||
              prop.name == 'target_shape' ||
              prop.name == 'kernel_size' ||
              prop.name == 'strides' ||
              prop.name == 'pool_size') {
              if (Array.isArray(this.record[prop.name])) {
                layer.config[prop.name] = this.record[prop.name];
              } else {
                // 配列として処理
                var tmp = [];
                this.record[prop.name].split(',').forEach(function (numString, index, array) {
                  var num = Number(numString);
                  if (numString == null || numString == '' || isNaN(num)) {
                    tmp.push(null);
                  } else {
                    tmp.push(num);
                  }
                });
                layer.config[prop.name] = tmp;
              }
            } else if (prop.name.indexOf('initializer') != -1) {
              // 初期化の場合はオブジェクトなので設定方法を変える
              if (layer.config[prop.name] == null || layer.config[prop.name]['class_name'] == null) {
                layer.config[prop.name] = new Object();
              }
              if (initializerMap[this.record[prop.name]] != null && initializerMap[this.record[prop.name]] != undefined) {
                layer.config[prop.name] = initializerMap[this.record[prop.name]];
              } else {
                layer.config[prop.name]['class_name'] = this.record[prop.name];
                // configの値に既存の値があれば変えない
                if (layer.config[prop.name]['config'] == null) {
                  layer.config[prop.name]['config'] = {};
                }
              }
            } else if (prop.name.indexOf('regularizer') != -1 ||
              prop.name.indexOf('constraint') != -1) {
              // 正規化・制約の場合はオブジェクトなので設定方法を変える
              if (layer.config[prop.name] == null || layer.config[prop.name]['class_name'] == null) {
                layer.config[prop.name] = new Object();
              }
              layer.config[prop.name]['class_name'] = this.record[prop.name];
              // configの値に既存の値があれば変えない
              if (layer.config[prop.name]['config'] == null) {
                layer.config[prop.name]['config'] = {};
              }
            } else {
              // 数値型以外はすべてテキスト
              if (prop.type == 'int' || prop.type == 'float' || prop.type == 'hex') {
                layer.config[prop.name] = Number(this.record[prop.name]);
              } else {
                layer.config[prop.name] = this.record[prop.name];
              }
            }
          }
        }

        // モデルを更新
        if (model.class_name == 'Sequential') {
          model.config[layerIndex] = layer;
        } else {
          model.config.layers[layerIndex] = layer;
        }

        modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');
        loadPackageWorkspace(model);
      },
      'Reset': function () {
        this.record = $.extend(true, null, editingLayerRecord);
        this.refresh();
      }
    }
  };

  // プロパティのテンプレートに該当するクラスがあれば、プロパティを追加する
  var additionalProperty = templateLayerPropertyMap[layer.class_name];
  if (additionalProperty != null) {
    form.fields = form.fields.concat(additionalProperty.fields);
  }

  // インプットレイヤーの場合はここでプロパティを追加
  if (layer.class_name == 'InputLayer') {
    var inputLayerProperty = [
      { name: 'batch_input_shape', type: 'text', required: true, html: { caption: 'Batch Input Shape', page: 0, attr: '' } },
      { name: 'dtype', type: 'list', required: true, html: { caption: 'Dtype', page: 0, attr: '' } },
    ];
    form.fields = form.fields.concat(inputLayerProperty);
  }

  // フィールドに対応したレコードを登録
  form.fields.forEach(function (prop, index) {
    if (prop.name != 'class_name') {
      // プロパティ登録・選択リストの項目を設定
      if (prop.name.indexOf('initializer') != -1) {
        // 初期化関数の場合は中身がオブジェクトなので、クラス名だけを取り出して設定する
        form.fields[index].options = { items: initializerListItems };
        if (layer.config[prop.name] != null && layer.config[prop.name]['class_name'] != null) {
          form.record[prop.name] = layer.config[prop.name].class_name;
        }
      } else if (prop.name.indexOf('regularizer') != -1) {
        form.fields[index].options = { items: regularizerListItems };
        if (layer.config[prop.name] != null && layer.config[prop.name]['class_name'] != null) {
          form.record[prop.name] = layer.config[prop.name].class_name;
        }
      } else if (prop.name.indexOf('constraint') != -1) {
        form.fields[index].options = { items: constraintListItems };
        if (layer.config[prop.name] != null && layer.config[prop.name]['class_name'] != null) {
          form.record[prop.name] = layer.config[prop.name].class_name;
        }
      } else if (prop.name.indexOf('activation') != -1) {
        form.fields[index].options = { items: activationListItems };
        if (layer.config[prop.name] != null) {
          form.record[prop.name] = layer.config[prop.name];
        }
      } else if (prop.name.indexOf('dtype') != -1) {
        form.fields[index].options = { items: dtypeListItems };
        if (layer.config[prop.name] != null) {
          form.record[prop.name] = layer.config[prop.name];
        }
      } else if (prop.name.indexOf('padding') != -1) {
        form.fields[index].options = { items: paddingListItems };
        if (layer.config[prop.name] != null) {
          form.record[prop.name] = layer.config[prop.name];
        }
      } else if (prop.name.indexOf('data_format') != -1) {
        form.fields[index].options = { items: dataFormatListItems };
        if (layer.config[prop.name] != null) {
          form.record[prop.name] = layer.config[prop.name];
        }
      } else {
        // プロパティを登録
        form.record[prop.name] = layer.config[prop.name];
      }
    }
  });

  // 編集中のレイヤーを登録
  editingLayerRecord = $.extend(true, null, form.record);

  return form;
}

/**
 * 設定用Formのインスタンスを開放する
 */
function destroySettingForm() {
  // 同一名は作成する前に一度破棄する
  if (w2ui['model-property-setting']) {
    w2ui['model-property-setting'].destroy();
  }
  if (w2ui['layer-property-setting']) {
    w2ui['layer-property-setting'].destroy();
  }
}
/*******************************************************************************
 * フッター領域
 ******************************************************************************/

/**
 * パネルの表示・非表示を切り替える
 */
function toggleNetworkPanel(id, field) {
  $('#' + id + ' > span').removeClass('ui-icon-circlesmall-plus');
  $('#' + id + ' > span').removeClass('ui-icon-circlesmall-minus');

  if (w2ui['layout-panel'].get(field).hidden === true) {
    // 表示
    $('#' + id + ' > span').addClass('ui-icon-circlesmall-minus');
  } else {
    // 非表示
    $('#' + id + ' > span').addClass('ui-icon-circlesmall-plus');
  }
  w2ui['layout-panel'].toggle(field);
}

/*******************************************************************************
 * 全般
 *******************************************************************************/
/**
 * 画面をロックする
 * 
 * @returns
 */
function lockScreen(message = 'Loading...') {
  w2popup.open({ width: 180, height: 70 });
  w2popup.lock(message, true);
}

/**
 * 画面のロックを解除する
 * 
 * @returns
 */
function unlockScreen() {
  w2popup.close();
}

/**
 * 選択状態を解除する
 * 
 * @returns
 */
function unHighLightAll() {
  for (var i = 0; i < selectedCellViews.length; i++) {
    selectedCellViews[i].unhighlight();
  }
  selectedCellViews = [];
}

/**
 * キーボード押下時のイベントを登録する
 * @returns
 */
function setKeyboardEvent() {
  if (document.addEventListener) {
    // イベントリスナーに対応している
    document.addEventListener('keydown', onKeydownEvent);
    document.addEventListener('mousedown', onMousedownEvent);
  } else if (document.attachEvent) {
    // アタッチイベントに対応している
    document.attachEvent('onkeydown', onKeydownEvent);
    document.attachEvent('onmousedown', onMousedownEvent);
  }
}

/**
 * キーボード押下時のイベント処理
 * @returns
 */
function onKeydownEvent(e) {
  if (e.key == 'Delete') {
    if (selectedCellViews.length == 0) {
      return;
    }

    // ワークスペースのモデルが未設定の場合はなにもしない
    if (modelMap[curWorkspaceName] == null) {
      return;
    }
    var model = JSON.parse(modelMap[curWorkspaceName].jsonString);

    // レイヤーと矢印を分離する
    var removeLayerNames = [];
    var removeConnectors = [];
    selectedCellViews.forEach(cell => {
      if (cell.model.attributes.type == 'Layer' || cell.model.attributes.type == 'InputLayer') {
        removeLayerNames.push(cell.model.id);
      }
      if (cell.model.attributes.type == 'link') {
        if (model.class_name == 'Sequential') {
          // シーケンシャルモデルでは矢印を削除させない
          console.log('here');
          return;
        }
        removeConnectors.push(cell);
      }
      cell.remove();
    });

    if (model.class_name == 'Sequential' && removeLayerNames.length == 0) {
      return;
    }

    lockScreen();

    // レイヤー削除処理
    if (removeLayerNames.length != 0) {
      // モデルを再構築
      if (model.class_name == 'Sequential') {
        model.config = model.config.filter(l => removeLayerNames == l.config.name);
      } else {
        // TODO: 1個に固定しているので、複数選択可能にしたら要変更
        model = deleteLayer(model, removeLayerNames[0]);
      }
    }

    // 矢印削除処理
    if (removeConnectors.length != 0) {
      if (model.class_name == 'Model') {
        removeConnectors.forEach(function (connector) {
          var sourceLayerName = connector.sourceView.model.attributes.id;
          var targetLayerName = connector.targetView.model.attributes.id;
          model.config.layers.forEach(function (layer, index) {
            if (layer.config.name == targetLayerName) {
              model.config.layers[index].inbound_nodes[0] = layer.inbound_nodes[0].filter(n => n[0] != sourceLayerName);
            }
          });
        });
      }
    }
    // 入出力レイヤーの再設定
    model = reconfigureInputAndOutputLayers(model);

    // 再構築したモデルをマップに設定し読込
    modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');
    loadPackageWorkspace(model);

    setJsonData();

    unlockScreen();
  }
}

/**
 * モデル上からレイヤーを削除する
 * @param name 削除するレイヤー名
 * @returns
 */
function deleteLayer(model, name) {
  var deletingLayer = model.config.layers.find(l => l.config.name == name);
  model.config.layers = model.config.layers.filter(l => l.config.name != name);

  // inbound_nodesの再構築
  model.config.layers.forEach(function (layer, index) {
    if (layer.inbound_nodes.length == 0) {
      return;
    }
    layer.inbound_nodes[0].forEach(function (node, node_index) {
      if (node[0] == deletingLayer.config.name) {
        if (deletingLayer.inbound_nodes.length == 0 || deletingLayer.inbound_nodes[0].length == 0) {
          layer.inbound_nodes[0].splice(node_index, 1);
        } else {
          layer.inbound_nodes[0] = deletingLayer.inbound_nodes[0];
        }
      }
    });
  });

  return model;
}

/**
 * input_layerとoutput_layerの再設定
 */
function reconfigureInputAndOutputLayers(model) {
  if (model.class_name == 'Model') {
    // input_layersの再構築
    // 入力レイヤーをリセットし、classがInputLayerになっている全てのレイヤーを入力レイヤーとする
    model.config.input_layers = [];
    var inputLayerCount = 0;
    model.config.layers.filter(l => l.class_name == 'InputLayer').forEach(function (layer, index) {
      model.config.input_layers.push([layer.config.name, 0, 0]);
    });
    // output_layersの再構築
    // 出力レイヤーをリセットし、自分自身を指すinbound_nodesが存在しない全てのレイヤーを出力レイヤーとする
    model.config.output_layers = [];
    model.config.layers.forEach(function (layer, index) {
      var isInbounded = false;
      model.config.layers.forEach(function (layer2, index2) {
        if (layer2.inbound_nodes.length > 0
          && layer2.inbound_nodes[0].filter(n => n[0] == layer.config.name).length != 0) {
          isInbounded = true;
        }
      })
      if (isInbounded == false) {
        model.config.output_layers.push([layer.config.name, 0, 0]);
      }
    });
  } else {
    //    // Sequentialレイヤーの場合は、最初のレイヤーに入力設定を追加
    //    model.config.forEach(function(layer, index){
    //      delete model.config[index].config['batch_input_shape'];
    //      delete model.config[index].config['dtype'];
    //    });
    //    model.config[0].config['batch_input_shape'] = [null, 784];
    //    model.config[0].config['dtype'] = 'float32';
  }

  return model;
}

/**
 * マウス押下イベント処理
 * @param e
 * @returns
 */
function onMousedownEvent(e) {
  unHighLightAll();
  selectedCellViews = [];
}

/**
 * 入力レイヤーをモデルに追加する
 * @returns
 */
function addInputLayer() {
  var model = JSON.parse(modelMap[curWorkspaceName].jsonString);
  // モデル内にInputLayerを追加
  var inputLayerTemplate = {
    'name': '',
    'class_name': 'InputLayer',
    'config': {
      'batch_input_shape': [
        null,
        784
      ],
      'dtype': 'float32',
      'sparse': false,
      'name': ''
    },
    'inbound_nodes': [[]]
  };

  // レイヤー名称からレイヤーを取得し、モデルに追加
  var sameClassLayers = model.config.layers.filter(l => l.class_name == 'InputLayer');
  var classCount = 1 + sameClassLayers.length;
  var inputLayerName = 'input_' + classCount;
  // 名前が重複している場合はカウントを上げる
  sameClassLayers.forEach(function (element, index, array) {
    if (element.config.name == 'input_' + classCount) {
      classCount++;
      inputLayerName = 'input_' + classCount;
    }
  });

  var inputLayer = $.extend(true, {}, inputLayerTemplate);
  inputLayer.name = inputLayerName;
  inputLayer.config.name = inputLayerName;
  model.config.layers.unshift(inputLayer);
  model = reconfigureInputAndOutputLayers(model);

  modelMap[curWorkspaceName].jsonString = JSON.stringify(model, null, '\t');
  loadPackageWorkspace(model);
}

/**
 * コンソールを表示する
 * 
 * @returns
 */
function openConsoleLog() {
  // ログ監視を開始する
  startTailLog();

  // ポップアップ表示
  w2popup.open({
    title: 'Console',
    body: '<div id="console" style="width:100%; height:95%; float: left;">'
      + '     <textarea style="width:100%; height:100%; resize: none;" id="console-text" readonly="readonly" wrap="off"></textarea>'
      + '   </div>'
      //      + '   <div id="console-python" style="width:50%; height:95%; float: left;">'
      //      + '     <textarea style="width:100%; height:100%; resize: none;" id="console-text-python" readonly="readonly" wrap="off"></textarea>'
      //      + '   </div>'
      + '   <div id="console-scroll" style="width:100%; height:5% clear:both;">'
      + '     <input name="console-scroll-check" type="checkbox" checked="checked">tail and Scroll</input>'
      + '   </div>',
    width: 800,
    height: 600,
    overflow: 'hidden',
    color: '#333',
    speed: '0.3',
    opacity: '0.8',
    modal: true,
    showClose: true,
    showMax: true,
    onClose: function (event) {
      // ログ監視を停止する
      stopTailLog();
    }
  });
}