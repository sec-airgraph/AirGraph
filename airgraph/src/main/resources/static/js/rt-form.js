/*************************************************************************
 * RtsProfile設定関連
 *************************************************************************/
/**
 * PackageProfile設定画面を表示する
 * 
 * @returns
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
 * @returns
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
    onClose: function (event) {
      $('#property-panel').w2form(createRtsProfileSettingForm(mainRtsMap[curWorkspaceName].rtsProfile, mainRtsMap[curWorkspaceName].modelProfile, true, true));
    }
  });
}

/**
 * RtsProfile設定用Formを生成する
 * 
 * @param rtsProfile
 * @param updateFlg
 * @returns
 */
function createRtsProfileSettingForm(rtsProfile, modelProfile, propertyAreaFlg, updateFlg) {
  destroySettingForm();

  var form = {
    name: 'rts-profile-setting',
    padding: 0,
    tabs: [
      { id: 'package-tab1', caption: 'RTS Profile' }
    ],
    record: {
      package_name: rtsProfile.id.split(':').length > 2 ? rtsProfile.id.split(':')[2] : '',
      package_ads: rtsProfile.sabstract,
      package_ver: rtsProfile.version,
      package_vender: rtsProfile.id.split(':').length > 1 ? rtsProfile.id.split(':')[1] : '',
      package_orgRemoteUrl: modelProfile.orgRemoteUrl,
      package_remoteUrl: modelProfile.remoteUrl
    }
  };

  if (updateFlg === true) {
    form['fields'] = [
      { name: 'package_name', type: 'text', required: true, html: { caption: 'Package Name', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_ver', type: 'text', required: true, html: { caption: 'Version', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_vender', type: 'text', required: true, html: { caption: 'Vender Name', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_ads', type: 'textarea', required: true, html: { caption: 'Abstract', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_remoteUrl', type: 'text', required: true, html: { caption: 'Remote Repository', page: 0, attr: 'style="width:300px"' } },
    ];
    form['actions'] = {
      'Update': function () {
        if (curState !== STATE.EDIT) {
          w2alert('初期化中・実行中は編集できません');
        } else if (this.validate().length === 0) {
          mainRtsMap[curWorkspaceName].rtsProfile.id = 'RTSystem:' + $('#package_vender').val() + ':' + $('#package_name').val() + ':' + $('#package_ver').val();
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
      { name: 'package_name', type: 'text', required: true, html: { caption: 'Package Name', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_ver', type: 'text', required: true, html: { caption: 'Version', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_vender', type: 'text', required: true, html: { caption: 'Vender Name', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_ads', type: 'textarea', required: true, html: { caption: 'Abstract', page: 0, attr: 'style="width:300px"' } },
      { name: 'package_orgRemoteUrl', type: 'text', required: false, html: { caption: 'Original Remote Repository', page: 0, attr: 'style="width:300px" readonly=readonly' } },
      { name: 'package_remoteUrl', type: 'text', required: true, html: { caption: 'Remote Repository', page: 0, attr: 'style="width:300px"' } },
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
          curWorkspaceName = getNextWorkspaceName(WORKSPACE_PREFIX + workspaceCounter);
          workspaceCounter++;

          // Packageを追加して読み込み直す
          var id = 'RTSystem:' + $('#package_vender').val() + ':' + $('#package_name').val() + ':' + $('#package_ver').val();
          addPackage(modelProfile.modelId, id, $('#package_ads').val(), $('#package_ver').val(), $('#package_remoteUrl').val());
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
 * @param modelId
 * @returns
 */
function openNewRtcProfileSettingPopup(modelId) {
  var rtc = jQuery.extend({}, componentMap[modelId]);
  openRtcSettingPopup(createRtcProfileSettingForm(rtc, 0, false, false), 'Component Setting', 500, 720);
}

/**
 * RtcProfile設定画面を編集状態で起動する
 * 
 * @param componentId
 * @returns
 */
function openEditRtcProfileSettingPopup(componentId) {
  var rtc = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);
  openRtcSettingPopup(createRtcProfileSettingForm(rtc, componentIndex, false, true), 'Component Setting', 500, 720);
}

/**
 * RTC用の設定画面を表示する
 * 
 * @param form
 * @param title
 * @param width
 * @param height
 * @returns
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
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '120px').css('font-size', '1.2em');
        $($('.w2ui-buttons').children()[1]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('width', '140px').css('font-size', '1.2em');
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
 * @param rtcProfile
 * @param rtcIndex
 * @param propertyAreaFlg
 * @param updateFlg
 * @returns
 */
function createRtcProfileSettingForm(rtc, rtcIndex, propertyAreaFlg, updateFlg) {
  destroySettingForm();

  // チェックボックス用
  var componentKindFields = createComponetKindFields();

  var form = {
    name: 'rtc-profile-setting',
    padding: 0,
    tabs: [],
    fields: [],
    record: {}
  };

  // BasicInfo
  form['tabs'].push({ id: 'component-tab1', caption: 'RTC Profile' });
  form['tabs'].push({ id: 'component-tab2', caption: 'RTC Activities' });
  Array.prototype.push.apply(form['fields'], createRtcProfileBasicInfoSettingForm(componentKindFields));
  Array.prototype.push.apply(form['fields'], createRtcProfileActivitySettingForm());
  Object.assign(form['record'], setRtcProfileBasicInfoToSettingForm(rtc.rtcProfile.basicInfo, rtc.rtcProfile.actions, rtc.rtcProfile.neuralNetworkInfo, componentKindFields, rtc.modelProfile));

  // Configuration
  // form['tabs'].push({ id: 'component-tab2', caption: 'Configuraiton' });

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
                // updatePackage(false);
                updateDnnModels(updated.rtcProfile.neuralNetworkInfo.modelName, true);
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
 * @param componentKindFields
 * @returns
 */
function createRtcProfileBasicInfoSettingForm(componentKindFields) {
  var basicForm = [
    { name: 'component_name', type: 'text', required: true, html: { caption: 'Module Name', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_desc', type: 'text', required: false, html: { caption: 'Module Description', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_ver', type: 'text', required: true, html: { caption: 'Version', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_vender', type: 'text', required: true, html: { caption: 'Vender Name', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_cat', type: 'text', required: true, html: { caption: 'Module Category', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_type', type: 'list', required: true, html: { caption: 'Component Type', page: 0, attr: 'style="width:300px"' }, options: { items: getComponentTypeChoices() } },
    { name: 'component_act', type: 'list', required: true, html: { caption: 'Activity Type', page: 0, attr: 'style="width:300px"' }, options: { items: getActivityTypeChoices() } },
    componentKindFields[0],
    componentKindFields[1],
    componentKindFields[2],
    { name: 'component_ins', type: 'int', required: false, html: { caption: 'Max Instance Size', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_execType', type: 'list', required: true, html: { caption: 'Execution Type', page: 0, attr: 'style="width:300px"' }, options: { items: getExecutionTypeChoices() } },
    { name: 'component_execRate', type: 'float', required: false, html: { caption: 'Execution Rate', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_abs', type: 'textarea', required: false, html: { caption: 'Abstract', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_rtc', type: 'text', required: false, html: { caption: 'RTC Type', page: 0, attr: 'style="width:300px"' } },
    { name: 'dnn_model_name', type: 'list', required: false, html: { caption: 'DNN Model Name', page: 0, attr: 'style="width:300px"' }, options: { items: getKerasModelChoices() } },
    { name: 'dataset_name', type: 'text', required: false, html: { caption: 'Dataset Name', page: 0, attr: 'style="width:300px"' } },
    { name: 'component_remoteUrl', type: 'text', required: true, html: { caption: 'Remote Repository', page: 0, attr: 'style="width:300px"' } },
  ];
  return basicForm;
}

/**
 * RtcProfile-BasicInfo-ComponentKind設定用<br>
 * チェックボックスのフィールドを生成する
 * 
 * @returns
 */
function createComponetKindFields() {
  var result = [];
  var header = true;
  var cnt = 1;
  var choices = getComponentKindChoices()
  for (key in choices) {
    var record = { name: 'component_comType' + cnt, type: 'checkbox', required: false, html: { caption: ' ', text: choices[key], page: 0 } };
    if (header && header === true) {
      record['html']['caption'] = 'Component Kind';
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
 * @returns
 */
function createRtcProfileActivitySettingForm() {
  var activityForm = [
    { name: 'act_oninitialize', type: 'checkbox', required: true, html: { caption: 'onInitialize', page: 1 } },
    { name: 'act_onfinalize', type: 'checkbox', required: true, html: { caption: 'onFinalize', page: 1 } },
    { name: 'act_onstartup', type: 'checkbox', required: true, html: { caption: 'onStartup', page: 1 } },
    { name: 'act_onshutdown', type: 'checkbox', required: true, html: { caption: 'onShutdown', page: 1 } },
    { name: 'act_onactivated', type: 'checkbox', required: true, html: { caption: 'onActivated', page: 1 } },
    { name: 'act_ondeactivated', type: 'checkbox', required: true, html: { caption: 'onDeactivated', page: 1 } },
    { name: 'act_onaborting', type: 'checkbox', required: true, html: { caption: 'onAborting', page: 1 } },
    { name: 'act_onerror', type: 'checkbox', required: true, html: { caption: 'onError', page: 1 } },
    { name: 'act_onreset', type: 'checkbox', required: true, html: { caption: 'onReset', page: 1 } },
    { name: 'act_onexecute', type: 'checkbox', required: true, html: { caption: 'onExecute', page: 1 } },
    { name: 'act_onstateupdate', type: 'checkbox', required: true, html: { caption: 'onStateUpdate', page: 1 } },
    { name: 'act_onratechanged', type: 'checkbox', required: true, html: { caption: 'onRateChanged', page: 1 } },
  ];
  return activityForm;
}

/**
 * RtcProfileの内容を設定用Formに展開する
 * 
 * @param basicInfo
 * @param neuralNetworkInfo
 * @param componentKindFields
 * @returns
 */
function setRtcProfileBasicInfoToSettingForm(basicInfo, actions, neuralNetworkInfo, componentKindFields, modelProfile) {
  var record = {
    // Basic Info
    component_name: basicInfo.moduleName,
    component_desc: basicInfo.moduleDescription,
    component_ver: basicInfo.version,
    component_vender: basicInfo.vendor,
    component_cat: basicInfo.moduleCategory,
    component_type: basicInfo.componentType,
    component_act: basicInfo.activityType,
    component_comType1: basicInfo.componentKind.indexOf(componentKindFields[0]['html']['text']) >= 0,
    component_comType2: basicInfo.componentKind.indexOf(componentKindFields[1]['html']['text']) >= 0,
    component_comType3: basicInfo.componentKind.indexOf(componentKindFields[2]['html']['text']) >= 0,
    component_ins: basicInfo.maxInstances,
    component_execType: basicInfo.executionType,
    component_execRate: basicInfo.executionRate,
    component_abs: basicInfo.sabstract,
    component_rtc: basicInfo.rtcType,
    // DNN Info
    dnn_model_name: neuralNetworkInfo.modelName,
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
 * @param rtcProfile
 * @returns
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
    componetKind += componentKindFields[0]['html']['text'];
  }
  if ($('#component_comType2').prop('checked') === true) {
    componetKind += componentKindFields[1]['html']['text'];
  }
  if ($('#component_comType3').prop('checked') === true) {
    componetKind += componentKindFields[2]['html']['text'];
  }
  if (componetKind) {
    componetKind += 'Component';
  }
  rtc.rtcProfile.basicInfo.componentKind = componetKind;
  rtc.rtcProfile.basicInfo.maxInstances = $('#component_ins').val();
  rtc.rtcProfile.basicInfo.executionType = $('#component_execType').val();
  rtc.rtcProfile.basicInfo.executionRate = $('#component_execRate').val();
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
 * @param componentId
 * @returns
 */
function openConfigurationPopup(componentId) {
  var rtcProfile = getComponentInPackage(componentId).rtcProfile;
  openRtcSettingPopupForConfguration(rtcProfile);
}

/**
 * コンフィギュレーション設定画面を表示する
 * 
 * @param rtcProfile
 * @returns
 */
function openRtcSettingPopupForConfguration(rtcProfile) {
  // RTCのコンフィギュレーション設定ポップアップを表示する
  w2popup.open({
    title: 'Configuration Parameter Setting',
    width: 650,
    height: 370,
    body: '<div id="rtc-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function (event) {
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
 * @returns
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
        tabs: [{ id: 'configuration-grid-tab1', caption: 'Configuration List' }
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
 * @param rtcProfile
 * @returns
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
      { field: 'name', caption: 'name', size: '100%', editable: { type: 'text' } }
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
  $(addBtn).on('click', function (event) {
    addRtcConfiguration();
  });

  // Gridの削除釦
  var delBtn = $('<button type="button">');
  delBtn.html('Delete')
  delBtn.attr('id', 'configuration-grid-delete-btn');
  delBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '3px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px').css('font-size', '1em');
  $('<span>').addClass('ui-icon').addClass('ui-icon-trash').appendTo(delBtn);
  $(delBtn).on('click', function (event) {
    deleteRtcConfiguration(w2ui['rtc-profile-configuration-grid']['curSelectRow']);
  });

  $('#rtc-profile-configuration-add-div').append(addBtn).append(delBtn);

  // コンフィギュレーション部分
  var configurationForm = {
    name: 'rtc-profile-configuration-setting',
    padding: 0,
    fields: [
      { name: 'conf_name', type: 'text', required: true, html: { caption: 'Name', page: 0, attr: 'style="width:250px"' } },
      { name: 'conf_type', type: 'list', required: true, html: { caption: 'Type', page: 0, attr: 'style="width:250px"' }, options: { items: getConfigurationTypeChoices() } },
      { name: 'conf_defaultvalue', type: 'text', required: true, html: { caption: 'Default Value', page: 0, attr: 'style="width:250px"' } },
      { name: 'conf_valname', type: 'text', required: false, html: { caption: 'Variable Name', page: 0, attr: 'style="width:250px"' } },
      { name: 'conf_unit', type: 'text', required: false, html: { caption: 'Unit', page: 0, attr: 'style="width:250px"' } },
      { name: 'conf_constraint', type: 'text', required: false, html: { caption: 'Constraint', page: 0, attr: 'style="width:250px"' } },
      { name: 'conf_widget', type: 'list', required: true, html: { caption: 'Widget', page: 0, attr: 'style="width:250px"' }, options: { items: getConfigurationWidgetChoices() } },
      { name: 'conf_step', type: 'text', required: false, html: { caption: 'Step', page: 0, attr: 'style="width:250px"' } },
    ],
    record: {}
  };

  var saveForm = {
    name: 'rtc-profile-configuration-save',
    padding: 0,
    tabs: [],
    fields: [{ name: 'dummy', type: 'text', required: false, html: { attr: 'style="display:none"' } }],
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
 * @param rtcProfile
 * @returns
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
 * @returns
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
 * @returns
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
 * @param index
 * @returns
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
      'conf_type': confData.dataType,
      'conf_defaultvalue': confData.defaultValue,
      'conf_valname': confData.variableName,
      'conf_unit': confData.unit,
      'conf_constraint': setConfigurationUnit(confData),
      'conf_widget': 'text',
      'conf_step': ''
    }
    $('#conf_name').val(confData.name);
    $('#conf_type').val(confData.dataType);
    $('#conf_defaultvalue').val(confData.defaultValue);
    $('#conf_valname').val(confData.variableName);
    $('#conf_unit').val(confData.unit);
    $('#conf_constraint').val(setConfigurationUnit(confData));
    $('#conf_widget').val('text');
    $('#conf_step').val('');
  }
}

/**
 * コンフィギュレーションのUnitを取得する
 * @param confData
 * @returns
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
 * @param index
 * @returns
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
 * @returns
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
 * @param index
 * @returns
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
 * @param list1
 * @param list2
 * @returns
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
 * データポート設定関連
 ******************************************************************************/
/**
 * データポート設定画面を新規作成状態で起動する
 * 
 * @param componentId
 * @param isIn
 * @returns
 */
function openNewDataPortSettingPopup(componentId, isIn) {
  var portData = getDefaultDataPort(componentId, isIn);
  openRtcSettingPopup(createOneDataPortSettingForm(portData, false), 'Data Port Setting', 400, 250);
}

/**
 * データポート設定画面を編集状態で起動する
 * 
 * @param portData
 * @returns
 */
function openEditDataPortSettingPopup(portData) {
  openRtcSettingPopup(createOneDataPortSettingForm(portData, true), 'Data Port Setting', 400, 250);
}

/**
 * ポート毎の設定画面を表示する
 * 
 * @param portData
 * @param updateFlg
 * @returns
 */
function createOneDataPortSettingForm(portData, updateFlg) {
  destroySettingForm();

  var form = {
    name: 'rtc-profile-setting',
    padding: 0,
    tabs: [
      { id: 'dataport-tab1', caption: 'Data Port' }
    ],
    fields: [
      { name: 'dataport_name', type: 'text', required: true, html: { caption: 'Port Name', page: 0, attr: 'style="width:200px"' } },
      { name: 'dataport_type', type: 'list', required: true, html: { caption: 'Data Type', page: 0, attr: 'style="width:200px"' }, options: { items: getDataTypeChoices(portData.componentName) } },
      { name: 'dataport_val_name', type: 'text', required: false, html: { caption: 'Variable Name', page: 0, attr: 'style="width:200px"' } }
    ],
    record: {
      dataport_name: portData.portName,
      dataport_type: portData.dataType,
      dataport_val_name: portData.valName
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
            var isIn = portData.isIn;

            // 変更前
            var oldPortName = portData.portName;
            var oldDataType = portData.dataType;
            var oldValName = portData.valName;
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
            var rtc = addDataPort(componentId, newPortName, newDataType, newValName, isIn);
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
 * @param componentId
 * @returns
 */
function openNewServicePortSettingPopup(componentId) {
  var portData = getDefaultServicePort(componentId);
  openRtcSettingPopupForServicePort(portData, false);
}

/**
 * サービスポート設定画面を編集状態で起動する
 * 
 * @param componentId
 * @returns
 */
function openEditServicePortSettingPopup(portData) {
  openRtcSettingPopupForServicePort(portData, true);
}

/**
 * サービスポート用の設定画面を表示する
 * 
 * @param portData
 * @param updateFlg
 * @returns
 */
function openRtcSettingPopupForServicePort(portData, updateFlg) {
  // RTCのサービスポート用の設定ポップアップを表示する
  w2popup.open({
    title: 'Service Port/Interface Setting',
    width: 900,
    height: 460,
    body: '<div id="rtc-profile-div" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
    onOpen: function (event) {
      event.onComplete = function (event) {
        // レイアウトを表示する
        $('#w2ui-popup #rtc-profile-div').w2layout(createOneServicePortSettingLayOut());
        // レイアウトにFormとGridを反映する
        createOneServicePortSettingForm(portData, updateFlg);
        // 釦を変更する
        $($('.w2ui-buttons').children()[0]).addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em');
        $('input[name=Upload_IDL_File]').addClass('ui-button ui-widget ui-corner-all').css('height', '28px').css('font-size', '1.2em').css('width', '120px');
      }
    }
  });
}

/**
 * サービスポート毎の設定画面のレイアウト部分を生成する
 * 
 * @returns
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
        tabs: [{ id: 'interfacelist-tab1', caption: 'Interface List' }
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
 * @param portData
 * @param updateFlg
 * @returns
 */
function createOneServicePortSettingForm(portData, updateFlg) {
  // ポート部分を生成
  var portForm = {
    name: 'rtc-profile-setting',
    padding: 0,
    tabs: [
      { id: 'serviceport-tab1', caption: 'Service Port' }
    ],
    fields: [
      { name: 'serviceport_name', type: 'text', required: true, html: { caption: 'Port Name', page: 0, attr: 'style="width:450px"' } },
      { name: 'serviceport_pos', type: 'list', required: true, html: { caption: 'Position', page: 0, attr: 'style="width:450px"' }, options: { items: getPortPositionChoices() } }
    ],
    record: {
      serviceport_name: portData.portName,
      serviceport_pos: portData.position,
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
      { field: 'name', caption: 'name', size: '100%', editable: { type: 'text' } }
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
  $(addBtn).on('click', function (event) {
    addServiceInterface();
  });

  // Gridの削除釦
  var delBtn = $('<button type="button">');
  delBtn.html('Delete')
  delBtn.attr('id', 'if-grid-delete-btn');
  delBtn.addClass('ui-button').addClass('ui-widget').addClass('ui-corner-all').css('padding', '5px').css('margin-top', '3px').css('margin-left', '5px').css('margin-right', '5px').css('width', '70px').css('font-size', '1em');
  $('<span>').addClass('ui-icon').addClass('ui-icon-trash').appendTo(delBtn);
  $(delBtn).on('click', function (event) {
    deleteServiceInterface(w2ui['rtc-profile-if-grid']['curSelectRow']);
  });

  $('#rtc-profile-if-add-div').append(addBtn).append(delBtn);

  // インタフェース部分
  var ifForm = {
    name: 'rtc-profile-if-setting',
    padding: 0,
    tabs: [
      { id: 'serviceinterface-tab1', caption: 'Service Interface' }
    ],
    fields: [
      { name: 'serviceif_name', type: 'text', required: true, html: { caption: 'Interface Name', page: 0, attr: 'style="width:500px"' } },
      { name: 'serviceif_direction', type: 'list', required: true, html: { caption: 'Direciton', page: 0, attr: 'style="width:500px"' }, options: { items: getIfDirectionChoices() } },
      { name: 'serviceif_instancename', type: 'text', required: false, html: { caption: 'Instance Name', page: 0, attr: 'style="width:500px"' } },
      { name: 'serviceif_valname', type: 'text', required: false, html: { caption: 'Variable Name', page: 0, attr: 'style="width:500px"' } },
      { name: 'serviceif_idlfile', type: 'list', required: true, html: { caption: 'IDL File', page: 0, attr: 'style="width:500px"' }, options: { items: getIdlFileChoices(portData.componentName) } },
      { name: 'serviceif_iftype', type: 'list', required: true, html: { caption: 'Interface Type', page: 0, attr: 'style="width:500px"' } },
      { name: 'serviceif_idlpath', type: 'text', required: false, html: { caption:'IDL Path', page:0, attr:'style="width:500px"' } },
    ],
    record: {},
    onChange: function (event) {
      if (event.target === 'serviceif_idlfile') {
        // IDLファイルを変更した場合、インタフェース型の選択肢を作りなおす
        updateInterfaceTypeChoices(portData.componentName, event.value_new);
      }
    }
  };

  var saveForm = { name: 'rtc-profile-if-save', padding: 0, tabs: [], fields: [{ name: 'dummy', type: 'text', required: false, html: { attr: 'style="display:none"' } }], record: {} };
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
            var rtc = addServicePort(componentId, newPortName, newPosition, newInterfaceList);
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
  };

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
 * @param portData
 * @returns
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
 * @returns
 */
function clearServiceInterfaceGridData() {
  w2ui['rtc-profile-if-grid'].clear();
  w2ui['rtc-profile-if-grid'].selectNone();
  $('#if-grid-delete-btn').button('disable');
  clearServiceInterfaceSetting();
}

/**
 * インタフェース設定部分をクリアする
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
 * @param index
 * @returns
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
      'serviceif_direction': ifData.direction,
      'serviceif_instancename': ifData.instanceName,
      'serviceif_valname': ifData.variableName,
      'serviceif_idlfile': ifData.idlFile,
      'serviceif_iftype': ifData.interfaceType,
      'serviceif_idlpath': ifData.path
    }
    $('#serviceif_name').val(ifData.name);
    $('#serviceif_direction').val(ifData.direction);
    $('#serviceif_instancename').val(ifData.instanceName);
    $('#serviceif_valname').val(ifData.variableName);
    $('#serviceif_idlfile').val(ifData.idlFile);
    $('#serviceif_iftype').val(ifData.interfaceType);
    $('#serviceif_idlpath').val(ifData.path);
  }
}

/**
 * 変更したインタフェースの内容を反映する
 * 
 * @param index
 * @returns
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
 * @param componentName
 * @param idlFileName
 * @returns
 */
function updateInterfaceTypeChoices(componentName, idlFileName) {
  // インタフェース型のリストを一度空にする
  delete w2ui["rtc-profile-if-setting"].fields[5]['options'];
  $('select[name=serviceif_iftype]').empty();
  $('select[name=serviceif_iftype]').append('<option value="">- none -</option>');

  if (idlFileName) {
    // IDLファイルが設定されている場合は選択肢を取得する
    var items = getInterfaceTypeChoices(componentName, idlFileName);
    w2ui["rtc-profile-if-setting"].fields[5]['options'] = items;
    if (items && Object.keys(items).length > 0) {
      for (key in items) {
        $('select[name=serviceif_iftype]').append('<option value="' + key + '">' + items[key] + '</option>')
      }
    }
  }
}

/**
 * サービスインタフェースを追加する
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
 * @param index
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
 * @param list1
 * @param list2
 * @returns
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
}

/*******************************************************************************
 * ソースコード設定関連
 ******************************************************************************/

/**
 * ソースコード生成ポップアップを表示する
 * 
 * @param in
 *          stanceName
 * @returns
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
      onClose: function (event) {
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
 * @param rtcId
 * @param codeDirectory
 * @returns
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
 * @param rootNode
 * @param componentId
 * @returns
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
 * @param id
 * @returns
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
 * @returns
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
 * @returns
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
 * ファル名からソースエディタのモードを変更する
 * 
 * @param fileName
 * @returns
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
 * @param id
 * @param className
 * @returns
 */
function addClassFile(id, className) {

  // IDからコンポーネントを探す
  var component = getComponentInPackage(id);
  var index = getComponentIndexInPackage(id);

  // 言語
  var language = component.rtcProfile.language.kind;

  var rootDir = component.codeDirectory.dirPath;

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

function deleteFile(filePath) {
  // 削除リストに追加
  mainRtsMap[curWorkspaceName].deleteFileList.push(filePath);
}

/*******************************************************************************
 * Git設定関連
 ******************************************************************************/

/**
 * Package用のGit連携画面を表示する
 */
function openPackageGitCommitPushPopup() {
  w2popup.open({
    title: 'Git Repository Link',
    width: 500,
    height: 400,
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
 */
function createPackageGitCommitPushForm(remoteUrl) {
  destroySettingForm();

  var form = {
    name: 'git-form',
    padding: 0,
    fields: [
      { name: 'git_repository', type: 'text', required: true, html: { caption: 'Remote Repository', page: 0, attr: 'style="width:300px;", disabled=disabled' } },
      { name: 'git_user', type: 'text', required: false, html: { caption: 'User Name', page: 0, attr: 'style="width:300px"' } },
      { name: 'git_password', type: 'text', required: false, html: { caption: 'Password', page: 0, attr: 'style="width:300px"' } },
      { name: 'git_message', type: 'textarea', required: true, html: { caption: 'Commit Message', page: 0, attr: 'style="width:300px"' } },
      { name: 'git_result', type: 'textarea', required: false, html: { caption: 'Result', page: 0, attr: 'style="height:100px;width:300px", disabled=disabled' } },
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
          $('#git_result').val(result);
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
          $('#git_result').val(result);
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
          $('#git_result').val(result);
        }
      }
    }
  }
  return form;
}

/**
 * Rtc用のGit連携画面を表示する
 */
function openRtcGitCommitPushPopup(componentId) {

  var component = getComponentInPackage(componentId);

  w2popup.open({
    title: 'Git Repository Link',
    width: 500,
    height: 400,
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
 */
function createRtcGitCommitPushForm(componentId, remoteUrl) {
  destroySettingForm();

  var form = {
    name: 'git-form',
    padding: 0,
    fields: [
      { name: 'git_repository', type: 'text', required: true, html: { caption: 'Remote Repository', page: 0, attr: 'style="width:300px;", disabled=disabled' } },
      { name: 'git_user', type: 'text', required: false, html: { caption: 'User Name', page: 0, attr: 'style="width:300px"' } },
      { name: 'git_password', type: 'text', required: false, html: { caption: 'Password', page: 0, attr: 'style="width:300px"' } },
      { name: 'git_message', type: 'textarea', required: true, html: { caption: 'Commit Message', page: 0, attr: 'style="width:300px"' } },
      { name: 'git_result', type: 'textarea', required: false, html: { caption: 'Result', page: 0, attr: 'style="height:100px;width:300px", disabled=disabled' } },
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
          $('#git_result').val(result);
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
          $('#git_result').val(result);
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
          $('#git_result').val(result);
        }
      }
    }
  }
  return form;
}

