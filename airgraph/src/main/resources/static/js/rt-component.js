/**********************************************************
 * RTC描画関連
 **********************************************************/

/**
 * RTC描画オブジェクト作成処理
 * @param T.B.D
 */
function createRtsystemViewObject(id, name, posX, posY) {

  // RTS作成
  var rtsystems = new joint.shapes.devs.Model({
    id: id,
    position: { x: posX, y: posY },
    size: {
      width: 20,
      height: 20
    },
    inPorts: [],
    outPorts: [],
    attrs: {
      '.label': {
        text: name,
        'ref-x': 35,
        'ref-y': 2,
        'font-size': 14,
        'font-family': 'sans-serif',
        'font-weight': 'normal',
        'text-anchor': 'left'
      },
      rect: { rx: 2, ry: 2, fill: '#c6efce', 'model-type': 'RTS' }
    }
  });

  // グラデーションをかける
  rtsystems.attr('rect/fill', {
    type: 'linearGradient',
    stops: [
      { offset: '0%', color: '#b7ffdb' },
      { offset: '100%', color: '#51ffa8' }
    ],
    attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
  });

  // 影をつける
  rtsystems.attr('rect/filter', { name: 'dropShadow', args: { dx: 2, dy: 2, blur: 3 } });

  return rtsystems;
}

/**
 * RTC描画オブジェクト作成処理
 * @param T.B.D
 */
function createRtcomponentViewObject(componentId, modelId, name, dataPorts, servicePorts, posX, posY, isComponentArea) {

  // ポート描画用の名称
  var inPortNames = [];
  var outPortNames = [];

  // データポート/サービスポート区分マップ
  var portTypeMap = new Object();

  if (dataPorts && dataPorts.length > 0) {
    for (var i = 0; i < dataPorts.length; i++) {
      if (dataPorts[i].portType == "DataInPort") {
        // 入力ポート
        inPortNames.push(dataPorts[i].name);
      } else if (dataPorts[i].portType == "DataOutPort") {
        // 出力ポート
        outPortNames.push(dataPorts[i].name);
      }
      portTypeMap[dataPorts[i].name] = dataPorts[i].portType;
    }
  }

  // サービスポート
  if (servicePorts && servicePorts.length > 0) {
    for (var i = 0; i < servicePorts.length; i++) {
      if (servicePorts[i].position === 'LEFT') {
        inPortNames.push(servicePorts[i].name);
      } else {
        outPortNames.push(servicePorts[i].name);
      }
      portTypeMap[servicePorts[i].name] = "ServicePort";
    }
  }

  // ポート数
  var expandSize = 0;
  var portNum = Math.max(inPortNames.length, outPortNames.length);
  if (portNum > 2) {
    expandSize = (30 * (portNum - 2));
  }

  // RTC作成
  var rtcomponents = new joint.shapes.devs.Model({
    componentId: componentId,
    id: modelId,
    position: { x: posX, y: posY },
    size: {
      width: isComponentArea ? 20 : 50,
      height: isComponentArea ? 20 : (50 + expandSize)
    },
    inPorts: inPortNames,
    outPorts: outPortNames,
    attrs: {
      '.label': {
        text: name,
        'ref-x': isComponentArea ? 35 : 0,
        'ref-y': isComponentArea ? 2 : -20,
        'font-family': 'sans-serif',
        'font-size': isComponentArea ? 14 : 16,
        'font-weight': isComponentArea ? 'normal' : 'bold',
        'text-anchor': 'left',
        'text-decoration': isComponentArea ? 'normal' : 'underline'
      },
      rect: { rx: isComponentArea ? 2 : 4, ry: isComponentArea ? 2 : 4, fill: '#8fc2f1', 'model-type': 'RTC' }
    }
  });

  // グラデーションをかける
  if (!isComponentArea && curState === STATE.EXEC) {
    rtcomponents.attr('rect/fill', {
      type: 'linearGradient',
      stops: [
        { offset: '0%', color: '#b2ffb5' },
        { offset: '100%', color: '#3dff32' }
      ],
      attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
    });
  } else {
    rtcomponents.attr('rect/fill', {
      type: 'linearGradient',
      stops: [
        { offset: '0%', color: '#b2d8ff' },
        { offset: '100%', color: '#3399ff' }
      ],
      attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%' }
    });
  }

  // 影をつける
  rtcomponents.attr('rect/filter', {
    name: 'dropShadow',
    args: { dx: 2, dy: 2, blur: 3 }
  });

  // ポート設定
  if (rtcomponents.portData.ports && rtcomponents.portData.ports.length > 0) {
    for (var i = 0; i < rtcomponents.portData.ports.length; i++) {
      rtcomponents.portData.ports[i].attrs['.port-body'].r = isComponentArea ? 3 : 6;
      rtcomponents.portData.ports[i].attrs['.port-body']['port-type'] = portTypeMap[rtcomponents.portData.ports[i]['id']];
      if (rtcomponents.portData.ports[i].attrs['.port-body']['port-type'] === 'DataInPort' || rtcomponents.portData.ports[i].attrs['.port-body']['port-type'] === 'DataOutPort') {
        rtcomponents.portData.ports[i].attrs['.port-body'].fill = '#436adb';
      } else {
        rtcomponents.portData.ports[i].attrs['.port-body'].fill = '#c6c6c6';
      }

      if (isComponentArea) {
        rtcomponents.portData.ports[i].attrs['.port-label'].text = '';
      }
    }
  }

  return rtcomponents;
}

/**
 * RTCを画面から削除する
 * @param id
 * @returns
 */
function deleteComponentViewObject(id) {
  if (mainGraph.attributes.cells.models && mainGraph.attributes.cells.models.length > 0) {
    for (var i = 0; i < mainGraph.attributes.cells.models.length; i++) {
      if (mainGraph.attributes.cells.models[i].id === id) {
        mainGraph.attributes.cells.remove(mainGraph.attributes.cells.models[i]);
        break;
      }
    }
  }
}

/**
 * すべてのRTCを画面から削除する
 * @returns
 */
function deleteAllComponentsViewObject() {
  if (mainGraph.attributes.cells.models && mainGraph.attributes.cells.models.length > 0) {
    for (var i = mainGraph.attributes.cells.models.length; i >= 0; i--) {
      mainGraph.attributes.cells.remove(mainGraph.attributes.cells.models[i]);
    }
  }
}

