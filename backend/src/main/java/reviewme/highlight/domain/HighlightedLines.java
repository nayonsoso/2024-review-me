package reviewme.highlight.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.Getter;
import reviewme.highlight.domain.exception.InvalidHighlightLineIndexException;
import reviewme.highlight.domain.exception.NegativeHighlightLineIndexException;

@Getter
public class HighlightedLines {

    public static final String LINE_SEPARATOR = "\n";

    private final List<HighlightedLine> lines;

    public HighlightedLines(String content) {
        this.lines = Arrays.stream(content.split(LINE_SEPARATOR))
                .map(HighlightedLine::new)
                .toList();
    }

    public void addRange(int lineIndex, int startIndex, int endIndex) {
        validateNonNegativeLineIndexNumber(lineIndex);
        validateLineIndexRange(lineIndex);
        HighlightedLine line = lines.get(lineIndex);
        line.addRange(startIndex, endIndex);
    }

    private void validateNonNegativeLineIndexNumber(int lineIndex) {
        if (lineIndex < 0) {
            throw new NegativeHighlightLineIndexException(lineIndex);
        }
    }

    private void validateLineIndexRange(int lineIndex) {
        if (lineIndex >= lines.size()) {
            throw new InvalidHighlightLineIndexException(lineIndex, lines.size());
        }
    }

    public List<Highlight> toHighlights(long answerId) {
        return IntStream.range(0, lines.size())
                .mapToObj(lineIndex -> lines.get(lineIndex).getRanges().stream()
                        .map(range -> new Highlight(answerId, lineIndex, range)))
                .flatMap(Function.identity())
                .toList();
    }
}
