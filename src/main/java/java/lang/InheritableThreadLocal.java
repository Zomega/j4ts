package java.lang;

public class InheritableThreadLocal<T> extends ThreadLocal<T> {

  protected T childValue(T parentValue) {
    return parentValue;
  }
}
