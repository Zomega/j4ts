package javaemul.internal.stream;

import java.util.Collection;

public class StreamRowCollector extends TransientStreamRow {
  protected final Collection collection;

  public StreamRowCollector(Collection collection) {
    this.collection = collection;
  }

  @SuppressWarnings("unchecked")
  public boolean item(Object a) {
    collection.add(a);
    return true;
  }

  public void end() {
    for (Object item : collection) {
      if (!next.item(item)) {
        break;
      }
    }
    next.end();
  }
}
