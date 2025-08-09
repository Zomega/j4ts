package java.lang;

public class ThreadLocal<T> {
  private T value;

  protected T initialValue() {
    return null;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }

  public void remove() {}

  T childValue(T parentValue) {
    throw new UnsupportedOperationException();
  }
}
