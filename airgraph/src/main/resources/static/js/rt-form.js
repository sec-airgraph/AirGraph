/*************************************************************************
 * RtsProfile設定関連
 *************************************************************************/
/**
 * PackageProfile設定画面を表示する
 * 
 * @param {*} orgModelId orgModelId
 * @returns {undefined}
 */
function openNewPackageProfileSetting(orgModelId) {
  // コピー元から引用する
  var orgPackage = systemMap[orgModelId];

  var rtsProfile = new Object();
  var modelProfile = new Object();

  rtsProfile.id = orgPackage.rtsProfile.id;
  rtsProfile.sabstract = orgPackage.rtsProfile.sabstract;
  rtsProfile.version = orgPackage.rtsProfile.version;
  modelProfile.modelId = orgModelId;
  modelProfile.orgRemoteUrl = orgPackage.modelProfile.remoteUrl;

  // 作業領域数は最新に変更しておく
  updateWorkspaceCount();
  var nextWorkspace = getNextWorkspaceName(WORKSPACE_PREFIX + workspaceCounter);
  modelProfile.remoteUrl = getDefaultGitUrlBase() + nextWorkspace + '.git'

  w2popup.open({
    title: 'Package Setting',
    width: 500,
    height: 380,
    body: '<div id="rts-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #rts-profile-div').w2form(createRtsProfileSettingForm(rtsProfile, modelProfile, false, false));
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['rts-profile-setting'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['rts-profile-setting'].resize();
      }
    }
  });
}

/**
 * PackageProfile設定画面を表示する
 * 
 * @returns {undefined}
 */
function openPackageProfileSetting() {
  w2popup.open({
    title: 'Package Setting',
    width: 500,
    height: 340,
    body: '<div id="rts-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #rts-profile-div').w2form(createRtsProfileSettingForm(mainRtsMap[curWorkspaceName].rtsProfile, mainRtsMap[curWorkspaceName].modelProfile, false, true));
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['rts-profile-setting'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['rts-profile-setting'].resize();
      }
    },
    onClose: function () {
      $('#property-panel').w2form(createRtsProfileSettingForm(mainRtsMap[curWorkspaceName].rtsProfile, mainRtsMap[curWorkspaceName].modelProfile, true, true));
    }
  });
}

/**
 * RtsProfile設定用Formを生成する
 * 
 * @param {*} rtsProfile rtsProfile
 * @param {*} modelProfile modelProfile
 * @param {*} propertyAreaFlg propertyAreaFlg
 * @param {*} updateFlg updateFlg
 * @returns {Map} form
 */
function createRtsProfileSettingForm(rtsProfile, modelProfile, propertyAreaFlg, updateFlg) {
  destroySettingForm();

  var form = {
    name: 'rts-profile-setting',
    focus: -1,
    padding: 0,
    tabs: [
      { id: 'package-tab1', text: 'RTS Profile' }
    ],
    record: {
      package_system_name: rtsProfile.id.split(':').length > 2 ? rtsProfile.id.split(':')[2] : '',
      package_ads: rtsProfile.sabstract,
      package_ver: rtsProfile.version,
      package_vender: rtsProfile.id.split(':').length > 1 ? rtsProfile.id.split(':')[1] : '',
      package_orgRemoteUrl: modelProfile.orgRemoteUrl,
      package_remoteUrl: modelProfile.remoteUrl
    }
  };

  if (updateFlg === true) {
    form['fields'] = [
      { field: 'package_system_name', type: 'text', required: true, html: { label: 'System Name', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_ver', type: 'text', required: true, html: { label: 'Version', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_vender', type: 'text', required: true, html: { label: 'Vender Name', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_ads', type: 'textarea', required: true, html: { label: 'Abstract', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_remoteUrl', type: 'text', required: true, html: { label: 'Remote Repository', page: 0, attr: 'style="width:300px"' } },
    ];
    form['actions'] = {
      'Update': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (this.validate().length === 0) {
          mainRtsMap[curWorkspaceName].rtsProfile.id = 'RTSystem:' + $('#package_vender').val() + ':' + $('#package_system_name').val() + ':' + $('#package_ver').val();
          mainRtsMap[curWorkspaceName].rtsProfile.sabstract = $('#package_ads').val();
          mainRtsMap[curWorkspaceName].rtsProfile.version = $('#package_ver').val();
          mainRtsMap[curWorkspaceName].modelProfile.remoteUrl = $('#package_remoteUrl').val();
          // 保存
          if (propertyAreaFlg === false) {
            w2popup.close();
          }
          updatePackage(true);
        }
      }
    }
  } else {
    form['fields'] = [
      { field: 'package_system_name', type: 'text', required: true, html: { label: 'System Name', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_ver', type: 'text', required: true, html: { label: 'Version', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_vender', type: 'text', required: true, html: { label: 'Vender Name', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_ads', type: 'textarea', required: true, html: { label: 'Abstract', page: 0, attr: 'style="width:300px"' } },
      { field: 'package_orgRemoteUrl', type: 'text', required: false, html: { label: 'Original Remote Repository', page: 0, attr: 'style="width:300px" readonly=readonly' } },
      { field: 'package_remoteUrl', type: 'text', required: true, html: { label: 'Remote Repository', page: 0, attr: 'style="width:300px"' } },
    ];
    form['actions'] = {
      'Create': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (this.validate().length === 0) {
          w2popup.close();
          // 一旦画面からすべてを削除
          deleteAllComponentsViewObject();
          selectedCellViews = [];

          // 作業領域を追加する
          curWorkspaceName = getNextWorkspaceName(WORKSPACE_PREFIX + $('#package_system_name').val());
          workspaceCounter++;

          // Packageを追加して読み込み直す
          var id = 'RTSystem:' + $('#package_vender').val() + ':' + $('#package_system_name').val() + ':' + $('#package_ver').val();
          addPackage(modelProfile.modelId, id, $('#package_ads').val(), $('#package_ver').val(), $('#package_remoteUrl').val(), $('#package_system_name').val(), 'local');
        }
      }
    }
  }
  return form;
}

/*******************************************************************************
 * RtcProfile設定関連
 ******************************************************************************/
/**
 * RtcProfile設定画面を新規作成状態で起動する
 * 
 * @param {*} modelId modelId
 * @returns {undefined}
 */
function openNewRtcProfileSettingPopup(modelId) {
  var rtc = jQuery.extend({}, componentMap[modelId]);
  openRtcSettingPopup(createRtcProfileSettingForm(rtc, 0, false, false), 'Component Setting', 500, 800);
}

/**
 * RtcProfile設定画面を編集状態で起動する
 * 
 * @param {*} componentId componentId
 * @returns {undefined}
 */
function openEditRtcProfileSettingPopup(componentId) {
  var rtc = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);
  openRtcSettingPopup(createRtcProfileSettingForm(rtc, componentIndex, false, true), 'Component Setting', 500, 800);
}

/**
 * RTC用の設定画面を表示する
 * 
 * @param {*} form form
 * @param {*} title title
 * @param {*} width width
 * @param {*} height height
 * @returns {undefined}
 */
function openRtcSettingPopup(form, title, width, height) {
  // RTCの設定ポップアップを表示する
  w2popup.open({
    title: title,
    width: width,
    height: height,
    body: '<div id="rtc-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #rtc-profile-div').w2form(form);
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '150px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '150px').css('font-size', '1.2em');
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['rtc-profile-setting'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['rtc-profile-setting'].resize();
      }
    }
  });
}

/**
 * RtcProfile設定用Formを生成する
 * 
 * @param {*} rtc rtc
 * @param {*} rtcIndex rtcIndex
 * @param {*} propertyAreaFlg propertyAreaFlg
 * @param {*} updateFlg updateFlg
 * @returns {Map} form
 */
function createRtcProfileSettingForm(rtc, rtcIndex, propertyAreaFlg, updateFlg) {

  if (w2ui['rtc-profile-setting']) {
  w2ui['rtc-profile-setting'].destroy();
  }

  // チェックボックス用
  let componentKindFields = createComponetKindFields();

  let form = {
    name: 'rtc-profile-setting',
    focus: -1,
    padding: 0,
    tabs: [],
    fields: [],
    record: {}
  };

  // BasicInfo
  form['tabs'].push({ id: 'component-tab1', text: 'RTC Profile' });
  form['tabs'].push({ id: 'component-tab2', text: 'RTC Activities' });
  Array.prototype.push.apply(form['fields'], createRtcProfileBasicInfoSettingForm(componentKindFields));
  Array.prototype.push.apply(form['fields'], createRtcProfileActivitySettingForm());
  Object.assign(form['record'], setRtcProfileBasicInfoToSettingForm(rtc.rtcProfile.basicInfo, rtc.rtcProfile.actions, rtc.rtcProfile.neuralNetworkInfo, componentKindFields, rtc.modelProfile));

  // Configuration
  // form['tabs'].push({ id: 'component-tab2', text: 'Configuraiton' });

  if (updateFlg === true) {
    // 更新時はモジュール名は変更不可とする
    form['fields'][0]['html']['attr'] = form['fields'][0]['html']['attr'] + 'disabled=disabled';
    // 更新時のアクション
    form['actions'] = {
      'Update Profile': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (this.validate().length === 0) {
          // 編集した情報を格納する
          var updated = setEditComponentInfo(rtc, componentKindFields);
          mainRtsMap[curWorkspaceName].rtcs[rtcIndex] = updated;
          if (propertyAreaFlg === false) {
            w2popup.close();
          }
          updatePackage(true);
        }
      },
      'Update DNN Model': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (this.validate().length === 0) {
          // 編集した情報を格納する
          var updated = setEditComponentInfo(rtc, componentKindFields);
          if (updated.rtcProfile.neuralNetworkInfo.modelName) {
            w2confirm('編集内容を保存した後、DNNモデルを更新します。<br/>時間がかかる可能性もありますが、更新してよろしいですか？', function (btn) {
              if (btn === 'Yes') {
                // 保存
                mainRtsMap[curWorkspaceName].rtcs[rtcIndex] = updated;
                if (propertyAreaFlg === false) {
                  w2popup.close();
                }
                // Keras EditorのpathUriを取得する
                let str = $('#keras_editor_host').val();
                if (str == '') {
                  w2alert('keras Editor が未選択です。<br/>Keras Editor Hostから選択してください。')
                }
                const url = str.split('(')[1].split(')')[0];
                updateDnnModels(updated.rtcProfile.neuralNetworkInfo.modelName, true, url);
              }
            });
          } else {
            w2alert('DNNモデル名が設定されていません');
          }
        }
      }
    }
  } else {
    // 新規追加時のアクション
    form['actions'] = {
      'Create': function () {
        if (this.validate().length === 0) {
          var componentName = $('#component_name').val();
          if (!w2utils.isAlphaNumeric(componentName)) {
            w2alert('コンポーネント名称に使用できない文字が含まれています。')
          } else {
            if (isAvailableComponentName(componentName)) {
              var newRtc = new Object();
              newRtc['workspaceName'] = curWorkspaceName;
              newRtc['rtcProfile'] = setEditComponentInfo(rtc, componentKindFields).rtcProfile;
              newRtc['modelProfile'] = new Object();
              newRtc['modelProfile']['remoteUrl'] = setEditComponentInfo(rtc, componentKindFields).modelProfile.remoteUrl;
              w2popup.close();
              createNewComponentAjax(newRtc);
            } else {
              w2alert('コンポーネントの名称が重複しています。')
            }
          }
        }
      }
    }
  }

  return form;
}

/**
 * RtcProfile-BasicInfo設定用Formを生成する
 * 
 * @param {*} componentKindFields componentKindFields
 * @returns {Array} basicForm
 */
