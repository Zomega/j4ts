package javaemul.internal.stream;

/**
 * @author Władysław Kargul
 */
public class StreamRowEnd extends TerminalStreamRow {
  private StreamRow previous;

  public StreamRowEnd(StreamRow previous) {
    this.previous = previous;
  }

  public void chain(StreamRow next) {
    if (previous != null) {
      previous.chain(next);
    }
    previous = next;
    next.chain(this);
  }

  public boolean item(Object a) {
    return true;
  }
}
