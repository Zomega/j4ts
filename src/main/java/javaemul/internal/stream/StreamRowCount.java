package javaemul.internal.stream;

public class StreamRowCount extends TerminalStreamRow {
  private long count;

  public long getCount() {
    return count;
  }

  public boolean item(Object a) {
    ++count;
    return true;
  }
}
