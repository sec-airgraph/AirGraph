/*************************************************************************
 * 右クリックメニュー設定
 *************************************************************************/
/**
 * 作業領域の右クリックメニュー設定
 */
function setMainAreaContextMenu() {
  $.contextMenu({
    selector: '#main-joint-area',
    callback: function (key, options) {
      if (key === 'save') {
        // 保存
        saveModel();
      } else if (key === 'edit') {
        // data_maker設定
        openDataMakerPopup();
      } else if (key === 'delete') {
        // モデル削除
        w2confirm('表示中のModelを削除します。<br/>よろしいですか？', function (btn) {
          if (btn === 'Yes') {
            deleteModel();
          }
        });
      }
    },
    items: {
      'save': { name: 'save Model', icon: 'edit' },
      'sep1': '---------',
      'edit': { name: 'edit Data Maker', icon: 'edit' },
      'sep2': '---------',
      'delete': { name: 'remove Model', icon: 'delete' }
    }
  });
}

function openDataMakerPopup() {

  sourceEditor.setModel(Monaco.Editor.createModel(modelMap[curWorkspaceName].dataMakerStr, 'python'));

  w2popup.open({
    title: 'Source Code Editor',
    width: 900,
    height: 600,
    showMax: true,
    body: '<div id="datamaker-editor-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #datamaker-editor-div').w2layout({
          name: 'layout-panel-datamaker-editor',
          padding: 0,
          panels: [
            { type: 'main', minSize: 350, overflow: 'hidden', content: $('#monaco-editor') },
          ]
        });
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['layout-panel-datamaker-editor'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['layout-panel-datamaker-editor'].resize();
      }
    },
    onClose: function (event) {
      // 保存する
      modelMap[curWorkspaceName].dataMakerStr = sourceEditor.getValue();

      w2ui['layout-panel-datamaker-editor'].destroy();

      var sourceEditorDiv = $('<div>');
      sourceEditorDiv.attr('id', 'monaco-editor');
      sourceEditorDiv.css('height', '100%').css('width', '99%');
      sourceEditorDiv.appendTo('#monaco-editor-parent');
      createDataMakerEditor();
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
 * ソースエディタを生成する
 * 
 * @returns
 */
function createDataMakerEditor() {
  require.config({
    baseUrl: '/mimosa/',
  });

  require(["vs/editor/editor.main"], function () {
    // エディター領域を取得する
    var editorE = document.querySelector('#monaco-editor');

    // エディターに表示する文字列を組み立てる
    var content = [].join('\n');

    // エディターを生成する
    sourceEditor = Monaco.Editor.create(editorE, {
      value: content,
      mode: "python",
      readOnly: false,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      autoSize: true,
      scrollbar: {
        handleMouseWheel: true
      },
      renderWhitespace: true,
      fontSize: 12
    });
  });
}