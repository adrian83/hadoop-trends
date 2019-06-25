import React, { Component } from 'react';
import './App.css';
import {
  BrowserRouter as Router,
  Route,
  Redirect,
  Switch
} from 'react-router-dom';

import Home from './Home';
import Favorites from './Favorites';
import Hashtags from './Hashtags';
import Retwitts from './Retwitts';
import Replies from './Replies';
import Menu from './Menu';




class App extends Component {
  render() {

    var hashtagsUrl = process.env.REACT_APP_BE_HOST + "/sse/hashtags"
    var favoritesUrl = process.env.REACT_APP_BE_HOST + "/sse/favorites"
    var retwittsUrl = process.env.REACT_APP_BE_HOST + "/sse/retwitts"
    var repliesUrl = process.env.REACT_APP_BE_HOST + "/sse/replies"

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
                <Route path="/favorites" component={() => <Favorites url={favoritesUrl} />} />
                <Route path="/hashtags" component={() => <Hashtags url={hashtagsUrl} />} />
                <Route path="/retwitts" component={() => <Retwitts url={retwittsUrl} />} />
                <Route path="/replies" component={() => <Replies url={repliesUrl} />} />
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
