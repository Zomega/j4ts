package java.nio;

import def.js.ArrayBuffer;
import def.js.Uint16Array;
import def.js.Uint8Array;

public class ByteOrder {
  public static final ByteOrder BIG_ENDIAN = new ByteOrder();

  public static final ByteOrder LITTLE_ENDIAN = new ByteOrder();

  private ByteOrder() {}

  @Override
  public String toString() {
    return this == BIG_ENDIAN ? "BIG_ENDIAN" : "LITTLE_ENDIAN";
  }

  public static ByteOrder nativeOrder() {
    if (NativeInstanceHolder.INSTANCE == null) {
      NativeInstanceHolder.INSTANCE = NativeInstanceHolder.nativeOrderTester();
    }

    return NativeInstanceHolder.INSTANCE;
  }

  private static class NativeInstanceHolder {
    private static ByteOrder INSTANCE;

    public static ByteOrder nativeOrderTester() {
      ArrayBuffer arrayBuffer = new ArrayBuffer(2);
      Uint8Array uint8Array = new Uint8Array(arrayBuffer);
      Uint16Array uint16array = new Uint16Array(arrayBuffer);

      uint8Array.set(Uint8Array.from(new Double[] {(double) 0xAA, (double) 0xBB}), 0);

      if (uint16array.$get(0) == 0xBBAA) return LITTLE_ENDIAN;
      if (uint16array.$get(0) == 0xAABB) return BIG_ENDIAN;

      throw new Error("Something crazy just happened in native order test");
    }
  }
}
