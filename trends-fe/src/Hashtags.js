import React, { Component } from 'react';


class Hashtags extends Component {

    constructor(props) {
      super(props);


      this.source = new EventSource("http://localhost:8080/sse/hashtags");
        this.source.onmessage = function(event) {
          console.log("onmessage " + event);
          var data = JSON.parse(event.data);
          console.log("data " + data);
        };

  this.source.onopen = function(event) {
    console.log("onopen " + event);
  };
    }

/*
  var source = new EventSource(this.url);
  source.onmessage = function(event) {
      var data = JSON.parse(event.data);
      onMsg(data);
  };
  source.onopen = function(event) {
    document.getElementById(i).innerHTML = "<div class=\"alert alert-info\" role=\"alert\">Data should appear soon.</div>";
  };
*/

  render() {
    return (
      <div>Hashtags</div>
    );
  }
}

export default Hashtags;
