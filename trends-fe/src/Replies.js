import React, { Component } from 'react';

import Error from './Error.js';
import Table from './Table.js';

class Replies extends Component {

  constructor(props) {
    super(props);

    this.state = {replies: []};
  }

  componentDidMount() {
    var self = this;
    this.source = new EventSource(self.props.url);

    this.source.onmessage = function(event) {
      var data = JSON.parse(event.data);
      self.setState({replies: data, error: null});
    };

    this.source.onopen = function(event) {
      self.setState({error: event});
    };
  }


  render() {

    if(this.state.replies === null || this.state.replies.length === 0){
      return (
        <div className="alert alert-info" role="alert">
          Data should appear in few seconds. Please wait.
        </div>);
    }

    return (
      <div>
        <h1 className="cover-heading">Replies</h1>
        <br/>
        <Error error={this.state.error} />
        <Table header={this.genHeader} parser={this.rowParser} rows={this.state.replies} />
      </div>
    );
  }

  rowParser(i, elem) {
    return (<tr key={elem.documentId}>
        <th scope="row">{i}</th>
        <th>{elem.documentId}</th>
        <th>{elem.twittId}</th>
        <th>{elem.count}</th>
        <th>{elem.userName}</th>
      </tr>);
  }

  genHeader() {
    return (
      <tr>
        <th scope="col">#</th>
        <th scope="col">ID</th>
        <th scope="col">Twitt id</th>
        <th scope="col">Count</th>
        <th scope="col">User</th>
      </tr>);
  }

}

export default Replies;
