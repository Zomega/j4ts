package java.util.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface Stream<T> {
  Stream<T> filter(Predicate<? super T> predicate);

  <R> Stream<R> map(Function<? super T, ? extends R> mapper);

  <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

  Stream<T> distinct();

  Stream<T> sorted();

  Stream<T> sorted(Comparator<? super T> comparator);

  Stream<T> peek(Consumer<? super T> action);

  Stream<T> limit(long maxSize);

  Stream<T> skip(long n);

  void forEach(Consumer<? super T> action);

  void forEachOrdered(Consumer<? super T> action);

  Object[] toArray();

  <A> A[] toArray(IntFunction<A[]> generator);

  T reduce(T identity, BinaryOperator<T> accumulator);

  Optional<T> reduce(BinaryOperator<T> accumulator);

  <R, A> R collect(Collector<? super T, A, R> collector);

  Optional<T> min(Comparator<? super T> comparator);

  Optional<T> max(Comparator<? super T> comparator);

  long count();

  boolean anyMatch(Predicate<? super T> predicate);

  boolean allMatch(Predicate<? super T> predicate);

  boolean noneMatch(Predicate<? super T> predicate);

  Optional<T> findFirst();

  Optional<T> findAny();

  Iterator<T> iterator();

  boolean isParallel();

  Stream<T> sequential();

  Stream<T> parallel();

  Stream<T> unordered();

  Stream<T> onClose(Runnable closeHandler);

  void close();

  @SafeVarargs
  static <T> Stream<T> of(T... values) {
    return Arrays.asList(values).stream();
  }

  // lazy hack for int stream
  <U> Stream<U> mapToObj(IntFunction<? extends U> mapper);
}
