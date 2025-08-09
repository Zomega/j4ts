package javaemul.internal.stream;

import java.util.function.Predicate;

public class CountingPredicate<T> implements Predicate<T> {
  private long countDown;

  public CountingPredicate(long n) {
    countDown = n;
  }

  @Override
  public boolean test(T t) {
    if (countDown <= 0) {
      return false;
    }
    --countDown;
    return true;
  }
}