/**
 * 位置情報を更新する
 * 
 * @returns
 */
function updateLocation() {

  // 画面上の全てのモデルに対して実行する
  if (mainGraph.attributes.cells.models) {

    var modelList = mainGraph.attributes.cells.models;
    for (var i = 0; i < modelList.length; i++) {

      var model = modelList[i];

      var isComponent = false;

      // コンポーネントの位置を保存する
      if (mainRtsMap[curWorkspaceName].rtsProfile.components) {

        var componentList = mainRtsMap[curWorkspaceName].rtsProfile.components;
        for (var j = 0; j < componentList.length; j++) {
          var component = componentList[j];
          if (component.instanceName === model.attributes.id) {
            isComponent = true;

            if (!mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location) {
              mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location = new Object();
            }
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location['direction'] = 'DOWN';
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location['width'] = 0;
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location['height'] = 0;
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location['x'] = mainGraph.attributes.cells.models[i].attributes.position.x;
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[component.id][0].location['y'] = mainGraph.attributes.cells.models[i].attributes.position.y;
            break;
          }
        }
      }

      if (isComponent !== true) {
        // コネクターの折れ線の位置を保存する
        if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors) {

          var dataPortConectorList = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
          for (var j = 0; j < dataPortConectorList.length; j++) {
            var dataPortConnector = dataPortConectorList[j];
            if (dataPortConnector.connectorId === model.attributes.id) {

              if (model.attributes.vertices) {
                var propList = [];
                // 折れ線以外を格納する
                for (var k = 0; k < mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[j].properties.length; k++) {
                  var orgProp = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[j].properties[k];
                  if (orgProp['name'] !== 'vertice') {
                    propList.push(orgProp);
                  }
                }

                for (var k = 0; k < model.attributes.vertices.length; k++) {
                  var property = new Object();
                  property['name'] = 'vertice';
                  property['value'] = '{"x": ' + model.attributes.vertices[k].x + ', "y": ' + model.attributes.vertices[k].y + '}';
                  propList.push(property);
                }
                mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[j].properties = propList;
              }
            }
          }
        }
      }
    }
  }


  // コンポーネントの位置を保存する
  if (mainRtsMap[curWorkspaceName].rtsProfile.components) {

    var componentList = mainRtsMap[curWorkspaceName].rtsProfile.components;
    for (var i = 0; i < componentList.length; i++) {
      if (mainGraph.attributes.cells.models) {
        for (var j = 0; j < mainGraph.attributes.cells.models.length; j++) {
          if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName === mainGraph.attributes.cells.models[j].attributes.id) {
            if (!mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location) {
              mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location = new Object();
            }
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location['direction'] = 'DOWN';
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location['width'] = 0;
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location['height'] = 0;
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location['x'] = mainGraph.attributes.cells.models[j].attributes.position.x;
            mainRtsMap[curWorkspaceName].rtsProfile.componentMap[mainRtsMap[curWorkspaceName].rtsProfile.components[i].id][0].location['y'] = mainGraph.attributes.cells.models[j].attributes.position.y;
            break;
          }
        }
      }
    }
  }
}

/**
 * PackageのComponent部分を更新する
 * @param rtc
 * @returns
 */
function updateRtcInPackage(rtc) {
  // RTC置き換え
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].modelProfile.modelId === rtc.modelProfile.modelId) {
        mainRtsMap[curWorkspaceName].rtcs[i] = rtc;
        break;
      }
    }
  }
}

/**********************************************************
 * データポート・サービスポート共通
 **********************************************************/
/**
 * ポート名称の重複をチェックする
 * @param componentId
 * @param index
 * @param portName
 * @param isData
 * @returns
 */
function existSamePortName(componentId, index, portName, isData) {
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);
  if (!rtcomponent || componentIndex < 0) {
    return false;
  }

  for (var i = 0; i < rtcomponent.rtcProfile.dataPorts.length; i++) {
    if (rtcomponent.rtcProfile.dataPorts[i].name === portName) {
      if (isData === false || i != index) {
        // 名称が一致していても自分自身ならOK
        return true;
      }
    }
  }

  for (var i = 0; i < rtcomponent.rtcProfile.servicePorts.length; i++) {
    if (rtcomponent.rtcProfile.servicePorts[i].name === portName) {
      if (isData === true || i != index) {
        // 名称が一致していても自分自身ならOK
        return true;
      }
    }
  }
  return false;
}

/**********************************************************
 * データポート関連
 **********************************************************/

/**
 * 新規作成時のデフォルトのデータポートを作成する
 * 
 * @param componentId
 * @param isIn
 */
function getDefaultDataPort(componentId, isIn) {
  // 入力・出力識別用
  var portName = '';
  var portType = '';
  if (isIn === true) {
    portName = 'inPort';
    portType = 'DataInPort';
  } else {
    portName = 'outPort';
    portType = 'DataOutPort';
  }

  var portData = new Object();
  portData.id = componentId;
  portData.isIn = isIn;

  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return portData;
  }

  var componentName = rtcomponent.modelProfile.modelName;
  if (rtcomponent.modelProfile.clonedDirectory.indexOf(componentName) < 0) {
    componentName = rtcomponent.modelProfile.gitName;
  }
  portData.componentName = componentName;

  // 既存ポートの数＋１をデフォルトのポート名とする
  var curPortSize = 0;
  if (rtcomponent.rtcProfile.dataPorts && rtcomponent.rtcProfile.dataPorts.length > 0) {
    for (var i = 0; i < rtcomponent.rtcProfile.dataPorts.length; i++) {
      if (rtcomponent.rtcProfile.dataPorts[i].portType === portType) {
        // 入力ポートの数を数える
        curPortSize++;
      }
    }
  }

  portData.portName = portName + (curPortSize + 1);
  portData.dataType = 'RTC::TimedLong';
  portData.valName = '';

  return portData;
}

/**
 * 入力ポートを追加する
 * 
 * @param componentId
 * @param portName
 * @param dataType
 * @param valName
 * @param isIn
 * @returns
 */
