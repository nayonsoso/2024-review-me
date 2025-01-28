package reviewme.auth.domain.excpetion;

import lombok.extern.slf4j.Slf4j;
import reviewme.global.exception.UnauthorizedException;

@Slf4j
public class PrincipalNotExistsException extends UnauthorizedException {

    public PrincipalNotExistsException() {
        super("인증 정보가 존재하지 않아요.");
        log.info("principal not exists");
    }

    public <T> PrincipalNotExistsException(T principal) {
        super("인증 정보가 존재하지 않아요.");
        log.info("principal not exists, targetPrincipal: {}", principal.getClass().getSimpleName());
    }
}
