package java.util.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IntStream {
  static Stream<Integer> range(int startInclusive, int endExclusive) {
    if (startInclusive >= endExclusive) {
      return Collections.<Integer>emptyList().stream();
    } else {
      List<Integer> result = new ArrayList<>();
      for (; startInclusive < endExclusive; ++startInclusive) {
        result.add(startInclusive);
      }
      return result.stream();
    }
  }
}
