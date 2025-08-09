package java.util.stream;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Collector<T, A, R> {
  Supplier<A> supplier();

  BiConsumer<A, T> accumulator();

  Set<Characteristics> characteristics();

  BinaryOperator<A> combiner();

  Function<A, R> finisher();

  enum Characteristics {
    /**
     * Indicates that this collector is <em>concurrent</em>, meaning that the result container can
     * support the accumulator function being called concurrently with the same result container
     * from multiple threads.
     *
     * <p>If a {@code CONCURRENT} collector is not also {@code UNORDERED}, then it should only be
     * evaluated concurrently if applied to an unordered data source.
     */
    CONCURRENT,

    /**
     * Indicates that the collection operation does not commit to preserving the encounter order of
     * input elements. (This might be true if the result container has no intrinsic order, such as a
     * {@link Set}.)
     */
    UNORDERED,

    /**
     * Indicates that the finisher function is the identity function and can be elided. If set, it
     * must be the case that an unchecked cast from A to R will succeed.
     */
    IDENTITY_FINISH
  };
}
