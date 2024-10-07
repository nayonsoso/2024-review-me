package reviewme.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import reviewme.review.domain.TextAnswer;

public interface TextAnswerRepository extends JpaRepository<TextAnswer, Long> {
}
