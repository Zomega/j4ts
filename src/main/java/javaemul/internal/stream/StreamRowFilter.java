package javaemul.internal.stream;

import java.util.function.Predicate;

public class StreamRowFilter extends TransientStreamRow {
  private final Predicate predicate;

  public StreamRowFilter(Predicate predicate) {
    this.predicate = predicate;
  }

  @SuppressWarnings("unchecked")
  public boolean item(Object a) {
    if (predicate.test(a)) {
      return next.item(a);
    }
    return true;
  }

  public void end() {
    next.end();
  }
}