function addDataPort(componentId, portName, dataType, valName, isIn) {
  // 入力・出力識別用
  var portType = '';
  if (isIn === true) {
    portType = 'DataInPort';
  } else {
    portType = 'DataOutPort';
  }

  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // RTCに追加
  var portRtc = { name: portName, portType: portType, dataType: dataType, variableName: valName };
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.dataPorts.push(portRtc);

  // Packageに追加
  var portRts = { name: portName, visible: true };
  var componentArr = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentArr) {
    if (!componentArr[0].dataPorts) {
      mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].dataPorts = new Object();
    }
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].dataPorts.push(portRts);
  }
  return rtcomponent;
};

/**
 * データポートを更新する
 * 
 * @param componentId
 * @param index
 * @param portName
 * @param dataType
 * @param valName
 * @returns
 */
function updateDataPort(componentId, index, portName, dataType, valName) {
  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // ポートを取得して更新
  var port = rtcomponent.rtcProfile.dataPorts[index];
  port.name = portName;
  port.dataType = dataType;
  port.variableName = valName;
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.dataPorts[index] = port;

  // Packageも更新
  var componentArr = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentArr && componentArr[0].dataPorts && componentArr[0].dataPorts.length > index) {
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].dataPorts[index].name = portName;
  }

  return rtcomponent;
};

/**
 * データポートを削除する
 * 
 * @param componentId
 * @param index
 * @returns
 */
function deleteDataPort(componentId, index) {
  // IDを元に作業領域オブジェクトマップから取得
  var rtcomponent = null;
  var componentIndex = -1;
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        rtcomponent = mainRtsMap[curWorkspaceName].rtcs[i];
        componentIndex = i;
        break;
      }
    }
  }

  // コンポーネントの取得に失敗
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // RTCから削除する
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.dataPorts.splice(index, 1);

  // Packageから削除する
  mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].dataPorts.splice(index, 1);
}

/**
 * 指定ポートが接続済かどうかを調べる
 * 
 * @param componentId
 * @param portName
 * @returns
 */
function isPortConnected(componentId, modelId, portName) {
  var dataPortConnectors = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
  if (dataPortConnectors && dataPortConnectors.length > 0) {
    for (var i = 0; i < dataPortConnectors.length; i++) {
      var dataPortConnector = dataPortConnectors[i];
      if ((dataPortConnector.sourceDataPort.componentId === componentId &&
        dataPortConnector.sourceDataPort.portName === modelId + '.' + portName) ||
        (dataPortConnector.targetDataPort.componentId === componentId &&
          dataPortConnector.targetDataPort.portName === modelId + '.' + portName)) {
        // 接続元か接続先に存在する
        return true;
      }
    }
  }
  return false;
}

/**
 * ログの出力を解除する
 * 
 * @param componentId
 * @param modelId
 * @param portName
 * @returns
 */
function removeLogger(componentId, modelId, portName) {
  // 一度非表示にしておく
  changeLoggerDisplay(componentId, modelId, portName, false);

  // 接続情報を削除する
  var dataPortConnectors = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
  if (dataPortConnectors && dataPortConnectors.length > 0) {
    for (var i = 0; i < dataPortConnectors.length; i++) {
      var dataPortConnector = dataPortConnectors[i];
      if (dataPortConnector.sourceDataPort.componentId === componentId &&
        dataPortConnector.sourceDataPort.instanceName === modelId &&
        dataPortConnector.sourceDataPort.portName === modelId + '.' + portName &&
        dataPortConnector.connectorType === 'logger') {
        // 接続情報を削除する
        deleteDataPortConnectionInfo(mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].connectorId);
      }
    }
  }
  // Packageの情報を更新する
  var componentArr = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentArr && componentArr[0].dataPorts) {
    for (var i = 0; i < componentArr[0].dataPorts.length; i++) {
      var dataport = componentArr[0].dataPorts[i];
      if (dataport.name === portName) {
        mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].dataPorts[i].logging = false;
        break;
      }
    }
  }
  // 保存しておく
  updatePackage(false);
}

/**
 * ロガーの表示・非表示を切り替える
 * 
 * @param componentId
 * @param modelId
 * @param portName
 * @param loggerVisible
 * @returns
 */
function changeLoggerDisplay(componentId, modelId, portName, loggerVisible) {
  // 接続情報を更新する
  var loggerId = null;

  var dataPortConnectorsList = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
  if (dataPortConnectorsList && dataPortConnectorsList.length > 0) {
    for (var i = 0; i < dataPortConnectorsList.length; i++) {

      var dataPortConnector = dataPortConnectorsList[i];
      if (dataPortConnector.sourceDataPort.componentId === componentId &&
        dataPortConnector.sourceDataPort.instanceName === modelId &&
        dataPortConnector.sourceDataPort.portName === modelId + '.' + portName &&
        dataPortConnector.connectorType === 'logger') {
        // 接続線の表示を切替える
        mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].visible = loggerVisible;
        // 接続先のロガーのIDを保持しておく
        loggerId = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].targetDataPort.componentId;
      }
    }

    // 同一ロガーを使用していて表示となっているコネクタの件数を数える
    var sameLoggerCnt = 0;
    for (var i = 0; i < dataPortConnectorsList.length; i++) {

      var dataPortConnector = dataPortConnectorsList[i];
      if (dataPortConnector.visible === true && dataPortConnector.targetDataPort.componentId === loggerId) {
        sameLoggerCnt++;
      }
    }

    // 表示の場合、もしくはロガーが１つのみ接続された状態で非表示の場合はロガーの表示も更新する
    if (loggerVisible === true || sameLoggerCnt === 0) {
      mainRtsMap[curWorkspaceName].rtsProfile.componentMap[loggerId][0].visible = loggerVisible;
    }
  }


  // Packageの情報を更新する（自分自身）
  var componentList = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentList && componentList.length > 0) {

    var rtcomponent = componentList[0];
    for (var i = 0; i < rtcomponent.dataPorts.length; i++) {
      if (rtcomponent.dataPorts[i].name === portName) {
        mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].dataPorts[i].loggerVisible = loggerVisible;
        break;
      }
    }
  }

  // 作業領域再読み込み
  reloadPackageWorkspace(100, 100, mainRtsMap[curWorkspaceName]);
}

/**********************************************************
 * データポートリンク情報関連
 **********************************************************/
/**
 * データポート接続情報を設定する
 * @returns
 */
