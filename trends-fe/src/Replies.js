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
      var twittUrl = "https://twitter.com/" + elem.username + "/status/" + elem.twittId;
      return (<tr key={elem.twittId}>
          <th scope="row">{i}</th>
          <th><a target="_blank" rel="noopener noreferrer" href={twittUrl}>{elem.twittId} - {elem.username}</a></th>
          <th>{elem.count}</th>
        </tr>);
  }

  genHeader() {
    return (
      <tr>
        <th scope="col">#</th>
        <th scope="col">Twitt</th>
        <th scope="col">Count</th>
      </tr>);
  }

}

export default Replies;
