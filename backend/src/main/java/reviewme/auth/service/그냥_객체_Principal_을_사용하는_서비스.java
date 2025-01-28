package reviewme.auth.service;

import reviewme.auth.domain.그냥_객체_Principal;
import reviewme.member.domain.Member;
import reviewme.reviewgroup.domain.ReviewGroup;

public class 그냥_객체_Principal_을_사용하는_서비스 {

    public void 회원이_사용하는_함수(그냥_객체_Principal principal) {
        if (principal.isMemberPrincipal()) {
            Member member = principal.getMember();
            // 회원 로직
        }
        throw new IllegalArgumentException("회원만 사용 가능한 함수입니다.");
    }

    public void 회원과_비회원_모두_사용_가능한_함수(그냥_객체_Principal principal) {
        if (principal.isMemberPrincipal()) {
            Member member = principal.getMember();
            // 회원 로직
        }

        if (principal.isReviewGroupPrincipal()) {
            ReviewGroup reviewGroup = principal.getReviewGroup();
            // 비회원 로직
        }
    }
}
