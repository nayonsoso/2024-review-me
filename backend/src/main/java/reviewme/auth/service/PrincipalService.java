package reviewme.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reviewme.auth.domain.MemberPrincipal;
import reviewme.auth.domain.Principal;
import reviewme.auth.domain.ReviewGroupPrincipal;

@Service
@RequiredArgsConstructor
public class PrincipalService {

    public boolean canAccessReviewGroup(Principal principal, long reviewGroupId) {
        if (principal instanceof ReviewGroupPrincipal) {
            return ((ReviewGroupPrincipal) principal).getReviewGroup().getId() == reviewGroupId;
        }
        if (principal instanceof MemberPrincipal) {
            // todo: 리뷰 그룹과 사용자 연결
        }
        return false;
    }
}
