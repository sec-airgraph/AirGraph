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
      { type: 'left', size: 300, resizable: true, style: pstyle, content: createComponentPanel() },
      { type: 'main', style: pstyle, content: createMainPanel() },
      { type: 'preview', size: '30%', resizable: true, style: pstyle, content: createConfigulationPanel() },
      { type: 'right', size: 460, resizable: true, style: pstyle, content: createPropertyPanel(), hidden: true },
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
  jointArea.css('position', 'absolute').css('width', '100%').css('height', '100%').css('overflow', 'hidden');
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
 * コンポーネント領域作成処理
 */
function createComponentPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'component-panel');

  // 検索窓追加
  var searchDiv = $('<div>');
  $('<input>').attr({
    type: 'text',
    id: 'component-search'
  }).css({
    margin: '5px',
    width: '75%'
  }).appendTo(searchDiv);

  // 検索釦追加
  var searchBtn = $('<button>');
  searchBtn.attr('id', 'component-search-button');
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
 * コンフィギュレーション領域作成処理
 */
function createConfigulationPanel() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'configulation-panel');
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

  // コンポーネント釦追加
  var componentBtn = $('<button type="button">');
  componentBtn.attr('id', 'component-button');
  componentBtn.html('Component')
  componentBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '110px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-circlesmall-minus').appendTo(componentBtn);
  $(componentBtn).on('click', function (event) {
    toggleComponentPanel('component-button', 'left');
  });
  panel.append(componentBtn);

  // プロパティ釦追加
  var propertyBtn = $('<button type="button">');
  propertyBtn.attr('id', 'property-button');
  propertyBtn.html('Property')
  propertyBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '110px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-circlesmall-plus').appendTo(propertyBtn);
  $(propertyBtn).on('click', function (event) {
    toggleComponentPanel('property-button', 'right');
  });
  panel.append(propertyBtn);

  // コンフィギュレーション釦追加
  var configBtn = $('<button type="button">');
  configBtn.attr('id', 'configuration-button');
  configBtn.html('Configuration')
  configBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '110px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-circlesmall-minus').appendTo(configBtn);
  $(configBtn).on('click', function (event) {
    toggleComponentPanel('configuration-button', 'preview');
  });
  panel.append(configBtn);

  // 作業領域選択
  var workspaceSelect = $('<select>');
  workspaceSelect.attr('id', 'workspace-selector');
  workspaceSelect.css('float', 'right').css('height', '27px');
  $(workspaceSelect).on('change', function () {
    // 作業領域設定
    curWorkspaceName = $(this).val();
    // 作業領域再読み込み
    reloadAllWorkspace();
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
          { type: 'menu', id: 'save-menu', caption: 'Save All', icon: 'fa fa-floppy-o' },
          { type: 'menu', id: 'git-menu', caption: 'Git Reposity Link', icon: 'fa fa-cloud-upload' }
        ]
      },
      {
        type: 'menu', id: 'component-menu', caption: 'Component', icon: 'fa fa-laptop',
        items: [
          { type: 'menu', id: 'build-menu', caption: 'Buid All', icon: 'fa fa-link' },
          { type: 'menu', id: 'clean-menu', caption: 'Clean All', icon: 'fa fa-chain-broken' },
          { type: 'menu', id: 'run-menu', caption: 'Run Package', icon: 'fa fa-play' },
          { type: 'menu', id: 'terminate-menu', caption: 'Terminate Package', icon: 'fa fa-stop' }
        ]
      },
      {
        type: 'menu', id: 'tool-menu', caption: 'Tools', icon: 'fa fa-tasks',
        items: [
          { type: 'menu', id: 'console-menu', caption: 'Open Console', icon: 'fa fa-television' },
          { type: 'menu', id: 'dataset-menu', caption: 'Open Dataset Monitor', icon: 'fa fa-television' },
          { type: 'menu', id: 'rtsprofile-setting', caption: 'Package Setting', icon: 'fa fa-cog' }
        ]
      },
      {
        type: 'menu', id: 'keras-menu', caption: 'Frameworks', icon: 'fa fa-external-link',
        items: [
          { type: 'menu', id: 'keras-menu', caption: 'Open Keras Editor', icon: 'fa fa-external-link' },
        ]
      },
      { type: 'spacer' },
      { type: 'button', id: 'current-state', caption: 'Mode : Edit', icon: 'fa fa-info-circle' }
    ],
    onClick: function (event) {
      // クリックされた時のイベント
      if (event.subItem) {
        switch (event.subItem.id) {
          case 'save-menu':
            // 保存
            updatePackage(true);
            break;
          case 'git-menu':
            // Git連携
            openPackageGitCommitPushPopup();
            break;
          case 'build-menu':
            // ビルド
            if (mainRtsMap[curWorkspaceName]) {
              buildPackageAll(curWorkspaceName);
              // コンソール表示
              openConsoleLog();
            }
            break;
          case 'clean-menu':
            // ビルド
            if (mainRtsMap[curWorkspaceName]) {
              cleanPackageAll(curWorkspaceName);
              // コンソール表示
              openConsoleLog();
            }
            break;
          case 'run-menu':
            // 実行
            if (mainRtsMap[curWorkspaceName]) {
              runPackage(curWorkspaceName);
              // コンソール表示
              openConsoleLog();
            }
            break;
          case 'terminate-menu':
            // 停止
            if (mainRtsMap[curWorkspaceName]) {
              terminatePackage(curWorkspaceName);
              // コンソール表示
              openConsoleLog();
            }
            break;
          case 'rtsprofile-setting':
            // RtsProfile設定画面表示
            if (mainRtsMap[curWorkspaceName]) {
              openPackageProfileSetting();
            }
            break;
          case 'console-menu':
            // コンソール表示
            openConsoleLog();
            break;
          case 'dataset-menu':
            // データセットモニタ
            openDatasetMonitor();
            break;
          case 'keras-menu':
            // Keras表示
            openKerasEditor();
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
  if (curWorkspaceName && mainRtsMap) {
    var options = [];
    for (key in mainRtsMap) {
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

/**
 * コンソールを表示する
 * 
 * @returns
 */
function openConsoleLog() {
  // ログ監視を開始する
  startTailLog();

  // コードエディタをポップアップ表示する
  w2popup.open({
    title: 'Console',
    width: 800,
    height: 600,
    showMax: true,
    body: '<div id="console-viewer-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #console-viewer-div').w2layout({
          name: 'layout-panel-console-viewer',
          padding: 0,
          panels: [
            { type: 'left', size: '50%', resizable: true, overflow: 'hidden', content: $('#wasanbon-log') },
            { type: 'main', size: '50%', resizable: true, overflow: 'hidden', content: $('#python-log') },
            { type: 'bottom', size: 25, content: '<input name="console-scroll-check" type="checkbox" checked="checked">Auto tail log and Scroll to bottom</input>' }
          ]
        });
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['layout-panel-console-viewer'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['layout-panel-console-viewer'].resize();
      }
    },
    onClose: function (event) {
      // ログ監視を停止する
      stopTailLog();

      w2ui['layout-panel-console-viewer'].destroy();

      var wasanbonLogDiv = $('<div>');
      wasanbonLogDiv.attr('id', 'wasanbon-log');
      wasanbonLogDiv.css('height', '100%').css('width', '99%');
      wasanbonLogDiv.appendTo('#wasanbon-log-parent');

      var wasanbonLogDiv = $('<div>');
      wasanbonLogDiv.attr('id', 'python-log');
      wasanbonLogDiv.css('height', '100%').css('width', '99%');
      wasanbonLogDiv.appendTo('#python-log-parent');

      createLogViewer();
    },
    onKeydown: function (event) {
      // ESCAPE key pressed
      if (event.originalEvent.keyCode == 27) {
        event.preventDefault()
      }
    }
  });
}

/**
 * Keras表示
 */
function openKerasEditor() {
  window.open(getUrlKerasMain());
}

/*******************************************************************************
 * コンポーネント領域
 ******************************************************************************/

/**
 * コンポーネント領域設定処理
 * 
 */
function setComponentAreaInfo() {
  // 検索用文字列取得
  var targetStr = $("#component-search").val();

  // コンポーネントエリアを初期化
  $('#component-panel').empty();

  // 検索窓追加
  var searchDiv = $('<div>');
  $('<input>').attr({
    type: 'text',
    id: 'component-search'
  }).css({
    margin: '5px',
    width: '75%'
  }).appendTo(searchDiv);

  // 検索釦追加
  var searchBtn = $('<button>');
  searchBtn.attr('id', 'component-search-button');
  searchBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '3px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-search').appendTo(searchBtn);
  $(searchBtn).on('click', function (event) {
    setComponentAreaInfo();
  });
  searchDiv.append(searchBtn);
  $('#component-panel').append(searchDiv);
  // 検索文字列戻す
  $("#component-search").val(targetStr);

  // 作業領域からコンポーネント領域アコーディオンを生成する
  if (Object.keys(mainRtsMap).length > 0) {
    $('#component-panel').append(createCompornentAccordionForWorkspace());
  }

  // コンポーネント領域アコーディオンを生成する
  var componentTabs = componentAreaData.componentTabs;
  $('#component-panel').append(createCompornentAccordion(componentTabs, 'graph', targetStr));

  // コンポーネント領域アコーディオンを有効化する
  $('.accordion').accordion({
    collapsible: true,
    heightStyle: "content"
  });

  // マークを描画し直す
  var rtcs = $('#component-panel rect[model-type = RTC]');
  if (rtcs && rtcs.length > 0) {
    for (var i = 0; i < rtcs.length; i++) {

      var text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      text.setAttribute('x', 4);
      text.setAttribute('y', 15);
      text.setAttribute('font-size', 16);
      text.setAttribute('font-weight', 'bold');
      text.setAttribute('fill', '#000');
      text.textContent = 'C';
      $(rtcs[i]).parent().append(text);
    }
  }
  var rtss = $('#component-panel rect[model-type = RTS]');
  if (rtss && rtss.length > 0) {
    for (var i = 0; i < rtss.length; i++) {

      if ($($(rtss[i]).parents('#graph-component-workspace')).length > 0) {
        // workspace
        var modelId = $(rtss[i]).parent().parent().attr('model-id');
        if (modelId === curWorkspaceName) {
          $(rtss[i]).attr('fill', 'pink');
        }
      } else {
        var text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        text.setAttribute('x', 4);
        text.setAttribute('y', 15);
        text.setAttribute('font-size', 16);
        text.setAttribute('font-weight', 'bold');
        text.setAttribute('fill', '#000');
        text.textContent = 'P';
        $(rtss[i]).parent().append(text);
      }
    }
  }

  // UbuntuChrome対応でポートの位置を再描画する
  if ($('.joint-port', '#graph-Rtc')) {
    for (var i = 0; i < $('.joint-port', '#graph-Rtc').length; i++) {
      $('.joint-port', '#graph-Rtc')[i].setAttribute('transform', $('.joint-port', '#graph-Rtc')[i].getAttribute('transform'));
    }
  }
}

/**
 * コンポーネント領域のアコーディオン作成処理
 * 
 * @param 領域情報
 * @param 親領域名称
 * @param 検索文字列
 */
function createCompornentAccordion(tabs, parentName, targetName) {
  // アコーディオン親
  var cmpAc = $('<div>');
  cmpAc.attr('class', 'accordion');

  if (tabs && tabs.length > 0) {
    for (var i = 0; i < tabs.length; i++) {
      var section = $('<h3>');
      // 表示名
      var tabName = tabs[i].tabName;
      section.html(tabName);

      var isNew = tabName.indexOf("New") >= 0;

      cmpAc.append(section);

      // 描画領域作成
      var jointArea;
      // 子供領域存在チェック
      var childTabs = tabs[i].childTabs;
      if (childTabs && childTabs.length > 0) {
        // 子供領域が存在する場合 -> 子供領域についてアコーディオン生成
        jointArea = createCompornentAccordion(childTabs, tabName, targetName);
      } else {
        // 子供領域が存在しない場合 -> 描画領域生成
        var divName = parentName + '-' + tabName;
        jointArea = $('<div>');
        jointArea.attr('id', divName);
        jointArea.attr('class', 'joint-area');

        // 描画オブジェクト生成
        createComponentGraphArea(jointArea, tabs[i].rtss, tabs[i].rtcs, false, isNew, targetName);
      }
      cmpAc.append(jointArea);
    }
  }
  return cmpAc;
}

/**
 * 作業領域情報からコンポーネント領域のアコーディオン作成処理
 */
function createCompornentAccordionForWorkspace() {
  // アコーディオン親
  var cmpAc = $('<div>');
  cmpAc.attr('class', 'accordion');

  var section = $('<h3>');
  // 表示名
  section.html('Workspace');
  cmpAc.append(section);

  // 描画領域作成
  var jointArea = $('<div>');
  jointArea.attr('id', 'graph-component-workspace');
  jointArea.attr('class', 'joint-area');

  for (key in mainRtsMap) {
    var arr = [];
    arr.push($.extend(true, {}, mainRtsMap[key]));
    createComponentGraphArea(jointArea, arr, null, true, false, '');
  }
  cmpAc.append(jointArea);
  return cmpAc;
}

/**
 * コンポーネントGraph領域作成処理
 * 
 * @param 親DIVタグ
 */
function createComponentGraphArea(areaElm, rtsystems, rtcomponents, isWork, isNew, targetName) {
  // 不正なデータを除外する
  var rtss = [];
  if (rtsystems && rtsystems.length > 0) {
    for (var i = 0; i < rtsystems.length; i++) {
      if (rtsystems[i].modelProfile.modelId) {
        rtss.push(rtsystems[i]);
      }
    }
  }
  var rtcs = [];
  if (rtcomponents && rtcomponents.length > 0) {
    for (var i = 0; i < rtcomponents.length; i++) {
      if (rtcomponents[i].modelProfile.modelId) {
        rtcs.push(rtcomponents[i]);
      }
    }
  }

  var height = 35;
  var rtssSize = 0;
  var allSize = 0;

  // 表示対象のものだけ表示するため、個数を調べる
  if (rtss && rtss.length > 0) {
    for (var i = 0; i < rtss.length; i++) {
      if (isWork || isNew || targetName == '' || rtss[i].modelProfile.modelName.toUpperCase().indexOf(targetName.toUpperCase()) >= 0) {
        rtssSize++;
        allSize++;
      }
    }
  }
  if (rtcs && rtcs.length > 0) {
    for (var i = 0; i < rtcs.length; i++) {
      if (isNew || targetName == '' || rtcs[i].modelProfile.modelName.toUpperCase().indexOf(targetName.toUpperCase()) >= 0) {
        allSize++;
      }
    }
  }

  if (allSize > 0) {
    height = 35 * allSize;
  }

  var graph = new joint.dia.Graph;
  var paper = new joint.dia.Paper({ el: areaElm, width: '100%', height: height, gridSize: 1, model: graph, interactive: false });

  paper.on('cell:pointerdown', function (cellView, e, x, y) {
    dragComponent(cellView, e, x, y);
  });

  var rtcCounter = 0;
  if (rtcs && rtcs.length > 0) {
    for (var i = 0; i < rtcs.length; i++) {
      if (isNew || targetName == '' || rtcs[i].modelProfile.modelName.toUpperCase().indexOf(targetName.toUpperCase()) >= 0) {
        // 表示対象

        // 描画オブジェクト作成
        graph.addCell(createRtcomponentViewObject(rtcs[i].rtcProfile.id, rtcs[i].modelProfile.modelId, rtcs[i].modelProfile.modelName,
          rtcs[i].rtcProfile.dataPorts, rtcs[i].rtcProfile.servicePorts, 10, (rtcCounter + rtssSize) * 35 + 10, true));

        if (isWork === false) {
          // mapに保持しておく
          componentMap[rtcs[i].modelProfile.modelId] = rtcs[i];
        }
        rtcCounter++;
      }
    }
  }

  var rtsCounter = 0;
  if (rtss && rtss.length > 0) {
    for (var i = 0; i < rtss.length; i++) {
      if (isWork || isNew || targetName == '' || rtss[i].modelProfile.modelName.toUpperCase().indexOf(targetName.toUpperCase()) >= 0) {
        // 表示対象

        // 描画オブジェクト作成
        if (isWork) {
          graph.addCell(createRtsystemViewObject(rtss[i].modelProfile.modelId, rtss[i].rtsProfile.id.split(':')[2], 10, rtsCounter * 35 + 10));
        } else {
          graph.addCell(createRtsystemViewObject(rtss[i].modelProfile.modelId, rtss[i].modelProfile.modelName, 10, rtsCounter * 35 + 10));
        }

        if (isWork === false) {
          // mapに保持しておく
          systemMap[rtss[i].modelProfile.modelId] = rtss[i];
          // Package内にしか存在しないRTCはMapにのみ追加しておく
          if (rtss[i].rtcs && rtss[i].rtcs.length > 0) {
            for (var j = 0; j < rtss[i].rtcs.length; j++) {
              if (!componentMap[rtss[i].rtcs[j].modelProfile.modelId]) {
                componentMap[rtss[i].rtcs[j].modelProfile.modelId] = rtss[i].rtcs[j];
              }
            }
          }
        }
        rtsCounter++;
      }
    }
  }
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
      attrs: { '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' } }
    }),
    validateConnection: function (cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
      // 接続可能なポートかのチェック
      return validateConnection(cellViewS, magnetS, cellViewT, magnetT);
    },
    validateMagnet: function (cellView, magnet) {
      // 接続先はInPortのみ
      return magnet.getAttribute('magnet') !== 'passive';
    },
    // 折れ線の大きさ
    snapLinks: { radius: 75 },
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

  // 作業領域イベント関連付
  setEventMainPaper();
}

