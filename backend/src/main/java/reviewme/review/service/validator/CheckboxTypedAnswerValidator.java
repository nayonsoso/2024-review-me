package reviewme.review.service.validator;

import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reviewme.template.domain.OptionGroup;
import reviewme.template.domain.SelectionRange;
import reviewme.template.domain.OptionItem;
import reviewme.template.domain.Question;
import reviewme.template.repository.OptionGroupRepository;
import reviewme.template.repository.OptionItemRepository;
import reviewme.template.repository.QuestionRepository;
import reviewme.review.domain.Answer;
import reviewme.review.domain.CheckboxAnswerSelectedOption;
import reviewme.review.domain.CheckboxAnswer;
import reviewme.review.service.exception.CheckBoxAnswerIncludedNotProvidedOptionItemException;
import reviewme.review.service.exception.OptionGroupNotFoundByQuestionIdException;
import reviewme.review.service.exception.SelectedOptionItemCountOutOfRangeException;
import reviewme.review.service.exception.SubmittedQuestionNotFoundException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckboxTypedAnswerValidator implements TypedAnswerValidator {

    private final QuestionRepository questionRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final OptionItemRepository optionItemRepository;

    @Override
    public boolean supports(Class<? extends Answer> answerClass) {
        return CheckboxAnswer.class.isAssignableFrom(answerClass);
    }

    @Override
    public void validate(Answer answer) {
        CheckboxAnswer checkboxAnswer = (CheckboxAnswer) answer;
        Question question = questionRepository.findById(checkboxAnswer.getQuestionId())
                .orElseThrow(() -> new SubmittedQuestionNotFoundException(checkboxAnswer.getQuestionId()));

        OptionGroup optionGroup = optionGroupRepository.findByQuestionId(question.getId())
                .orElseThrow(() -> new OptionGroupNotFoundByQuestionIdException(question.getId()));

        validateOnlyIncludingProvidedOptionItem(checkboxAnswer, optionGroup);
        validateCheckedOptionItemCount(checkboxAnswer, optionGroup);
    }

    private void validateOnlyIncludingProvidedOptionItem(CheckboxAnswer checkboxAnswer, OptionGroup optionGroup) {
        List<Long> providedOptionItemIds = optionItemRepository.findAllByOptionGroupId(optionGroup.getId())
                .stream()
                .map(OptionItem::getId)
                .toList();
        List<Long> answeredOptionItemIds = extractAnsweredOptionItemIds(checkboxAnswer);

        if (!new HashSet<>(providedOptionItemIds).containsAll(answeredOptionItemIds)) {
            throw new CheckBoxAnswerIncludedNotProvidedOptionItemException(
                    checkboxAnswer.getQuestionId(), providedOptionItemIds, answeredOptionItemIds
            );
        }
    }

    private void validateCheckedOptionItemCount(CheckboxAnswer checkboxAnswer, OptionGroup optionGroup) {
        SelectionRange selectionRange = optionGroup.getSelectionRange();
        int answeredOptionItemCount = extractAnsweredOptionItemIds(checkboxAnswer).size();

        if (selectionRange.isOutOfRange(answeredOptionItemCount)) {
            throw new SelectedOptionItemCountOutOfRangeException(
                    checkboxAnswer.getQuestionId(),
                    answeredOptionItemCount,
                    selectionRange.getMinSelectionCount(),
                    selectionRange.getMaxSelectionCount()
            );
        }
    }

    private List<Long> extractAnsweredOptionItemIds(CheckboxAnswer checkboxAnswer) {
        return checkboxAnswer.getSelectedOptionIds()
                .stream()
                .map(CheckboxAnswerSelectedOption::getSelectedOptionId)
                .toList();
    }
}