function setDataPortConnectionInfo() {
  if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors && mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.length > 0) {

    var dataPortConnectorList = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
    for (var i = 0; i < dataPortConnectorList.length; i++) {

      var dataPortConnector = dataPortConnectorList[i];
      if (dataPortConnector.sourceDataPort && dataPortConnector.targetDataPort) {

        var connectorId = dataPortConnector.connectorId;
        var sourceRtc = dataPortConnector.sourceDataPort.instanceName;
        var sourcePorts = dataPortConnector.sourceDataPort.portName.split('.');
        var sourcePort = sourcePorts[sourcePorts.length - 1];
        var destRtc = dataPortConnector.targetDataPort.instanceName;
        var destPorts = dataPortConnector.targetDataPort.portName.split('.');
        var destPort = destPorts[sourcePorts.length - 1];
        var propeties = dataPortConnector.properties;

        // ロガーは緑にする
        if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].connectorType === 'logger') {
          $('[port = ' + sourcePort + '][port-type=DataOutPort]', '#main-joint-area').attr('fill', '#00FF00')
        }

        // 非表示の接続情報は表示しない
        if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].visible === false) {
          continue;
        }

        var link = new joint.dia.Link({
          id: connectorId,
          source: {
            id: sourceRtc,
            port: sourcePort
          },
          target: {
            id: destRtc,
            port: destPort
          },
          attrs: {
            '.': { 'filter': { name: 'dropShadow', args: { dx: 2, dy: 2, blur: 3 } } },
            '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' }
          }
        });

        // 折れ線情報も復活させる
        if (propeties) {
          var vertices = [];
          for (var j = 0; j < propeties.length; j++) {
            if (propeties[j]['name'] === 'vertice') {
              var vertice = $.parseJSON(propeties[j]['value']);
              vertices.push(vertice);
            }
          }
          if (vertices.length > 0) {
            link.set('vertices', vertices);
          }
        }
        mainGraph.addCell(link);
        mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].connectorId = link.id;

        // イベントを設定する
        setEventDataportLink(mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i]);
      }
    }
  }
};

/**
 * データポート接続情報を更新する
 * 
 * @param rtc
 * @param oldPortName
 * @param newPortName
 * @param isIn
 * @returns
 */
function updateDataPortConnectionInfo(rtc, oldPortName, newPortName, isIn) {
  // リンク情報を再定義
  var instanceName = null;
  for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.components.length; i++) {
    if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].id === rtc.rtcProfile.id) {
      instanceName = mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName;
    }
  }
  if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors && mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.length > 0) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.length; i++) {
      if (isIn === false) {
        for (var j = 0; j < rtc.rtcProfile.dataPorts.length; j++) {
          if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].sourceDataPort.portName === (instanceName + '.' + oldPortName)) {
            mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].sourceDataPort.portName = instanceName + '.' + newPortName;
          }
        }
      } else {
        for (var j = 0; j < rtc.rtcProfile.dataPorts.length; j++) {
          if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].targetDataPort.portName === (instanceName + '.' + oldPortName)) {
            mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].targetDataPort.portName = instanceName + '.' + newPortName;
          }
        }
      }
    }
  }
}

/**
 * データポート接続情報を削除する
 * @param connectorId
 * @returns
 */
function deleteDataPortConnectionInfo(connectorId) {
  // 画面から削除
  deleteComponentViewObject(connectorId);

  // モデルから削除
  if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors && mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.length > 0) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors[i].connectorId === connectorId) {
        mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.splice(i, 1);
        break;
      }
    }
  }
}

/**
 * データポート接続情報を追加する
 * @param connectorId
 * @param sourcePortData
 * @param targetPortData
 * @returns
 */
function addDataPortConnectionInfo(connectorId, sourcePortData, targetPortData) {
  // 接続情報クラス
  var dataPortConnector = new Object();
  dataPortConnector['connectorId'] = connectorId;
  dataPortConnector['name'] = sourcePortData.modelId + '.' + sourcePortData.portName + '_' + targetPortData.modelId + '.' + targetPortData.portName;
  dataPortConnector['dataType'] = getConnectionPortDataType(sourcePortData.dataType);
  dataPortConnector['dataflowType'] = 'push';
  dataPortConnector['interfaceType'] = 'corba_cdr';
  dataPortConnector['pushInterval'] = '0.0';
  dataPortConnector['subscriptionType'] = 'new';
  dataPortConnector['visible'] = true;
  dataPortConnector['properties'] = [];

  // 接続元
  var sourceDataPort = new Object();
  sourceDataPort['componentId'] = sourcePortData.id;
  sourceDataPort['portName'] = sourcePortData.modelId + '.' + sourcePortData.portName;
  sourceDataPort['instanceName'] = sourcePortData.modelId;
  sourceDataPort['properties'] = [];
  for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.components.length; i++) {
    if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName === sourcePortData.modelId) {
      var propSource = new Object();
      propSource['name'] = 'COMPONENT_PATH_ID';
      propSource['value'] = mainRtsMap[curWorkspaceName].rtsProfile.components[i].pathUri;
      sourceDataPort['properties'].push(propSource);
      break;
    }
  }
  dataPortConnector['sourceDataPort'] = sourceDataPort;

  // 接続先
  var targetDataPort = new Object();
  targetDataPort['componentId'] = targetPortData.id;
  targetDataPort['portName'] = targetPortData.modelId + '.' + targetPortData.portName;
  targetDataPort['instanceName'] = targetPortData.modelId;
  targetDataPort['properties'] = [];
  for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.components.length; i++) {
    if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName === targetPortData.modelId) {
      var propTarget = new Object();
      propTarget['name'] = 'COMPONENT_PATH_ID';
      propTarget['value'] = mainRtsMap[curWorkspaceName].rtsProfile.components[i].pathUri;
      targetDataPort['properties'].push(propTarget);
      break;
    }
  }
  dataPortConnector['targetDataPort'] = targetDataPort;

  // その他の情報
  var propDataFlowType = new Object();
  propDataFlowType['name'] = 'dataport.dataflow_type';
  propDataFlowType['value'] = 'push';
  dataPortConnector['properties'].push(propDataFlowType);
  var propSerializerCdrEndian = new Object();
  propSerializerCdrEndian['name'] = 'dataport.serializer.cdr.endian';
  propSerializerCdrEndian['value'] = 'little,big';
  dataPortConnector['properties'].push(propSerializerCdrEndian);
  var propDataType = new Object();
  propDataType['name'] = 'dataport.data_type';
  propDataType['value'] = getConnectionPortDataType(sourcePortData.dataType);
  dataPortConnector['properties'].push(propDataType);
  var propInterfaceType = new Object();
  propInterfaceType['name'] = 'dataport.interface_type';
  propInterfaceType['value'] = 'corba_cdr';
  dataPortConnector['properties'].push(propInterfaceType);
  var propSubscriptionType = new Object();
  propSubscriptionType['name'] = 'dataport.subscription_type';
  propSubscriptionType['value'] = 'new';
  dataPortConnector['properties'].push(propSubscriptionType);

  if (mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors == null) {
    mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors = [];
  }
  mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors.push(dataPortConnector);
}

