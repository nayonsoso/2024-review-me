import styled from '@emotion/styled';

import media from '@/utils/media';

export const Description = styled.section`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  margin: 0;

  border-radius: ${({ theme }) => theme.borderRadius.basic} ${({ theme }) => theme.borderRadius.basic} 0 0;

  ${media.xSmall} {
    padding: 0 1rem;
  }
`;

export const DescriptionSide = styled.div`
  display: flex;
  flex-wrap: wrap;
  width: 100%;
`;

export const ProjectInfoContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 100%;
`;

export const ProjectName = styled.p`
  margin-top: 0;
  font-size: ${({ theme }) => theme.fontSize.medium};
  font-weight: ${({ theme }) => theme.fontWeight.bold};

  ${media.xSmall} {
    font-size: ${({ theme }) => theme.fontSize.mediumSmall};
  }
`;

export const RevieweeNameAndDateContainer = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  font-size: ${({ theme }) => theme.fontSize.basic};

  ${media.small} {
    flex-direction: column;

    gap: 1rem;
  }

  ${media.xSmall} {
    font-size: ${({ theme }) => theme.fontSize.small};
  }
`;

export const RevieweeNameWrapper = styled.p`
  margin-top: 0;
`;

export const RevieweeName = styled.span`
  color: ${({ theme }) => theme.colors.primary};
`;
