import React from 'react';
import './App.css';
import { BrowserRouter as Router, NavLink, Route, Switch } from 'react-router-dom';
import CreateCurrentAccount from './components/CreateCurrentAccount';
import GetCustomer from './components/GetCustomer';

function App() {
  return (
    <div>
      <nav className="navbar navbar-expand navbar-dark bg-dark">
        <div className="navbar-nav mr-auto">
          <li className="nav-item">
            <NavLink to={"/create-account/current"} className="nav-link">
              Create a Current Account
              </NavLink>
          </li>
          <li className="nav-item">
            <NavLink to={"/get-customer"} className="nav-link">
              Get a Customer by ID
              </NavLink>
          </li>
        </div>
      </nav>

      <div className="container">
        <Router>
          <div className="col-md-6">
            <h1 className="text-center" style={style}>React Banking Application</h1>
            <Switch>
              <Route path="/create-account/current" component={CreateCurrentAccount} />
              <Route path="/get-customer" component={GetCustomer} />
            </Switch>
          </div>
        </Router>
      </div>
    </div>
  );
}

const style = {
  color: 'red',
  margin: '10px'
}

export default App;