/**********************************************************
 * サービスポート関連
 **********************************************************/

/**
 * 新規作成時のデフォルトのサービスポートを作成する
 * 
 * @param componentId
 * @param isIn
 */
function getDefaultServicePort(componentId) {
  // 入力・出力識別用
  var portName = 'servicePort';

  var portData = new Object();
  portData.id = componentId;

  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return portData;
  }

  // 既存ポートの数＋１をデフォルトのポート名とする
  var curPortSize = 0;
  if (rtcomponent.rtcProfile.servicePorts && rtcomponent.rtcProfile.servicePorts.length > 0) {
    for (var i = 0; i < rtcomponent.rtcProfile.servicePorts.length; i++) {
      // 入力ポートの数を数える
      curPortSize++;
    }
  }

  var componentName = rtcomponent.modelProfile.modelName;
  if (rtcomponent.modelProfile.clonedDirectory.indexOf(componentName) < 0) {
    componentName = rtcomponent.modelProfile.gitName;
  }
  portData.componentName = componentName;
  portData.portName = portName + (curPortSize + 1);
  portData.position = 'LEFT';
  portData.valName = '';

  return portData;
}

/**
 * サービスポートを追加する
 * 
 * @param componentId
 * @param portName
 * @param position
 * @param interfaceList
 * @returns
 */
function addServicePort(componentId, portName, position, interfaceList) {
  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // RTCに追加
  var portRtc = { name: portName, position: position, serviceInterfaces: interfaceList };
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.servicePorts.push(portRtc);

  // Packageに追加
  var portRts = { name: portName, visible: true };
  var componentArr = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentArr) {
    if (!componentArr[0].servicePorts) {
      mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].servicePorts = new Object();
    }
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].servicePorts.push(portRts);
  }
  return rtcomponent;
}

/**
 * サービスポートを更新する
 * 
 * @param componentId
 * @param portName
 * @param position
 * @param interfaceList
 * @returns
 */
function updateServicePort(componentId, index, portName, position, interfaceList) {
  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // RTCに追加
  var port = rtcomponent.rtcProfile.servicePorts[index];
  port.name = portName;
  port.position = position;
  port.serviceInterfaces = interfaceList;
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.servicePorts[index] = port

  // Packageに追加
  var componentArr = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentArr && componentArr[0].servicePorts && componentArr[0].servicePorts.length > index) {
    mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].servicePorts[index].name = portName;
  }

  return rtcomponent;
}

/**
 * サービスポートを削除する
 * 
 * @param componentId
 * @param index
 * @returns
 */
function deleteServicePort(componentId, index) {
  // IDを元に作業領域オブジェクトマップから取得
  var rtcomponent = null;
  var componentIndex = -1;
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        rtcomponent = mainRtsMap[curWorkspaceName].rtcs[i];
        componentIndex = i;
        break;
      }
    }
  }

  // コンポーネントの取得に失敗
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // RTCから削除する
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.servicePorts.splice(index, 1);

  // Packageから削除する
  mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].servicePorts.splice(index, 1);
}

/**********************************************************
 * サービスポートリンク情報関連
 **********************************************************/
/**
 * サービスポート接続情報を設定する
 * @returns
 */
function setServicePortConnectionInfo() {
  if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors && mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.length > 0) {

    var servicePortConnectorList = mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors;
    for (var i = 0; i < servicePortConnectorList.length; i++) {

      var servicePortConnector = servicePortConnectorList[i];
      if (servicePortConnector.sourceServicePort && servicePortConnector.targetServicePort) {

        var connectorId = servicePortConnector.connectorId;
        var sourceRtc = servicePortConnector.sourceServicePort.instanceName;
        var sourcePorts = servicePortConnector.sourceServicePort.portName.split('.');
        var sourcePort = sourcePorts[sourcePorts.length - 1];
        var destRtc = servicePortConnector.targetServicePort.instanceName;
        var destPorts = servicePortConnector.targetServicePort.portName.split('.');
        var destPort = destPorts[sourcePorts.length - 1];
        var propeties = servicePortConnector.properties;

        // 非表示の接続情報は表示しない
        if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].visible === false) {
          continue;
        }

        var link = new joint.dia.Link({
          id: connectorId,
          source: {
            id: sourceRtc,
            port: sourcePort
          },
          target: {
            id: destRtc,
            port: destPort
          },
          attrs: {
            '.': { 'filter': { name: 'dropShadow', args: { dx: 2, dy: 2, blur: 3 } } },
            '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' }
          }
        });

        // 折れ線情報も復活させる
        if (propeties) {
          var vertices = [];
          for (var j = 0; j < propeties.length; j++) {
            if (propeties[j]['name'] === 'vertice') {
              var vertice = $.parseJSON(propeties[j]['value']);
              vertices.push(vertice);
            }
          }
          if (vertices.length > 0) {
            link.set('vertices', vertices);
          }
        }
        mainGraph.addCell(link);
        mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].connectorId = link.id;

        // イベントを設定する
        setEventServiceportLink(mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i]);
      }
    }
  }
};

/**
 * サービスポート接続情報を更新する
 * 
 * @param rtc
 * @param oldPortName
 * @param newPortName
 * @param isIn
 * @returns
 */
