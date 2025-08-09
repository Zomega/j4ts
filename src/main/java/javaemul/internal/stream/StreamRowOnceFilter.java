package javaemul.internal.stream;

import java.util.Optional;
import java.util.function.Predicate;

public class StreamRowOnceFilter extends TerminalStreamRow {
  private final Predicate predicate;
  private boolean predicateValue;
  private Optional firstMatch = Optional.empty();

  public Optional getFirstMatch() {
    return firstMatch;
  }

  public boolean getPredicateValue() {
    return predicateValue;
  }

  public StreamRowOnceFilter(Predicate predicate) {
    this.predicate = predicate;
  }

  @SuppressWarnings("unchecked")
  public boolean item(Object a) {
    predicateValue = predicate.test(a);
    if (predicateValue) {
      firstMatch = Optional.of(a);
    }
    return !predicateValue;
  }
}
