package javaemul.internal.stream;

public interface StreamRow {
  void chain(StreamRow next);

  boolean item(Object a);

  void end();
}