function createRtcProfileBasicInfoSettingForm(componentKindFields) {
  let basicForm = [
    { field: 'component_name', type: 'text', required: true, html: { label: 'Module Name', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_desc', type: 'text', required: false, html: { label: 'Module Description', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_ver', type: 'text', required: true, html: { label: 'Version', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_vender', type: 'text', required: true, html: { label: 'Vender Name', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_cat', type: 'text', required: true, html: { label: 'Module Category', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_type', type: 'list', required: true, html: { label: 'Component Type', page: 0, attr: 'style="width:300px"' }, options: { items: getComponentTypeChoices() } },
    { field: 'component_act', type: 'list', required: true, html: { label: 'Activity Type', page: 0, attr: 'style="width:300px"' }, options: { items: getActivityTypeChoices() } },
    componentKindFields[0],
    componentKindFields[1],
    componentKindFields[2],
    { field: 'component_ins', type: 'int', required: false, html: { label: 'Max Instance Size', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_execType', type: 'list', required: true, html: { label: 'Execution Type', page: 0, attr: 'style="width:300px"' }, options: { items: getExecutionTypeChoices() } },
    { field: 'component_execRate', type: 'float', required: false, html: { label: 'Execution Rate', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_abs', type: 'textarea', required: false, html: { label: 'Abstract', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_rtc', type: 'text', required: false, html: { label: 'RTC Type', page: 0, attr: 'style="width:300px"' } },
    { field: 'dnn_model_name', type: 'list', required: false, html: { label: 'DNN Model Name', page: 0, attr: 'style="width:300px"' }, options: { items: getKerasModelChoices() } },
    { field: 'keras_editor_host', type: 'list', required: false, html: { label: 'Keras Editor Host', page: 0, attr: 'style="width:300px"' }, options: { items: getKerasEditorHost() } },
    { field: 'dataset_name', type: 'text', required: false, html: { label: 'Dataset Name', page: 0, attr: 'style="width:300px"' } },
    { field: 'component_remoteUrl', type: 'text', required: true, html: { label: 'Remote Repository', page: 0, attr: 'style="width:300px"' } },
  ];
  return basicForm;
}

/**
 * RtcProfile-BasicInfo-ComponentKind設定用<br>
 * チェックボックスのフィールドを生成する
 * 
 * @returns {Array} result
 */
function createComponetKindFields() {
  var result = [];
  var header = true;
  var cnt = 1;
  var choices = getComponentKindChoices()
  for (key in choices) {
    var record = { field: 'component_comType' + cnt, type: 'checkbox', required: false, html: { text: ' ', label: choices[key], page: 0 } };
    if (header && header === true) {
      record['html']['text'] = 'Component Kind';
      header = false;
    }
    result.push(record);
    cnt++;
  }

  return result;
}

/**
 * RtcProfile-Activity設定用Formを生成する
 * 
 * @returns {Array} activityForm
 */
function createRtcProfileActivitySettingForm() {
  let activityForm = [
    { field: 'act_oninitialize', type: 'checkbox', required: true, html: { label: 'onInitialize', page: 1 } },
    { field: 'act_onfinalize', type: 'checkbox', required: true, html: { label: 'onFinalize', page: 1 } },
    { field: 'act_onstartup', type: 'checkbox', required: true, html: { label: 'onStartup', page: 1 } },
    { field: 'act_onshutdown', type: 'checkbox', required: true, html: { label: 'onShutdown', page: 1 } },
    { field: 'act_onactivated', type: 'checkbox', required: true, html: { label: 'onActivated', page: 1 } },
    { field: 'act_ondeactivated', type: 'checkbox', required: true, html: { label: 'onDeactivated', page: 1 } },
    { field: 'act_onaborting', type: 'checkbox', required: true, html: { label: 'onAborting', page: 1 } },
    { field: 'act_onerror', type: 'checkbox', required: true, html: { label: 'onError', page: 1 } },
    { field: 'act_onreset', type: 'checkbox', required: true, html: { label: 'onReset', page: 1 } },
    { field: 'act_onexecute', type: 'checkbox', required: true, html: { label: 'onExecute', page: 1 } },
    { field: 'act_onstateupdate', type: 'checkbox', required: true, html: { label: 'onStateUpdate', page: 1 } },
    { field: 'act_onratechanged', type: 'checkbox', required: true, html: { label: 'onRateChanged', page: 1 } },
  ];
  return activityForm;
}

/**
 * RtcProfileの内容を設定用Formに展開する
 * 
 * @param {*} basicInfo basicInfo
 * @param {*} actions actions
 * @param {*} neuralNetworkInfo neuralNetworkInfo
 * @param {*} componentKindFields componentKindFields
 * @param {*} modelProfile modelProfile
 * @returns {Map} record
 */
function setRtcProfileBasicInfoToSettingForm(basicInfo, actions, neuralNetworkInfo, componentKindFields, modelProfile) {
  let record = {
    // Basic Info
    component_name: basicInfo.moduleName,
    component_desc: basicInfo.moduleDescription,
    component_ver: basicInfo.version,
    component_vender: basicInfo.vendor,
    component_cat: basicInfo.moduleCategory,
    component_type: {text: basicInfo.componentType, id: basicInfo.componentType},
    component_act: {text: basicInfo.activityType, id: basicInfo.activityType},
    component_comType1: basicInfo.componentKind.indexOf(componentKindFields[0]['html']['label']) >= 0,
    component_comType2: basicInfo.componentKind.indexOf(componentKindFields[1]['html']['label']) >= 0,
    component_comType3: basicInfo.componentKind.indexOf(componentKindFields[2]['html']['label']) >= 0,
    component_ins: basicInfo.maxInstances,
    component_execType: {text: basicInfo.executionType, id: basicInfo.executionType},
    component_execRate: basicInfo.executionRate,
    component_abs: basicInfo.sabstract,
    component_rtc: basicInfo.rtcType,
    // DNN Info
    dnn_model_name: {text: neuralNetworkInfo.modelName, id: neuralNetworkInfo.modelName},
    dataset_name: neuralNetworkInfo.datasetName,
    // Activities
    act_oninitialize: actions.onInitialize.implemented === true,
    act_onfinalize: actions.onFinalize.implemented === true,
    act_onstartup: actions.onStartup.implemented === true,
    act_onshutdown: actions.onShutdown.implemented === true,
    act_onactivated: actions.onActivated.implemented === true,
    act_ondeactivated: actions.onDeactivated.implemented === true,
    act_onaborting: actions.onAborting.implemented === true,
    act_onerror: actions.onError.implemented === true,
    act_onreset: actions.onReset.implemented === true,
    act_onexecute: actions.onExecute.implemented === true,
    act_onstateupdate: actions.onStateUpdate.implemented === true,
    act_onratechanged: actions.onRateChanged.implemented === true,

    // GIT
    component_remoteUrl: modelProfile.remoteUrl,
  }
  return record;
}

/**
 * Componentの編集情報を格納する
 * 
 * @param {*} rtc rtc
 * @param {*} componentKindFields componentKindFields
 * @returns {*} rtc
 */
function setEditComponentInfo(rtc, componentKindFields) {
  var id = 'RTC:' + $('#component_vender').val() + ':' + $('#component_cat').val() + ':' + $('#component_name').val() + ':' + $('#component_ver').val();
  rtc.rtcProfile.id = id;
  // Basic Info
  rtc.rtcProfile.basicInfo.moduleName = $('#component_name').val();
  rtc.rtcProfile.basicInfo.version = $('#component_ver').val();
  rtc.rtcProfile.basicInfo.vendor = $('#component_vender').val();
  rtc.rtcProfile.basicInfo.moduleCategory = $('#component_cat').val();
  rtc.rtcProfile.basicInfo.moduleDescription = $('#component_desc').val();
  rtc.rtcProfile.basicInfo.componentType = $('#component_type').val();
  rtc.rtcProfile.basicInfo.activityType = $('#component_act').val();
  var componetKind = '';
  if ($('#component_comType1').prop('checked') === true) {
    componetKind += componentKindFields[0]['html']['label'];
  }
  if ($('#component_comType2').prop('checked') === true) {
    componetKind += componentKindFields[1]['html']['label'];
  }
  if ($('#component_comType3').prop('checked') === true) {
    componetKind += componentKindFields[2]['html']['label'];
  }
  if (componetKind) {
    componetKind += 'Component';
  }
  rtc.rtcProfile.basicInfo.componentKind = componetKind;
  rtc.rtcProfile.basicInfo.maxInstances = $('#component_ins').val().replace(',', '');
  rtc.rtcProfile.basicInfo.executionType = $('#component_execType').val();
  rtc.rtcProfile.basicInfo.executionRate = $('#component_execRate').val().replace(',', '');
  rtc.rtcProfile.basicInfo.sabstract = $('#component_abs').val();
  rtc.rtcProfile.basicInfo.rtcType = $('#component_rtc').val();
  // NN Info
  rtc.rtcProfile.neuralNetworkInfo.modelName = $('#dnn_model_name').val();
  rtc.rtcProfile.neuralNetworkInfo.datasetName = $('#dataset_name').val();
  // Activities
  rtc.rtcProfile.actions.onInitialize.implemented = $('#act_oninitialize').prop('checked') === true;
  rtc.rtcProfile.actions.onFinalize.implemented = $('#act_onfinalize').prop('checked') === true;
  rtc.rtcProfile.actions.onStartup.implemented = $('#act_onstartup').prop('checked') === true;
  rtc.rtcProfile.actions.onShutdown.implemented = $('#act_onshutdown').prop('checked') === true;
  rtc.rtcProfile.actions.onActivated.implemented = $('#act_onactivated').prop('checked') === true;
  rtc.rtcProfile.actions.onDeactivated.implemented = $('#act_ondeactivated').prop('checked') === true;
  rtc.rtcProfile.actions.onAborting.implemented = $('#act_onaborting').prop('checked') === true;
  rtc.rtcProfile.actions.onError.implemented = $('#act_onerror').prop('checked') === true;
  rtc.rtcProfile.actions.onReset.implemented = $('#act_onreset').prop('checked') === true;
  rtc.rtcProfile.actions.onExecute.implemented = $('#act_onexecute').prop('checked') === true;
  rtc.rtcProfile.actions.onStateUpdate.implemented = $('#act_onstateupdate').prop('checked') === true;
  rtc.rtcProfile.actions.onRateChanged.implemented = $('#act_onratechanged').prop('checked') === true;

  // Git
  rtc.modelProfile.remoteUrl = $('#component_remoteUrl').val();

  return rtc;
}

/**
 * コンフィギュレーション設定画面を起動する
 * 
 * @param {*} componentId componentId
 * @returns {undefined}
 */
function openConfigurationPopup(componentId) {
  var rtcProfile = getComponentInPackage(componentId).rtcProfile;
  openRtcSettingPopupForConfguration(rtcProfile);
}

/**
 * コンフィギュレーション設定画面を表示する
 * 
 * @param {*} rtcProfile rtcProfile
 * @returns {undefined}
 */
function openRtcSettingPopupForConfguration(rtcProfile) {
  // RTCのコンフィギュレーション設定ポップアップを表示する
  w2popup.open({
    title: 'Configuration Parameter Setting',
    width: 650,
    height: 370,
    body: '<div id="rtc-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        // レイアウトを表示する
        $('#w2ui-popup #rtc-profile-div').w2layout(createRtcConfigurationSettingLayout());
        // レイアウトにFormとGridを反映する
        createRtcConfigurationSettingForm(rtcProfile);
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
      }
    }
  });
}

/**
 * コンフィギュレーション設定画面のレイアウト部分を生成する
 * 
 * @returns {Map} layout
 */
function createRtcConfigurationSettingLayout() {
  destroySettingForm();

  // レイアウト
  var pstyle = 'border: none; padding: 0px;';
  var layout = {
    name: 'rtc-profile-layout',
    panels: [
      {
        type: 'left', size: 200, resizable: false, style: pstyle,
        content: '<div style="height:100%;"><div id="rtc-profile-configuration-grid-div" style="height:85%;"></div><div id="rtc-profile-configuration-add-div" style="height:15%;"></div></div>',
        tabs: [{ id: 'configuration-grid-tab1', text: 'Configuration List' }
        ]
      },
      {
        type: 'main', style: pstyle,
        content: '<div id="rtc-profile-configuration-form-div" style="height:100%;"></div>'
      },
      {
        type: 'bottom', size: 50, resizable: false, style: pstyle,
        content: '<div id="rtc-profile-configuration-save-div" style="height:100%;"></div>'
      },
    ]
  };
  return layout;
}

/**
 * コンフィギュレーション設定画面のレイアウトにフォームを反映する
 * 
 * @param {*} rtcProfile rtcProfile
 * @returns {undefined}
 */
function createRtcConfigurationSettingForm(rtcProfile) {
  // コンフィギュレーション部分のGridを生成
  var configurationGrid = {
    name: 'rtc-profile-configuration-grid',
    show: {
      selectColumn: true
    },
    multiSelect: false,
    columns: [
      { field: 'name', text: 'name', size: '100%', editable: { type: 'text' } }
    ],
    onClick: function (event) {
      if (w2ui['rtc-profile-configuration-grid']['curSelectRow'] >= 0 && w2ui['rtc-profile-configuration-setting'].validate().length > 0) {
        // 入力エラーがある場合は遷移させない
        w2ui['rtc-profile-configuration-grid'].selectNone();
        w2ui['rtc-profile-configuration-grid'].select(w2ui['rtc-profile-configuration-grid']['curSelectRow']);
      } else {
        if (w2ui['rtc-profile-configuration-grid']['curSelectRow'] >= 0) {
          saveRtcConfigurationSettingData(w2ui['rtc-profile-configuration-grid']['curSelectRow']);
        }
        w2ui['rtc-profile-configuration-grid'].selectNone();
        w2ui['rtc-profile-configuration-grid'].select(event.recid);
        setRtcConfigurationSettingData(event.recid);
        // 選択したインタフェースの情報を設定する
        w2ui['rtc-profile-configuration-grid']['curSelectRow'] = event.recid;
      }
      event.preventDefault();
    }
  };

  // Gridの追加釦
  var addBtn = $('<button type="button">');
  addBtn.html('Add')
  addBtn.attr('id', 'configuration-grid-add-btn');
  addBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '3px').css('margin-left', '20px').css('margin-right', '5px').css('width', '70px').css('font-size', '1em');
  $('<span>').addClass('ui-icon').addClass('ui-icon-plus').appendTo(addBtn);
  $(addBtn).on('click', function () {
    addRtcConfiguration();
  });

  // Gridの削除釦
  var delBtn = $('<button type="button">');
  delBtn.html('Delete')
  delBtn.attr('id', 'configuration-grid-delete-btn');
  delBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '3px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px').css('font-size', '1em');
  $('<span>').addClass('ui-icon').addClass('ui-icon-trash').appendTo(delBtn);
  $(delBtn).on('click', function () {
    deleteRtcConfiguration(w2ui['rtc-profile-configuration-grid']['curSelectRow']);
  });

  $('#rtc-profile-configuration-add-div').append(addBtn).append(delBtn);

  // コンフィギュレーション部分
  var configurationForm = {
    name: 'rtc-profile-configuration-setting',
    padding: 0,
    fields: [
      { field: 'conf_name', type: 'text', required: true, html: { label: 'Name', page: 0, attr: 'style="width:250px"' } },
      { field: 'conf_type', type: 'list', required: true, html: { label: 'Type', page: 0, attr: 'style="width:250px"' }, options: { items: getConfigurationTypeChoices() } },
      { field: 'conf_defaultvalue', type: 'text', required: true, html: { label: 'Default Value', page: 0, attr: 'style="width:250px"' } },
      { field: 'conf_valname', type: 'text', required: false, html: { label: 'Variable Name', page: 0, attr: 'style="width:250px"' } },
      { field: 'conf_unit', type: 'text', required: false, html: { label: 'Unit', page: 0, attr: 'style="width:250px"' } },
      { field: 'conf_constraint', type: 'text', required: false, html: { label: 'Constraint', page: 0, attr: 'style="width:250px"' } },
      { field: 'conf_widget', type: 'list', required: true, html: { label: 'Widget', page: 0, attr: 'style="width:250px"' }, options: { items: getConfigurationWidgetChoices() } },
      { field: 'conf_step', type: 'text', required: false, html: { label: 'Step', page: 0, attr: 'style="width:250px"' } },
    ],
    record: {}
  };

  var saveForm = {
    name: 'rtc-profile-configuration-save',
    focus: -1,
    padding: 0,
    tabs: [],
    fields: [{ field: 'dummy', type: 'text', required: false, html: { attr: 'style="display:none"' } }],
    record: {},
    actions: {
      'Update': function () {
        if (w2ui['rtc-profile-configuration-grid']['curSelectRow'] >= 0) {
          // 一時保存
          saveRtcConfigurationSettingData(w2ui['rtc-profile-configuration-grid']['curSelectRow']);
        }
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (w2ui['rtc-profile-configuration-grid']['curSelectRow'] >= 0 && w2ui['rtc-profile-configuration-setting'].validate().length === 0) {
          var componentId = rtcProfile.id;
          // 変更前
          var oldConfigurationList = rtcProfile.configurationSet.configurations;
          // 変更後
          var newConfigurationList = getRtcConfigurationFromGrid();

          if (rtcConfigurationChanged(oldConfigurationList, newConfigurationList)) {
            // コンフィギュレーション更新
            var rtc = updateRtcConfiguration(componentId, newConfigurationList);
            // PackageのRTC更新
            updateRtcInPackage(rtc);
            w2popup.close();
            updatePackage(true);
          }
        }
      }
    }
  }

  // パネルに設定する
  $('#rtc-profile-configuration-grid-div').w2grid(configurationGrid);
  $('#rtc-profile-configuration-form-div').w2form(configurationForm);
  $('#rtc-profile-configuration-save-div').w2form(saveForm);

  // ボタンを設定
  $('#configuration-grid-add-btn').button();
  $('#configuration-grid-delete-btn').button();

  // Gridにデータを表示する
  setRtcConfigurationGridData($.extend(true, {}, rtcProfile));
}

/**
 * コンフィギュレーション設定のGridに値を設定する
 * 
 * @param {*} rtcProfile rtcProfile
 * @returns {undefined}
 */
function setRtcConfigurationGridData(rtcProfile) {
  // 一度クリアする
  clearRtcConfigurationGridData();

  // 選択したコンフィギュレーションのINDEX
  var selectedIfIndex = -1;

  var confArray = [];
  if (rtcProfile.configurationSet && rtcProfile.configurationSet.configurations && rtcProfile.configurationSet.configurations.length > 0) {
    for (var i = 0; i < rtcProfile.configurationSet.configurations.length; i++) {
      var record = { recid: i, name: rtcProfile.configurationSet.configurations[i].name, confData: rtcProfile.configurationSet.configurations[i] };
      confArray.push(record);
      if (selectedIfIndex < 0) {
        selectedIfIndex = i;
      }
    }
  }

  if (confArray.length > 0) {
    // Gridに追加する
    w2ui['rtc-profile-configuration-grid'].add(confArray);
  }
  $('#configuration-grid-add-btn').button('enable');
}

/**
 * コンフィギュレーションのGridをクリアする
 * 
 * @returns {undefined}
 */
function clearRtcConfigurationGridData() {
  w2ui['rtc-profile-configuration-grid'].clear();
  w2ui['rtc-profile-configuration-grid'].selectNone();
  $('#configuration-grid-delete-btn').button('disable');
  clearRtcConfigurationSetting();
}

/**
 * コンフィギュレーション設定部分をクリアする
 * 
 * @returns {undefined}
 */
function clearRtcConfigurationSetting() {
  // コンフィギュレーション設定項目もdisableにする
  $('#conf_name').attr('disabled', 'disabled');
  $('#conf_type').attr('disabled', 'disabled');
  $('#conf_defaultvalue').attr('disabled', 'disabled');
  $('#conf_valname').attr('disabled', 'disabled');
  $('#conf_unit').attr('disabled', 'disabled');
  $('#conf_constraint').attr('disabled', 'disabled');
  $('#conf_widget').attr('disabled', 'disabled');
  $('#conf_step').attr('disabled', 'disabled');
  // 値を空
  w2ui['rtc-profile-configuration-setting'].clear()
}

/**
 * 選択したコンフィギュレーションのデータを設定する
 * 
 * @param {*} index index
 * @returns {undefined}
 */
function setRtcConfigurationSettingData(index) {
  var confData = w2ui['rtc-profile-configuration-grid'].records[index]['confData'];

  if (confData) {
    // 釦を有効化
    $('#configuration-grid-delete-btn').button('enable');
    // enable化
    $('#conf_name').removeAttr('disabled');
    $('#conf_type').removeAttr('disabled');
    $('#conf_defaultvalue').removeAttr('disabled');
    $('#conf_valname').removeAttr('disabled');
    $('#conf_unit').removeAttr('disabled');
    $('#conf_constraint').removeAttr('disabled');
    $('#conf_widget').removeAttr('disabled');
    // $('#conf_step').removeAttr('disabled');
    // 値設定
    w2ui['rtc-profile-configuration-setting'].record = {
      'conf_name': confData.name,
      'conf_type': {id: confData.dataType, text: confData.dataType},
      'conf_defaultvalue': confData.defaultValue,
      'conf_valname': confData.variableName,
      'conf_unit': confData.unit,
      'conf_constraint': setConfigurationUnit(confData),
      'conf_widget': {id: 'text', text: 'text'},
      'conf_step': ''
    }
    w2ui['rtc-profile-configuration-setting'].refresh();
  }
}

/**
 * コンフィギュレーションのUnitを取得する
 * 
 * @param {*} confData confData
 * @returns {*} コンフィギュレーションのUnit
 */
function setConfigurationUnit(confData) {
  if (confData && confData.constraint && confData.constraint.constraintUnitType
    && confData.constraint.constraintUnitType.propertyIsEqualTo) {
    return confData.constraint.constraintUnitType.propertyIsEqualTo.literal;
  }
  return '';
}

/**
 * 変更したコンフィギュレーションの内容を反映する
 * 
 * @param {*} index index
 * @returns {undefined}
 */
function saveRtcConfigurationSettingData(index) {
  // 変更したデータを反映する
  var confData = w2ui['rtc-profile-configuration-grid'].records[index]['confData'];
  confData.name = $('#conf_name').val();
  confData.dataType = $('#conf_type').val();
  confData.defaultValue = $('#conf_defaultvalue').val();
  confData.variableName = $('#conf_valname').val();
  confData.unit = $('#conf_unit').val();
  if ($('#conf_constraint').val()) {
    confData.constraint = new Object();
    confData.constraint['constraintUnitType'] = new Object();
    confData.constraint['constraintUnitType']['propertyIsEqualTo'] = new Object();
    confData.constraint['constraintUnitType']['propertyIsEqualTo']['matchCase'] = false;
    confData.constraint['constraintUnitType']['propertyIsEqualTo']['literal'] = $('#conf_constraint').val();
  } else {
    confData.constraint = null;
  }
  confData.properties = new Object();
  confData.properties['name'] = '__widget__';
  confData.properties['value'] = 'text';
  w2ui['rtc-profile-configuration-grid'].records[index]['confData'] = confData;

  // 名称も変更する
  w2ui['rtc-profile-configuration-grid'].records[index]['name'] = confData.name;
  w2ui['rtc-profile-configuration-grid'].reload();
}

/**
 * コンフィギュレーションを追加する
 * 
 * @returns {undefined}
 */
function addRtcConfiguration() {
  if (w2ui['rtc-profile-configuration-grid']['curSelectRow'] >= 0 && w2ui['rtc-profile-configuration-setting'].validate().length > 0) {
    // 選択しているレコードにエラーがある場合は何もしない
    // NOP
  } else {
    // レコード数
    var recordCnt = w2ui['rtc-profile-configuration-grid'].records.length;
    var confName = 'conf_name' + recordCnt;
    var confData = new Object();
    confData['name'] = confName;
    var record = { recid: recordCnt, name: confName, confData: confData };
    // Gridに追加する
    w2ui['rtc-profile-configuration-grid'].add(record);
  }
}

/**
 * コンフィギュレーションを削除する
 * 
 * @param {*} index index
 * @returns {undefined}
 */
function deleteRtcConfiguration(index) {
  clearRtcConfigurationSetting();
  w2ui['rtc-profile-configuration-grid'].records.splice(index, 1);
  if (w2ui['rtc-profile-configuration-grid'].records && w2ui['rtc-profile-configuration-grid'].records.length > 0) {
    for (var i = 0; i < w2ui['rtc-profile-configuration-grid'].records.length; i++) {
      w2ui['rtc-profile-configuration-grid'].records[i]['recid'] = i;
    }
  }
  w2ui['rtc-profile-configuration-grid'].reload();
  w2ui['rtc-profile-configuration-grid'].selectNone();
  w2ui['rtc-profile-configuration-grid']['curSelectRow'] = -1;
}

/**
 * 編集したコンフィギュレーションのデータを取得する
 * 
 * @returns {Array} configurationList
 */
function getRtcConfigurationFromGrid() {
  var configurationList = null;
  if (w2ui['rtc-profile-configuration-grid'].records && w2ui['rtc-profile-configuration-grid'].records.length > 0) {
    configurationList = [];
    for (var i = 0; i < w2ui['rtc-profile-configuration-grid'].records.length; i++) {
      configurationList.push(w2ui['rtc-profile-configuration-grid'].records[i]['confData']);
    }
  }
  return configurationList;
}

/**
 * コンフィギュレーションの変更をチェックする
 * 
 * @param {*} list1 list1
 * @param {*} list2 list2
 * @returns {boolean} result
 */
function rtcConfigurationChanged(list1, list2) {
  if (list1 === null && list2 === null) {
    return false;
  } else if (list1 === null || list2 === null) {
    return true;
  } else {
    if (list1.length !== list2.length) {
      return true;
    } else {
      for (var i = 0; i < list1.length; i++) {
        if (list1[i].name !== list2[i].name) {
          return true;
        } else if (list1[i].dataType !== list2[i].dataType) {
          return true;
        } else if (list1[i].defaultValue !== list2[i].defaultValue) {
          return true;
        } else if (list1[i].variableName !== list2[i].variableName) {
          return true;
        } else if (list1[i].unit !== list2[i].unit) {
          return true;
        } else {
          if (list1[i].constraint === null && list2[i].constraint === null) {
            return false;
          } else if (list1[i].constraint === null || list2[i].constraint === null) {
            return true;
          } else if (list1[i].constraint.constraintUnitType.propertyIsEqualTo.literal
            != list2[i].constraint.constraintUnitType.propertyIsEqualTo.literal) {
            return true;
          }
        }
      }
    }
  }
  return false;
}


/*******************************************************************************
 * ホスト設定関連
 ******************************************************************************/
/**
 * ホスト設定用Formを生成する
 * 
 * @returns {Map} form
 */
function createHostSettingForm() {

  var form = {
    name: 'host-setting',
    focus: -1,
    padding: 0,
    tabs: [
      { id: 'host-tab1', text: 'Add Wasanbon Host' },
      { id: 'host-tab2', text: 'Add AirGraph Host' }
    ],
    record: {
    NSPort: '2809',
    WWPort: '8000',
    AirGraph_Port: '8080'
    },
  }; 
  form['fields'] = [
    // Wasanbon Host
    { field: 'Wasanbon_HostName', type: 'text', required: true, html: { label: 'Host Name', page: 0, attr: 'style="width:300px"' } },
    { field: 'Wasanbon_IP', type: 'text', required: true, html: { label: 'IP', page: 0, attr: 'style="width:300px"' } },
    { field: 'Wasanbon_NSPort', type: 'text', required: true, html: { label: 'Nameserver Port', page: 0, attr: 'style="width:300px"' } },
    { field: 'Wasanbon_WWPort', type: 'text', html: { label: 'Webframework Port', page: 0, attr: 'style="width:300px;position:absolute;right:10"' } },
    // AirGraph Host
    { field: 'AirGraph_HostName', type: 'text', required: true, html: { label: 'Host Name', page: 1, attr: 'style="width:300px"' } },
    { field: 'AirGraph_IP', type: 'text', required: true, html: { label: 'IP', page: 1, attr: 'style="width:300px"' } },
    { field: 'AirGraph_Port', type: 'text', required: true, html: { label: 'AirGraph Port', page: 1, attr: 'style="width:300px"' } },
  ];
  form['actions'] = {
    'Add': function () {
      let currentpage = 0;
      if ($('#tabs_host-setting_tabs_tab_host-tab2').is('[class="w2ui-tab active"]')) {
        currentpage = 1;
      }
      // wasanbon Hostの場合
      if (currentpage == 0 && this.validate(false).filter(f => f.field.page == currentpage).length === 0) {

         /* 編集状態なら、非表示に切り替える */
        let btn = document.getElementById('save-btn');
        if (btn.style.visibility == "visible") {
          // ホスト一覧を編集不可状態にする
          toggleVisibility();
        }
        // すでに登録されているホスト名 or (IP,Port)が入力されている場合は、エラー
        if (isHostNameUnique(true, $('#Wasanbon_HostName').val(), $('#Wasanbon_IP').val(), $('#Wasanbon_NSPort').val()) == 'true') {
         // ホスト認証ダイアログを表示する
          openHostAuthPopup(this, $('#Wasanbon_HostName').val(), $('#Wasanbon_IP').val(), $('#Wasanbon_NSPort').val(), $('#Wasanbon_WWPort').val());
        }
        else {
          w2alert('ERROR: Host Name or (IP, Port) are already used');
        }
      // AirGraph Hostの場合
      } else if (currentpage == 1 && this.validate(false).filter(f => f.field.page == currentpage).length === 0) {
        if (isHostNameUnique(false, $('#AirGraph_HostName').val(), $('#AirGraph_IP').val(), $('#AirGraph_Port').val()) == 'true') {
          let res = addAirGraphHost($('#AirGraph_HostName').val(), $('#AirGraph_IP').val(), $('#AirGraph_Port').val());
          if (res === 'true') {
            w2alert('Successfully Added!');
            addAirGraphHostDataToList($('#AirGraph_HostName').val(), $('#AirGraph_IP').val(), $('#AirGraph_Port').val());
            addAirGraphHostEditForm($('#AirGraph_HostName').val(), $('#AirGraph_IP').val(), $('#AirGraph_Port').val());
          } else {
            w2alert('ERROR: Cannot Add New Host');
          }
        } else {
          w2alert('ERROR: Host Name or (IP, Port) are already used');
        }
        
      } else {
        this.validate();
      }
    }
  }
  return form;
}

/**
 * ホスト一覧を生成する
 * 
 * @returns {Map} form
 */
function createHostListForm() {
  var form = {
    name: 'host-list',
    focus: -1,
    padding: 0,
    tabs: [
      { id: 'host-tab3', text: 'Wasanbon Host List' },
      { id: 'host-tab4', text: 'AirGraph Host List' }
    ],
    formHTML:
      '<div class="w2ui-page page-0" style="top: 38px; bottom: 59px; display: block;">' +
      '<div class="w2ui-column-container">' +
      '<div class="w2ui-column col-0">' +
      '<ul id="host-list" display="list-item">' +
      '</ul>' +
      '</div>' +
      '</div>' +
      '</div>' +
      '<div class="w2ui-page page-1" style="top: 38px; bottom: 59px; display: block;">' +
      '<div class="w2ui-column-container">' +
      '<div class="w2ui-column col-0">' +
      '<ul id="airgraph-host-list" display="list-item">' +
      '</ul>' +
      '</div>' +
      '</div>' +
      '</div>' +
      '<div id="host-edit-btn-div" class="w2ui-buttons host-property" style="position:absolute;text-align:center;">' +
      '<button name="Edit">Edit</button>' +
      '<button name="save-btn" id="save-btn" style="visibility:hidden;font-size:11px;">Save</button>' +
      '</div>'
  };

  form['actions'] = {
    'Edit': function () {
      // ホスト一覧をホスト編集フォームにし、編集保存ボタンを表示する
     toggleVisibility();
    },
    'save-btn': function () {
     toggleVisibility();

      let res = saveHostChanges();

      if (res === 'true') {
         /* 編集をリストに反映する*/
        updateHostChanges();
         /* 削除するホストがあれば、要素ごと削除する*/
        destroyIconAndButton();
        // RTC割り当て領域の更新
        updateRTCDataToHostAssignArea();
        // RTCアイコン上のホスト名も変更する
        reloadAssignedHostName(false);
      }
    }
  }
  return form;
}

/**
 * ホスト編集を保存する
 * 
 * @returns {*} 実行結果
 */
function saveHostChanges() {

  // 重複していないかチェックするためのリスト
  let w_checkHostUnique = [];
  let a_checkHostUnique = [];
  //送信するリクエストボディ
  let requestBody = [];
  let w_requestBody = [];
  let a_requestBody = [];
  let w_hosts = document.getElementById('host-list').getElementsByClassName('edit-host');
  let a_hosts = document.getElementById('airgraph-host-list').getElementsByClassName('edit-host');

  for (let i = 0; i < w_hosts.length; i++) {
    // name, IP, port のすべてが空文字なら、{ID: ID, name: '', IP: '', port: ''}をリクエストボディに追加
    if (w_hosts[i].children[1].value === ''
      && w_hosts[i].children[3].value === ''
      && w_hosts[i].children[5].value === ''
      && w_hosts[i].children[7].value === ''){

      let content = {};
      let ID = w_hosts[i].getAttribute('id').replace('edit-host-', '');
      content = {id: ID, hostName: '', ip: '', nsport: '', wwport: ''};
      w_requestBody.push(content);
    }
    // name, IP, port のどれかが空文字なら、エラーなので終了
    else if (w_hosts[i].children[1].value === ''
      || w_hosts[i].children[3].value === ''
      || w_hosts[i].children[5].value === ''
      || w_hosts[i].children[7].value === ''
      ){
        w2alert('Wrong Changes.');
        //編集状態に戻る
        return null
    }
    else {
      content = {};
      let ID = w_hosts[i].getAttribute('id').replace('edit-host-', '');
      let name = w_hosts[i].getElementsByClassName('edit-host-name')[0].value;
      let IP = w_hosts[i].getElementsByClassName('edit-host-ip')[0].value;
      let nsport = w_hosts[i].getElementsByClassName('edit-host-port')[0].value;
      let wwport = w_hosts[i].getElementsByClassName('edit-ww-port')[0].value;
      content = {id: ID, hostName: name, ip: IP, nsport: nsport, wwport: wwport};
      w_checkHostUnique.push(content);
      w_requestBody.push(content);
    }
  }

  for (let i = 0; i < a_hosts.length; i++) {
    // name, ip, port のすべてが空文字なら、{ID: ID, name: '', IP: '', port: ''}をリクエストボディに追加
    if (a_hosts[i].children[0].value === ''
      && a_hosts[i].children[2].value === ''
      && a_hosts[i].children[4].value === ''){

      let content = {};
      let id = a_hosts[i].getAttribute('id').replace('edit-airgraph-host-', '');
      content = {id: id, hostName: '', ip: '', port: ''};
      a_requestBody.push(content);
    }
    // name, IP, port のどれかが空文字なら、エラーなので終了
    else if (a_hosts[i].children[0].value === ''
      || a_hosts[i].children[2].value === ''
      || a_hosts[i].children[4].value === ''
      ){
        w2alert('Wrong Changes.');
        //編集状態に戻る
        return null
    }
    else {
      content = {};
      let id = a_hosts[i].getAttribute('id').replace('edit-airgraph-host-', '');
      let name = a_hosts[i].getElementsByClassName('edit-host-name')[0].value;
      let ip = a_hosts[i].getElementsByClassName('edit-host-ip')[0].value;
      let port = a_hosts[i].getElementsByClassName('edit-host-port')[0].value;
      content = {id: id, hostName: name, ip: ip, port: port};
      a_checkHostUnique.push(content);
      a_requestBody.push(content);
    }
  }
  for (let i = 0; i < w_checkHostUnique.length - 1; i++) {
    for (let j = i + 1; j < w_checkHostUnique.length; j++) {
      // ホスト名が重複していないか調べる
      if (w_checkHostUnique[i]['hostName'] == w_checkHostUnique[j]['hostName']) {
        w2alert('ERROR: Host Name are not Unique');
        return null;
      }
      // IP, Portが重複していないか調べる
      if (w_checkHostUnique[i]['ip'] == w_checkHostUnique[j]['ip'] && w_checkHostUnique[i]['nsport'] == w_checkHostUnique[j]['nsport']) {
        w2alert('ERROR: (IP, Nameserver Port) are not Unique');
        return null;
      }
    }
  }
  for (let i = 0; i < a_checkHostUnique.length - 1; i++) {
    for (let j = i + 1; j < a_checkHostUnique.length; j++) {
      // ホスト名が重複していないか調べる
      if (a_checkHostUnique[i]['hostName'] == a_checkHostUnique[j]['hostName']) {
        w2alert('ERROR: Host Name are not Unique');
        return null;
      }
      // IP, Portが重複していないか調べる
      if (a_checkHostUnique[i]['ip'] == a_checkHostUnique[j]['ip'] && a_checkHostUnique[i]['port'] == a_checkHostUnique[j]['port']) {
        w2alert('ERROR: (IP, AirGraph Port) are not Unique');
        return null;
      }
    }
  }
  
  requestBody.push(w_requestBody);
  requestBody.push(a_requestBody);
  return updateHostConfigFile(requestBody);
}

/**
 * ホストの編集をリストに反映する
 *
 * @returns {undefined}
 */
function updateHostChanges(){
  // ホストリストを取得
  let editedHosts = document.getElementById('host-list').children;
  // ホストリストの長さを取得
  let len = editedHosts.length;
  // リストに反映する
  for (let i = 1; i < len; i +=2) {
    let newName = editedHosts[i].children[1].value;
    let newIp = editedHosts[i].children[3].value;
    let newNsPort = editedHosts[i].children[5].value;
    let newWwPort = editedHosts[i].children[7].value;
    editedHosts[i - 1].children[1].textContent = newName;
    editedHosts[i - 1].children[3].textContent = newIp;
    editedHosts[i - 1].children[5].textContent = newNsPort;
    editedHosts[i - 1].children[7].textContent = newWwPort;

    // hostMapに反映する
    let id = editedHosts[i - 1].getAttribute('id').replace('host-', '');
    // もしバツ釦が押されていたら、hostMapから消す
    if(editedHosts[i - 1].getAttribute('delete') == 'true') {
      hostMap.delete(id);
      continue;
    }
    let password = hostMap.get(id)['password'];
    let newHost = {hostName: newName, id: id, ip: newIp, password: password, nsport: newNsPort, wwport: newWwPort};
    hostMap.set(id, newHost);
  }
  // ホストリストを取得
  editedHosts = document.getElementById('airgraph-host-list').children;
  // ホストリストの長さを取得
  len = editedHosts.length;
  // リストに反映する
  for (let i = 1; i < len; i +=2) {
    let newName = editedHosts[i].children[0].value;
    let newIp = editedHosts[i].children[2].value;
    let newPort = editedHosts[i].children[4].value;
    editedHosts[i - 1].children[0].textContent = newName;
    editedHosts[i - 1].children[2].textContent = newIp;
    editedHosts[i - 1].children[4].textContent = newPort;

    // airGraphHostMapに反映する
    let id = editedHosts[i - 1].getAttribute('id').replace('airgraph-host-', '');
    // もしバツ釦が押されていたら、hostMapから消す
    if(editedHosts[i - 1].getAttribute('delete') == 'true') {
      airGraphHostMap.delete(id);
      continue;
    }
    let newHost = {hostName: newName, id: id, ip: newIp, port: newPort};
    airGraphHostMap.set(id, newHost);
  }
}

/**
 *  ホストのデータを破棄する
 * 
 * @param {*} id id
 * @returns {undefined}
 */
function deleteHostData(id) {
  // 親要素を取得し、タグをつける
  var parentEditDiv = document.getElementById(id);
  parentEditDiv.setAttribute('delete' ,'true');
  //該当するホストのテキストだけを削除する
  var contents = parentEditDiv.children;
  
  // wasanbon-hostなら
  if (id.indexOf('edit-host-') >= 0) {
    contents[1].value = '';
    contents[3].value = '';
    contents[5].value = '';
    contents[7].value = '';
  // airgraph-hostなら
  } else {
    contents[0].value = '';
    contents[2].value = '';
    contents[4].value = '';
  }

  // ホスト一覧からも削除するフラグを立てる
  var previousDiv = parentEditDiv.previousElementSibling;
  previousDiv.setAttribute('delete' ,'true');
  
}

/**
 *  バツボタンを押されたホストの要素を破棄する
 * 
 * @returns {undefined}
 */
function destroyIconAndButton(){
  $('[delete="true"]').remove();
}

/**
 * ボタン表示・非表示を切り替える
 * 
 * @returns {undefined}
 */
function toggleVisibility() {

  // ホストリストの長さを取得（リストとフォームどちらもカウント）
  let w_hosts = document.getElementById('host-list').children;
  let a_hosts = document.getElementById('airgraph-host-list').children;
  let w_len = w_hosts.length;
  let a_len = a_hosts.length;


  // ホストリスト、編集フォームの表示、非表示を入れ替える
  for (let i = 0; i < w_len; i++) {
    if (w_hosts[i].style.display == "none") {
      w_hosts[i].style.display = "block";
    }
    else {
      w_hosts[i].style.display = "none";
    }
  }

  for (let i = 0; i < a_len; i++) {
    if (a_hosts[i].style.display == "none") {
      a_hosts[i].style.display = "block";
    }
    else {
      a_hosts[i].style.display = "none";
    }
  }

  // saveボタンの表示、非表示を入れ替える
  var btn = document.getElementById('save-btn');

  if (btn.style.visibility == "hidden") {
    btn.style.visibility = "visible";
  }
  else {
    btn.style.visibility = "hidden";
  }
}

/**
 * ホスト割り当てボックスを生成する
 * 
 * @returns {Map} form
 */
function createRTCSettingForm() {
  var form = {
    name: 'host-assign',
    focus: -1,
    padding: 0,
    tabs: [
      { id: 'host-tab3', text: 'Assign to RTCs' }
    ],
    record: {
    },
    formHTML:
      '<div style="width: 500px">' +
      '<div id="RTC-assign-area" class="w2ui-page page-0" style="background-color:#fff;">' +
      '</div>' +
      '</div>'
  };
  return form;
}

/**
 * 作業領域にあるRTCを、RTC割り当て領域の一覧に表示する
 * 
 * @returns {undefined}
 */
function updateRTCDataToHostAssignArea() {
  
  //すでにあるRTC一覧削除
  $('#RTC-assign-area').empty();
  
  // 作業領域内のRTCを取得
  let RTCs = document.getElementById('main-panel').getElementsByClassName('joint-type-devs');
  
  for (i = 0; i < RTCs.length; i++) {
    // 雛形を作成        
        let newRTC = document.createElement('div');
        newRTC.style.height = '25px';
     
         // 雛形のIDを作成
         let newID = 'RTC-assign-' + RTCs[i].id;
       newRTC.setAttribute('id', newID);
       newRTC.setAttribute('class', 'w2ui-field');
       let span = document.createElement('span');

    // RTC名を表示
    let name = RTCs[i].getElementsByTagName('tspan')[0].textContent;
    span.textContent = name;
    
    newRTC.appendChild(span);
    
    //セレクトボックスを追加
    let select = document.createElement('select');
    select.setAttribute('class', 'hosts');
    select.setAttribute('style', 'width:100px;float:right');
    select.setAttribute('onchange', 'addHostNameToRTCIconFromSelectBox(this)');

    select.onclick = function () {
      /* 編集状態なら、非表示に切り替える */
      let btn = document.getElementById("save-btn");
      if (btn.style.visibility == "visible") {
        toggleVisibility();
      }
    }
    
    // セレクトボックスの選択肢を更新
      updateRTCAssignhostsList(select);
    
    newRTC.appendChild(select);

    //RTC割り当て領域に追加
    $('#RTC-assign-area').append(newRTC);
  }
}

/**
 * RTC割り当てボックスの選択肢を更新する
 * 
 * @param {*} selectBox selectBox 
 * @returns {undefined}
 */
function updateRTCAssignhostsList(selectBox){
  
  // すでにあるセレクトボックスのリスト削除
  while(selectBox.lastClild) {
    selectBox.removeChild(selectBox.lastClild);
  }
  
  // 最初に空欄を追加
  let defaultName = document.createElement('option');
  selectBox.appendChild(defaultName);
  if (!isEmpty(hostMap)){
    // ホストマップのホスト名を追加
    hostMap.forEach((host, hostId) => {
      let newName = document.createElement('option');
      newName.setAttribute('value', hostId + '-' + host['hostName']);
      newName.textContent = host['hostName'];
      selectBox.appendChild(newName);
  });
  }
}

/**
 * RTCID, HostID, hostNameを取得し、ホスト名をアイコンに表示する
 * 
 * @param {object} obj obj
 * @returns {undefined}
 */
function addHostNameToRTCIconFromSelectBox(obj) {
  
  //セレクトボックスのselected属性を消す
  for (let i = 0; i < obj.children.length; i++){
    if (obj.children[i].hasAttribute('selected')){
      obj.children[i].removeAttribute('selected');
    }
  }
  
  // RTCのアイコンのIDを取得
  let RTCID = obj.parentElement.getAttribute('id');
  RTCID = RTCID.replace('RTC-assign-', '');
 
  // 選択されたホストID・ホスト名を取得
  let hostIDAndName = obj.value.split('-');
  let hostID = hostIDAndName[0];
  let hostName = hostIDAndName[1];
  
  // すでに表示されているホスト名を削除
  $('#' + RTCID + ' .AssignedHost').remove();

  // conponent-Id を作成
  let componentId = document.getElementById(RTCID).getElementsByTagName('rect')[0].getAttribute('component-id');
  
  // instanceName を取得
  let instanceName = document.getElementById(RTCID).getAttribute('model-id');


  let newUri = null;
  let newUriIP = null;
  let newUriPort = null;
  // 何も選択されていなければ、デフォルトに戻す
  if (hostName == undefined){
    
    newUriIP = 'localhost'
    newUriPort = '2809'
    createAssignedHostNameOnRtcIcon(RTCID, 'local', 'localhost');
  }
  else {
    // RTCアイコン上にホスト名を表示する
    createAssignedHostNameOnRtcIcon(RTCID, hostID, hostName);
    
    // newPathUriを作成
    newUriIP = document.getElementById('host-' + hostID).getElementsByClassName('host-ip')[0].textContent;
    if (newUriIP == '127.0.0.1') {
      newUriIP = 'localhost';
    }
    newUriPort = document.getElementById('host-' + hostID).getElementsByClassName('host-port')[0].textContent;
  }
  
  newUri = newUriIP + ':' + newUriPort + '/' + instanceName + '.rtc';
  
  // mainRtsMapを変更する
  changePathUriInPackage(componentId, newUri);
  // defaultsystem.xmlを更新する
  updatePackage();
}

/**
 * ホスト名をアイコンに表示する
 * @param {*} hostName hostName
 * @param {*} RTCID RTCID
 * @param {*} hostID hostID
 * @returns {undefined}
 */
function updateHostNameOnRTCIcon(hostName, RTCID, hostID) {
  
  // すでに表示されているホスト名を削除
  $('#' + RTCID + ' .AssignedHost').remove();
  
  // 何も選択されていなければ、そこで終了
  if (hostName == ''){
    createAssignedHostNameOnRtcIcon(RTCID, 'local', 'localhost');
    return null;
  }
  
  // RTCアイコン上にホスト名を表示する
  createAssignedHostNameOnRtcIcon(RTCID, hostID, hostName);
  
  // 対応するセレクトボックスのvalueも更新しておく
  changeSelectedAttribute(RTCID, hostID, hostName)
}

/**
 * RTCアイコンにホスト名を表示する
 * 
 * @param {*} RTCID RTCID
 * @param {*} hostID hostID
 * @param {*} hostName hostName
 * @returns {undefined}
 */
function createAssignedHostNameOnRtcIcon(RTCID, hostID, hostName) {

  // RTCアイコンの高さを取得する
  let height = Number(document.getElementById(RTCID).getElementsByTagName('rect')[0].getAttribute('height'));
  height += 20;
  
  // テキストタグ作成
  let SVGHostName = document.createElementNS('http://www.w3.org/2000/svg', 'text');
  SVGHostName.setAttribute('id', 'RTC-assigned-host-name-' + RTCID +'-' + hostID);
  SVGHostName.setAttribute('class', 'AssignedHost');
  SVGHostName.setAttribute('transform', 'translate(0,' + height + ')');
  SVGHostName.setAttribute('fill', '#000');
  SVGHostName.setAttribute('font-weight', 'bold');
  SVGHostName.textContent = 'Host: ' + hostName;

  // テキストタグ追加
  var Icon = document.getElementById(RTCID).firstChild;
  Icon.appendChild(SVGHostName);

}

/**
 * 更新時、すでに割り当てたホストを反映する
 * 
 * @param {*} isreload isreload
 * @returns {undefined}
 */
function reloadAssignedHostName(isreload) {
  
  let hostID = null;
  let hostName = null;
  // 作業領域に表示されているコンポーネントを取得
  let components = document.getElementById('main-panel').getElementsByTagName('rect');

  for (let i = 0; i < components.length; i++){
    // RTCIDを取得
    RTCID = components[i].parentNode.parentNode.getAttribute('id');
    
    // 画面ロード時
    if (isreload) {
      // DefaultSystem.xml を参照し、割り当てられているホストがあるか調べる
      let componentId = components[i].getAttribute('component-id');
      // RTCにホストが割り振られていなければ終了
      let pathurl = getPathUriInPackage(componentId);
      // ホスト割り当てがされていなければ終了
      if (pathurl.split('/')[0] == 'localhost:2809') {
        //textタグを表示
        updateHostNameOnRTCIcon('localhost', RTCID, 'local');
      }
      // ホストが割り当てられていれば
      else {
        //pathurlからIp、Passを取得
        let ip = pathurl.split('/')[0].split(':')[0];
        let nsport = pathurl.split('/')[0].split(':')[1];
        
        if (!isEmpty(hostMap)){
          // IP,PassからホストIDを取得
          hostMap.forEach((host, hostId) => {
            if (host['ip'] == ip && host['nsport'] == nsport) {
              hostID = hostId;
              hostName = host['hostName'];
              //textタグを表示
              updateHostNameOnRTCIcon(hostName, RTCID, hostID);
              // 対応するセレクトボックスのvalueも更新しておく
              changeSelectedAttribute(RTCID, hostID, hostName);
            }
          });
        }
      }
    }
    // ホスト割り当て時なら  
    else {
      let assignedHost = components[i].parentElement.getElementsByClassName('AssignedHost');
      // RTCにホストが割り振られていなければ終了
      if (assignedHost.length == 0) {
        break;
      }
      else {
        // ホストIDを取得
        hostID = assignedHost[0].getAttribute('id').replace('RTC-assigned-host-name-' + RTCID + '-', '');
        
        if (!document.getElementById(hostID)){
          // RTCアイコン上のホスト名を削除
          assignedHost[0].remove();
        }
        else {
          // hostIDから、ホストリストを参照しホスト名を取得
          hostName = hostMap.get(hostID)['hostName'];
          //textタグを表示
          updateHostNameOnRTCIcon(hostName, RTCID, hostID);
        }
      }
    } 
  }
}

/**
 * host割り当てボックスのselected属性を変更する
 * 
 * @param {*} RTCID RTCID
 * @param {*} hostID hostID
 * @param {*} hostName hostName
 * @returns {undefined}
 */
function changeSelectedAttribute(RTCID, hostID, hostName) {
  
  // 対応するセレクトボックスのvalueも更新しておく
  let selectBox = document.getElementById('RTC-assign-' + RTCID).getElementsByTagName('select')[0];
  for (let i = 0; i < selectBox.children.length; i++){
    if (selectBox.children[i].hasAttribute('selected')){
      selectBox.children[i].removeAttribute('selected');
    }
    if (selectBox.children[i].getAttribute('value') == hostID + '-' + hostName) {
      selectBox.children[i].setAttribute('selected', 'selected');
    }
  }
}

/**
 * ホスト認証画面を表示する
 * 
 * @param {*} obj obj
 * @param {*} hostname hostname
 * @param {*} ip ip
 * @param {*} nsport nsport
 * @param {*} wwport wwport
 * @returns {undefined}
 */
function openHostAuthPopup(obj, hostname, ip, nsport, wwport) {
  // RTCの設定ポップアップを表示する
  w2popup.open({
    title: "Host Authentication",
    width: '400px',
    height: '300px',
    body: '<div id="host-auth-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#host-auth-div').w2form(createHostAuthForm(obj, hostname, ip, nsport, wwport));
        // 釦を変更する
        $($('#host-auth-div .w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
      }
    }
  });

}

/**
 * ホスト認証画面を表示する
 * 
 * @param {*} obj obj
 * @param {*} hostname hostname
 * @param {*} ip ip
 * @param {*} nsport nsport
 * @param {*} wwport wwport
 * @returns {Map} form
 */
function createHostAuthForm(obj, hostname, ip, nsport, wwport) {
  if (w2ui['host-auth']) {
    w2ui['host-auth'].destroy();
  }
  var form = {
    name: 'host-auth',
    focus: -1,
    padding: 0,
    record: {
    },
    fields: [
      { field: 'host-id', type: 'text', required: true, html: { label: 'ID', page: 0, attr: 'style="width:200px"' } },
      { field: 'host-auth-password', type: 'password', required: true, html: { label: 'Password', page: 0, attr: 'style="width:200px;"' } },
  ],
    actions: {
      'OK': function () {
        if (this.validate().length === 0) {
       id = $('#host-id').val();
       password = $('#host-auth-password').val();
       if(isHostIdUnique(id) === 'false') {
              /* 認証エラーダイアログを表示 */
        w2alert('Host ID is not Unique');
        return;
       }
          let result = registerHostToConfigFile(hostname, ip, nsport, wwport, id, password);
          if (result === 'true') {
           /* ポップアップを閉じる */
          w2popup.close();
           /* ホスト一覧に追加する */
          addHostDataToList(id, hostname, ip, nsport, wwport);
           /* ホスト一覧に編集フォームも追加する */
          addHostEditForm(id, hostname, ip, nsport, wwport); 
           // hostMapにも追加する
          hostMap.set(id, {id: id, hostName: hostname, ip: ip, nsport: nsport, wwport: wwport, password: password });

           /* RTC割り当てボックスを更新する */
          updateRTCDataToHostAssignArea();

      // セレクトボックス・RTCアイコン上のホスト名を更新する
      reloadAssignedHostName(false);
           /* フォームのバッファをクリアする */
          obj.clear();
          }
          else {
            /* 認証エラーダイアログを表示 */
      w2alert('Host ID or Password are invalid');
          }

        }
        else {
          w2alert('Host ID or Password are invalid');
        }
      }
    }
  };

  return form
}

/**
 * 認証できたホストをホスト一覧に追加する
 *
 * @param {*} ID ID
 * @param {*} HostName HostName
 * @param {*} IP IP
 * @param {*} nsport nsport
 * @param {*} wwport wwport
 * @returns {undefined}
 */
function addHostDataToList(ID, HostName, IP, nsport, wwport) {
  
  let newHost = $('<div style="visibility:visible">');
  let hostId = 'host-' + ID;
  newHost.attr('id', hostId);
  newHost.append(
    '<img class="nameserver" src="../img/NS_not_running.png" style="height:15px;width:15px;margin-right:5px">'
  );

  let newHostName = $(
        '<nobr class="host-data-editable host-name" style="display:inline"></nobr>'
  ).text(HostName);
  newHost.append(newHostName);
  
  let colon1 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon1);
  
  let newHostIP = $(
        '<nobr class="host-data-editable host-ip" style="display:inline"></nobr>'
  ).text(IP);
  newHost.append(newHostIP);
  
  let colon2 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon2);
  
  let newHostPort = $(
        '<nobr class="host-data-editable host-port" style="display:inline"></nobr>'
  ).text(nsport);
  newHost.append(newHostPort);
  
  let slash = $(
        '<div style="display:inline"></div>'
  ).text('/ ');
  newHost.append(slash);
  
  let newWWPort = $(
        '<nobr class="host-data-editable ww-port" style="display:inline"></nobr>'
  ).text(wwport);
  newHost.append(newWWPort);
    
  $('#host-list').append(newHost);
}