/**
 * 接続可能なポートかをチェックする
 * 
 * @param cellViewS
 * @param magnetS
 * @param cellViewT
 * @param magnetT
 * @returns
 */
function validateConnection(cellViewS, magnetS, cellViewT, magnetT) {
  var result = true;
  if (cellViewS === cellViewT || magnetS === null || magnetT === null) {
    // 接続元と接続先が同じ場合、対象がNullの場合はNG
    result = false;
  } else {
    // 接続元がDataPortかServicePortかで処理を分岐する
    if (magnetS.getAttribute('port-type') === 'DataInPort') {
      // DataInPortの場合は無条件でNG
      result = false;
    } else if (magnetS.getAttribute('port-type') === 'DataOutPort') {
      // DataOutPortの場合はDataInPortでなければNG
      if (magnetT.getAttribute('port-group') !== 'in' || magnetT.getAttribute('port-type') !== 'DataInPort') {
        result = false;
      }
    } else if (magnetS.getAttribute('port-type') === 'ServicePort') {
      // ServicePortの場合はServicePortでなければNG
      if (magnetT.getAttribute('port-type') !== 'ServicePort') {
        result = false;
      }
    }
  }
  return result;
}


/**
 * 作業領域イベント関連付
 * 
 * @returns
 */
function setEventMainPaper() {
  // コンポーネントマウスダウン
  mainPaper.off('cell:pointerdown');
  mainPaper.on('cell:pointerdown', function (cellView, e, x, y) {
    var toolRemove = $(e.target).parents('.tool-remove')[0];
    // リンクの削除マーククリック
    if (toolRemove) {
      // モデルから削除する
      deleteDataPortConnectionInfo(cellView.model.id);
      deleteServicePortConnectionInfo(cellView.model.id);
    }
  });

  // コンポーネントクリック
  mainPaper.off('cell:pointerclick');
  mainPaper.on('cell:pointerclick', function (cellView, e, x, y) {
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

    if (cellView.model instanceof joint.dia.Link) {
      // リンクのクリック
      // コンフィギュレーション設定の削除
      clearComponentGridData();
      clearConfigurationGridData();
      // RtsProfile設定エリア表示
      setPropertyAreaRtsProfile();
    } else {
      // コンポーネントのクリック
      // コンフィギュレーション設定
      setComponentGridData(cellView.model.attributes.componentId);
      // RtcProfile設定エリア表示
      setPropertyAreaRtcProfile(cellView.model.attributes.componentId);
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

    if (cellView.model instanceof joint.dia.Link) {
      // リンクのクリック
      // コンフィギュレーション設定の削除
      clearComponentGridData();
      clearConfigurationGridData();
      // RtsProfile設定エリア表示
      setPropertyAreaRtsProfile();
    } else {
      // コンポーネントのクリック
      // コンフィギュレーション設定
      setComponentGridData(cellView.model.attributes.componentId);
      // RtcProfile設定エリア表示
      setPropertyAreaRtcProfile(cellView.model.attributes.componentId);
    }
  });

  // コンポーネント外クリック
  mainPaper.off('blank:pointerclick');
  mainPaper.on('blank:pointerclick', function (cellView, e, x, y) {
    // ハイライト非表示
    unHighLightAll();
    // コンフィギュレーション設定の削除
    clearComponentGridData();
    clearConfigurationGridData();
    // RtsProfile設定エリア表示
    setPropertyAreaRtsProfile();
  });

  // コンポーネント接続イベント
  mainPaper.off('link:connect');
  mainPaper.on('link:connect', function (linkView, e, connectedToView, magnetElement, type) {
    var sourcePortData = $('text', linkView.sourceMagnet.parentElement).data();
    var targetPortData = $('text', linkView.targetMagnet.parentElement).data();

    // モデルに追加
    if (linkView.sourceMagnet.getAttribute('port-type') === 'DataOutPort' && linkView.targetMagnet.getAttribute('port-type') === 'DataInPort') {
      // データポートの場合
      addDataPortConnectionInfo(linkView.model.id, sourcePortData, targetPortData);
    } else {
      // サービスポートの場合
      addServicePortConnectionInfo(linkView.model.id, sourcePortData, targetPortData);
    }
  });

  // コンポーネント接続解除イベント
  mainPaper.off('link:disconnect');
  mainPaper.on('link:disconnect', function (linkView, e, disconnectedFrom, magnetElement, type) {
    // 画面・モデルから削除
    deleteDataPortConnectionInfo(linkView.model.id);
    deleteServicePortConnectionInfo(linkView.model.id);
  });
}

/**
 * 作業領域をすべて展開する
 * 
 * @param rtsList
 * @returns
 */
function loadAllPackagesWorkspace(rtsList) {
  for (var i = 0; i < rtsList.length; i++) {
    if (!curWorkspaceName) {
      // 作業領域が指定されていない場合は１件目を指定
      curWorkspaceName = rtsList[i].modelProfile.modelId;
    }
    workspaceCounter++;

    // 展開開始
    if (curWorkspaceName === rtsList[i].modelProfile.modelId) {
      // 現在の作業領域と同じ場合は展開する

      // 実行状況を確認しておく
      if (isRunningPackage(curWorkspaceName)) {
        setState(STATE.EXEC);
      } else {
        setState(STATE.EDIT);
      }

      loadPackageWorkspace(100, 100, rtsList[i]);
    } else {
      // それ以外の場合はMapに追加するのみ
      mainRtsMap[rtsList[i].modelProfile.modelId] = $.extend(true, {}, rtsList[i]);
    }
  }
  // 作業領域選択コンボ設定
  setWorkspaceSelectMenu();
  // RtsProfile設定領域表示
  setPropertyAreaRtsProfile();
  // コンポーネント領域再描画
  setComponentAreaInfo();
}

/**
 * 作業領域を再展開する
 * 
 * @param posXDef
 * @param posYDef
 * @param rtsystem
 * @returns
 */
function reloadPackageWorkspace(posXDef, posYDef, rtsystem) {
  // 一旦画面からすべてを削除
  deleteAllComponentsViewObject();
  // 作業領域再読み込み
  loadPackageWorkspace(posXDef, posYDef, rtsystem);
}

/**
 * 作業領域を展開する
 * 
 * @param posXDef
 * @param posYDef
 * @param rtsystem
 * @returns
 */
function loadPackageWorkspace(posXDef, posYDef, rtsystem) {
  // Mapに追加する
  mainRtsMap[rtsystem.modelProfile.modelId] = $.extend(true, {}, rtsystem);

  // RTSに含まれるRTCを展開していく
  var rtcs = rtsystem.rtcs;
  if (rtcs && rtcs.length > 0) {

    // 描画位置を設定
    var posX = posXDef;
    var posY = posYDef;

    for (var i = 0; i < rtcs.length; i++) {
      // ドロップされたRTCをコンポーネント領域オブジェクトマップから取得
      var rtcomponent = componentMap[rtcs[i].modelProfile.modelId];

      // システム内のコンポーネント情報も取得
      var rtcDef = null;
      if (rtsystem.rtsProfile.componentMap[rtcs[i].rtcProfile.id] && rtsystem.rtsProfile.componentMap[rtcs[i].rtcProfile.id].length > 0) {
        rtcDef = rtsystem.rtsProfile.componentMap[rtcs[i].rtcProfile.id][0];
      }

      // rtcDefが存在しない->git定義にのみ存在しているので除外する
      if (!rtcDef) {
        continue;
      }

      // 非表示のRTCは表示しない
      if (rtcDef.visible === false) {
        continue;
      }

      // 作業領域用にオブジェクトを再作成する
      var rtcId = rtcs[i].rtcProfile.id;
      var modelId = rtcs[i].modelProfile.modelId + counter;
      counter++;

      // インスタンス名が定義されている場合はそれを用いる
      if (rtcDef.instanceName) {
        modelId = rtcDef.instanceName;
      }

      var s = createRtcomponentViewObject(rtcId, modelId, rtcs[i].modelProfile.modelName, rtcs[i].rtcProfile.dataPorts, rtcs[i].rtcProfile.servicePorts, 0, 0, false);

      // 位置が定義されている場合はそれを用いる
      if (rtcDef.location && rtcDef.location.x && rtcDef.location.y && (rtcDef.location.x > 0 || rtcDef.location.y > 0)) {
        posX = rtcDef.location.x;
        posY = rtcDef.location.y;
      } else if (i > 0) {
        posX += 300;
        if (posX > mainPaper.$el.width()) {
          posX = posXDef;
          posY += 300;
        }
      }
      s.position(posX, posY);

      // 作業領域に追加
      mainGraph.addCell(s);

      // 作業領域イベント関連付
      setEventMainPaper();

      // RTCイベント関連付
      setEventRtcomponent(rtcId, modelId, rtcs[i]);
    }

    // データポートリンク情報を再現する
    setDataPortConnectionInfo();

    // サービスポートリンク情報を再現する
    setServicePortConnectionInfo();
  }
}

/**
 * コンポーネント領域から作業領域へのドラッグ処理
 * 
 * @param cellView
 * @param e
 * @param x
 * @param y
 * @returns
 */
function dragComponent(cellView, e, x, y) {
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
  $("#flyPaper").offset({ left: e.pageX - offset.x, top: e.pageY - offset.y });

  // マウス移動イベントを設定
  $('body').on('mousemove.fly', function (e) {
    $("#flyPaper").offset({ left: e.pageX - offset.x, top: e.pageY - offset.y });
  });

  // マウスアップイベントを設定
  $('body').on('mouseup.fly', function (e) {
    var x = e.pageX;
    var y = e.pageY;
    var target = $('#main-joint-area').offset();

    if (x > target.left && x < target.left + $('#main-joint-area').width() && y > target.top && y < target.top + $('#main-panel').height()) {
      // 対象の領域内でマウスが離された場合
      dropComponent(x, y, offset, target, targetId);
    }
    // イベントを解除
    $('body').off('mousemove.fly').off('mouseup.fly');

    // 追加したオブジェクトを削除
    flyShape.remove();
    $('#flyPaper').remove();
  });
};

/**
 * コンポーネント領域から作業領域へのドロップ処理
 * 
 * @param x
 * @param y
 * @param offset
 * @param target
 * @param modelId
 * @returns
 */
function dropComponent(x, y, offset, target, modelId) {
  if (modelId.indexOf('rts_work') == 0) {
    // 作業領域をドロップされた場合
    curWorkspaceName = modelId;
    // 作業領域再読み込み
    reloadAllWorkspace();
  } else if (modelId.indexOf('rts') == 0) {
    // RTSをドロップされた場合

    // 新規パッケージ作成
    openNewPackageProfileSetting(modelId);

  } else if (modelId.indexOf('rtc') == 0) {
    // RTCをドロップされた場合
    if (curState != STATE.EDIT) {
      w2alert('実行中はシステム構成を変更できません');
      return;
    }

    if (modelId.indexOf('rtc_blank') == 0) {
      // 新規コンポーネント作成
      openNewRtcProfileSettingPopup(modelId);
    } else {
      // 既存パッケージのコピー
      addComponent(modelId);
    }
  }
};

/**
 * 次の作業領域名を取得する
 * 
 * @param workspaceName
 * @returns
 */
function getNextWorkspaceName(workspaceName) {
  var result = workspaceName;
  if (mainRtsMap[workspaceName]) {
    // 存在する場合は_1をつけてチェックする
    result = getNextWorkspaceName(workspaceName + '_1');
  }
  return result;
}

/**
 * 作業エリアの状態を設定する
 * 
 * @param nextState
 * @returns
 */
function setState(nextState) {
  // 状態を更新
  curState = nextState;

  // イベントを設定
  if (curState !== STATE.EDIT) {
    // ポートのイベントを全て無効化
    $('path').css('pointer-events', 'none');
    // コネクション情報削除のツールを非表示
    $('.link-tools .tool-remove').css('display', 'none');
    $('.marker-arrowhead').css('display', 'none');
  } else {
    // ポートのイベントを全て無効化解除
    $('path').css('pointer-events', 'auto');
    // コネクション情報削除のツールを表示
    $('.link-tools .tool-remove').css('display', 'inline');
    $('.marker-arrowhead').css('display', 'inline');
  }

  // 表記を変更
  if (curState === STATE.INIT) {
    w2ui['toolbar'].set('current-state', { caption: 'Mode : Init' });
  } else if (curState === STATE.EDIT) {
    w2ui['toolbar'].set('current-state', { caption: 'Mode : Edit' });
  } else if (curState === STATE.EXEC) {
    w2ui['toolbar'].set('current-state', { caption: 'Mode : Exec' });
  }
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
 * 画面をロックする
 * 
 * @returns
 */
function lockScreen() {
  if (lockCnt === 0) {
    w2utils.lock($('#main-joint-area'), "Loading...", false);
  }
  lockCnt++;
}

/**
 * 画面のロックを解除する
 * 
 * @returns
 */
function unlockScreen() {
  lockCnt--;
  if (lockCnt <= 0) {
    w2utils.unlock($('#main-joint-area'));
    lockCnt = 0;
  }
}

/**
 * プロパティ領域にRtsProfileの設定Formを表示する
 * 
 * @returns
 */
function setPropertyAreaRtsProfile() {
  if (curWorkspaceName && mainRtsMap) {
    // RtsProfile設定Formを設定する
    $('#property-panel').w2form(createRtsProfileSettingForm(mainRtsMap[curWorkspaceName].rtsProfile, mainRtsMap[curWorkspaceName].modelProfile, true, true));
    // 釦を変更する
    $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
  }
}

/**
 * プロパティ領域にRtcProfileの設定Formを表示する
 * 
 * @param componentId
 * @returns
 */
function setPropertyAreaRtcProfile(componentId) {
  // RtsProfile設定Formを設定する
  var rtc = null;
  var rtcIndex = 0;
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        rtcIndex = i;
        rtc = mainRtsMap[curWorkspaceName].rtcs[i];
        break;
      }
    }
  }
  $('#property-panel').w2form(createRtcProfileSettingForm(rtc, rtcIndex, true, true));
  // 釦を変更する
  $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
  $($('.w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '140px').css('font-size', '1.2em');
}

/*******************************************************************************
 * コンフィギュレーション領域
 ******************************************************************************/

/**
 * コンフィギュレーション領域のレイアウト処理
 * 
 * @returns
 */
function layoutPanelConfiguration() {
  var pstyle = 'border: none; padding: 0px;';
  $('#configulation-panel').w2layout({
    name: 'configulation-panel',
    padding: 0,
    panels: [
      { type: 'left', size: 400, resizable: true, style: pstyle, content: createConfiguretionComponent() },
      { type: 'main', style: pstyle, content: createConfiguretionSetting() },
      { type: 'right', size: 80, style: pstyle, content: createConfiguretionButtons() }
    ]
  });

  // 釦をDisableにしておく
  $('#component-grid-clone-btn').button();
  $('#component-grid-clone-btn').button('disable');
  $('#component-grid-add-btn').button();
  $('#component-grid-add-btn').button('disable');
  $('#component-grid-delete-btn').button();
  $('#component-grid-delete-btn').button('disable');
  $('#configuraiton-grid-add-btn').button();
  $('#configuraiton-grid-add-btn').button('disable');
  $('#configuraiton-grid-delete-btn').button();
  $('#configuraiton-grid-delete-btn').button('disable');
  $('#configuraiton-edit-btn').button();
  $('#configuraiton-edit-btn').button('disable');
  $('#configuraiton-apply-btn').button();
  $('#configuraiton-apply-btn').button('enable');
  $('#configuraiton-cancel-btn').button();
  $('#configuraiton-cancel-btn').button('enable');
}

/**
 * コンフィギュレーション領域左部分
 * 
 * @returns
 */
function createConfiguretionComponent() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'configuration-component');
  panel.css('background-color', '#fff');
  var table = $('<table>');
  table.css('width', '100%').css('height', '100%');

  // コンポーネント名設定
  var trTop = $('<tr>');
  trTop.attr('height', '25px')
  var tdTopLeft = $('<td>');
  tdTopLeft.html('ComponentName');
  tdTopLeft.css('width', '120px');
  tdTopLeft.css('font-size', '12px');
  var tdTopRight = $('<td>');
  $('<input>').attr({
    type: 'text',
    id: 'configuration-component-text',
    readonly: 'readonly'
  }).css({
    width: '100%'
  }).appendTo(tdTopRight);

  // Grid表示
  var trMiddle = $('<tr>');
  var tdMiddle = $('<td>');
  tdMiddle.attr('colspan', 2);
  tdMiddle.attr('height', '100%');
  var gridDiv = $('<div>');
  gridDiv.attr('id', 'component-grid-panel');
  gridDiv.css('width', '100%').css('height', '100%');
  tdMiddle.append(gridDiv);

  // 釦表示
  var trBottom = $('<tr>');
  var tdBottom = $('<td>');
  tdBottom.attr('colspan', 2);
  tdBottom.attr('height', '25%');
  var btnDiv = $('<div>');
  btnDiv.css('width', '100%').css('height', '100%');
  tdBottom.append(btnDiv);

  // 複製釦
  var cloneBtn = $('<button type="button">');
  cloneBtn.html('Copy')
  cloneBtn.attr('id', 'component-grid-clone-btn');
  cloneBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-copy').appendTo(cloneBtn);
  $(cloneBtn).on('click', function (event) {
    copyConfigurationGridData($('#configuration-component-text').val(), $('#configuration-setting-text').val());
  });
  btnDiv.append(cloneBtn);

  // 追加釦
  var addBtn = $('<button type="button">');
  addBtn.html('Add')
  addBtn.attr('id', 'component-grid-add-btn');
  addBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-plus').appendTo(addBtn);
  $(addBtn).on('click', function (event) {
    addConfigurationGridData($('#configuration-component-text').val(), $('#configuration-setting-text').val());
  });
  btnDiv.append(addBtn);

  // 削除釦
  var delBtn = $('<button type="button">');
  delBtn.html('Delete')
  delBtn.attr('id', 'component-grid-delete-btn');
  delBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-trash').appendTo(delBtn);
  $(delBtn).on('click', function (event) {
    deleteConfigurationGridData($('#configuration-component-text').val(), $('#configuration-setting-text').val());
  });
  btnDiv.append(delBtn);

  trTop.append(tdTopLeft);
  trTop.append(tdTopRight);
  trMiddle.append(tdMiddle);
  trBottom.append(tdBottom);

  table.append(trTop);
  table.append(trMiddle);
  table.append(trBottom);

  panel.append(table);

  return panel;
}

