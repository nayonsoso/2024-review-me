export const EDITOR_LINE_CLASS_NAME = 'editor__line';
import { HighlightArea } from '@/components/highlight/components/HighlightEditor/hooks/useCheckHighlight';
export const EDITOR_ANSWER_CLASS_NAME = 'editor__answer';
export const HIGHLIGHT_MENU_CLASS_NAME = 'editor__menu-highlight';
export const HIGHLIGHT_SPAN_CLASS_NAME = 'highlighted';
export const SYNTAX_BASIC_CLASS_NAME = 'syntax';
// 버튼 관련
export const HIGHLIGHT_MENU_STYLE_SIZE = {
  height: 30,
  shadow: 10,
};
export const HIGHLIGHT_BUTTON_WIDTH = 42;
export const GAP_WIDTH_SELECTION_AND_HIGHLIGHT_BUTTON = 5;

export const HIGHLIGHT_MENU_WIDTH: { [key in HighlightArea | 'longPress']: number } = (() => {
  return {
    partial: HIGHLIGHT_BUTTON_WIDTH * 2,
    none: HIGHLIGHT_BUTTON_WIDTH,
    full: HIGHLIGHT_BUTTON_WIDTH,
    longPress: HIGHLIGHT_BUTTON_WIDTH,
  };
})();