/**
 * 認証できたAirGraphホストをホスト一覧に追加する
 *
 * @param {*} hostName ホスト名
 * @param {*} ip IPアドレス
 * @param {*} port ポート
 * @returns {undefined}
 */
function addAirGraphHostDataToList(hostName, ip, port) {
  let id = 0;
  if ($('#airgraph-host-list').children().length > 0) {
    id = Math.floor($('#airgraph-host-list').children().length / 2);
  }
  let newHost = $('<div style="visibility:visible">');
  let hostId = 'airgraph-host-' + String(id);
  newHost.attr('id', hostId);

  let newHostName = $(
        '<nobr class="host-data-editable host-name" style="display:inline"></nobr>'
  ).text(hostName);
  newHost.append(newHostName);
  
  let colon1 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon1);
  
  let newHostIP = $(
        '<nobr class="host-data-editable host-ip" style="display:inline"></nobr>'
  ).text(ip);
  newHost.append(newHostIP);
  
  let colon2 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon2);
  
  let newHostPort = $(
        '<nobr class="host-data-editable host-port" style="display:inline"></nobr>'
  ).text(port);
  newHost.append(newHostPort);
    
  $('#airgraph-host-list').append(newHost);
}

/**
 * ホスト編集時にInput要素を生成する
 * 
 * @param {*} id ID
 * @param {*} HostName HostName
 * @param {*} IP IP
 * @param {*} nsport nsport
 * @param {*} wwport wwport
 * @returns {undefined}
 */