/**
 * コンフィギュレーション領域右部分
 * 
 * @returns
 */
function createConfiguretionSetting() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.attr('id', 'configuration-setting');
  panel.css('background-color', '#fff');
  var table = $('<table>');
  table.css('width', '100%').css('height', '100%');

  // コンポーネント名設定
  var trTop = $('<tr>');
  trTop.attr('height', '25px')
  var tdTopLeft = $('<td>');
  tdTopLeft.html('ConfigurationSet:');
  tdTopLeft.css('width', '120px');
  tdTopLeft.css('font-size', '12px');
  var tdTopRight = $('<td>');
  $('<input>').attr({
    type: 'text',
    id: 'configuration-setting-text',
    readonly: 'readonly'
  }).css({
    width: '100%'
  }).appendTo(tdTopRight);

  // Grid表示
  var trMiddle = $('<tr>');
  var tdMiddle = $('<td>');
  tdMiddle.attr('colspan', 2);
  tdMiddle.attr('height', '100%');
  var gridDiv = $('<div>');
  gridDiv.attr('id', 'configuraiton-grid-panel');
  gridDiv.css('width', '100%').css('height', '100%');
  tdMiddle.append(gridDiv);

  // 釦表示
  var trBottom = $('<tr>');
  var tdBottom = $('<td>');
  tdBottom.attr('colspan', 2);
  tdBottom.attr('height', '25%');
  var btnDiv = $('<div>');
  btnDiv.css('width', '100%').css('height', '100%');
  tdBottom.append(btnDiv);

  // 追加釦
  var addBtn = $('<button type="button">');
  addBtn.html('Add')
  addBtn.attr('id', 'configuraiton-grid-add-btn');
  addBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-plus').appendTo(addBtn);
  btnDiv.append(addBtn);

  // 削除釦
  var delBtn = $('<button type="button">');
  delBtn.html('Delete')
  delBtn.attr('id', 'configuraiton-grid-delete-btn');
  delBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-trash').appendTo(delBtn);
  btnDiv.append(delBtn);

  trTop.append(tdTopLeft);
  trTop.append(tdTopRight);
  trMiddle.append(tdMiddle);
  trBottom.append(tdBottom);

  table.append(trTop);
  table.append(trMiddle);
  table.append(trBottom);

  panel.append(table);

  return panel;
}

