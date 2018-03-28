import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import PropTypes from 'prop-types';

class Menu extends Component {


    render() {

      console.log('-------- props', this.props);
        const { match, location, history } = this.props;

        console.log('match', match, 'location', location, 'history', history);


        var menuElems = [
          this.menuElem('Favorites', '/favorites', location.pathname),
          this.menuElem('Hashtags', '/hashtags', location.pathname),
          this.menuElem('Replies', '/replies', location.pathname),
          this.menuElem('Retwitts', '/retwitts', location.pathname)
        ]

        return (
          <div className="inner">
            <h3 className="masthead-brand">Trends&nbsp;</h3>
            <nav className="nav nav-masthead justify-content-center">
{ menuElems }
            </nav>
          </div>);
    }

    menuElem(label, path, pathname) {
      if(pathname.startsWith(path)){
        return <span className="nav-link active" key={ label } ><Link to={path}>{label}</Link></span>
      }
      return <span className="nav-link" key={ label } ><Link to={path}>{label}</Link></span>
    }

}


Menu.propTypes = {
  match: PropTypes.object.isRequired,
  location: PropTypes.object.isRequired,
  history: PropTypes.object.isRequired,
};

Menu = withRouter(Menu)

export default Menu;
