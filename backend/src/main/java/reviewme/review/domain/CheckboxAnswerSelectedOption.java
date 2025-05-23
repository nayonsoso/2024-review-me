package reviewme.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "checkbox_answer_selected_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class CheckboxAnswerSelectedOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checkbox_answer_id", nullable = false, insertable = false, updatable = false)
    private long checkboxAnswerId;

    @Column(name = "selected_option_id", nullable = false)
    private long selectedOptionId;

    public CheckboxAnswerSelectedOption(long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