function addHostEditForm(id, HostName, IP, nsport, wwport) {
  
  let newHost = $('<div>');
  let hostId = 'edit-host-' + id;
  newHost.attr('id', hostId);
  newHost.attr('class', 'edit-host');
  newHost.attr('style', 'display:none');
  
  newHost.append(
    '<img class="nameserver" src="../img/NS_not_running.png" style="height:15px;width:15px;margin-right:5px">'
  );

  let newHostName = $(
        '<input type="text" class="edit-host-name" style="width:80px">'
  ).attr('value', HostName);
  newHost.append(newHostName);
  
  let colon1 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon1);
  
  let newHostIP = $(
        '<input type="text" class="edit-host-ip" style="width:80px">'
  ).attr('value', IP);
  newHost.append(newHostIP);
  
  let colon2 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon2);
  
  let newHostPort = $(
        '<input type="text" class="edit-host-port" style="width:50px">'
  ).attr('value', nsport);
  newHost.append(newHostPort);
  
  let slash = $(
        '<div style="display:inline"></div>'
  ).text('/ ');
  newHost.append(slash);
  
  let newWWPort = $(
        '<input type="text" class="edit-ww-port" style="width:50px">'
  ).attr('value', wwport);
  newHost.append(newWWPort);
  
  if (id !== 'local') {
    let deleteBtn = $(
      '<img class="host-data-delete-btn ui-icon ui-icon-close"  style="display:inline;float:right">'
    );
     deleteBtn.on('click', function(){
      deleteHostData(hostId);
    });
    newHost.append(deleteBtn);
  }
  
  $('#host-list').append(newHost);
}

