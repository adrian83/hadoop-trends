import React, { Component } from 'react';

import Error from './Error.js';
import Table from './Table.js';

class Retwitts extends Component {

  constructor(props) {
    super(props);

    this.state = {retwitts: []};
  }

  componentDidMount() {
    var self = this;
    this.source = new EventSource(self.props.url);

    this.source.onmessage = function(event) {
      var data = JSON.parse(event.data);
      self.setState({retwitts: data, error: null});
    };

    this.source.onopen = function(event) {
      self.setState({error: event});
    };
  }


  render() {

    if(this.state.retwitts === null || this.state.retwitts.length === 0){
      return (
        <div className="alert alert-info" role="alert">
          Data should appear in few seconds. Please wait.
        </div>);
    }

    return (
      <div>
        <h1 className="cover-heading">Retwitts</h1>
        <br/>
        <Error error={this.state.error} />
        <Table header={this.genHeader} parser={this.rowParser} rows={this.state.retwitts} />
      </div>
    );
  }

  rowParser(i, elem) {
      var twittUrl = "https://twitter.com/" + elem.username + "/status/" + elem.twittId;
      return (<tr key={elem.twittId}>
          <th scope="row">{i}</th>
          <th><a target="_blank" href={twittUrl}>{elem.twittId} - {elem.username}</a></th>
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

export default Retwitts;
