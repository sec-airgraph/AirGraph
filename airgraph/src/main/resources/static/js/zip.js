/*******************************************************************************
 *Zipファイルに関する処理
 ******************************************************************************/

/**
 * zipファイル解凍処理本体.
 * 
 * @param {*} file 
 * @param {*} callback zip解凍されたファイル群を引数にとるコールバック関数. See: https://github.com/imaya/zlib.js
 */
function unzipFiles(file, callback) {
  var zipReader = new FileReader();
  zipReader.onload = function () {
    try {
      var zipArr = new Uint8Array(zipReader.result);
      var unzipped = new Zlib.Unzip(zipArr);
      callback(unzipped);
    } catch (e) {
      return;
    }
  }
  zipReader.readAsArrayBuffer(file);
}
