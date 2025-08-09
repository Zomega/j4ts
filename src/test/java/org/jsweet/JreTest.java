package org.jsweet;

import java.util.Collection;
import java.util.stream.Stream;
import javaemul.internal.stream.StreamHelper;

public class JreTest extends BaseJreTest {
  @Override
  protected <T> Stream<T> stream(Collection<T> c) {
    return new StreamHelper<>(c);
  }
}