/**
 * コンフィギュレーション設定
 * 
 * @returns
 */
function createConfiguretionButtons() {
  // 背景
  var panel = $('<div>');
  panel.attr('class', 'panel');
  panel.css('background-color', '#fff');

  // 編集釦
  var editBtn = $('<button type="button">');
  editBtn.html('Edit')
  editBtn.attr('id', 'configuraiton-edit-btn');
  editBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '2px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-pencil').appendTo(editBtn);
  panel.append(editBtn);

  // 適用釦
  var applyBtn = $('<button type="button">');
  applyBtn.html('Apply')
  applyBtn.attr('id', 'configuraiton-apply-btn');
  applyBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '10px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-play').appendTo(applyBtn);
  $(applyBtn).on('click', function (event) {
    updatePackage(false);
  });
  panel.append(applyBtn);

  // キャンセル釦
  var cancelBtn = $('<button type="button">');
  cancelBtn.html('Cancel')
  cancelBtn.attr('id', 'configuraiton-cancel-btn');
  cancelBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '10px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px');
  $('<span>').addClass('ui-icon').addClass('ui-icon-close').appendTo(cancelBtn);
  $(cancelBtn).on('click', function (event) {
    reloadConfigurationGridData($('#configuration-component-text').val());
  });
  panel.append(cancelBtn);

  return panel;
}

