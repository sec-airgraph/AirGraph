/*******************************************************************************
 *共通関数
 ******************************************************************************/

/**
 * オブジェクトが空かどうか確かめる
 * @param {Map} map マップオブジェクト
 * @returns {boolean} 空かどうか
 */
function isEmpty(map) {
  let length = map.size;
  if (length > 0) {
    return false;
  }
  return true;
}

/**
 * エスケープされた改行文字を改行コードに変換する.
 *
 * @param {string} s 変換対象文字列
 * @returns 変換後文字列
 */
function convertEscapedNewLine(s) {
  return s.replace('\\n', '\n') ?? s;
}