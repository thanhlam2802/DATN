import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';


const api = axios.create({
    baseURL: API_BASE_URL
  });


  export const markOrderSuccess = (orderId, transactionId) =>
    api.put(
      `/orders/success-order/${orderId}`,
      transactionId,          
      { headers: { 'Content-Type': 'text/plain' } } 
    );

  export const addItemToCart = (orderId, request) =>  
    api.post(`/cart/${orderId}/items`, request );