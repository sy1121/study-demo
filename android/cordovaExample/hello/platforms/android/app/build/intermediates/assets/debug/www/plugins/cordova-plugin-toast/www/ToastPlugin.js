cordova.define("cordova-plugin-toast.ToastPlugin", function(require, exports, module) {
var exec = require('cordova/exec');
exports.showToast = function (arg0, success, error) {
    exec(success, error, 'ToastPlugin', 'showToast', [arg0]);
};
});
