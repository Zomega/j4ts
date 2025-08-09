package javaemul.internal.stream;

import java.util.function.Predicate;

public class StreamRowFilterFlop extends TransientStreamRow {
  private final Predicate predicate;

  public StreamRowFilterFlop(Predicate predicate) {
    this.predicate = predicate;
  }

  @SuppressWarnings("unchecked")
  public boolean item(Object a) {
    boolean test = predicate.test(a);
    if (test) {
      return next.item(a);
    }
    return false;
  }

  public void end() {
    next.end();
  }
}
