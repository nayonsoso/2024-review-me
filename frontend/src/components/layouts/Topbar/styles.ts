import styled from '@emotion/styled';

export const Layout = styled.section<{ $hideBorderBottom: boolean }>`
  z-index: ${({ theme }) => theme.zIndex.topbar};

  display: flex;
  justify-content: space-between;

  box-sizing: border-box;
  width: 100%;
  height: ${({ theme }) => theme.componentHeight.topbar};
  padding: 2rem 2.5rem;

  border-bottom: ${({ theme, $hideBorderBottom }) =>
    $hideBorderBottom ? 'none' : `0.1rem solid ${theme.colors.lightGray}`};
`;

export const Container = styled.div`
  display: flex;
  gap: 2rem;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  height: 100%;
`;

export const UserProfile = styled.img`
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
`;
