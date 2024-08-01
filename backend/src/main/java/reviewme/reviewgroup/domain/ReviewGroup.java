package reviewme.reviewgroup.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reviewme.review.domain.exception.InvalidProjectNameLengthException;
import reviewme.review.domain.exception.InvalidRevieweeNameLengthException;

@Entity
@Table(name = "review_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewGroup {

    private static final int MAX_REVIEWEE_LENGTH = 50;
    private static final int MAX_PROJECT_NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewee", nullable = false)
    private String reviewee;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "review_request_code", nullable = false)
    private String reviewRequestCode;

    @Column(name = "group_access_code", nullable = false)
    private String groupAccessCode;

    public ReviewGroup(String reviewee, String projectName, String reviewRequestCode, String groupAccessCode) {
        validateRevieweeLength(reviewee);
        validateProjectNameLength(projectName);
        this.reviewee = reviewee;
        this.projectName = projectName;
        this.reviewRequestCode = reviewRequestCode;
        this.groupAccessCode = groupAccessCode;
    }

    private void validateRevieweeLength(String reviewee) {
        if (reviewee.isBlank() || reviewee.length() > MAX_REVIEWEE_LENGTH) {
            throw new InvalidRevieweeNameLengthException(MAX_REVIEWEE_LENGTH);
        }
    }

    private void validateProjectNameLength(String projectName) {
        if (projectName.isBlank() || projectName.length() > MAX_PROJECT_NAME_LENGTH) {
            throw new InvalidProjectNameLengthException(MAX_PROJECT_NAME_LENGTH);
        }
    }
}