import { gql } from '../../graphqlClient';

export const IMAGE_FRAGMENT = gql`
  fragment ImageFragment on ImageResponse {
    id
    url
    altText
    uploadedAt
    publicId
  }
`; 