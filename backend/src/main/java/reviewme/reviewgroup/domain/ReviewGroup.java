package reviewme.reviewgroup.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reviewme.reviewgroup.domain.exception.InvalidProjectNameLengthException;
import reviewme.reviewgroup.domain.exception.InvalidRevieweeNameLengthException;

@Entity
@Table(name = "review_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class ReviewGroup {

    private static final int MIN_REVIEWEE_LENGTH = 1;
    private static final int MAX_REVIEWEE_LENGTH = 50;
    private static final int MIN_PROJECT_NAME_LENGTH = 1;
    private static final int MAX_PROJECT_NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = true)
    private Long memberId;

    @Column(name = "template_id", nullable = false)
    private long templateId;

    @Column(name = "reviewee", nullable = false)
    private String reviewee;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "review_request_code", nullable = false, unique = true)
    private String reviewRequestCode;

    @Embedded
    private GroupAccessCode groupAccessCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private ReviewGroup(@Nullable Long memberId, long templateId, String reviewee, String projectName,
                        String reviewRequestCode, String groupAccessCode) {
        validateRevieweeLength(reviewee);
        validateProjectNameLength(projectName);
        this.memberId = memberId;
        this.templateId = templateId;
        this.reviewee = reviewee;
        this.projectName = projectName;
        this.reviewRequestCode = reviewRequestCode;
        if (groupAccessCode != null) {
            this.groupAccessCode = new GroupAccessCode(groupAccessCode);
        } else {
            this.groupAccessCode = null;
        }
        this.createdAt = LocalDateTime.now();
    }

    public ReviewGroup(Long memberId, long templateId, String reviewee, String projectName, String reviewRequestCode) {
        this(memberId, templateId, reviewee, projectName, reviewRequestCode, null);
    }

    public ReviewGroup(long templateId, String reviewee, String projectName, String reviewRequestCode,
                       String groupAccessCode) {
        this(null, templateId, reviewee, projectName, reviewRequestCode, groupAccessCode);
    }

    private void validateRevieweeLength(String reviewee) {
        if (reviewee.length() < MIN_REVIEWEE_LENGTH || reviewee.length() > MAX_REVIEWEE_LENGTH) {
            throw new InvalidRevieweeNameLengthException(reviewee.length(), MIN_REVIEWEE_LENGTH, MAX_REVIEWEE_LENGTH);
        }
    }

    private void validateProjectNameLength(String projectName) {
        if (projectName.length() < MIN_PROJECT_NAME_LENGTH || projectName.length() > MAX_PROJECT_NAME_LENGTH) {
            throw new InvalidProjectNameLengthException(
                    projectName.length(), MIN_PROJECT_NAME_LENGTH, MAX_PROJECT_NAME_LENGTH
            );
        }
    }

    public boolean matchesGroupAccessCode(String code) {
        return groupAccessCode != null && groupAccessCode.matches(code);
    }

    public String getGroupAccessCode() {
        return groupAccessCode != null ? groupAccessCode.getCode() : null;
    }

    public boolean isMadeByMember(long memberId) {
        return this.memberId != null && this.memberId == memberId;
    }
}
