import React, { Component } from 'react';

import Error from './Error.js';
import Table from './Table.js';

class Retweets extends Component {

  constructor(props) {
    super(props);

    this.state = {retweets: []};
  }

  componentDidMount() {
    var self = this;
    this.source = new EventSource(self.props.url);

    this.source.onmessage = function(event) {
      var data = JSON.parse(event.data);
      self.setState({retweets: data, error: null});
    };

    this.source.onopen = function(event) {
      self.setState({error: event});
    };
  }


  render() {

    if(this.state.retweets === null || this.state.retweets.length === 0){
      return (
        <div className="alert alert-info" role="alert">
          Data should appear in few seconds. Please wait.
        </div>);
    }

    return (
      <div>
        <h1 className="cover-heading">Retweets</h1>
        <br/>
        <Error error={this.state.error} />
        <Table header={this.genHeader} parser={this.rowParser} rows={this.state.retweets} />
      </div>
    );
  }

  rowParser(i, elem) {
      var tweetUrl = "https://twitter.com/" + elem.username + "/status/" + elem.tweetId;
      return (<tr key={elem.tweetId}>
          <th scope="row">{i}</th>
          <th><a target="_blank" rel="noopener noreferrer" href={tweetUrl}>{elem.tweetId} - {elem.username}</a></th>
          <th>{elem.count}</th>
        </tr>);
  }

  genHeader() {
    return (
      <tr>
        <th scope="col">#</th>
        <th scope="col">Tweet</th>
        <th scope="col">Count</th>
      </tr>);
  }

}

export default Retweets;
