var JSPlugin = new function() {

    this.Text2Speech=function(msg) {
        JSBridgePlugin.Text2Speech(msg);
    };

    this.sendText=function(msg) {
        JSBridgePlugin.sendText(msg);
     };

    this.showToast=function(msg) {
        JSBridgePlugin.showToast(msg);
    };

    this.startMetronom=function(exercise, page){
        JSBridgePlugin.startMetronom(exercise, page);
    };

    this.stopMetronom=function(){
        JSBridgePlugin.stopMetronom();
    };

};