function updateServicePortConnectionInfo(rtc, oldPortName, newPortName) {
  // リンク情報を再定義
  var instanceName = null;
  for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.components.length; i++) {
    if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].id === rtc.rtcProfile.id) {
      instanceName = mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName;
    }
  }
  if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors && mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.length > 0) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.length; i++) {
      for (var j = 0; j < rtc.rtcProfile.servicePorts.length; j++) {
        // サービスポートは接続元・接続先両方を見る必要がある
        if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].sourceDataPort.portName === (instanceName + '.' + oldPortName)) {
          mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].sourceDataPort.portName = instanceName + '.' + newPortName;
        }
        if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].targetDataPort.portName === (instanceName + '.' + oldPortName)) {
          mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].targetDataPort.portName = instanceName + '.' + newPortName;
        }
      }
    }
  }
}

/**
 * サービスポート接続情報を削除する
 * @param connectorId
 * @returns
 */
function deleteServicePortConnectionInfo(connectorId) {
  // 画面から削除
  deleteComponentViewObject(connectorId);

  // モデルから削除
  if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors && mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.length > 0) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors[i].connectorId === connectorId) {
        mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.splice(i, 1);
        break;
      }
    }
  }
}

/**
 * サービスポート接続情報を追加する
 * @param connectorId
 * @param sourcePortData
 * @param targetPortData
 * @returns
 */
function addServicePortConnectionInfo(connectorId, sourcePortData, targetPortData) {
  // 接続情報クラス
  var servicePortConnector = new Object();
  servicePortConnector['connectorId'] = connectorId;
  servicePortConnector['name'] = sourcePortData.modelId + '.' + sourcePortData.portName + '_' + targetPortData.modelId + '.' + targetPortData.portName;
  servicePortConnector['visible'] = true;
  servicePortConnector['properties'] = [];

  // 接続元
  var sourceServicePort = new Object();
  sourceServicePort['componentId'] = sourcePortData.id;
  sourceServicePort['portName'] = sourcePortData.modelId + '.' + sourcePortData.portName;
  sourceServicePort['instanceName'] = sourcePortData.modelId;
  sourceServicePort['properties'] = [];
  for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.components.length; i++) {
    if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName === sourcePortData.modelId) {
      var propSource = new Object();
      propSource['name'] = 'COMPONENT_PATH_ID';
      propSource['value'] = mainRtsMap[curWorkspaceName].rtsProfile.components[i].pathUri;
      sourceServicePort['properties'].push(propSource);
      break;
    }
  }
  servicePortConnector['sourceServicePort'] = sourceServicePort;

  // 接続先
  var targetServicePort = new Object();
  targetServicePort['componentId'] = targetPortData.id;
  targetServicePort['portName'] = targetPortData.modelId + '.' + targetPortData.portName;
  targetServicePort['instanceName'] = targetPortData.modelId;
  targetServicePort['properties'] = [];
  for (var i = 0; i < mainRtsMap[curWorkspaceName].rtsProfile.components.length; i++) {
    if (mainRtsMap[curWorkspaceName].rtsProfile.components[i].instanceName === targetPortData.modelId) {
      var propTarget = new Object();
      propTarget['name'] = 'COMPONENT_PATH_ID';
      propTarget['value'] = mainRtsMap[curWorkspaceName].rtsProfile.components[i].pathUri;
      targetServicePort['properties'].push(propTarget);
      break;
    }
  }
  servicePortConnector['targetServicePort'] = targetServicePort;

  // その他の情報
  var strictness = new Object();
  strictness['name'] = 'port.connection.strictness';
  strictness['value'] = 'strict';
  servicePortConnector['properties'].push(strictness);

  if (mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors == null) {
    mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors = [];
  }
  mainRtsMap[curWorkspaceName].rtsProfile.servicePortConnectors.push(servicePortConnector);
}

/**********************************************************
 * コンフィギュレーション設定関連
 **********************************************************/
/**
 * コンフィギュレーション設定を更新する
 */
function updateRtcConfiguration(componentId, configurationList) {
  // コンポーネントを取得する
  var rtcomponent = getComponentInPackage(componentId);
  var componentIndex = getComponentIndexInPackage(componentId);;
  if (!rtcomponent || componentIndex < 0) {
    return null;
  }

  // RTCに追加
  if (rtcomponent.rtcProfile.configurationSet === null) {
    mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.configurationSet = new Object();
  }
  mainRtsMap[curWorkspaceName].rtcs[componentIndex].rtcProfile.configurationSet['configurations'] = configurationList;

  return rtcomponent;
}

/**********************************************************
 * イベント設定関連
 **********************************************************/
/**
 * RTCにイベントを設定する
 * 
 * @param id
 * @returns
 */
