import CopyIcon from '@/assets/copy.svg';

import * as S from './styles';

interface CopyTextButtonProps {
  targetText: string;
  alt: string;
}

const CopyTextButton = ({ targetText, alt }: CopyTextButtonProps) => {
  const handleCopyTextButtonClick = async (event: React.MouseEvent) => {
    event.stopPropagation();

    try {
      await navigator.clipboard.writeText(targetText);
      alert('텍스트가 클립보드에 복사되었어요');
    } catch (error) {
      if (error instanceof Error) throw new Error('텍스트 복사에 실패했어요');
    }
  };

  return (
    <S.CopyTextButton onClick={handleCopyTextButtonClick}>
      <img src={CopyIcon} alt={alt} />
    </S.CopyTextButton>
  );
};

export default CopyTextButton;
