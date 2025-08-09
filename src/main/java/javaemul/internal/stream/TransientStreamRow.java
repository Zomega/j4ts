package javaemul.internal.stream;

public abstract class TransientStreamRow implements StreamRow {
  protected StreamRow next;

  public void chain(StreamRow next) {
    this.next = next;
  }
}
