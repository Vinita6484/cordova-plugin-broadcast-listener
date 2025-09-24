var exec = require('cordova/exec');

exports.startListening = function(success, error) {
    exec(success, error, "BroadcastPlugin", "startListening", []);
};
