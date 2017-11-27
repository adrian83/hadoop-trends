
class SseClient {
  constructor(url, infoContainer, onMessage) {
    this.url = url;
    this.infoContainer = infoContainer;
    this.onMessage = onMessage;
  }
  
  start() {
	  var onMsg = this.onMessage;
	  var i = this.infoContainer;
	    var source = new EventSource(this.url);
	    source.onmessage = function(event) {
	        var data = JSON.parse(event.data);
	        onMsg(data);
	    };
	    source.onopen = function(event) {
	    	document.getElementById(i).innerHTML = "<div class=\"alert alert-info\" role=\"alert\">Data should appear soon.</div>";
	    };
  }
  
}
