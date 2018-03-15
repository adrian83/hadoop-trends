import React, { Component } from 'react';


class Hashtags extends Component {

    constructor(props) {
      super(props);

      this.state = {hashtags: []};
      var self = this;

      this.source = new EventSource("http://localhost:8080/sse/hashtags");
        this.source.onmessage = function(event) {
          console.log("onmessage " + event);
          var data = JSON.parse(event.data);
          console.log("data " + data);
          self.setState({hashtags: data});
        };

      this.source.onopen = function(event) {
        console.log("onopen " + event);
      };
    }


  render() {
    var i = 1;
    var elems2 = this.state.hashtags.map((elem) => {
      var numb = React.createElement('th', {'scope': 'row'}, i++);
      var id = React.createElement('th', null, elem.documentId);
      var name = React.createElement('th', null, elem.name);
      var count = React.createElement('th', null, elem.count);

      return React.createElement('tr', null, [numb, id, name, count]);
    })

    if(i === 1){
      return (<div>NOTHING</div>);
    }

    return (
      <div>
        <table className="table">
          <thead>
            <tr>
              <th scope="col">#</th>
              <th scope="col">ID</th>
              <th scope="col">Tag</th>
              <th scope="col">Count</th>
            </tr>
          </thead>
          <tbody>
            {elems2}
          </tbody>
        </table>
      </div>
    );
  }
}

export default Hashtags;