/**
 * コンフィギュレーション設定-コンポーネント部分のGrid作成
 * 
 * @returns
 */
function createComponentGrid() {
  $('#component-grid-panel').w2grid({
    name: 'component-grid',
    show: {
      selectColumn: true
    },
    multiSelect: false,
    columns: [
      { field: 'config', caption: 'config', size: '100%', editable: { type: 'text' } }
    ],
    onChange: function (event) {
      // 設定し直す
      saveComponentGridData($('#configuration-component-text').val(), event.value_previous, event.value_new);
    },
    onSelect: function (event) {
      // 選択したコンフィギュレーション設定の情報を設定する
      setConfigurationGridData($('#configuration-component-text').val(), event.recid);
    }
  });
}

/**
 * コンフィギュレーション設定-コンフィギュレーション部分のGrid作成
 * 
 * @returns
 */
function createConfigurationSetGrid() {
  $('#configuraiton-grid-panel').w2grid({
    name: 'configuration-grid',
    columns: [
      { field: 'name', caption: 'name', size: '20%', editable: { type: 'text' } },
      { field: 'value', caption: 'value', size: '80%', editable: { type: 'text' } }
    ],
    onChange: function (event) {
      saveConfigurationGridData($('#configuration-component-text').val(), $('#configuration-setting-text').val(), event.input_id.slice(-1) === '1', event.recid, event.value_new, event.value_original);
    }
  });
}

