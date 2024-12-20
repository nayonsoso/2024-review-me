import { useLayoutEffect, useRef, useState } from 'react';

import useStepList from '@/pages/ReviewWritingPage/progressBar/hooks/useStepList';
import { Direction } from '@/pages/ReviewWritingPage/types';

import * as S from './styles';

interface MobileProgressBarProps {
  currentCardIndex: number;
  handleCurrentCardIndex: (direction: Direction) => void;
}

const MobileProgressBar = ({ currentCardIndex, handleCurrentCardIndex }: MobileProgressBarProps) => {
  const { stepList } = useStepList({ currentCardIndex });

  const progressBarRef = useRef<HTMLDivElement>(null);
  const stepRefs = useRef<HTMLDivElement[]>([]);
  const animationFrameId = useRef<number | null>(null);

  const [currentCardIndexDescription, setCurrentCardIndexDescription] = useState('');

  useLayoutEffect(() => {
    if (!progressBarRef.current || !stepRefs.current[currentCardIndex]) return;

    if (animationFrameId.current) {
      cancelAnimationFrame(animationFrameId.current);
    }

    const scrollProgressBar = () => {
      setTimeout(() => {
        stepRefs.current[currentCardIndex].scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'center' });
      }, 250);
    };

    setCurrentCardIndexDescription(
      `현재 질문 카드는, 전체 ${stepList.length}개 카드 중, ${currentCardIndex + 1}번째 카드입니다. ${stepList[currentCardIndex].sectionName}`,
    );

    animationFrameId.current = requestAnimationFrame(scrollProgressBar);
  }, [currentCardIndex]);

  const handleClick = (index: number) => {
    const { isMovingAvailable } = stepList[index];
    if (isMovingAvailable) handleCurrentCardIndex(index);
  };

  return (
    <S.ProgressBarContainer>
      <S.ProgressBar ref={progressBarRef}>
        {stepList.map((step, index) => (
          <S.StepWrapper key={step.sectionId} ref={(el) => (stepRefs.current[index] = el!)}>
            {index > 0 ? <S.DottedLine /> : <S.EmptyBlock />}
            <S.StepButton
              $isDone={step.isDone}
              $isMovingAvailable={step.isMovingAvailable}
              $isCurrentStep={step.isCurrentStep}
              onClick={() => handleClick(index)}
              type="button"
              aria-label={step.isCurrentStep ? `현재 질문 카드는, ${step.sectionName}입니다` : `${step.sectionName}`}
              disabled={!step.isMovingAvailable}
            >
              {step.sectionName}
            </S.StepButton>
            {index < stepList.length - 1 ? <S.DottedLine /> : <S.EmptyBlock />}
          </S.StepWrapper>
        ))}
      </S.ProgressBar>
      <span className="sr-only" aria-live="polite">
        {currentCardIndexDescription}
      </span>
    </S.ProgressBarContainer>
  );
};

export default MobileProgressBar;
