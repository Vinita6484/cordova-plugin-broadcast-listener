var exec;

function getExec() {
    if (!exec) {
        exec = require('cordova/exec');
    }
    return exec;
}

exports.startListening = function(success, error) {
    if (typeof success !== 'function' || typeof error !== 'function') {
        throw new Error('Invalid callback functions');
    }
    getExec()(success, error, "BroadcastPlugin", "startListening", []);
};

exports.stopListening = function(success, error) {
    if (typeof success !== 'function' || typeof error !== 'function') {
        throw new Error('Invalid callback functions');
    }
    getExec()(success, error, "BroadcastPlugin", "stopListening", []);
};
