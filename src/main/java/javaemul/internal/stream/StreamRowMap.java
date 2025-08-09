package javaemul.internal.stream;

import java.util.function.Function;

public class StreamRowMap extends TransientStreamRow {
  private final Function map;

  public StreamRowMap(Function map) {
    this.map = map;
  }

  @SuppressWarnings("unchecked")
  public boolean item(Object a) {
    return next.item(map.apply(a));
  }

  public void end() {
    next.end();
  }
}