/**
 * AirGraphのホストをホストリストに追加する.
 *
 * @param {*} hostName ホスト名
 * @param {*} ip IPアドレス
 * @param {*} port ポート番号
 * @returns {undefined}
 */
function addAirGraphHostEditForm(hostName, ip, port) {
  
  let id = 0;
  if ($('#airgraph-host-list').children().length > 0) {
    id = Math.floor($('#airgraph-host-list').children().length / 2);
  }
  let newHost = $('<div>');
  let hostId = 'edit-airgraph-host-' + String(id);
  newHost.attr('id', hostId);
  newHost.attr('class', 'edit-host');
  newHost.attr('style', 'display:none');

  let newHostName = $(
        '<input type="text" class="edit-host-name" style="width:80px">'
  ).attr('value', hostName);
  newHost.append(newHostName);
  
  let colon1 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon1);
  
  let newHostIP = $(
        '<input type="text" class="edit-host-ip" style="width:80px">'
  ).attr('value', ip);
  newHost.append(newHostIP);
  
  let colon2 = $(
        '<div style="display:inline"></div>'
  ).text(': ');
  newHost.append(colon2);
  
  let newHostPort = $(
        '<input type="text" class="edit-host-port" style="width:50px">'
  ).attr('value', port);
  newHost.append(newHostPort);
  
  let deleteBtn = $(
    '<img class="host-data-delete-btn ui-icon ui-icon-close"  style="display:inline;float:right">'
  );
   deleteBtn.on('click', function(){
    deleteHostData(hostId);
  });
  newHost.append(deleteBtn);
    
  $('#airgraph-host-list').append(newHost);
}

/**
 * ホスト定義ファイルの中のホストをリストに追加していく
 * 
 * @param {*} hosts hosts
 * @returns {undefined}
 */
function updateHostList(hosts) {
  if (hosts === undefined){
    return null;
  }
  
  // すでにあるリストを消去する
  $('#host-list').empty();
  $('#airgraph-host-list').empty();

  hostMap = new Map();
  airGraphHostMap = new Map();
  
  for (let i = 0; i < hosts[0].length; i++) {
    // ホストビューに反映する
    addHostDataToList(hosts[0][i]["id"], hosts[0][i]["hostName"], hosts[0][i]["ip"], hosts[0][i]["nsport"], hosts[0][i]["wwport"]);
    addHostEditForm(hosts[0][i]["id"], hosts[0][i]["hostName"], hosts[0][i]["ip"], hosts[0][i]["nsport"], hosts[0][i]["wwport"]);
    
    // ホストマップに追加する
    hostMap.set(hosts[0][i]["id"], hosts[0][i]);
  }
  for (let i = 0; i < hosts[1].length; i++) {
    // ホストビューに反映する
    addAirGraphHostDataToList(hosts[1][i]["hostName"], hosts[1][i]["ip"], hosts[1][i]["port"]);
    addAirGraphHostEditForm(hosts[1][i]["hostName"], hosts[1][i]["ip"], hosts[1][i]["port"]);
    
    // ホストマップに追加する
    airGraphHostMap.set(hosts[1][i]["id"], hosts[1][i]);
  }
}


/*******************************************************************************
 * データポート設定関連
 ******************************************************************************/
/**
 * データポート設定画面を新規作成状態で起動する
 * 
 * @param {*} componentId componentId
 * @param {*} isIn isIn
 * @returns {undefined}
 */
function openNewDataPortSettingPopup(componentId, isIn) {
  var portData = getDefaultDataPort(componentId, isIn);
  openRtcSettingPopup(createOneDataPortSettingForm(portData, false), 'Data Port Setting', 400, 250);
}

/**
 * データポート設定画面を編集状態で起動する
 * 
 * @param {*} portData portData
 * @returns {undefined}
 */
function openEditDataPortSettingPopup(portData) {
  openRtcSettingPopup(createOneDataPortSettingForm(portData, true), 'Data Port Setting', 400, 250);
}

/**
 * ポート毎の設定画面を表示する
 * 
 * @param {*} portData portData
 * @param {*} updateFlg updateFlg
 * @returns {Map} form
 */
function createOneDataPortSettingForm(portData, updateFlg) {
  destroySettingForm();

  var form = {
    name: 'rtc-profile-setting',
    focus: -1,
    padding: 0,
    tabs: [
      { id: 'dataport-tab1', text: 'Data Port' }
    ],
    fields: [
      { field: 'dataport_name', type: 'text', required: true, html: { label: 'Port Name', page: 0, attr: 'style="width:200px"' } },
      { field: 'dataport_type', type: 'list', required: true, html: { label: 'Data Type', page: 0, attr: 'style="width:200px"' }, options: { items: getDataTypeChoices(portData.componentName) } },
      { field: 'dataport_val_name', type: 'text', required: false, html: { label: 'Variable Name', page: 0, attr: 'style="width:200px"' } }
    ],
    record: {
      dataport_name: portData.portName,
      dataport_type: {id: portData.dataType, text: portData.dataType},
      dataport_val_name: portData.variableName
    }
  };

  if (updateFlg === true) {
    // 編集の場合
    if (isPortConnected(portData.id, portData.modelId, portData.portName)) {
      // ポートが接続されている場合は編集不可とする
      form['fields'][0]['html']['attr'] = 'style="width:200px;"disabled="disabled"';
      form['fields'][1]['html']['attr'] = 'style="width:200px;"disabled="disabled"';
      form['fields'][2]['html']['attr'] = 'style="width:200px;"disabled="disabled"';
    } else {
      // 更新時のアクション
      form['actions'] = {
        'Update': function () {
          if (curState !== STATE.EDIT) {
            w2alert('初期化中・実行中は編集できません');
          } else if (this.validate().length === 0) {
            var componentId = portData.id;
            var index = portData.index;

            // 変更前
            var oldPortName = portData.portName;
            var oldDataType = portData.dataType;
            var oldValName = portData.variableName;
            // 変更後
            var newPortName = $('#dataport_name').val();
            var newDataType = $('#dataport_type').val();
            var newValName = $('#dataport_val_name').val();
            if (existSamePortName(componentId, index, newPortName, true)) {
              w2alert('ポート名称が重複しています');
            } else {
              w2popup.close();
              if (oldPortName !== newPortName || oldDataType !== newDataType || oldValName !== newValName) {
                // データポート更新
                var rtc = updateDataPort(componentId, index, newPortName, newDataType, newValName);
                // PackageのRTC更新
                updateRtcInPackage(rtc);
                // 作業領域再読み込み
                // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
                updatePackage(true);
              }
            }
          }
        }
      }
    }
  } else {
    // 新規追加の場合
    form['actions'] = {
      'Create': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (this.validate().length === 0) {
          // 変更後
          var newPortName = $('#dataport_name').val();
          var newDataType = $('#dataport_type').val();
          var newValName = $('#dataport_val_name').val();

          var componentId = portData.id;
          var rtc = getComponentInPackage(componentId);
          var isIn = portData.isIn;

          if (existSamePortName(componentId, -1, newPortName, true)) {
            w2alert('ポート名称が重複しています');
          } else {
            w2popup.close();
            // データポート追加
            rtc = addDataPort(componentId, newPortName, newDataType, newValName, isIn);
            // PackageのRTC更新
            updateRtcInPackage(rtc);
            // 作業領域再読み込み
            // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
            updatePackage(true);
          }
        }
      }
    }
  }
  return form;
}

/*******************************************************************************
 * サービスポート設定関連
 ******************************************************************************/
/**
 * サービスポート設定画面を新規作成状態で起動する
 * 
 * @param {*} componentId componentId
 * @returns {undefined}
 */
function openNewServicePortSettingPopup(componentId) {
  var portData = getDefaultServicePort(componentId);
  openRtcSettingPopupForServicePort(portData, false);
}

/**
 * サービスポート設定画面を編集状態で起動する
 * 
 * @param {*} portData portData
 * @returns {undefined}
 */
function openEditServicePortSettingPopup(portData) {
  openRtcSettingPopupForServicePort(portData, true);
}

/**
 * サービスポート用の設定画面を表示する
 * 
 * @param {*} portData portData
 * @param {*} updateFlg updateFlg
 * @returns {undefined}
 */
function openRtcSettingPopupForServicePort(portData, updateFlg) {
  // RTCのサービスポート用の設定ポップアップを表示する
  w2popup.open({
    title: 'Service Port/Interface Setting',
    width: 900,
    height: 460,
    body: '<div id="rtc-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        // レイアウトを表示する
        $('#w2ui-popup #rtc-profile-div').w2layout(createOneServicePortSettingLayOut());
        // レイアウトにFormとGridを反映する
        createOneServicePortSettingForm(portData, updateFlg);
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em').css('width', '120px');
      }
    }
  });
}

/**
 * サービスポート毎の設定画面のレイアウト部分を生成する
 * 
 * @returns {Map} layout
 */
