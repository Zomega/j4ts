package javaemul.internal.stream;

import java.util.function.Predicate;

public class StreamRowAllFilter extends TerminalStreamRow {
  private final Predicate predicate;
  private boolean predicateValue;
  private long attempts;

  public boolean getPredicateValue() {
    return attempts == 0 || predicateValue;
  }

  public StreamRowAllFilter(Predicate predicate) {
    this.predicate = predicate;
  }

  public boolean item(Object a) {
    ++attempts;
    predicateValue &= predicate.test(a);
    return predicateValue;
  }
}
