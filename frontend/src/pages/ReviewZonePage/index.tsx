import { useEffect } from 'react';
import { useNavigate } from 'react-router';
import { useRecoilState } from 'recoil';

import ReviewZoneIcon from '@/assets/reviewZone.svg';
import { Button, ImgWithSkeleton } from '@/components';
import { ROUTE } from '@/constants';
import { useGetReviewGroupData, useSearchParamAndQuery, useModals } from '@/hooks';
import { reviewRequestCodeAtom } from '@/recoil';
import { calculateParticle } from '@/utils';

import PasswordModal from './components/PasswordModal';
import * as S from './styles';

const MODAL_KEYS = {
  content: 'CONTENT_MODAL',
};
const BUTTON_SIZE = {
  width: '28rem',
  height: '8.5rem',
};
const IMG_HEIGHT = '15rem';

const ReviewZonePage = () => {
  const { isOpen, openModal, closeModal } = useModals();
  const [storedReviewRequestCode, setStoredReviewRequestCode] = useRecoilState(reviewRequestCodeAtom);

  const navigate = useNavigate();

  const { param: reviewRequestCode } = useSearchParamAndQuery({
    paramKey: 'reviewRequestCode',
  });

  if (!reviewRequestCode) throw new Error('유효하지 않은 리뷰 요청 코드예요');

  useEffect(() => {
    if (!storedReviewRequestCode && reviewRequestCode) {
      setStoredReviewRequestCode(reviewRequestCode);
    }
  }, []);

  const { data: reviewGroupData } = useGetReviewGroupData({ reviewRequestCode });

  const handleReviewWritingButtonClick = () => {
    navigate(`/${ROUTE.reviewWriting}/${reviewRequestCode}`);
  };

  const handleReviewListButtonClick = () => {
    openModal(MODAL_KEYS.content);
  };

  return (
    <S.ReviewZonePage>
      <ImgWithSkeleton imgWidth={BUTTON_SIZE.width} imgHeight={IMG_HEIGHT}>
        <S.ReviewZoneMainImg src={ReviewZoneIcon} alt="" $height={IMG_HEIGHT} />
      </ImgWithSkeleton>
      <S.ReviewGuideContainer>
        <S.ReviewGuide>{`${reviewGroupData.projectName}${calculateParticle({ target: reviewGroupData.projectName, particles: { withFinalConsonant: '을', withoutFinalConsonant: '를' } })} 함께한`}</S.ReviewGuide>
        <S.ReviewGuide>{`${reviewGroupData.revieweeName}의 리뷰 공간이에요`}</S.ReviewGuide>
      </S.ReviewGuideContainer>
      <S.ButtonContainer>
        <Button styleType="primary" type="button" onClick={handleReviewWritingButtonClick} style={BUTTON_SIZE}>
          <S.ButtonTextContainer>
            <S.ButtonText>리뷰 쓰기</S.ButtonText>
            <S.ButtonDescription>작성한 리뷰는 익명으로 제출돼요</S.ButtonDescription>
          </S.ButtonTextContainer>
        </Button>
        <Button styleType="secondary" type="button" onClick={handleReviewListButtonClick} style={BUTTON_SIZE}>
          <S.ButtonTextContainer>
            <S.ButtonText>리뷰 확인하기</S.ButtonText>
            <S.ButtonDescription>비밀번호로 내가 받은 리뷰를 확인할 수 있어요</S.ButtonDescription>
          </S.ButtonTextContainer>
        </Button>
      </S.ButtonContainer>
      {isOpen(MODAL_KEYS.content) && (
        <PasswordModal reviewRequestCode={reviewRequestCode} closeModal={() => closeModal(MODAL_KEYS.content)} />
      )}
    </S.ReviewZonePage>
  );
};

export default ReviewZonePage;
