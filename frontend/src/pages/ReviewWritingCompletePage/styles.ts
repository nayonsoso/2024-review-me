import styled from '@emotion/styled';

import media from '@/utils/media';

export const Layout = styled.section`
  height: 70vh;
`;

export const Container = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  display: flex;
  flex-direction: column;
  gap: 3.5rem;
  align-items: center;
  justify-content: center;

  width: 100%;
`;

export const ReviewComplete = styled.div`
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: center;

  width: 100%;
`;

export const ImgWrapper = styled.div`
  width: 4rem;
  height: 4rem;

  ${media.xSmall} {
    width: 2.8rem;
    height: 2.8rem;
  }

  img {
    width: 100%;
    height: 100%;
  }
`;

export const Title = styled.p`
  font-size: 2.8rem;
  font-weight: bold;

  ${media.xSmall} {
    font-size: ${({ theme }) => theme.fontSize.mediumSmall};
  }
`;

export const HomeIcon = styled.img`
  width: 2rem;
  height: 2rem;

  ${media.xSmall} {
    width: 1.2rem;
    height: 1.2rem;
  }
`;

export const HomeText = styled.p`
  height: 1.6rem;
  margin-left: 0.5rem;

  ${media.xSmall} {
    height: 1.2rem;
    font-size: 1.2rem;
  }
`;