function createOneServicePortSettingLayOut() {
  destroySettingForm();

  // レイアウト
  var pstyle = 'border: none; padding: 0px;';
  var layout = {
    name: 'rtc-profile-layout',
    panels: [
      {
        type: 'top', size: 125, resizable: false, style: pstyle,
        content: '<div id="rtc-profile-service-form-div" style="height:100%;"></div>'
      },
      {
        type: 'left', size: 200, resizable: false, style: pstyle,
        content: '<div style="height:100%;"><div id="rtc-profile-if-grid-div" style="height:85%;"></div><div id="rtc-profile-if-add-div" style="height:15%;"></div></div>',
        tabs: [{ id: 'interfacelist-tab1', text: 'Interface List' }
        ]
      },
      { type: 'main', style: pstyle, content: '<div id="rtc-profile-if-form-div" style="height:100%;"></div>' },
      { type: 'bottom', size: 50, resizable: false, style: pstyle, content: '<div id="rtc-profile-if-save-div" style="height:100%;"></div>' },
    ]
  };
  return layout;
}

/**
 * サービスポート毎の設定画面のレイアウトにフォームを反映する
 * 
 * @param {*} portData portData
 * @param {*} updateFlg updateFlg
 * @returns {undefined}
 */
function createOneServicePortSettingForm(portData, updateFlg) {
  // ポート部分を生成
  var portForm = {
    name: 'rtc-profile-setting',
    padding: 0,
    tabs: [
      { id: 'serviceport-tab1', text: 'Service Port' }
    ],
    fields: [
      { field: 'serviceport_name', type: 'text', required: true, html: { label: 'Port Name', page: 0, attr: 'style="width:450px"' } },
      { field: 'serviceport_pos', type: 'list', required: true, html: { label: 'Position', page: 0, attr: 'style="width:450px"' }, options: { items: getPortPositionChoices() } }
    ],
    record: {
      serviceport_name: portData.portName,
      serviceport_pos: {id: portData.position, text: portData.position},
    }
  };

  // インタフェース部分のGridを生成
  var ifGrid = {
    name: 'rtc-profile-if-grid',
    show: {
      selectColumn: true
    },
    multiSelect: false,
    columns: [
      { field: 'name', text: 'name', size: '100%', editable: { type: 'text' } }
    ],
    onClick: function (event) {
      if (w2ui['rtc-profile-if-grid']['curSelectRow'] >= 0 && w2ui['rtc-profile-if-setting'].validate().length > 0) {
        // 入力エラーがある場合は遷移させない
        w2ui['rtc-profile-if-grid'].selectNone();
        w2ui['rtc-profile-if-grid'].select(w2ui['rtc-profile-if-grid']['curSelectRow']);
      } else {
        if (w2ui['rtc-profile-if-grid']['curSelectRow'] >= 0) {
          saveServiceInterfaceSettingData(w2ui['rtc-profile-if-grid']['curSelectRow']);
        }
        w2ui['rtc-profile-if-grid'].selectNone();
        w2ui['rtc-profile-if-grid'].select(event.recid);
        setServiceInterfaceSettingData(event.recid);
        // 選択したインタフェースの情報を設定する
        w2ui['rtc-profile-if-grid']['curSelectRow'] = event.recid;
      }
      event.preventDefault();
    },
    componentName: portData.componentName
  };

  // Gridの追加釦
  var addBtn = $('<button type="button">');
  addBtn.html('Add')
  addBtn.attr('id', 'if-grid-add-btn');
  addBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '3px').css('margin-left', '20px').css('margin-right', '5px').css('width', '70px').css('font-size', '1em');
  $('<span>').addClass('ui-icon').addClass('ui-icon-plus').appendTo(addBtn);
  $(addBtn).on('click', function () {
    addServiceInterface();
  });

  // Gridの削除釦
  var delBtn = $('<button type="button">');
  delBtn.html('Delete')
  delBtn.attr('id', 'if-grid-delete-btn');
  delBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '3px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px').css('font-size', '1em');
  $('<span>').addClass('ui-icon').addClass('ui-icon-trash').appendTo(delBtn);
  $(delBtn).on('click', function () {
    deleteServiceInterface(w2ui['rtc-profile-if-grid']['curSelectRow']);
  });

  $('#rtc-profile-if-add-div').append(addBtn).append(delBtn);

  // インタフェース部分
  var ifForm = {
    name: 'rtc-profile-if-setting',
    padding: 0,
    tabs: [
      { id: 'serviceinterface-tab1', text: 'Service Interface' }
    ],
    fields: [
      { field: 'serviceif_name', type: 'text', required: true, html: { label: 'Interface Name', page: 0, attr: 'style="width:500px"' } },
      { field: 'serviceif_direction', type: 'list', required: true, html: { label: 'Direciton', page: 0, attr: 'style="width:500px"' }, options: { items: getIfDirectionChoices() } },
      { field: 'serviceif_instancename', type: 'text', required: false, html: { label: 'Instance Name', page: 0, attr: 'style="width:500px"' } },
      { field: 'serviceif_valname', type: 'text', required: false, html: { label: 'Variable Name', page: 0, attr: 'style="width:500px"' } },
      { field: 'serviceif_idlfile', type: 'list', required: true, html: { label: 'IDL File', page: 0, attr: 'style="width:500px"' }, options: { items: getIdlFileChoices(portData.componentName) } },
      { field: 'serviceif_iftype', type: 'list', required: true, html: { label: 'Interface Type', page: 0, attr: 'style="width:500px"' } },
      { field: 'serviceif_idlpath', type: 'text', required: false, html: { label: 'IDL Path', page: 0, attr: 'style="width:500px"' } },
    ],
    record: {},
    onChange: function (event) {
      if (event.target === 'serviceif_idlfile') {
        event.done(function () {
          // IDLファイルを変更した場合、インタフェース型の選択肢を作りなおす
          updateInterfaceTypeChoices(portData.componentName, event.value_new.text);
        });
      }
    }
  };

  var saveForm = { name: 'rtc-profile-if-save', padding: 0, tabs: [], fields: [{ field: 'dummy', type: 'text', required: false, html: { attr: 'style="display:none"' } }], record: {} };
  if (updateFlg === true) {
    // 更新時のアクション
    saveForm['actions'] = {
      'Update': function () {
        if (w2ui['rtc-profile-if-grid']['curSelectRow'] >= 0) {
          // 一時保存
          saveServiceInterfaceSettingData(w2ui['rtc-profile-if-grid']['curSelectRow']);
        }
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (w2ui['rtc-profile-setting'].validate().length === 0 ||
          (w2ui['rtc-profile-if-grid']['curSelectRow'] >= 0 && w2ui['rtc-profile-if-setting'].validate().length === 0)) {
          var componentId = portData.id;
          var index = portData.index;
          // 変更前
          var oldPortName = portData.portName;
          var oldPosition = portData.position;
          var oldInterfaceList = portData.interfaceArray;
          // 変更後
          var newPortName = $('#serviceport_name').val();
          var newPosition = $('#serviceport_pos').val();
          var newInterfaceList = getInterfaceDataFromGrid();

          if (existSamePortName(componentId, index, newPortName, false)) {
            w2alert('ポート名称が重複しています');
          } else {
            w2popup.close();
            if (oldPortName !== newPortName || oldPosition !== newPosition || serviceInterfaceChanged(oldInterfaceList, newInterfaceList)) {
              // サービスポート更新
              var rtc = updateServicePort(componentId, index, newPortName, newPosition, newInterfaceList);
              // PackageのRTC更新
              updateRtcInPackage(rtc);
              // 作業領域再読み込み
              // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
              updatePackage(true);
            }
          }
        }
      }, 'Upload_IDL_File': function () {
        $('#idl-package-name').val(curWorkspaceName);
        $('#idl-component-name').val(portData.componentName);
        // ファイルアップロード画面を開く
        $('#idl-upload').click();
      }
    }
  } else {
    // 新規追加時のアクション
    saveForm['actions'] = {
      'Create': function () {
        if (w2ui['rtc-profile-if-grid']['curSelectRow'] >= 0) {
          // 一時保存
          saveServiceInterfaceSettingData(w2ui['rtc-profile-if-grid']['curSelectRow']);
        }
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (w2ui['rtc-profile-setting'].validate().length === 0 &&
          (w2ui['rtc-profile-if-grid'].records.length === 0 ||
            w2ui['rtc-profile-if-setting'].validate().length === 0)) {
          // 変更後
          var newPortName = $('#serviceport_name').val();
          var newPosition = $('#serviceport_pos').val();
          var newInterfaceList = getInterfaceDataFromGrid();

          var componentId = portData.id;
          var rtc = getComponentInPackage(componentId);

          if (existSamePortName(componentId, -1, newPortName, false)) {
            w2alert('ポート名称が重複しています');
          } else {
            w2popup.close();
            // データポート追加
            rtc = addServicePort(componentId, newPortName, newPosition, newInterfaceList);
            // PackageのRTC更新
            updateRtcInPackage(rtc);
            // 作業領域再読み込み
            // reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
            updatePackage(true);
          }
        }
      }, 'Upload_IDL_File': function () {
        $('#idl-package-name').val(curWorkspaceName);
        $('#idl-component-name').val(portData.componentName);
        // ファイルアップロード画面を開く
        $('#idl-upload').click();
      }
    }
  }

  // パネルに設定する
  $('#rtc-profile-service-form-div').w2form(portForm);
  $('#rtc-profile-if-grid-div').w2grid(ifGrid);
  $('#rtc-profile-if-form-div').w2form(ifForm);
  $('#rtc-profile-if-save-div').w2form(saveForm);

  // 釦を設定
  $('#if-grid-add-btn').button();
  $('#if-grid-delete-btn').button();

  // Gridにデータを表示する
  setServiceInterfaceGridData($.extend(true, {}, portData));
}

/**
 * サービスインタフェースのGridに値を設定する
 * 
 * @param {*} portData portData
 * @returns {undefined}
 */
function setServiceInterfaceGridData(portData) {
  // 一度クリアする
  clearServiceInterfaceGridData();

  // 選択したインタフェースのINDEX
  var selectedIfIndex = -1;

  var ifArray = [];
  if (portData.interfaceArray && portData.interfaceArray.length > 0) {
    for (var i = 0; i < portData.interfaceArray.length; i++) {
      var record = { recid: i, name: portData.interfaceArray[i].name, ifData: portData.interfaceArray[i] };
      ifArray.push(record);
      if (selectedIfIndex < 0) {
        selectedIfIndex = i;
      }
    }
  }

  if (ifArray.length > 0) {
    // Gridに追加する
    w2ui['rtc-profile-if-grid'].add(ifArray);
  }
  $('#if-grid-add-btn').button('enable');
}

/**
 * サービスインタフェースのGridをクリアする
 * 
 * @returns {undefined}
 */
function clearServiceInterfaceGridData() {
  w2ui['rtc-profile-if-grid'].clear();
  w2ui['rtc-profile-if-grid'].selectNone();
  $('#if-grid-delete-btn').button('disable');
  clearServiceInterfaceSetting();
}

/**
 * インタフェース設定部分をクリアする
 * 
 * @returns {undefined}
 */
function clearServiceInterfaceSetting() {
  // インタフェース設定項目もdisableにする
  $('#serviceif_name').attr('disabled', 'disabled');
  $('#serviceif_direction').attr('disabled', 'disabled');
  $('#serviceif_instancename').attr('disabled', 'disabled');
  $('#serviceif_valname').attr('disabled', 'disabled');
  $('#serviceif_idlfile').attr('disabled', 'disabled');
  $('#serviceif_iftype').attr('disabled', 'disabled');
  $('#serviceif_idlpath').attr('disabled', 'disabled');
  // 値を空
  w2ui['rtc-profile-if-setting'].clear()
}

/**
 * 選択したサービスインタフェースのデータを設定する
 * 
 * @param {*} index index
 * @returns {undefined}
 */
function setServiceInterfaceSettingData(index) {
  var ifData = w2ui['rtc-profile-if-grid'].records[index]['ifData'];
  var componentName = w2ui['rtc-profile-if-grid']['componentName'];
  var idlFileName;

  if (ifData && ifData.idlFile) {
    idlFileName = ifData.idlFile;
  }

  // インタフェース型の選択肢を作りなおす
  updateInterfaceTypeChoices(componentName, idlFileName);

  if (ifData) {
    // 釦を有効化
    $('#if-grid-delete-btn').button('enable');
    // enable化
    $('#serviceif_name').removeAttr('disabled');
    $('#serviceif_direction').removeAttr('disabled');
    $('#serviceif_instancename').removeAttr('disabled');
    $('#serviceif_valname').removeAttr('disabled');
    $('#serviceif_idlfile').removeAttr('disabled');
    $('#serviceif_iftype').removeAttr('disabled');
    $('#serviceif_idlpath').removeAttr('disabled');
    // 値設定
    w2ui['rtc-profile-if-setting'].record = {
      'serviceif_name': ifData.name,
      'serviceif_direction': {id: ifData.direction, text: ifData.direction},
      'serviceif_instancename': ifData.instanceName,
      'serviceif_valname': ifData.variableName,
      'serviceif_idlfile': {id: ifData.idlFile, text: ifData.idlFile},
      'serviceif_iftype': {id: ifData.interfaceType, text: ifData.interfaceType},
      'serviceif_idlpath': ifData.path
    }
    w2ui['rtc-profile-if-setting'].refresh();
  }
}

/**
 * 変更したインタフェースの内容を反映する
 * 
 * @param {*} index index
 * @returns {undefined}
 */
function saveServiceInterfaceSettingData(index) {
  // 変更したデータを反映する
  var ifData = w2ui['rtc-profile-if-grid'].records[index]['ifData'];
  ifData.name = $('#serviceif_name').val();
  ifData.direction = $('#serviceif_direction').val();
  ifData.instanceName = $('#serviceif_instancename').val();
  ifData.variableName = $('#serviceif_valname').val();
  ifData.idlFile = $('#serviceif_idlfile').val();
  ifData.interfaceType = $('#serviceif_iftype').val();
  ifData.path = $('#serviceif_idlpath').val();
  w2ui['rtc-profile-if-grid'].records[index]['ifData'] = ifData;

  // 名称も変える
  w2ui['rtc-profile-if-grid'].records[index]['name'] = ifData.name;
  w2ui['rtc-profile-if-grid'].reload();
}

/**
 * インタフェース型のリストを作りなおす
 * 
 * @param {*} componentName componentName
 * @param {*} idlFileName idlFileName
 * @returns {undefined}
 */
function updateInterfaceTypeChoices(componentName, idlFileName) {
  // インタフェース型のリストを一度空にする
  w2ui["rtc-profile-if-setting"].clear('serviceif_iftype');
  w2ui["rtc-profile-if-setting"].fields[5]['options']['items'] = [];
  w2ui["rtc-profile-if-setting"].refresh();

  if (idlFileName) {
    // IDLファイルが設定されている場合は選択肢を取得する
    var items = getInterfaceTypeChoices(componentName, idlFileName);
    if (items && Object.keys(items).length > 0) {
      w2ui["rtc-profile-if-setting"].fields[5]['options']['items'] = items;
    }
    w2ui["rtc-profile-if-setting"].refresh();
  }
}

/**
 * サービスインタフェースを追加する
 * 
 * @returns {undefined}
 */
function addServiceInterface() {
  if (w2ui['rtc-profile-if-grid']['curSelectRow'] >= 0 && w2ui['rtc-profile-if-setting'].validate().length > 0) {
    // 選択しているレコードにエラーがある場合は何もしない
    // NOP
  } else {
    // レコード数
    var recordCnt = w2ui['rtc-profile-if-grid'].records.length;
    var ifName = 'interface' + recordCnt;
    var ifData = new Object();
    ifData['name'] = ifName;
    var record = { recid: recordCnt, name: ifName, ifData: ifData };
    // Gridに追加する
    w2ui['rtc-profile-if-grid'].add(record);
  }
}

/**
 * サービスインタフェースを削除する
 * 
 * @param {*} index index
 * @returns {undefined}
 */
