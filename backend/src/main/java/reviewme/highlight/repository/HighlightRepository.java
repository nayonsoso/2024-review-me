package reviewme.highlight.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import reviewme.highlight.domain.Highlight;

public interface HighlightRepository extends Repository<Highlight, Long>, HighlightJdbcRepository {

    Highlight save(Highlight highlight);

    boolean existsById(long id);

    @Modifying
    @Query("""
            DELETE FROM Highlight h
            WHERE h.answerId IN :answerIds
            """)
    void deleteAllByAnswerIds(Collection<Long> answerIds);

    @Query("""
            SELECT h
            FROM Highlight h
            WHERE h.answerId IN :answerIds
            ORDER BY h.lineIndex, h.highlightRange.startIndex ASC
            """)
    List<Highlight> findAllByAnswerIdsOrderedAsc(Collection<Long> answerIds);
}
