package javaemul.internal.stream;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamRowFlatMap extends TransientStreamRow {
  private final Function<Object, Stream> flatMap;

  @SuppressWarnings("unchecked")
  public StreamRowFlatMap(Function flatMap) {
    this.flatMap = flatMap;
  }

  @SuppressWarnings("unchecked")
  public boolean item(Object a) {
    final Stream subStream = flatMap.apply(a);
    try {
      try {
        subStream.forEach(
            o -> {
              if (!next.item(o)) {
                throw new StopException();
              }
              ;
            });
      } catch (StopException e) {
        return false;
      }
    } finally {
      subStream.close();
    }
    return true;
  }

  public void end() {
    next.end();
  }
}
