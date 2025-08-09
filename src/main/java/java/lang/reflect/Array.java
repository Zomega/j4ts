package java.lang.reflect;

import jsweet.lang.Replace;

public class Array {
  private Array() {}

  @Replace("let array = []; array.length = length; return array;")
  public static Object newInstance(Class<?> componentType, int length)
      throws NegativeArraySizeException {
    return null;
  }
}
