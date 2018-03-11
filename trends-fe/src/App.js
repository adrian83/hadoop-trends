import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import {
  BrowserRouter as Router,
  Route,
  Link
} from 'react-router-dom';

import Favorites from './Favorites';
import Hashtags from './Hashtags';
import Home from './Home';

class App extends Component {
  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
          <Router>
            <div className="container">
              <Route exact path="/" component={Home} />
              <Route path="/favorites" component={Favorites} />
              <Route path="/hashtags" component={Hashtags} />
            </div>
          </Router>

      </div>
    );
  }
}

export default App;