/**
 * 選択したコンポーネントの情報を設定する
 * 
 * @param componentId
 * @returns
 */
function setComponentGridData(componentId) {
  // 一度クリアする
  clearComponentGridData();

  // コンポーネント情報
  $('#configuration-component-text').val(componentId);

  // コンフィギュレーション設定取得
  var selectedConfig = 'default';

  // データ存在フラグ
  var isExistData = false;

  var addDataArray = [];
  if (mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {
    selectedConfig = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editActiveConfigurationSet;
    var configurationSetMap = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap;
    if (configurationSetMap) {
      for (key in configurationSetMap) {
        var addData = { recid: configurationSetMap[key][0]['id'], config: configurationSetMap[key][0]['id'] };
        addDataArray.push(addData);
      }
      isExistData = true;
    } else {
      var addData = { recid: 'default', config: 'default' };
      addDataArray.push(addData);
    }
  } else {
    var addData = { recid: 'default', config: 'default' };
    addDataArray.push(addData);
  }

  // 追加する
  w2ui['component-grid'].add(addDataArray);

  // １行目を選択状態とする
  w2ui['component-grid'].select(selectedConfig);

  // コンフィギュレーション設定も表示する
  // setConfigurationGridData(componentId, selectedConfig);

  // 釦Enable制御
  if (isExistData) {
    $('#component-grid-add-btn').button('enable');
    $('#component-grid-clone-btn').button('enable');
    $('#component-grid-delete-btn').button('enable');
  }
}

/**
 * 選択したコンフィギュレーションの情報を設定する
 * 
 * @param componentId
 * @param configurationId
 * @returns
 */
function setConfigurationGridData(componentId, configurationId) {
  // コンフィギュレーション部分をクリア
  clearConfigurationGridData();

  if (componentId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId]) {
    // 選択したレコードに対応するコンフィギュレーションを取得
    var configurationSet = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0];
    if (configurationSet && configurationSet.configurationDatas && configurationSet.configurationDatas.length > 0) {

      // コンポーネント情報
      $('#configuration-setting-text').val(configurationId);
      var addDataArray = [];
      for (var i = 0; i < configurationSet.configurationDatas.length; i++) {
        var addData = { recid: configurationSet.configurationDatas[i]['name'], name: configurationSet.configurationDatas[i]['name'], value: configurationSet.configurationDatas[i]['data'] };
        addDataArray.push(addData);
      }
      // 追加する
      w2ui['configuration-grid'].add(addDataArray);
    }
  }
}

