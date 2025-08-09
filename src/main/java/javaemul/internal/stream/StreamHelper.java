package javaemul.internal.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamHelper<T> implements Stream<T> {
  private RunnableChain onCloseChain;
  private final StreamRow head;
  private final StreamRow end;
  private final Collection<T> data;

  private Stream chain(StreamRow streamRow) {
    end.chain(streamRow);
    return this;
  }

  private void play() {
    for (Object item : data) {
      if (!head.item(item)) {
        break;
      }
    }
    head.end();
  }

  @SuppressWarnings("unchecked")
  private Optional foldRight(Optional<T> identity, BinaryOperator accumulator) {
    StreamRowReduce rowReduce = new StreamRowReduce(identity, accumulator);
    chain(rowReduce);
    play();
    return rowReduce.getResult();
  }

  public StreamHelper(Collection<T> data) {
    head = new StreamRowMap(o -> o);
    end = new StreamRowEnd(head);
    head.chain(end);
    this.data = data;
  }

  @SuppressWarnings("unchecked")
  public Stream<T> filter(Predicate<? super T> predicate) {
    return chain(new StreamRowFilter(predicate));
  }

  @SuppressWarnings("unchecked")
  public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
    return chain(new StreamRowMap(mapper));
  }

  @SuppressWarnings("unchecked")
  public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
    return chain(new StreamRowMap(n -> mapper.apply((Integer) n)));
  }

  @SuppressWarnings("unchecked")
  public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
    return chain(new StreamRowFlatMap(mapper));
  }

  @SuppressWarnings("unchecked")
  public Stream<T> distinct() {
    return chain(new StreamRowCollector(new LinkedHashSet()));
  }

  @SuppressWarnings("unchecked")
  public Stream<T> sorted() {
    return sorted((a, b) -> ((Comparable) a).compareTo(b));
  }

  @SuppressWarnings("unchecked")
  public Stream<T> sorted(Comparator<? super T> comparator) {
    return chain(new StreamRowSortingCollector(new ArrayList(), comparator));
  }

  @SuppressWarnings("unchecked")
  public Stream<T> peek(Consumer<? super T> action) {
    return chain(new StreamRowMap(new ConsumingFunction(action)));
  }

  @SuppressWarnings("unchecked")
  public Stream<T> limit(long maxSize) {
    return chain(new StreamRowFilterFlop(new CountingPredicate(maxSize)));
  }

  @SuppressWarnings("unchecked")
  public Stream<T> skip(long n) {
    CountingPredicate p = new CountingPredicate(n);
    return chain(new StreamRowFilter(v -> !p.test(v)));
  }

  @SuppressWarnings("unchecked")
  public void forEach(Consumer<? super T> action) {
    peek(action);
    play();
  }

  public void forEachOrdered(Consumer<? super T> action) {
    forEach(action);
  }

  @SuppressWarnings("unchecked")
  public Object[] toArray() {
    List result = new ArrayList();
    chain(new StreamRowCollector(result));
    play();
    return result.toArray();
  }

  @SuppressWarnings("unchecked")
  public <A> A[] toArray(IntFunction<A[]> generator) {
    List<A> result = new ArrayList();
    chain(new StreamRowCollector(result));
    play();
    return result.toArray(generator.apply(result.size()));
  }

  @SuppressWarnings("unchecked")
  public T reduce(T identity, BinaryOperator<T> accumulator) {
    return (T) foldRight(Optional.of(identity), accumulator).get();
  }

  @SuppressWarnings("unchecked")
  public Optional<T> reduce(BinaryOperator<T> accumulator) {
    return foldRight(Optional.empty(), accumulator);
  }

  @SuppressWarnings("unchecked")
  public <R, A> R collect(Collector<? super T, A, R> collector) {
    A container = collector.supplier().get();
    BiConsumer accumulator = collector.accumulator();
    chain(new StreamRowMap(new ConsumingFunction(item -> accumulator.accept(container, item))));
    play();
    return (R) container;
  }

  @SuppressWarnings("unchecked")
  public Optional<T> min(Comparator<? super T> comparator) {
    return foldRight(Optional.empty(), (a, b) -> comparator.compare((T) a, (T) b) <= 0 ? a : b);
  }

  @SuppressWarnings("unchecked")
  public Optional<T> max(Comparator<? super T> comparator) {
    return foldRight(Optional.empty(), (a, b) -> comparator.compare((T) a, (T) b) >= 0 ? a : b);
  }

  @SuppressWarnings("unchecked")
  public long count() {
    final StreamRowCount counter = new StreamRowCount();
    chain(counter);
    play();
    return counter.getCount();
  }

  @SuppressWarnings("unchecked")
  public boolean anyMatch(Predicate<? super T> predicate) {
    StreamRowOnceFilter streamRow = new StreamRowOnceFilter(predicate);
    chain(streamRow);
    play();
    return streamRow.getPredicateValue();
  }

  @SuppressWarnings("unchecked")
  public boolean allMatch(Predicate<? super T> predicate) {
    StreamRowAllFilter streamRow = new StreamRowAllFilter(predicate);
    chain(streamRow);
    play();
    return streamRow.getPredicateValue();
  }

  public boolean noneMatch(Predicate<? super T> predicate) {
    return allMatch(v -> !predicate.test(v));
  }

  @SuppressWarnings("unchecked")
  public Optional<T> findFirst() {
    StreamRowOnceFilter streamRow = new StreamRowOnceFilter(o -> true);
    chain(streamRow);
    play();
    return streamRow.getFirstMatch();
  }

  public Optional<T> findAny() {
    return findFirst();
  }

  @SuppressWarnings("unchecked")
  public Iterator<T> iterator() {
    List<T> result = new ArrayList<>();
    chain(new StreamRowCollector(result));
    play();
    return result.iterator();
  }

  public boolean isParallel() {
    return false;
  }

  public Stream<T> sequential() {
    return this;
  }

  public Stream<T> parallel() {
    return this;
  }

  public Stream<T> unordered() {
    return this;
  }

  public Stream<T> onClose(Runnable closeHandler) {
    RunnableChain chainItem = new RunnableChain(new QuiteRunnable(closeHandler));
    if (onCloseChain == null) {
      onCloseChain = chainItem;
    } else {
      onCloseChain.chain(chainItem);
    }
    return this;
  }

  public void close() {
    if (onCloseChain == null) {
      return;
    }
    onCloseChain.runChain();
  }

  // not implemented

  public IntStream mapToInt(ToIntFunction<? super T> mapper) {
    throw new IllegalStateException();
  }

  public LongStream mapToLong(ToLongFunction<? super T> mapper) {
    throw new IllegalStateException();
  }

  public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
    throw new IllegalStateException();
  }

  public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
    throw new IllegalStateException();
  }

  public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
    throw new IllegalStateException();
  }

  public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
    throw new IllegalStateException();
  }

  public Spliterator<T> spliterator() {
    throw new IllegalStateException();
  }

  public <U> U reduce(
      U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
    throw new IllegalStateException();
  }

  public <R> R collect(
      Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
    throw new IllegalStateException();
  }
}
