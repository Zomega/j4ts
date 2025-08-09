package javaemul.internal.stream;

public abstract class TerminalStreamRow implements StreamRow {
  public void chain(StreamRow next) {
    if (next.getClass() == StreamRowEnd.class) {
      return;
    }
    throw new IllegalStateException();
  }

  public void end() {
    // relax
  }
}
