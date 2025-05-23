import { useEffect, useState } from 'react';

import { AlertModal, CopyTextButton, Checkbox } from '@/components';
import { useGetUserProfile } from '@/hooks/oAuth';

import * as S from './styles';
interface ReviewZoneURLModalProps {
  reviewZoneURL: string;
  closeModal: () => void;
}

const ReviewZoneURLModal = ({ reviewZoneURL, closeModal }: ReviewZoneURLModalProps) => {
  const [isChecked, setIsChecked] = useState(false);
  const { isUserLoggedIn } = useGetUserProfile();

  const handleCheckboxClick = () => {
    setIsChecked(!isChecked);
  };

  const handleCloseButtonClick = () => {
    if (isChecked) closeModal();
  };

  const handleBeforeUnload = (event: BeforeUnloadEvent) => {
    event.preventDefault();
  };

  const handlePopState = () => {
    if (window.confirm('링크가 사라질 수 있어요. 계속하시겠어요?')) {
      closeModal();
    } else {
      // 히스토리를 다시 추가해 뒤로가기 취소
      window.history.pushState(null, '', window.location.href);
    }
  };

  useEffect(() => {
    window.addEventListener('beforeunload', handleBeforeUnload); // 새로고침 방지
    window.addEventListener('popstate', handlePopState); // 뒤로가기 이벤트(history change) 감지

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
      window.removeEventListener('popstate', handlePopState);
    };
  }, []);

  return (
    <AlertModal
      closeButton={{ content: '닫기', type: isChecked ? 'primary' : 'disabled', handleClick: handleCloseButtonClick }}
      handleClose={null}
      isClosableOnBackground={false}
    >
      <S.ReviewZoneURLModal>
        <S.ModalTitle>아래 요청 URL을 확인해주세요</S.ModalTitle>
        <S.ReviewZoneURLModalItem>
          <S.RequestURLContainer>
            <S.DataName>리뷰 요청 URL</S.DataName>
            <CopyTextButton targetText={reviewZoneURL} alt="리뷰 공간 페이지 링크 복사하기" />
          </S.RequestURLContainer>
          <S.Data>{reviewZoneURL}</S.Data>
        </S.ReviewZoneURLModalItem>
        <S.CheckContainer>
          <Checkbox
            id="is-confirmed-checkbox"
            isChecked={isChecked}
            handleChange={handleCheckboxClick}
            $style={{
              width: '2.7rem',
              height: '2.7rem',
            }}
          />
          <S.CheckMessage>
            {isUserLoggedIn ? '링크는 프로젝트 목록에서 확인할 수 있어요!' : '링크를 저장해두었어요!'}
          </S.CheckMessage>
        </S.CheckContainer>
        {!isUserLoggedIn && <S.WarningMessage>* 창이 닫히면 링크를 다시 확인할 수 없어요!</S.WarningMessage>}
      </S.ReviewZoneURLModal>
    </AlertModal>
  );
};

export default ReviewZoneURLModal;
