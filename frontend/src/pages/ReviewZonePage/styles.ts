import styled from '@emotion/styled';

import media from '@/utils/media';

export const ReviewZonePage = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

export const ReviewZoneMainImg = styled.img<{ $height: string }>`
  width: 43rem;
  height: ${(props) => props.$height};
`;

export const ReviewGuideContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  margin-top: 3rem;
  padding-left: 0.2rem;
`;

export const ReviewGuide = styled.p`
  font-size: 2.2rem;
  font-weight: ${({ theme }) => theme.fontWeight.bold};
  overflow-wrap: break-word;

  ${media.small} {
    font-size: 1.8rem;
  }
`;

export const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3rem;
  margin-top: 1.5rem;

  ${media.xSmall} {
    gap: 2rem;
  }
`;

export const ButtonTextContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  ${media.xSmall} {
    gap: 0.5rem;
  }
`;

export const ButtonText = styled.p`
  font-size: 2rem;
  font-weight: ${({ theme }) => theme.fontWeight.bold};

  ${media.small} {
    font-size: 1.8rem;
  }
`;

export const ButtonDescription = styled.p`
  font-size: 1.3rem;

  ${media.small} {
    font-size: 1.1rem;
  }
`;
