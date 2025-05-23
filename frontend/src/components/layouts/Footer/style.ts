import styled from '@emotion/styled';

import media from '@/utils/media';

export const Footer = styled.footer`
  position: absolute;
  bottom: 0;
  left: 0;

  display: flex;
  gap: 3.2rem;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: ${({ theme }) => theme.componentHeight.footer};
  padding: 2rem 1rem;

  font-size: ${({ theme }) => theme.fontSize.small};
  color: ${({ theme }) => theme.colors.gray};

  background-color: ${({ theme }) => theme.colors.white};

  border-top: 0.2rem solid ${({ theme }) => theme.colors.lightGray};

  ${media.xSmall} {
    flex-direction: column;
    gap: 0.2rem;
    font-size: 1.2rem;
  }
`;

export const Link = styled.a`
  &,
  &:visited,
  &:hover,
  &:focus,
  &:active {
    color: inherit;
    text-decoration: none;
  }
`;
