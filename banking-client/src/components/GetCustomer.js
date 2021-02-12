import React, { Component, useState } from 'react';
import ApiService from '../services/ApiService';
import { withRouter } from 'react-router-dom'

const api = new ApiService();

class GetCustomer extends Component {

  constructor(props) {
    super(props);
    this.state = {
      customerId: '',
      customer: null,
      isLoading: true
    };
    this.getCustomer = this.getCustomer.bind(this);
  }

  getCustomer = async (e) => {
    e.preventDefault();
    this.setState({ isLoading: true });
    let customerId = this.state.customerId;
    api.getCustomerById(customerId)
      .then(response => response.json())
      .then(json => this.setState({ customer: json, isLoading: false }));
  }

  onChange = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  }

  render() {
    const customer = this.state.customer;
    const isLoading = this.state.isLoading;

    return (
      <div>
        <div>
          <h2 className="text-center">Get Customer by ID</h2>
          <form>
            <div className="form-group">
              <label>Customer ID:</label>
              <input type="text" placeholder="customerId" name="customerId" className="form-control" value={this.state.customerId} onChange={this.onChange} />
            </div>

            <button className="btn btn-success" onClick={this.getCustomer}>Get Customer</button>
          </form>
        </div>
        {!isLoading ? (
          <div className="customer-container p-2 m-2 d-flex flex-column">
            <h3>Customer ID: {customer.id}</h3>
            <div className="customer-body">
              <div className="subtitle-container">
                <h3>First Name: {customer.firstName}</h3>
                <h3>Last Name: {customer.surname}</h3>
                <h3>
                  Accounts:
        {customer.accounts.map(account => (
                  <div key={account.id}>
                    <h5>ID: {account.id}</h5>
                    <h5>Type: {account.type}</h5>
                    <h5>Balance: {account.balance}</h5>
                    <h5>Date Created: {account.dateCreated}</h5>
                    <h5>Date Updated: {account.dateUpdated}</h5>
                    <h5>
                      Transactions: {account.transactions.map(transaction => (
                      <div key={transaction.id}>
                        <p>Amount: {transaction.amount}</p>
                        <p>Type: {transaction.type}</p>
                      </div>
                    ))}
                    </h5>
                  </div>
                ))}
                </h3>
              </div>
            </div>
          </div>
        ) : (<p></p>)}
      </div>
    );
  }
}

export default withRouter(GetCustomer);
