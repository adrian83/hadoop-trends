import React, { Component } from 'react';


class Hashtags extends Component {

    constructor(props) {
      super(props);

      this.state = {hashtags: []};
      var st = this;


      this.source = new EventSource("http://localhost:8080/sse/hashtags");
        this.source.onmessage = function(event) {
          console.log("onmessage " + event);
          var data = JSON.parse(event.data);
          console.log("data " + data);
          st.setState({hashtags: data});
        };

      this.source.onopen = function(event) {
        console.log("onopen " + event);
      };
    }


  render() {

    var elems = this.state.hashtags.map((elem) => {
          return React.createElement('div', null, elem.name);
        })

    return (
      <div>{elems}</div>
    );
  }
}

export default Hashtags;
