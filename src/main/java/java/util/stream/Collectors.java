package java.util.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Collectors {
  private static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key %s", u));
    };
  }

  static final Set<Collector.Characteristics> CH_ID =
      Collections.unmodifiableSet(Collections.singleton(Collector.Characteristics.IDENTITY_FINISH));

  static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

  static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Set<Characteristics> characteristics;
    private final Function<A, R> finisher;

    CollectorImpl(
        Supplier<A> supplier,
        BiConsumer<A, T> accumulator,
        BinaryOperator<A> combiner,
        Function<A, R> finisher,
        Set<Characteristics> characteristics) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.combiner = combiner;
      this.finisher = finisher;
      this.characteristics = characteristics;
    }

    CollectorImpl(
        Supplier<A> supplier,
        BiConsumer<A, T> accumulator,
        BinaryOperator<A> combiner,
        Set<Characteristics> characteristics) {
      this(supplier, accumulator, combiner, castingIdentity(), characteristics);
    }

    CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner) {
      this(supplier, accumulator, combiner, castingIdentity(), null);
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
      return i -> (R) i;
    }

    @Override
    public BiConsumer<A, T> accumulator() {
      return accumulator;
    }

    @Override
    public Supplier<A> supplier() {
      return supplier;
    }

    @Override
    public BinaryOperator<A> combiner() {
      return combiner;
    }

    @Override
    public Function<A, R> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return characteristics;
    }
  }

  public static <T> Collector<T, ?, List<T>> toList() {
    return new CollectorImpl<>(
        () -> new ArrayList<>(),
        (l, i) -> l.add(i),
        (left, right) -> {
          left.addAll(right);
          return left;
        });
  }

  public static <T> Collector<T, ?, Set<T>> toSet() {
    return new CollectorImpl<>(
        () -> new HashSet<>(),
        (s, i) -> s.add(i),
        (left, right) -> {
          left.addAll(right);
          return left;
        });
  }

  public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, throwingMerger(), HashMap<K, U>::new);
  }

  public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction) {
    return toMap(keyMapper, valueMapper, mergeFunction, HashMap<K, U>::new);
  }

  public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction,
      Supplier<M> mapSupplier) {
    BiConsumer<M, T> accumulator =
        (map, element) ->
            map.merge(keyMapper.apply(element), valueMapper.apply(element), mergeFunction);
    return new CollectorImpl<>(mapSupplier, accumulator, mapMerger(mergeFunction));
  }

  private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(
      BinaryOperator<V> mergeFunction) {
    return (m1, m2) -> {
      for (Map.Entry<K, V> e : m2.entrySet()) m1.merge(e.getKey(), e.getValue(), mergeFunction);
      return m1;
    };
  }

  /**
   * Returns a {@code Collector} that concatenates the input elements into a {@code String}, in
   * encounter order.
   *
   * @return a {@code Collector} that concatenates the input elements into a {@code String}, in
   *     encounter order
   */
  public static Collector<CharSequence, ?, String> joining() {
    return new CollectorImpl<CharSequence, StringBuilder, String>(
        StringBuilder::new,
        StringBuilder::append,
        (r1, r2) -> {
          r1.append(r2);
          return r1;
        });
  }

  /**
   * Returns a {@code Collector} that concatenates the input elements, separated by the specified
   * delimiter, in encounter order.
   *
   * @param delimiter the delimiter to be used between each element
   * @return A {@code Collector} which concatenates CharSequence elements, separated by the
   *     specified delimiter, in encounter order
   */
  public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
    return joining(delimiter, "", "");
  }

  /**
   * Returns a {@code Collector} that concatenates the input elements, separated by the specified
   * delimiter, with the specified prefix and suffix, in encounter order.
   *
   * @param delimiter the delimiter to be used between each element
   * @param prefix the sequence of characters to be used at the beginning of the joined result
   * @param suffix the sequence of characters to be used at the end of the joined result
   * @return A {@code Collector} which concatenates CharSequence elements, separated by the
   *     specified delimiter, in encounter order
   */
  public static Collector<CharSequence, ?, String> joining(
      CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
    return new CollectorImpl<>(
        () -> new StringJoiner(delimiter, prefix, suffix), StringJoiner::add, StringJoiner::merge);
  }

  public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(
      Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
    return groupingBy(classifier, HashMap<K, D>::new, downstream);
  }

  public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(
      Function<? super T, ? extends K> classifier,
      Supplier<M> mapFactory,
      Collector<? super T, A, D> downstream) {
    Supplier<A> downstreamSupplier = downstream.supplier();
    BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
    BiConsumer<Map<K, A>, T> accumulator =
        (m, t) -> {
          K key =
              Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key");
          A container = m.computeIfAbsent(key, k -> downstreamSupplier.get());
          downstreamAccumulator.accept(container, t);
        };
    BinaryOperator<Map<K, A>> merger = Collectors.<K, A, Map<K, A>>mapMerger(downstream.combiner());
    @SuppressWarnings("unchecked")
    Supplier<Map<K, A>> mangledFactory = (Supplier<Map<K, A>>) mapFactory;

    if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
      return new CollectorImpl<>(mangledFactory, accumulator, merger, CH_ID);
    } else {
      @SuppressWarnings("unchecked")
      Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
      Function<Map<K, A>, M> finisher =
          intermediate -> {
            intermediate.replaceAll((k, v) -> downstreamFinisher.apply(v));
            @SuppressWarnings("unchecked")
            M castResult = (M) intermediate;
            return castResult;
          };
      return new CollectorImpl<>(mangledFactory, accumulator, merger, finisher, CH_NOID);
    }
  }

  public static <T, U, A, R> Collector<T, ?, R> mapping(
      Function<? super T, ? extends U> mapper, Collector<? super U, A, R> downstream) {
    BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
    return new CollectorImpl<>(
        downstream.supplier(),
        (r, t) -> downstreamAccumulator.accept(r, mapper.apply(t)),
        downstream.combiner(),
        downstream.finisher(),
        downstream.characteristics());
  }

  public static <T> Collector<T, ?, T> reducing(T identity, BinaryOperator<T> op) {
    return new CollectorImpl<>(
        boxSupplier(identity),
        (a, t) -> {
          a[0] = op.apply(a[0], t);
        },
        (a, b) -> {
          a[0] = op.apply(a[0], b[0]);
          return a;
        },
        a -> a[0],
        CH_NOID);
  }

  public static <T, U> Collector<T, ?, U> reducing(
      U identity, Function<? super T, ? extends U> mapper, BinaryOperator<U> op) {
    return new CollectorImpl<>(
        boxSupplier(identity),
        (a, t) -> {
          a[0] = op.apply(a[0], mapper.apply(t));
        },
        (a, b) -> {
          a[0] = op.apply(a[0], b[0]);
          return a;
        },
        a -> a[0],
        CH_NOID);
  }

  @SuppressWarnings("unchecked")
  private static <T> Supplier<T[]> boxSupplier(T identity) {
    return () -> (T[]) new Object[] {identity};
  }
}
