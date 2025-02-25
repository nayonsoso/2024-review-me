import { useEffect } from 'react';

import Portal from '../Portal';

import * as S from './styles';

export type ToastPositionType = 'top' | 'bottom';

interface IconProps {
  src: string;
  alt: string;
}

interface ToastProps {
  icon?: IconProps;
  message: string;
  duration: number;
  position: ToastPositionType;
  handleOpenModal: (isOpen: boolean) => void;
  handleModalMessage?: (message: string) => void;
}

const Toast = ({ icon, message, duration, position, handleOpenModal, handleModalMessage }: ToastProps) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      handleOpenModal(false);
      if (handleModalMessage) handleModalMessage('');
    }, duration);

    return () => clearTimeout(timer);
  }, [handleOpenModal]);

  return (
    <Portal disableScroll={false}>
      <S.ToastModalContainer duration={duration} position={position}>
        {icon && <S.WarningIcon src={icon.src} alt={icon.alt} />}
        <S.ErrorMessage>{message}</S.ErrorMessage>
      </S.ToastModalContainer>
    </Portal>
  );
};

export default Toast;
