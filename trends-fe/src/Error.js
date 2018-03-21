import React, { Component } from 'react';


class Error extends Component {

  constructor(props) {
    super(props);
  }

  render() {
    if(this.props.error){
      return React.createElement('div', {className: "alert alert-danger"}, this.props.error);
    }
    return (<br/>);
  }
}

export default Error;
