import React, { Component } from 'react';
import './App.css';
import { NavLink, Route, Switch } from 'react-router-dom';
import CreateCurrentAccount from './components/CreateCurrentAccount';
import GetCustomer from './components/GetCustomer';

class App extends Component {
  render() {
    return (
      <div>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <div className="navbar-nav mr-auto">
            <li>
              <NavLink to="/" className="nav-link">
                Home
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink to="/create-account/current" className="nav-link">
                Create a Current Account
                </NavLink>
            </li>
            <li className="nav-item">
              <NavLink to="/get-customer" className="nav-link">
                Get a Customer by ID
                </NavLink>
            </li>
          </div>
        </nav>

        <div className="container">
            <div className="col-md-6">
              <h1 className="text-center" style={style}>React Banking Application</h1>
              <Switch>
                <Route path="/create-account/current" component={CreateCurrentAccount} />
                <Route path="/get-customer" component={GetCustomer} />
              </Switch>
            </div>
        </div>
      </div>
    );
  }
}

const style = {
  color: 'red',
  margin: '10px'
}

export default App;
