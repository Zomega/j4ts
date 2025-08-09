package javaemul.internal.stream;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumingFunction<T> implements Function<T, T> {
  private final Consumer<T> consumer;

  public ConsumingFunction(Consumer<T> consumer) {
    this.consumer = consumer;
  }

  @Override
  public T apply(T t) {
    consumer.accept(t);
    return t;
  }
}
