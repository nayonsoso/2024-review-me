package reviewme.auth.domain;

import lombok.Getter;
import reviewme.auth.domain.excpetion.PrincipalNotExistsException;
import reviewme.member.domain.Member;
import reviewme.reviewgroup.domain.ReviewGroup;

@Getter
public class 그냥_객체_Principal {

    private final Object principal;
    private final Class<?> type;

    public 그냥_객체_Principal(Object principal, Class<?> type) {
        this.principal = principal;
        this.type = type;
    }

    public boolean isMemberPrincipal() {
        return type.equals(Member.class);
    }

    public boolean isReviewGroupPrincipal() {
        return type.equals(ReviewGroup.class);
    }

    public Member getMember() {
        try {
            return (Member) principal;
        } catch (Exception e) {
            throw new PrincipalNotExistsException(Member.class);
        }
    }

    public ReviewGroup getReviewGroup() {
        try {
            return (ReviewGroup) principal;
        } catch (Exception e) {
            throw new PrincipalNotExistsException(ReviewGroup.class);
        }
    }
}
