import axios from 'axios';
import { getBearerToken } from '@/services/TokenService';

const API_BASE_URL = 'http://localhost:8080/api/v1';


const api = axios.create({
    baseURL: API_BASE_URL
  });


  export const markOrderSuccess = (orderId, transactionId) =>
    api.put(
      `/orders/success-order/${orderId}`,
      transactionId,          
      { headers: { 
        'Content-Type': 'text/plain',
        'Authorization': getBearerToken()
      } } 
    );

  export const addItemToCart = (orderId, request) =>  
    api.post(`/cart/${orderId}/items`, request );