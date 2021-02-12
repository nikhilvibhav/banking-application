class ApiService {

  headers = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  }

  BASE_URL = 'http://localhost:8080/api/bank/v1'

  async getCustomerById(id) {
    return await fetch(`${this.BASE_URL}/customer/${id}`, {
      method: 'GET',
      headers: this.headers
    });
  }

  async createCurrentAccount(request) {
    return await fetch(`${this.BASE_URL}/account/current`, {
      method: 'POST',
      headers: this.headers,
      body: JSON.stringify(request)
    });
  }
}

export default ApiService;