function deleteServiceInterface(index) {
  clearServiceInterfaceSetting();
  w2ui['rtc-profile-if-grid'].records.splice(index, 1);
  if (w2ui['rtc-profile-if-grid'].records && w2ui['rtc-profile-if-grid'].records.length > 0) {
    for (var i = 0; i < w2ui['rtc-profile-if-grid'].records.length; i++) {
      w2ui['rtc-profile-if-grid'].records[i]['recid'] = i;
    }
  }
  w2ui['rtc-profile-if-grid'].reload();
  w2ui['rtc-profile-if-grid'].selectNone();
  w2ui['rtc-profile-if-grid']['curSelectRow'] = -1;
}

/**
 * 編集したインタフェースのデータを取得する
 * 
 * @returns {Array} interfaceList
 */
function getInterfaceDataFromGrid() {
  var interfaceList = null;
  if (w2ui['rtc-profile-if-grid'].records && w2ui['rtc-profile-if-grid'].records.length > 0) {
    interfaceList = [];
    for (var i = 0; i < w2ui['rtc-profile-if-grid'].records.length; i++) {
      interfaceList.push(w2ui['rtc-profile-if-grid'].records[i]['ifData']);
    }
  }
  return interfaceList;
}

/**
 * サービスインタフェースの変更をチェックする
 * 
 * @param {*} list1 list1
 * @param {*} list2 list2
 * @returns {boolean} result
 */
function serviceInterfaceChanged(list1, list2) {
  if (list1 === null && list2 === null) {
    return false;
  } else if (list1 === null) {
    return true;
  } else if (list2 === null) {
    return true;
  } else {
    if (list1.length !== list2.length) {
      return true;
    } else {
      for (var i = 0; i < list1.length; i++) {
        if (list1[i].name !== list2[i].name) {
          return true;
        } else if (list1[i].direction !== list2[i].direction) {
          return true;
        } else if (list1[i].instanceName !== list2[i].instanceName) {
          return true;
        } else if (list1[i].variableName !== list2[i].variableName) {
          return true;
        } else if (list1[i].idlFile !== list2[i].idlFile) {
          return true;
        } else if (list1[i].interfaceType !== list2[i].interfaceType) {
          return true;
        }
      }
    }
  }
  return false;
}

/*******************************************************************************
 * Profile設定共通
 ******************************************************************************/
/**
 * 設定用Formのインスタンスを開放する
 * 
 * @returns {undefined}
 */
function destroySettingForm() {
  // 同一名は作成する前に一度破棄する
  if (w2ui['rts-profile-setting']) {
    w2ui['rts-profile-setting'].destroy();
  }
  if (w2ui['rtc-profile-setting']) {
    w2ui['rtc-profile-setting'].destroy();
  }
  if (w2ui['rtc-profile-if-grid']) {
    w2ui['rtc-profile-if-grid'].destroy();
  }
  if (w2ui['rtc-profile-if-setting']) {
    w2ui['rtc-profile-if-setting'].destroy();
  }
  if (w2ui['rtc-profile-if-save']) {
    w2ui['rtc-profile-if-save'].destroy();
  }
  if (w2ui['rtc-profile-layout']) {
    w2ui['rtc-profile-layout'].destroy();
  }
  if (w2ui['rtc-profile-configuration-grid']) {
    w2ui['rtc-profile-configuration-grid'].destroy();
  }
  if (w2ui['rtc-profile-configuration-setting']) {
    w2ui['rtc-profile-configuration-setting'].destroy();
  }
  if (w2ui['rtc-profile-configuration-save']) {
    w2ui['rtc-profile-configuration-save'].destroy();
  }
  if (w2ui['git-form']) {
    w2ui['git-form'].destroy();
  }
  if (w2ui['add-update-to-binder']) {
    w2ui['add-update-to-binder'].destroy();
  }
  if (w2ui['host-auth']) {
    w2ui['host-auth'].destroy();
  }

}

/*******************************************************************************
 * ソースコード設定関連
 ******************************************************************************/

/**
 * ソースコード生成ポップアップを表示する
 * 
 * @param {*} id id
 * @returns {undefined}
 */
function openSourceCodePopup(id) {

  // 対象のRTCのコードディレクトリ管理情報を取得する
  var codeDirectory = getCodeDirectoryInPackageRtcDir(id);

  if (codeDirectory) {
    // ツリーを生成する
    var rootNode = createCodeSideBar(id, codeDirectory, '');
    rootNode['group'] = true;

    // ツリーからサイドバーを生成する
    var sideBarData = createSidebarData(rootNode, id);

    // 右クリックメニューを生成する
    setSourceEditorFolderContextMenu();
    setSourceEdtiorFileContextMenu();

    // コードエディタをポップアップ表示する
    w2popup.open({
      title: 'Source Code Editor',
      width: 900,
      height: 600,
      showMax: true,
      body: '<div id="souce-editor-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
      onOpen: function (event) {
        event.onComplete = function () {
          $('#w2ui-popup #souce-editor-div').w2layout({
            name: 'layout-panel-source-editor',
            padding: 0,
            panels: [
              { type: 'left', size: 300, resizable: true, minSize: 200, content: $().w2sidebar(sideBarData) },
              { type: 'main', minSize: 350, overflow: 'hidden', content: $('#monaco-editor') },
            ]
          });
        }
      },
      onMax: function (event) {
        event.onComplete = function () {
          w2ui['layout-panel-source-editor'].resize();
        }
      },
      onMin: function (event) {
        event.onComplete = function () {
          w2ui['layout-panel-source-editor'].resize();
        }
      },
      onClose: function () {
        // 保存する
        if (sourceEditor['sourcePath']) {
          mainRtsMap[curWorkspaceName].editSourceCode[sourceEditor['sourcePath']] = sourceEditor.getValue();
        }

        w2ui['soruce-editor-sidebar'].destroy();
        w2ui['layout-panel-source-editor'].destroy();

        var sourceEditorDiv = $('<div>');
        sourceEditorDiv.attr('id', 'monaco-editor');
        sourceEditorDiv.css('height', '100%').css('width', '99%');
        sourceEditorDiv.appendTo('#monaco-editor-parent');
        createSourceEditor();
      },
      onKeydown: function (event) {
        // ESCAPE key pressed
        if (event.originalEvent.keyCode == 27) {
          event.preventDefault()
        }
      }
    });
  }
}

/**
 * ソースコードのディレクトリ構成を生成する
 * 
 * @param {*} rtcId rtcId
 * @param {*} codeDirectory codeDirectory
 * @param {*} prefix prefix
 * @returns {*} node
 */
function createCodeSideBar(rtcId, codeDirectory, prefix) {
  if (codeDirectory) {
    // ルートディレクトリ
    var node = { rtcId: rtcId, id: prefix + codeDirectory.curDirName, text: codeDirectory.curDirName, img: 'icon-folder', expanded: true, isCode: false, nodes: [] };
    if (codeDirectory.codePathMap) {
      // ソースコードの名称と絶対パスMAP
      for (key in codeDirectory.codePathMap) {
        if ($.inArray(codeDirectory.codePathMap[key], mainRtsMap[curWorkspaceName].deleteFileList) < 0) {
          // 削除リストに含まれていない場合にのみ追加
          var nodeChild = { rtcId: rtcId, id: prefix + key, text: key, path: codeDirectory.codePathMap[key], img: 'icon-page', isCode: true, nodes: [] };
          node.nodes.push(nodeChild);
        }
      }
    }
    if (codeDirectory.directoryMap) {
      // ディレクトリ名と配下の構成情報のMAP
      for (key in codeDirectory.directoryMap) {
        // 再帰的に呼び出す
        var nodeDir = createCodeSideBar(rtcId, codeDirectory.directoryMap[key], prefix + key + '-');
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
 * ソースコードポップアップの左の領域を生成する
 * 
 * @param {*} rootNode rootNode
 * @param {*} id id
 * @returns {Map} sidebarData
 */
function createSidebarData(rootNode, id) {
  var sidebarData = {
    name: 'soruce-editor-sidebar',
    componentId: id,
    nodes: [rootNode],
    onClick: function (event) {
      // 右クリックメニューの紐付を解除
      if ($('.rtc-source-file-menu')) {
        $('.rtc-source-file-menu').removeClass('rtc-source-file-menu');
      }

      // サイドメニューをクリックされた時のイベント
      if (event.object && event.object.isCode === true) {
        // 保存する
        if (sourceEditor['sourcePath']) {
          mainRtsMap[curWorkspaceName].editSourceCode[sourceEditor['sourcePath']] = sourceEditor.getValue();
        }

        setState(STATE.INIT);
        sourceEditor.updateOptions({ readOnly: false });
        sourceEditor.setModel(Monaco.Editor.createModel('', ''));

        var codeMap = null;
        if (mainRtsMap[curWorkspaceName].rtcs) {
          for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
            if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === event.object.rtcId) {
              codeMap = mainRtsMap[curWorkspaceName].rtcs[i].pathContentMap;
              break;
            }
          }
        }

        // MAPからデータを取得する
        if (codeMap) {
          sourceEditor['sourcePath'] = event.object.path;
          var value = '';
          if (mainRtsMap[curWorkspaceName].editSourceCode[sourceEditor['sourcePath']]) {
            // 編集データが存在する場合はそれを設定する
            value = mainRtsMap[curWorkspaceName].editSourceCode[sourceEditor['sourcePath']];
          } else {
            // 未編集の場合は元のデータを設定する
            value = codeMap[event.object.path];
          }
          changeEditorMode(value, event.object.id);
        }

        // 右クリックメニューを紐付け
        $(document.getElementById('node_' + event.target)).addClass('rtc-source-file-menu');
      } else {
        // ソースコード以外の場合
        sourceEditor.updateOptions({ readOnly: true });
        sourceEditor['sourcePath'] = '';
        sourceEditor.setModel(Monaco.Editor.createModel('', ''));
      }
      setState(STATE.EDIT);
    }
  };
  return sidebarData;
}

/**
 * ソースコードポップアップの左の領域を再作成する
 * 
 * @param {*} id id
 * @returns {undefined}
 */
function updateSidebarData(id) {
  // 編集中のファイルを一時保存
  if (sourceEditor['sourcePath']) {
    mainRtsMap[curWorkspaceName].editSourceCode[sourceEditor['sourcePath']] = sourceEditor.getValue();
  }

  // 一度削除
  w2ui['soruce-editor-sidebar'].destroy();

  // 対象のRTCのコードディレクトリ管理情報を取得する
  var codeDirectory = getCodeDirectoryInPackageRtcDir(id);

  if (codeDirectory) {
    // ツリーを生成する
    var rootNode = createCodeSideBar(id, codeDirectory, '');
    rootNode['group'] = true;

    // ツリーからサイドバーを生成する
    var sideBarData = createSidebarData(rootNode, id);

    // 再設定
    w2ui['layout-panel-source-editor'].content('left', $().w2sidebar(sideBarData));
  }
}

/**
 * ソースエディタを生成する
 * 
 * @returns {undefined}
 */
function createSourceEditor() {
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
      mode: "cpp",
      readOnly: true,
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

/**
 * ログビューアを生成する
 * 
 * @returns {undefined}
 */
function createLogViewer() {
  require.config({
    baseUrl: '/mimosa/',
  });

  require(["vs/editor/editor.main"], function () {
    var content = [].join('\n');

    // wasanbon用ログビューアを生成する
    var editorW = document.querySelector('#wasanbon-log');
    wasanbonLogViewer = Monaco.Editor.create(editorW, {
      value: content,
      mode: "ini",
      readOnly: true,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      autoSize: true,
      scrollbar: {
        handleMouseWheel: true
      },
      renderWhitespace: true,
      fontSize: 12,
      lineNumbers: false
    });

    // python用ログビューアを生成する
    var editorP = document.querySelector('#python-log');
    pythonLogViewer = Monaco.Editor.create(editorP, {
      value: content,
      mode: "python",
      readOnly: true,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      autoSize: true,
      scrollbar: {
        handleMouseWheel: true
      },
      renderWhitespace: true,
      fontSize: 12,
      lineNumbers: false
    });
  });
}

/**
 * リモートログダイアログのタブを生成する
 * 
 * @param {*} hostId hostId ※空文字はairgraphからWasanbon実行時のログであることを示す
 * @param {*} fileName fileName
 * @param {*} fileContent fileContent
 * @returns {undefined}
 */
function addRemoteLogTab(hostId, fileName, fileContent) {
  let LOCAL_WASANBON_LOG_TYPE = '';
  let hostName;
  var newDiv = document.createElement('div');
  if (hostId == LOCAL_WASANBON_LOG_TYPE){
    newDiv.setAttribute('id', fileName);
  }
  else {
    // hostIdからホスト名を求める
    hostName = hostMap.get(hostId)['hostName'];
    newDiv.setAttribute('id', hostName + '-' + fileName);
    newDiv.style.display = 'none';
  }
  newDiv.style.height = '100%';
  newDiv.style.width = '100%';

  // ログ表示部分を生成
  newLogViewer = Monaco.Editor.create(newDiv, {
    value: fileContent,
    mode: "ini",
    readOnly: true,
    scrollBeyondLastLine: false,
    automaticLayout: true,
    autoSize: true,
    scrollbar: {
      handleMouseWheel: true
    },
    renderWhitespace: true,
    fontSize: 12,
    lineNumbers: false
  });

  let remoteLogMainDiv = document.getElementById('log-panel');
  remoteLogMainDiv.appendChild(newDiv);
  
  // tabを生成する
  let targetTab = document.getElementById('tabs_layout-panel-console-viewer_top_tabs_right');
  let newTab = document.createElement('div');
  newTab.setAttribute('class', 'w2ui-tab');
  if (hostId == LOCAL_WASANBON_LOG_TYPE) {
    newTab.textContent = fileName;
    newTab.style.backgroundColor = "rgb(220, 220, 220)";
  }
  else {
    newTab.textContent = hostName + '-' + fileName;
  }
  
  let tabs = targetTab.parentNode;
  // tabがクリックされたら、クリックされたタブをアクティブにする＋タブに紐付いたDivが表示される
   var f = function(obj) {
    /* すべてのタブを白色にする */
    for (let i = 1; i < tabs.children.length - 1; i++) {
       tabs.children[i].style.backgroundColor = "#fafafa"
    }
    /*すべてのDivを非表示にする*/
    for (let i = 0; i < remoteLogMainDiv.children.length; i++) {
       remoteLogMainDiv.children[i].style.cssText = 'display:none';
     }
     /* クリックされたタブを色を濃くする */
     obj.target.style.backgroundColor = "rgb(220, 220, 220)";
     /* このタブに紐づくDivを表示する */
     document.getElementById(obj.target.textContent).style.cssText = 'display:block';
     document.getElementById(obj.target.textContent).style.height = '100%';
     document.getElementById(obj.target.textContent).style.width = '100%';

   }
  newTab.addEventListener('click', f);
  targetTab.before(newTab);
}

/**
 * ファル名からソースエディタのモードを変更する
 * 
 * @param {*} value value
 * @param {*} fileName fileName
 * @returns {undefined}
 */
function changeEditorMode(value, fileName) {
  // 拡張子を取得
  var reg = /(.*)(?:\.([^.]+$))/;
  if (fileName && fileName.match(reg) && fileName.match(reg).length > 2) {
    var extention = fileName.match(reg)[2].toLowerCase();

    if (extention === 'c' || extention === 'cpp' || extention === 'h' || extention === 'hpp' || extention === 'idl') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'cpp'));
      sourceEditor.updateOptions({ insertSpaces: true });
    } else if (extention === 'java') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'java'));
      sourceEditor.updateOptions({ insertSpaces: true });
    } else if (extention === 'py') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'python'));
      sourceEditor.updateOptions({ insertSpaces: false });
    } else if (extention === 'conf') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'ini'));
      sourceEditor.updateOptions({ insertSpaces: true });
    } else if (extention === 'xml') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'xml'));
      sourceEditor.updateOptions({ insertSpaces: true });
    } else if (extention === 'yml') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'ruby'));
      sourceEditor.updateOptions({ insertSpaces: true });
    } else if (extention === 'txt' || extention === 'in' || extention === 'cmake') {
      sourceEditor.setModel(Monaco.Editor.createModel(value, 'powershell'));
      sourceEditor.updateOptions({ insertSpaces: true });
    } else {
      sourceEditor.setModel(Monaco.Editor.createModel(value, ''));
      sourceEditor.updateOptions({ insertSpaces: true });
    }
  } else {
    sourceEditor.setModel(Monaco.Editor.createModel(value, ''));
    sourceEditor.updateOptions({ insertSpaces: true });
  }
}

