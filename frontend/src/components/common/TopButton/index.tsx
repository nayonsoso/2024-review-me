import UpperArrowIcon from '@/assets/upperArrow.svg';
import useTopButton from '@/hooks/useTopButton';

import * as S from './style';

const TopButton = () => {
  const { showTopButton, scrollToTop } = useTopButton();

  if (!showTopButton) return null;

  return (
    <S.TopButton onClick={scrollToTop} type="button">
      <S.ArrowImage src={UpperArrowIcon} alt="위 화살표"></S.ArrowImage>
    </S.TopButton>
  );
};

export default TopButton;