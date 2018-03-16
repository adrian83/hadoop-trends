import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import {
  BrowserRouter as Router,
  Route,
  Redirect,
  Switch,
  Link
} from 'react-router-dom';

import Home from './Home';
import Favorites from './Favorites';
import Hashtags from './Hashtags';
import Retwitts from './Retwitts';
import Replies from './Replies';




class App extends Component {
  render() {
    return (
      <div className="App">
          <Router>
            <Switch>
              <Route exact path="/" component={Home} />
              <Route path="/favorites" component={Favorites} />
              <Route path="/hashtags" component={Hashtags} />
              <Route path="/retwitts" component={Retwitts} />
              <Route path="/replies" component={Replies} />
              <Redirect to="/" />
            </Switch>
          </Router>
      </div>
    );
  }
}

export default App;
