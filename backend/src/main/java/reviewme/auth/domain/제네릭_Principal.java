package reviewme.auth.domain;

import lombok.Getter;

@Getter
public class 제네릭_Principal<T> {

    private final T principal;
    private final Class<T> type;

    public 제네릭_Principal(T principal, Class<T> type) {
        this.principal = principal;
        this.type = type;
    }
}
