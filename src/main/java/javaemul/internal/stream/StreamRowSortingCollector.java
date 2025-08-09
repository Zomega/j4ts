package javaemul.internal.stream;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StreamRowSortingCollector extends StreamRowCollector {
  private Comparator comparator;

  public StreamRowSortingCollector(List collection, Comparator comparator) {
    super(collection);
    this.comparator = comparator;
  }

  @Override
  public void end() {
    Collections.sort((List) collection, comparator);
    super.end();
  }
}
