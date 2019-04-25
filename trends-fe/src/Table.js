import React, { Component } from 'react';


class Table extends Component {

  render() {

    var i = 1;
    var elems = this.props.rows.map((elem) => {
      return this.props.parser(i++, elem);
    })

    return (
      <table className="table">
        <thead>
          {this.props.header()}
        </thead>
        <tbody>
          {elems}
        </tbody>
      </table>

    );
  }
}

export default Table;
