package reviewme.fixture;

import reviewme.template.domain.OptionGroup;

public class OptionGroupFixture {

    public static OptionGroup 선택지_그룹(long questionId) {
        return new OptionGroup(questionId, 1, 2);
    }
}
