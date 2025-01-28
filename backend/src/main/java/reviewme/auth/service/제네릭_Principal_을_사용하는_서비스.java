package reviewme.auth.service;

import reviewme.auth.domain.제네릭_Principal;
import reviewme.member.domain.Member;
import reviewme.reviewgroup.domain.ReviewGroup;

public class 제네릭_Principal_을_사용하는_서비스 {

    public void 회원이_사용하는_함수(제네릭_Principal<Member> principal) {
        Member member = principal.getPrincipal();
        throw new IllegalArgumentException("회원만 사용 가능한 함수입니다.");
    }

    public void 회원과_비회원_모두_사용_가능한_함수(제네릭_Principal<?> principal) {
        if (principal.getType() == Member.class) {
            Member member = (Member) principal.getPrincipal();
            // 회원 로직
        }

        if (principal.getType() == ReviewGroup.class) {
            ReviewGroup reviewGroup = (ReviewGroup) principal.getPrincipal();
            // 비회원 로직
        }
    }
}