/**
 * 選択したコンフィギュレーションの情報をコピーする
 * 
 * @param componentId
 * @param configurationId
 * @returns
 */
function copyConfigurationGridData(componentId, configurationId) {
  if (componentId && configurationId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {

    // コピー元
    var srcData = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId];

    // コピー後のID
    var newConfigurationId = configurationId + '_1';

    // コピーする
    var copyData = [];
    copyData.push($.extend(true, {}, srcData[0]));
    copyData[0]['id'] = newConfigurationId;

    // コピーしたデータを追加する
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[newConfigurationId] = copyData;

    // コピー先を選択状態とする
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editActiveConfigurationSet = newConfigurationId;

    // Gridに展開する
    setComponentGridData(componentId);
  }
}

/**
 * 選択したコンフィギュレーションの情報を元に新規で追加する
 * 
 * @param componentId
 * @param configurationId
 * @returns
 */
function addConfigurationGridData(componentId, configurationId) {
  if (componentId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {

    var newData = null;
    var newConfigurationId = null;
    if (configurationId) {
      // コピー元
      var srcData = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId];

      // コピー後のID
      newConfigurationId = configurationId + '_1';

      // コピーする
      newData = [];
      newData.push($.extend(true, {}, srcData));
      newData[0]['id'] = newConfigurationId;

      // すべてのValueを空にする
      for (var i = 0; i < newData[0].configurationDatas.length; i++) {
        newData[0].configurationDatas[i]['data'] = '';
      }
    } else {
      // 新規の場合
      newConfigurationId = 'default';
      newData = [];
      var newConfigurationSet = { 'id': newConfigurationId, 'configurationDatas': [] };
      var newConfigurationData = { 'name': '', 'data': '' };
      newConfigurationSet['configurationDatas'].push(newConfigurationData);
      newData.push(newConfigurationSet);
    }

    // コピーしたデータを追加する
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[newConfigurationId] = newData;

    // コピー先を選択状態とする
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editActiveConfigurationSet = newConfigurationId;

    // Gridに展開する
    setComponentGridData(componentId);
  }
}

