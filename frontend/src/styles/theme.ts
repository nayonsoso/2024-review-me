import { Theme } from '@emotion/react';
import { CSSProperties } from 'react';

import { ThemeProperty } from '../types';

export const formWidth = '70rem';

export const scrollbarWidth = {
  basic: '1.2rem',
  small: '0.5rem',
};

export const breadcrumbSize = {
  paddingLeft: '2rem',
};

export const confirmModalSize = {
  maxWidth: '90vw',
  padding: '3.2rem',
};

export const contentModalSize = {
  maxWidth: '90vw',
  padding: '3.2rem',
  smallPadding: '2rem',
};

export const writtenReviewLayoutSize = {
  largeMinWidth: '45rem',
  largeMaxHeight: '90rem',
  largeMaxWidth: '45rem',
};

export const componentHeight = {
  footer: '6rem',
  topbar: '7rem',
  breadCrumb: '4.3rem',
  navigationTab: '4rem',
};

export const breakpoint = {
  xxSmall: 320,
  xSmall: 430,
  small: 768,
  medium: 1024,
  large: 1025,
};

// NOTE: 1rem = 10px
export const fontSize: ThemeProperty<CSSProperties['fontSize']> = {
  small: '1.4rem',
  basic: '1.6rem',
  mediumSmall: '2.0rem',
  medium: '2.4rem',
  large: '3.2rem',
  h2: '4.8rem',
};
export const borderRadius: ThemeProperty<CSSProperties['borderRadius']> = {
  basic: '0.8rem',
};

export const fontWeight: ThemeProperty<CSSProperties['fontWeight']> = {
  normal: '400',
  medium: '500', // NOTE: 기본 weight
  semibold: '600',
  bold: '700',
  bolder: '800',
};

export const colors: ThemeProperty<CSSProperties['color']> = {
  primary: '#7361DF',
  primaryHover: '#9082E6',
  lightPurple: '#E6E3F6',
  palePurple: '#F5F4FF',
  highlight: '#E5DAFF',
  black: '#1E2022',
  white: '#FFFFFF',
  lightGray: '#F1F2F4',
  placeholder: '#D3D3D3',
  gray: '#7F7F7F',
  sidebarBackground: `rgba(0, 0, 0, 0.25)`,
  disabled: '#D8D8D8',
  disabledText: '#7F7F7F',
  emptyContentText: '#CBD6DE',
  red: '#FF0000',
};

export const zIndex: ThemeProperty<CSSProperties['zIndex']> = {
  main: 1,
  dropdown: 997,
  topbar: 998,
  modal: 999,
};

export const breakpoints = {
  xxSmall: 320,
  xSmall: 425,
  small: 768,
  medium: 1024,
  large: 1025,
};

const theme: Theme = {
  fontSize,
  fontWeight,
  zIndex,
  colors,
  scrollbarWidth,
  breakpoint,
  borderRadius,
  formWidth,
  componentHeight,
  confirmModalSize,
  contentModalSize,
  breadcrumbSize,
  writtenReviewLayoutSize,
};

export default theme;