function setEventRtcomponent(id, modelId, rtcomponent) {
  // ComponentIdを設定しておく
  $('rect', '[model-id = ' + modelId + ']').attr('component-id', id);

  // 全体右クリックメニュー設定
  $('rect', '[model-id = ' + modelId + ']').addClass('rtc-context-menu');

  // 全体ダブルクリックイベント設定
  $('rect', '[model-id = ' + modelId + ']').off('dblclick', '**');
  $('rect', '[model-id = ' + modelId + ']').on('dblclick', function (event) {
    openSourceCodePopup(id);
  });

  // データポート・形状変更およびメニュー設定
  for (var i = 0; i < rtcomponent.rtcProfile.dataPorts.length; i++) {
    var port = rtcomponent.rtcProfile.dataPorts[i];
    var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');

    // RTSプロファイルから情報を取得する
    var logging = false;
    var loggerVisible = false;
    if (mainRtsMap[curWorkspaceName].rtsProfile.componentMap[id] && mainRtsMap[curWorkspaceName].rtsProfile.componentMap[id][0].dataPorts) {
      for (var j = 0; j < mainRtsMap[curWorkspaceName].rtsProfile.componentMap[id][0].dataPorts.length; j++) {
        if (mainRtsMap[curWorkspaceName].rtsProfile.componentMap[id][0].dataPorts[j].name === port.name) {
          logging = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[id][0].dataPorts[j].logging;
          loggerVisible = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[id][0].dataPorts[j].loggerVisible;
          break;
        }
      }
    }

    var target = null;
    var parent = null;
    var div = null;
    if (port.portType == "DataInPort") {
      // 入力ポート
      target = $('circle[port-group=in][port=' + port.name + ']', '[model-id = ' + modelId + ']');
      path.setAttribute('d', 'M -12 -7 L 0 -7 0 7 -12 7 -7 0 Z');
      path.setAttribute('magnet', 'passive');
    } else if (port.portType == "DataOutPort") {
      // 出力ポート
      target = $('circle[port-group=out][port=' + port.name + ']', '[model-id = ' + modelId + ']');
      path.setAttribute('d', 'M 0 -7 L 7 -7 12 0 7 7 0 7 Z');
      path.setAttribute('magnet', target.attr('magnet'));
    }

    path.setAttribute('class', 'port-body');
    path.setAttribute('id', target.attr('id'));
    path.setAttribute('port', target.attr('port'));
    path.setAttribute('port-group', target.attr('port-group'));
    path.setAttribute('port-type', port.portType);
    path.setAttribute('fill', '#436adb');
    path.setAttribute('stroke', target.attr('stroke'));
    parent = target.parent();
    target.remove();
    parent.append(path);
    // UbuntuChromeでポート位置が不正のため設定し直す
    parent[0].setAttribute('transform', parent[0].getAttribute('transform'));

    var componentName = rtcomponent.modelProfile.modelName;
    if (rtcomponent.modelProfile.clonedDirectory.indexOf(componentName) < 0) {
      componentName = rtcomponent.modelProfile.gitName;
    }
    div = $('text', parent);
    if (div) {
      if (port.portType == "DataInPort") {
        $(div).addClass('rtc-data-inport-menu');
      } else {
        $(div).addClass('rtc-data-outport-menu');
      }
      $(div).attr('font-size', 12);
      $(div).attr('font-weight', 'bold');
      $(div).data({
        'id': id,
        'modelId': modelId,
        'componentName': componentName,
        'isIn': port.portType == "DataInPort",
        'isData': true,
        'index': i,
        'portName': port.name,
        'dataType': port.dataType,
        'variableName': port.variableName,
        'logging': logging,
        'loggerVisible': loggerVisible
      });
    }
  }

  // サービスポート・形状変更およびメニュー設定
  if (rtcomponent.rtcProfile.servicePorts && rtcomponent.rtcProfile.servicePorts.length > 0) {
    var componentName = rtcomponent.modelProfile.modelName;
    if (rtcomponent.modelProfile.clonedDirectory.indexOf(componentName) < 0) {
      componentName = rtcomponent.modelProfile.gitName;
    }
    for (var i = 0; i < rtcomponent.rtcProfile.servicePorts.length; i++) {
      var port = rtcomponent.rtcProfile.servicePorts[i];
      // サービスポートはポート単位でのみ表示
      // 表示位置を反映する
      if (port.position === 'LEFT') {
        var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        path.setAttribute('d', 'M -12 -7 L 0 -7 0 7 -12 7 Z');
        var target = $('circle[port-group=in][port=' + port.name + ']', '[model-id = ' + modelId + ']');
        var parent = target.parent();

        path.setAttribute('class', 'port-body');
        path.setAttribute('id', target.attr('id'));
        path.setAttribute('port', target.attr('port'));
        path.setAttribute('port-group', target.attr('port-group'));
        path.setAttribute('port-type', 'ServicePort');
        path.setAttribute('fill', '#c6c6c6');
        path.setAttribute('stroke', target.attr('stroke'));
        path.setAttribute('magnet', target.attr('magnet'));
        target.remove();
        parent.append(path);
        // UbuntuChromeでポート位置が不正のため設定し直す
        parent[0].setAttribute('transform', parent[0].getAttribute('transform'));
        var div = $('text', parent);
        if (div) {
          $(div).addClass('rtc-serviceport-menu');
          $(div).attr('font-size', 12);
          $(div).attr('font-weight', 'bold');
          $(div).data({ 'id': id, 'componentName': componentName, 'modelId': modelId, 'index': i, 'portName': port.name, 'position': port.position, 'interfaceArray': port.serviceInterfaces });
        }
      } else {
        var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        path.setAttribute('d', 'M 0 -7 L 12 -7 12 7 0 7 Z');
        var target = $('circle[port-group=out][port=' + port.name + ']', '[model-id = ' + modelId + ']');
        var parent = target.parent();

        path.setAttribute('class', 'port-body');
        path.setAttribute('id', target.attr('id'));
        path.setAttribute('port', target.attr('port'));
        path.setAttribute('port-group', target.attr('port-group'));
        path.setAttribute('port-type', 'ServicePort');
        path.setAttribute('fill', '#c6c6c6');
        path.setAttribute('stroke', target.attr('stroke'));
        path.setAttribute('magnet', target.attr('magnet'));
        target.remove();
        parent.append(path);
        // UbuntuChromeでポート位置が不正のため設定し直す
        parent[0].setAttribute('transform', parent[0].getAttribute('transform'));
        var div = $('text', parent);
        if (div) {
          $(div).addClass('rtc-serviceport-menu');
          $(div).attr('font-size', 12);
          $(div).attr('font-weight', 'bold');
          $(div).data({ 'id': id, 'componentName': componentName, 'modelId': modelId, 'index': i, 'portName': port.name, 'position': port.position, 'interfaceArray': port.serviceInterfaces });
        }
      }
    }
  }
}

/**
 * データポートリンクにイベントを設定する
 */
function setEventDataportLink(dataPortConnector) {
  // 全体右クリックメニュー設定
  // TODO: 詳細部分
  $('[model-id = ' + dataPortConnector.connectorId + ']').addClass('rtc-link-menu');
  $('[model-id = ' + dataPortConnector.connectorId + ']').data({
    'connectorId': dataPortConnector.connectorId,
    'name': dataPortConnector.name,
    'dataType': dataPortConnector.dataType,
    'dataflowType': dataPortConnector.dataflowType,
    'interfaceType': dataPortConnector.interfaceType,
    'subscriptionType': dataPortConnector.subscriptionType
  });
}

/**
 * サービスポートリンクにイベントを設定する
 */
function setEventServiceportLink(servicePortConnector) {
  // 全体右クリックメニュー設定
  // TODO: 詳細部分
  $('[model-id = ' + servicePortConnector.connectorId + ']').addClass('rtc-link-menu');
  $('[model-id = ' + servicePortConnector.connectorId + ']').data({
    'connectorId': servicePortConnector.connectorId,
    'name': servicePortConnector.name
  });
}

/**********************************************************
 * モデル操作関連
 **********************************************************/