/**
 * 選択したコンフィギュレーションを削除する
 * 
 * @param componentId
 * @param configurationId
 * @returns
 */
function deleteConfigurationGridData(modelId, configurationId) {
  if (componentId && configurationId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {
    // 削除する
    delete mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId];

    if (!mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap ||
      Object.keys(mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap).length === 0) {
      mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap = new Object();
      // 初期値は追加しておく
      var configurationSet = new Object();
      var configurationSets = [];
      configurationSet['id'] = 'default';
      configurationSets.push(configurationSet);
      mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap['default'] = configurationSets;
    }

    // Gridに展開する
    setComponentGridData(componentId);
  }
}

/**
 * コンフィギュレーションの設定を戻す
 * 
 * @param componentId
 * @returns
 */
function reloadConfigurationGridData(componentId) {
  if (componentId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {

    // 元の内容を展開し直す
    var orgData = $.extend(true, {}, mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].configurationSetMap);
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap = orgData;

    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editActiveConfigurationSet
      = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].activeConfigurationSet;

    // Gridに展開する
    setComponentGridData(componentId);
  }
}

/**
 * コンフィギュレーション名を変更する
 * 
 * @param componentId
 * @param oldConfigurationId
 * @param newConfigurationId
 * @returns
 */
function saveComponentGridData(componentId, oldConfigurationId, newConfigurationId) {
  if (!newConfigurationId || newConfigurationId === '') {
    // Gridに展開する
    setComponentGridData(componentId);
  } else if (componentId && oldConfigurationId && newConfigurationId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {

    // コピー元
    var srcData = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[oldConfigurationId];

    // コピーする
    var copyData = $.extend(true, {}, srcData);
    copyData[0]['id'] = newConfigurationId;

    // コピーしたデータを追加する
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[newConfigurationId] = copyData;

    // コピー元を削除する
    delete mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[oldConfigurationId];

    // コピー先を選択状態とする
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editActiveConfigurationSet = newConfigurationId;

    // Gridに展開する
    setComponentGridData(componentId);
  }
}

/**
 * コンフィギュレーション設定を変更する
 * 
 * @param componentId
 * @param configurationId
 * @param name
 * @param newValue
 * @returns
 */
function saveConfigurationGridData(componentId, configurationId, isValue, name, newValue, oldValue) {
  if (componentId && configurationId && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]
    && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId].length > 0) {

    // コピー元
    var srcData = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId];

    if (isValue) {
      // Valueの変更
      for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0].configurationDatas.length; i++) {
        if (mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0].configurationDatas[i]['name'] === name) {
          mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0].configurationDatas[i]['data'] = newValue;
          break;
        }
      }
    } else {
      // Nameの変更
      for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0].configurationDatas.length; i++) {
        if (mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0].configurationDatas[i]['name'] === oldValue) {
          mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].editConfigurationSetMap[configurationId][0].configurationDatas[i]['name'] = newValue;
        }
      }
    }

    // Gridに展開する
    setComponentGridData(componentId);
  }
}


/**
 * コンフィギュレーション設定-コンポーネント部分のGridをクリアする
 * 
 * @returns
 */
function clearComponentGridData() {
  w2ui['component-grid'].clear();
  w2ui['component-grid'].selectNone();
  $('#configuration-component-text').val(null);
  $('#component-grid-clone-btn').button('disable');
  $('#component-grid-add-btn').button('disable');
  $('#component-grid-delete-btn').button('disable');
}

/**
 * コンフィギュレーション設定-コンフィギュレーション部分のGridをクリアする
 * 
 * @returns
 */
function clearConfigurationGridData() {
  w2ui['configuration-grid'].clear();
  $('#configuration-setting-text').val(null);
  $('#configuraiton-grid-add-btn').button('disable');
  $('#configuraiton-grid-delete-btn').button('disable');
}

/*******************************************************************************
 * フッター領域
 ******************************************************************************/

/**
 * パネルの表示・非表示を切り替える
 */
function toggleComponentPanel(id, field) {
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
