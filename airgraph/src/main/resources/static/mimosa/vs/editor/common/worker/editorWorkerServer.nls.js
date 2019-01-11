/*!-----------------------------------------------------------
 * Copyright (C) Microsoft Corporation. All rights reserved.
 * Version: 7c44f3b480bb5d938a5b96d72f78f799cc5a24f7
 * Released under the MIT license
 * https://github.com/Microsoft/vscode/blob/master/LICENSE.txt
 *-----------------------------------------------------------*/
define("vs/editor/common/worker/editorWorkerServer.nls",{"vs/base/common/http":["Bad request. The request cannot be fulfilled due to bad syntax.","Unauthorized. The server is refusing to respond.","Forbidden. The server is refusing to respond.","Not Found. The requested location could not be found.","Method not allowed. A request was made using a request method not supported by that location.","Not Acceptable. The server can only generate a response that is not accepted by the client.","Proxy Authentication Required. The client must first authenticate itself with the proxy.","Request Timeout. The server timed out waiting for the request.","Conflict. The request could not be completed because of a conflict in the request.","Gone. The requested page is no longer available.",'Length Required. The "Content-Length" is not defined.',"Precondition Failed. The precondition given in the request evaluated to false by the server.","Request Entity Too Large. The server will not accept the request, because the request entity is too large.","Request-URI Too Long. The server will not accept the request, because the URL is too long.","Unsupported Media Type. The server will not accept the request, because the media type is not supported.","Internal Server Error.","Not Implemented. The server either does not recognize the request method, or it lacks the ability to fulfill the request.","Service Unavailable. The server is currently unavailable (overloaded or down).","HTTP status code {0}"],"vs/base/common/json":["Invalid symbol","Invalid number format","Property name expected","Value expected","Colon expected","Value expected","Comma expected","Value expected","Closing brace expected","Value expected","Comma expected","Value expected","Closing bracket expected","Value expected","End of content expected"],"vs/base/common/severity":["Error","Warning","Info"],"vs/editor/common/model/textModelWithTokens":["The mode has failed while tokenizing the input."],"vs/editor/common/modes/languageExtensionPoint":["Contributes language declarations.","ID of the language.","Name aliases for the language.","File extensions associated to the language.","File names associated to the language.","Mime types associated to the language.","A regular expression matching the first line of a file of the language.","A relative path to a file containing configuration options for the language.","Empty value for `contributes.{0}`","property `{0}` is mandatory and must be of type `string`","property `{0}` can be omitted and must be of type `string[]`","property `{0}` can be omitted and must be of type `string[]`","property `{0}` can be omitted and must be of type `string`","property `{0}` can be omitted and must be of type `string`","property `{0}` can be omitted and must be of type `string[]`","property `{0}` can be omitted and must be of type `string[]`","Invalid `contributes.{0}`. Expected an array."],"vs/platform/configuration/common/configurationRegistry":["Contributes configuration settings.","A summary of the settings. This label will be used in the settings file as separating comment.","Description of the configuration properties.","if set, 'configuration.type' must be set to 'object","'configuration.title' must be a string","'configuration.properties' must be an object"],"vs/platform/jsonschemas/common/jsonContributionRegistry":["Describes a JSON file using a schema. See json-schema.org for more info.","A unique identifier for the schema.","The schema to verify this document against ","A descriptive title of the element","A long description of the element. Used in hover menus and suggestions.","A default value. Used by suggestions.","A number that should cleanly divide the current value (i.e. have no remainder)","The maximum numerical value, inclusive by default.","Makes the maximum property exclusive.","The minimum numerical value, inclusive by default.","Makes the minimum property exclusive.","The maximum length of a string.","The minimum length of a string.","A regular expression to match the string against. It is not implicitly anchored.","For arrays, only when items is set as an array. If it is a schema, then this schema validates items after the ones specified by the items array. If it is false, then additional items will cause validation to fail.","For arrays. Can either be a schema to validate every element against or an array of schemas to validate each item against in order (the first schema will validate the first element, the second schema will validate the second element, and so on.","The maximum number of items that can be inside an array. Inclusive.","The minimum number of items that can be inside an array. Inclusive.","If all of the items in the array must be unique. Defaults to false.","The maximum number of properties an object can have. Inclusive.","The minimum number of properties an object can have. Inclusive.","An array of strings that lists the names of all properties required on this object.","Either a schema or a boolean. If a schema, then used to validate all properties not matched by 'properties' or 'patternProperties'. If false, then any properties not matched by either will cause this schema to fail.","Not used for validation. Place subschemas here that you wish to reference inline with $ref","A map of property names to schemas for each property.","A map of regular expressions on property names to schemas for matching properties.","A map of property names to either an array of property names or a schema. An array of property names means the property named in the key depends on the properties in the array being present in the object in order to be valid. If the value is a schema, then the schema is only applied to the object if the property in the key exists on the object.","The set of literal values that are valid","Either a string of one of the basic schema types (number, integer, null, array, object, boolean, string) or an array of strings specifying a subset of those types.","Describes the format expected for the value.","An array of schemas, all of which must match.","An array of schemas, where at least one must match.","An array of schemas, exactly one of which must match.","A schema which must not match."],"vs/platform/plugins/common/abstractPluginService":["Extension `{1}` failed to activate. Reason: unknown dependency `{0}`.","Extension `{1}` failed to activate. Reason: dependency `{0}` failed to activate.","Extension `{0}` failed to activate. Reason: more than 10 levels of dependencies (most likely a dependency loop).","Activating extension `{0}` failed: {1}."],"vs/platform/plugins/common/pluginsRegistry":["Got empty extension description","property `{0}` is mandatory and must be of type `string`","property `{0}` is mandatory and must be of type `string`","property `{0}` is mandatory and must be of type `string`","property `{0}` is mandatory and must be of type `object`","property `{0}` is mandatory and must be of type `string`","property `{0}` can be omitted or must be of type `string[]`","property `{0}` can be omitted or must be of type `string[]`","properties `{0}` and `{1}` must both be specified or must both be omitted","property `{0}` can be omitted or must be of type `string`","Expected `main` ({0}) to be included inside extension's folder ({1}). This might make the extension non-portable.","properties `{0}` and `{1}` must both be specified or must both be omitted","The display name for the extension used in the VS Code gallery.","The categories used by the VS Code gallery to categorize the extension.","Banner used in the VS Code marketplace.","The banner color on the VS Code marketplace page header.","The color theme for the font used in the banner.","The publisher of the VS Code extension.","Activation events for the VS Code extension.","Dependencies to other extensions. The id of an extension is always ${publisher}.${name}. For example: vscode.csharp.","Script executed before the package is published as a VS Code extension.","All contributions of the VS Code extension represented by this package.","Indicated whether VS Code should load your code as AMD or CommonJS. Default: false."]});
//# sourceMappingURL=editorWorkerServer.nls.js.map
