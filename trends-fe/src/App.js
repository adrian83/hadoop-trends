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
      <div class="cover-container d-flex h-100 p-3 mx-auto flex-column">

      <header class="masthead mb-auto">
        <div class="inner">
          <h3 class="masthead-brand">Trends</h3>
          <nav class="nav nav-masthead justify-content-center">
            <a class="nav-link active" href="/hashtags">Hashtags</a>
            <a class="nav-link" href="/favorites">Favorites</a>
            <a class="nav-link" href="/replies">Replies</a>
            <a class="nav-link" href="/retwitts">Retwitts</a>
          </nav>
        </div>
      </header>


      <main role="main" className="inner cover">
      <div className="App">
          <Router>
            <Switch>
              <Route exact path="/" component={Home} />
              <Route path="/favorites" component={() => <Favorites url="http://localhost:8080/sse/favorites" />} />
              <Route path="/hashtags" component={() => <Hashtags url="http://localhost:8080/sse/hashtags" />} />
              <Route path="/retwitts" component={() => <Retwitts url="http://localhost:8080/sse/retwitts" />} />
              <Route path="/replies" component={() => <Replies url="http://localhost:8080/sse/replies" />} />
              <Redirect to="/" />
            </Switch>
          </Router>
      </div>
      </main>

      <footer class="mastfoot mt-auto">
        <div class="inner">
          <p>Cover template for <a href="https://getbootstrap.com/">Bootstrap</a>, by <a href="https://twitter.com/mdo">@mdo</a>.</p>
        </div>
      </footer>

          </div>
    );
  }
}

export default App;
