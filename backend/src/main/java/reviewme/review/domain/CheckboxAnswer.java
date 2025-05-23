package reviewme.review.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reviewme.review.domain.exception.QuestionNotAnsweredException;

@Entity
@Table(name = "checkbox_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Getter
public class CheckboxAnswer extends Answer {

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "checkbox_answer_id", nullable = false, updatable = false)
    private List<CheckboxAnswerSelectedOption> selectedOptionIds;

    public CheckboxAnswer(long questionId, List<Long> selectedOptionIds) {
        validateSelectedOptionIds(questionId, selectedOptionIds);
        this.questionId = questionId;
        this.selectedOptionIds = selectedOptionIds.stream()
                .map(CheckboxAnswerSelectedOption::new)
                .toList();
    }

    private void validateSelectedOptionIds(long questionId, List<Long> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            throw new QuestionNotAnsweredException(questionId);
        }
    }
}
