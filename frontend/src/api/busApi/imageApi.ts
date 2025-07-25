/**
 * API functions for handling image uploads and management.
 */

import axios from 'axios';
import { graphqlRequest } from '../graphqlClient';

// Use the base URL from the shared axios instance if possible,
// otherwise default to localhost. This assumes your REST API and GraphQL
// are on the same server.  
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const UPLOAD_ENDPOINT = `${API_BASE_URL}/api/upload`;

const CREATE_IMAGE_MUTATION = `
  mutation CreateImage($input: CreateImageInput!) {
    createImage(input: $input) {
      id
      url
      publicId
      altText
    }
  }
`;

/**
 * Uploads an image file to the server via REST endpoint.
 * @param file The image file to upload.
 * @param onProgress A callback function to track upload progress.
 * @returns The data returned from the server, typically { url, publicId }.
 */
export async function uploadImage(file: File, onProgress?: (progress: number) => void): Promise<{ url: string; publicId: string }> {
  const formData = new FormData();
  formData.append('file', file);

  const token = localStorage.getItem('accessToken');

  try {
    const response = await axios.post<{ url: string; publicId: string }>(UPLOAD_ENDPOINT, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        ...(token && { Authorization: `Bearer ${token}` }),
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(percentCompleted);
        }
      },
    });
    
    // Kiểm tra và return đúng structure
    const data = (response.data as any).data || response.data;
    
    if (!data || !data.url || !data.publicId) {
      console.error('Invalid upload response structure:', data);
      throw new Error('Upload response missing required fields (url, publicId)');
    }
    
    return {
      url: data.url,
      publicId: data.publicId
    };
  } catch (error) {
    console.error('Upload error:', error);
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data?.error || 'Upload failed');
    }
    throw error;
  }
}

/**
 * Creates an image record in the database via GraphQL mutation
 * after a successful upload.
 * @param input The data for the new image record.
 * @returns The newly created Image object from GraphQL.
 */
export async function createImageRecord(input: { url: string; publicId: string; altText?: string }): Promise<{ id: string; url: string }> {
  try {
    // Validation
    if (!input || !input.url || !input.publicId) {
      console.error('Invalid input for createImageRecord:', input);
      throw new Error('Input must have url and publicId fields');
    }
    
    const response = await graphqlRequest({
      query: CREATE_IMAGE_MUTATION,
      variables: { input }
    });
    
    return response.data.createImage;
  } catch (error) {
    console.error('createImageRecord error:', error);
    throw new Error('Could not save image record to database.');
  }
}

export const ImageAPI = {
  uploadImage,
  createImageRecord,
}; 