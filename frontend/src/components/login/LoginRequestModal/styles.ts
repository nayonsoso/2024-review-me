import styled from '@emotion/styled';

export const LoginRequestModal = styled.div`
  display: flex;
  flex-direction: column;
  height: 9.5rem;
`;

export const LoginRequestLabel = styled.p`
  margin-bottom: 1rem;
  font-size: 1.4rem;
`;

export const ErrorMessage = styled.p`
  margin-top: 0.6rem;
  font-size: 1.2rem;
  color: ${({ theme }) => theme.colors.red};
`;