/**
 * componentIdからComponentを取得する
 * 
 * @param componentId
 * @returns
 */
function getComponentInPackage(componentId) {
  var rtcomponent = null;
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        rtcomponent = mainRtsMap[curWorkspaceName].rtcs[i];
        break;
      }
    }
  }
  return rtcomponent;
}

/**
 * componentIdからComponentのINDEXを取得する
 * 
 * @param componentId
 * @returns
 */
function getComponentIndexInPackage(componentId) {
  var componentIndex = -1;
  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        componentIndex = i;
        break;
      }
    }
  }
  return componentIndex;
}

/**
 * componentIdからインスタンス名を取得
 * 
 * @param componentId
 * @returns 
 */
function getInstanceNameInPackage(componentId) {
  var instanceName = null;

  var componentList = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentList && componentList.length > 0) {
    instanceName = componentList[0].instanceName;
  }
  return instanceName;
}

/**
 * componentIdからアクティブなコンフィギュレーション設定を取得する
 * 
 * @param componentId
 * @returns
 */
function getActiveConfigInPackage(componentId) {
  var activeConfigSet = null;

  var componentList = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentList && componentList.length > 0) {
    activeConfigSet = componentList[0].activeConfigurationSet;
  }
  return activeConfigSet;
}

/**
 * componentIdとポートインスタンス名から<br>
 * ロガーに接続されているコネクタ名を取得する
 * 
 * @param componentId
 * @param instancePortName
 * @returns
 */
function getConnectorNameInPackage(componentId, portInstanceName) {
  var connectorName = null;

  var dataportConnectorList = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
  if (dataportConnectorList && dataportConnectorList.length > 0) {
    for (var i = 0; i < dataportConnectorList.length; i++) {

      var dataPortConnector = dataportConnectorList[i];
      if (dataPortConnector.connectorType === 'logger' &&
        dataPortConnector.sourceDataPort.componentId === componentId &&
        dataPortConnector.sourceDataPort.portName === portInstanceName) {
        connectorName = dataPortConnector.name;
        break;
      }
    }
  }
  return connectorName;
}

/**
 * componentIdとポートインスタンス名から<br>
 * ロガーとして使用しているコンポーネントのIDを取得する
 * 
 * @param componentId
 * @param instancePortName
 * @returns
 */
function getLoggerIdInPackage(componentId, portInstanceName) {
  var loggerId = null;

  var dataportConnectorList = mainRtsMap[curWorkspaceName].rtsProfile.dataPortConnectors;
  if (dataportConnectorList && dataportConnectorList.length > 0) {
    for (var i = 0; i < dataportConnectorList.length; i++) {

      var dataPortConnector = dataportConnectorList[i];

      if (dataPortConnector.connectorType === 'logger' &&
        dataPortConnector.sourceDataPort.componentId === componentId &&
        dataPortConnector.sourceDataPort.portName === portInstanceName) {
        loggerId = dataPortConnector.targetDataPort.componentId;
        break;
      }
    }
  }
  return loggerId;
}

/**
 * componentId,activeConfigSet,confignameから<br>
 * Configの設定値を取得する
 * 
 * @param componentId
 * @param activeConfig
 * @param configKey
 * @returns
 */
function getActiveConfigDataInPackage(componentId, activeConfigSet, configName) {
  var directoryPath = null;

  var componentList = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId];
  if (componentList && componentList.length > 0) {

    if (componentList[0].configurationSets) {
      var configurationSetList = componentList[0].configurationSets;

      for (var i = 0; i < configurationSetList.length; i++) {
        var configSet = configurationSetList[i];

        if (configSet.configurationDatas && configSet.id === activeConfigSet) {

          for (var j = 0; j < configSet.configurationDatas.length; j++) {
            var configData = configSet.configurationDatas[j];

            if (configData.name === configName) {
              directoryPath = configData.data;
              break;
            }
          }
          break;
        }
      }
    }
  }
  return directoryPath;
}

/**
 * componentIdからpathURIを取得
 * 
 * @param componentId
 * @returns 
 */
function getPathUriInPackage(componentId) {
  var pathUri = null;
  if (mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId]) {
    pathUri = mainRtsMap[curWorkspaceName].rtsProfile.componentMap[componentId][0].pathUri;
  }
  return pathUri
}

/**
 * componentIdからコードディレクトリ管理情報を取得する
 * 
 * @param componentId
 * @returns
 */
function getCodeDirectoryInPackageRtcDir(componentId) {
  var codeDirectory = null;

  if (mainRtsMap[curWorkspaceName].rtcs) {
    for (var i = 0; i < mainRtsMap[curWorkspaceName].rtcs.length; i++) {
      if (mainRtsMap[curWorkspaceName].rtcs[i].rtcProfile.id === componentId) {
        codeDirectory = mainRtsMap[curWorkspaceName].rtcs[i].codeDirectory;
        break;
      }
    }
  }
  return codeDirectory;
}

/**
 * modelIdからComponent名を取得
 * 
 * @param modelId
 * @returns 
 */
function getComponentNameInComponents(modelId) {
  var componentName = null;
  if (componentMap[modelId] && componentMap[modelId].modelProfile) {
    componentName = componentMap[modelId].modelProfile.modelName;
  }
  return componentName;
}

/**
 * modelIdからGitリポジトリ名を取得
 * 
 * @param modelId
 * @returns 
 */
function getRepositryNameInComponents(modelId) {
  var gitName = null;
  if (componentMap[modelId] && componentMap[modelId].modelProfile) {
    gitName = componentMap[modelId].modelProfile.gitName;
  }
  return gitName;
}

/**
 * modelIdからCloneされているディレクトリ名を取得
 * 
 * @param modelId
 * @returns
 */
function getClonedDirectory(modelId) {
  var clonedDirectory = null;
  if (componentMap[modelId] && componentMap[modelId].modelProfile) {
    clonedDirectory = componentMap[modelId].modelProfile.clonedDirectory;
  }
  return clonedDirectory;
}

/**********************************************************
 * その他
 **********************************************************/
/**
 * ポート名称から接続情報のポート名称に変換する
 */
function getConnectionPortDataType(portDataType) {
  var strs = portDataType.split('::');
  return 'IDL:' + strs[0] + '/' + strs[1] + ':1.0';
}