/**
 * クラスファイルを追加する
 * 
 * @param {*} id id
 * @param {*} className className
 * @returns {undefined}
 */
function addClassFile(id, className) {

  // IDからコンポーネントを探す
  var component = getComponentInPackage(id);
  var index = getComponentIndexInPackage(id);

  // 言語
  var language = component.rtcProfile.language.kind;

  if (language === 'C++') {
    var upperClassName = className.toUpperCase();
    // C++
    var sourceFileName = className + '.cpp';
    var sourceFilePath = component.codeDirectory.directoryMap['src'].dirPath + '/' + sourceFileName;
    var headerFileName = className + '.h';
    var headerFilePath = component.codeDirectory.directoryMap['include'].directoryMap[component.rtcProfile.basicInfo.moduleName].dirPath + '/' + headerFileName;

    var sourceStr = [
      '// -*- C++ -*-',
      '/*!',
      ' * @file  ' + sourceFileName,
      ' * @date  $Date$',
      ' *',
      ' * $Id$',
      ' */',
      '',
      '#include "' + headerFileName + '"',
      '',
      className + '::' + className + '() {',
      '  // TODO Auto-generated constructor stub',
      '',
      '}',
      '',
      className + '::~' + className + '() {',
      '  // TODO Auto-generated destructor stub',
      '}'
    ].join('\n');

    var headerStr = [
      '// -*- C++ -*-',
      '/*!',
      ' * @file  ' + headerFileName,
      ' * @date  $Date$',
      ' *',
      ' * $Id$',
      ' */',
      '',
      '#ifndef ' + upperClassName + '_H_',
      '#define ' + upperClassName + '_H_',
      '',
      'class ' + className + ' {',
      'public:',
      '  ' + className + '();',
      '  virtual ~' + className + '();',
      '};',
      '',
      '#endif /* ' + upperClassName + '_H_ */'
    ].join('\n');

    // ファイルパスを設定
    mainRtsMap[curWorkspaceName].rtcs[index].codeDirectory.directoryMap['src'].codePathMap[sourceFileName] = sourceFilePath;
    mainRtsMap[curWorkspaceName].rtcs[index].codeDirectory.directoryMap['include'].directoryMap[component.rtcProfile.basicInfo.moduleName].codePathMap[headerFileName] = headerFilePath;

    // 編集済としてファイルを設定
    mainRtsMap[curWorkspaceName].editSourceCode[sourceFilePath] = sourceStr;
    mainRtsMap[curWorkspaceName].editSourceCode[headerFilePath] = headerStr;

  } else if (language === 'Python') {
    // Python
    var fileName = className + '.py';
    var filePath = component.codeDirectory.dirPath + '/' + fileName;

    var fileStr = [
      '#!/usr/bin/env python',
      '# -*- coding: utf-8 -*-',
      '# -*- Python -*-',
      '',
      'class ' + className + '(object):',
      '\t\'\'\'',
      '\tclassdocs',
      '\t\'\'\'',
      '',
      '',
      '\tdef __init__(self, params):',
      '\t\t\'\'\'',
      '\t\tConstructor',
      '\t\t\'\'\'',
    ].join('\n');

    // ファイルパスを設定
    mainRtsMap[curWorkspaceName].rtcs[index].codeDirectory.codePathMap[fileName] = filePath;

    // 編集済としてファイルを設定
    mainRtsMap[curWorkspaceName].editSourceCode[filePath] = fileStr;
  }
}

/**
 * ファイルを削除する
 * 
 * @param {*} filePath filePath
 * @returns {undefined}
 */
function deleteFile(filePath) {
  // 削除リストに追加
  mainRtsMap[curWorkspaceName].deleteFileList.push(filePath);
}

/*******************************************************************************
 * Git設定関連
 ******************************************************************************/

/**
 * Package用のGit連携画面を表示する
 * 
 * @returns {undefined}
 */
function openPackageGitCommitPushPopup() {
  w2popup.open({
    title: 'Git Repository Link',
    width: 500,
    height: 400,
    showMax: true,
    body: '<div id="rts-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #rts-profile-div').w2form(createPackageGitCommitPushForm(mainRtsMap[curWorkspaceName].modelProfile.remoteUrl));
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[2]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['git-form'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['git-form'].resize();
      }
    }
  });
}

/**
 * Package用Git連携用Form作成
 * 
 * @param {*} remoteUrl remoteUrl
 * @returns {Map} form
 */
function createPackageGitCommitPushForm(remoteUrl) {
  if (w2ui['git-form']) {
    w2ui['git-form'].destroy();
  }

  var form = {
    name: 'git-form',
    focus: -1,
    padding: 0,
    fields: [
      { field: 'git_repository', type: 'text', required: true, html: { label: 'Remote Repository', page: 0, attr: 'style="width:100%;", disabled=disabled' } },
      { field: 'git_user', type: 'text', required: false, html: { label: 'User Name', page: 0, attr: 'style="width:100%"' } },
      { field: 'git_password', type: 'password', required: false, html: { label: 'Password', page: 0, attr: 'style="width:100%"' } },
      { field: 'git_message', type: 'textarea', required: true, html: { label: 'Commit Message', page: 0, attr: 'style="width:100%"' } },
      { field: 'git_result', type: 'textarea', required: false, html: { label: 'Result', page: 0, attr: 'style="height:100%;width:100%", disabled=disabled' } },
    ],
    record: {
      git_repository: remoteUrl
    },
    actions: {
      'Commit': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は連携できません');
        } else if (this.validate().length === 0) {
          // packageをCommitする
          var commitMessage = $('#git_message').val();
          var result = commitPackage(commitMessage);
          $('#git_result').val(convertEscapedNewLine(result));
        }
      },
      'Commit & Push': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は連携できません');
        } else if (this.validate().length === 0) {
          // packageをPushする
          var user = $('#git_user').val();
          var pass = $('#git_password').val();
          var commitMessage = $('#git_message').val();
          var result = pushPackage(user, pass, commitMessage);
          $('#git_result').val(convertEscapedNewLine(result));
        }
      },
      'Pull': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は連携できません');
        } else {
          // packageをPushする
          var user = $('#git_user').val();
          var pass = $('#git_password').val();
          var result = pullPackage(user, pass);
          $('#git_result').val(convertEscapedNewLine(result));
        }
      }
    }
  }
  return form;
}

/**
 * Rtc用のGit連携画面を表示する
 * 
 * @param {*} componentId componentId
 * @returns {undefined}
 */
function openRtcGitCommitPushPopup(componentId) {

  var component = getComponentInPackage(componentId);

  w2popup.open({
    title: 'Git Repository Link',
    width: 500,
    height: 400,
    showMax: true,
    body: '<div id="rts-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#w2ui-popup #rts-profile-div').w2form(createRtcGitCommitPushForm(componentId, component.modelProfile.remoteUrl));
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[2]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['git-form'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['git-form'].resize();
      }
    }
  });
}

/**
 * Rtc用Git連携用Form作成
 * 
 * @param {*} componentId componentId
 * @param {*} remoteUrl remoteUrl
 * @returns {Map} form
 */
function createRtcGitCommitPushForm(componentId, remoteUrl) {
  //destroySettingForm();

  var form = {
    name: 'git-form',
    focus: -1,
    padding: 0,
    fields: [
      { field: 'git_repository', type: 'text', required: true, html: { label: 'Remote Repository', page: 0, attr: 'style="width:100%;", disabled=disabled' } },
      { field: 'git_user', type: 'text', required: false, html: { label: 'User Name', page: 0, attr: 'style="width:100%"' } },
      { field: 'git_password', type: 'password', required: false, html: { label: 'Password', page: 0, attr: 'style="width:100%"' } },
      { field: 'git_message', type: 'textarea', required: true, html: { label: 'Commit Message', page: 0, attr: 'style="width:100%"' } },
      { field: 'git_result', type: 'textarea', required: false, html: { label: 'Result', page: 0, attr: 'style="height:100%;width:100%", disabled=disabled' } },
    ],
    record: {
      git_repository: remoteUrl
    },
    actions: {
      'Commit': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は連携できません');
        } else if (this.validate().length === 0) {
          // RTCをCommitする
          var commitMessage = $('#git_message').val();
          var result = commitComponent(componentId, commitMessage);
          $('#git_result').val(convertEscapedNewLine(result));
        }
      },
      'Commit & Push': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は連携できません');
        } else if (this.validate().length === 0) {
          // RTCをCommitする
          var user = $('#git_user').val();
          var pass = $('#git_password').val();
          var commitMessage = $('#git_message').val();
          var result = pushComponent(componentId, user, pass, commitMessage);
          $('#git_result').val(convertEscapedNewLine(result));
        }
      },
      'Pull': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は連携できません');
        } else {
          // RTCをPullする
          var user = $('#git_user').val();
          var pass = $('#git_password').val();
          var result = pullComponent(componentId, user, pass);
          $('#git_result').val(convertEscapedNewLine(result));
        }
      }
    }
  }
  return form;
}

/*******************************************************************************
 * Binder設定関連
 ******************************************************************************/
/**
 * Binder作成画面を表示する 
 * 
 * @returns {undefined}
 */
function openCreateBinderPopup() {
  
  // github Userを取得する
  let githubConfig = getGitHubConfigFile();
  let githubUserName = githubConfig.username;
  let githubToken = githubConfig.token; 
  // ポップアップを表示する
  w2popup.open({
    title: 'Create Binder',
    width: 350,
    height: 200,
    body:
      '<div style="height:70%;text-align:center; padding:20px 10px;">fork Binder repository to Github(user: ' + githubUserName + ')</div>' +
      '<div class="w2ui-buttons create-binder" style="height:30%;text-align:center;">' +
      '<input type="button" id="create-binder-OK" value="OK" name="OK" style="padding:5px 10px;"/>' +
      '<input type="button" value="Cancel" name="Cancel" style="padding:5px 10px;"onclick="w2popup.close();"/>' +
      '</div>',
    onOpen: function (event) {
      event.onComplete = function () {
        // 釦を変更する
        $($('.w2ui-buttons.create-binder').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('width', '80px').css('margin', '10px 5px').css('font-size', '1.2em');
        $($('.w2ui-buttons.create-binder').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('width', '80px').css('margin', '10px 5px').css('font-size', '1.2em');
      }
    },
  });
  
  // OKボタンの押下イベントを設定
  $('#create-binder-OK').click(function () {
        createBinder(githubUserName, githubToken);
      w2popup.close();
  })
  
}

/**
 * Binder追加・更新ダイアログを表示する
 * 
 * @param {*} isPackage isPackage
 * @param {*} isAdd isAdd
 * @param {*} Id Id
 * @param {*} title title
 * @returns {undefined}
 */
function openAddorUpdatetoBinderPopup(isPackage, isAdd, Id, title) {
  // ポップアップを表示する
  w2popup.open({
    title: title,
    width: 500,
    height: 340,
    showMax: true,
    body: '<div id="add-update-to-binder-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function () {
        $('#add-update-to-binder-div').w2form(createAddorUpdatetoBinderForm(isPackage, isAdd, Id));
        // 釦を変更する
        $($('#add-update-to-binder-div .w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
        $($('#add-update-to-binder-div .w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
      }
    },
    onMax: function (event) {
      event.onComplete = function () {
        w2ui['add-update-to-binder'].resize();
      }
    },
    onMin: function (event) {
      event.onComplete = function () {
        w2ui['add-update-to-binder'].resize();
      }
    }
  });
}

/**
 * Binder追加・更新ダイアログのフォームを生成する
 * @param {*} isPackage isPackage
 * @param {*} isAdd isAdd
 * @param {*} Id Id
 * @returns {Map} form
 */
function createAddorUpdatetoBinderForm(isPackage, isAdd, Id) {
  if (w2ui['add-update-to-binder']) {
    w2ui['add-update-to-binder'].destroy();
  }
  var form = {
    name: "add-update-to-binder",
    focus: -1,
    padding: 0,
    record: {
    },
    actions: {
      'Commit': function () {
       if (this.validate().length === 0) {
             let packageName = curWorkspaceName.replace('rts_', '');
        // binderName を取得
        let binderName = getGitHubConfigFile();
        binderName = binderName.username + '_owner';

        // パッケージの追加・更新なら
        if (isPackage) {
          // 追加なら
          if (isAdd) {
              //Binderにパッケージを追加
              addPackageToBinder(packageName, binderName);
          }
          // 更新なら
          else {
            //Binderのパッケージを更新
              updatePackageToBinder('dev', packageName, binderName);
          }
        }
        // RTCの追加・更新なら
        else {
          //IDからRTC名を取得
          let rtcName = Id.split(':')[3];
          // 追加なら
          if (isAdd) {
              //BinderにRTCを追加
              addRtcToBinder('dev', packageName, rtcName, binderName);
          }
          // 更新なら
          else {
            //BinderのRTCを更新
              updateRtcToBinder('dev', packageName, rtcName, binderName);
          }
        }
        // comment を取得
        let comment = $('#commit-message').val();
        //BinderをCommit
         let result = commitBinder(binderName, comment);

          $('#result').val(convertEscapedNewLine(result));

        }
      },
      'Commit & Push': function () {
     if (this.validate().length === 0) {
             let packageName = curWorkspaceName.replace('rts_', '');
          // binderName を取得
        let binderName = getGitHubConfigFile();
        binderName = binderName.username + '_owner';

        // パッケージの追加・更新なら
        if (isPackage) {
         // 追加なら
          if (isAdd) {
              //Binderにパッケージを追加
              addPackageToBinder(packageName, binderName);
          }
          // 更新なら
          else {
            //Binderのパッケージを更新
              updatePackageToBinder('dev', packageName, binderName);
          }
        }
         // RTCの追加・更新なら
        else {
          //IDからRTC名を取得
          let rtcName = Id.split(':')[3];
          // 追加なら
          if (isAdd) {
              //BinderにRTCを追加
              addRtcToBinder('dev', packageName, rtcName, binderName);
          }
          // 更新なら
          else {
            //BinderのRTCを更新
              updateRtcToBinder('dev', packageName, rtcName, binderName);
          }
        }
        
        // comment を取得
        let comment = $('#commit-message').val();
        //BinderをCommit
        let result = commitBinder(binderName, comment);
        $('#result').val(convertEscapedNewLine(result));
        
        //BinderをPush
        result = pushBinder(binderName, comment);
          $('#result').val(convertEscapedNewLine(result));
       }
      },
    }
  };

  form['fields'] = [
    { field: 'commit-message', type: 'textarea', required: true, html: { label: 'Commit Message', page: 0, attr: 'style="height:100%;width:100%"' } },
    { field: 'result', type: 'textarea', html: { label: 'Result', page: 0, attr: 'style="height:100%;width:100%", disabled=disabled' } },

  ];
  return form
}

/*******************************************************************************
 * デプロイ設定関連
 ******************************************************************************/
/**
 * デプロイエラーダイアログを表示する
 * 
 * @param msg 追加メッセージ
 * @returns {undefined}
 */
function openCreateDeployErrPopup(msg) {
  // ポップアップを表示する
  w2popup.open({
    title: 'Deploy',
    showClose: true,
    showMax: true,
    body: 'deploy ERROR<br><br>' + msg,
    actions: {
      Ok(event) {
        w2popup.close();
      },
      Retry(event) {
        w2popup.close();
        deployAllRtcs();
      }
    }
  });
}

/**
 * デプロイ向けダイアログを表示する.
 * 
 * @param msg bodyメッセージ
 */
function openCreateDeployPopup(msg) {
  // ポップアップを表示する
  w2popup.open({
    title: 'Deploy',
    body: msg,
    showClose: true,
    actions: {
      Ok(event) {
        w2popup.close();
      }
    }
  });
}