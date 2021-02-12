import React, { Component } from 'react';
import ApiService from '../services/ApiService';
import { withRouter } from 'react-router-dom'

const api = new ApiService();

class CreateCurrentAccount extends Component {

  constructor(props) {
    super(props);
    this.state = {
      customerId: '',
      initialCredit: '',
      isSuccess: false
    };
    this.createCurrentAccount = this.createCurrentAccount.bind(this);
  }

  createCurrentAccount = async (e) => {
    e.preventDefault();
    let account = { customerId: this.state.customerId, initialCredit: this.state.initialCredit };
    api.createCurrentAccount(account)
      .then(res => {
        if (res.ok) {
          this.setState({ isSuccess: true });
        }
      });
  }

  onChange = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  }

  render() {
    const isSuccess = this.state.isSuccess;

    return (
      <div>
        <h2 className="text-center">Create Current Account</h2>
        <form>
          <div className="form-group">
            <label>Customer ID:</label>
            <input type="text" placeholder="customerId" name="customerId" className="form-control" value={this.state.customerId} onChange={this.onChange} />
          </div>

          <div className="form-group">
            <label>Initial Credit:</label>
            <input type="number" placeholder="initialCredit" name="initialCredit" className="form-control" value={this.state.initialCredit} onChange={this.onChange} />
          </div>

          <button className="btn btn-success" onClick={this.createCurrentAccount}>Create</button>
        </form>

        <div>
          {isSuccess ? (<h3>Account Created Successfully</h3>) : (<p></p>)}
        </div>
      </div>
    );
  }
}

export default withRouter(CreateCurrentAccount);
