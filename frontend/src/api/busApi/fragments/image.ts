import { gql } from '@apollo/client/core';

export const IMAGE_FRAGMENT = gql`
  fragment ImageFragment on ImageResponse {
    id
    url
    altText
    uploadedAt
    publicId
  }
`; 