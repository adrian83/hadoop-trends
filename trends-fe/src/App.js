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
import Menu from './Menu';




class App extends Component {
  render() {
    return (
      <Router>
      <div className="cover-container d-flex h-100 p-3 mx-auto flex-column">

      <header className="masthead mb-auto">
        <Menu/>
      </header>



      <main role="main" className="inner cover">
      <div className="App">

            <Switch>
              <Route exact path="/" component={Home} />
              <Route path="/favorites" component={() => <Favorites url="http://localhost:8080/sse/favorites" />} />
              <Route path="/hashtags" component={() => <Hashtags url="http://localhost:8080/sse/hashtags" />} />
              <Route path="/retwitts" component={() => <Retwitts url="http://localhost:8080/sse/retwitts" />} />
              <Route path="/replies" component={() => <Replies url="http://localhost:8080/sse/replies" />} />
              <Redirect to="/" />
            </Switch>

      </div>
      </main>

      <footer className="mastfoot mt-auto">
        <div className="inner">
          <p>Cover template for <a href="https://getbootstrap.com/">Bootstrap</a>, by <a href="https://twitter.com/mdo">@mdo</a>.</p>
        </div>
      </footer>

          </div>
          </Router>
    );
  }
}

export default App;
