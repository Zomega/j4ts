package java.math;

public class BigInteger {

  // Static fields
  public static final BigInteger ONE = new BigInteger("1");
  public static final BigInteger ZERO = new BigInteger("0");

  // Internal value representation (for stubbing, can be anything)
  private String value;
  private int radix = 10; // Stores the radix if provided

  public BigInteger(String val) {
    // TODO: Implement
    this.value = val;
    this.radix = 10; // Default to base 10
  }

  public BigInteger(String val, int radix) {
    // TODO: Implement
    this.value = val;
    this.radix = radix;
  }

  // Instance methods
  public BigInteger multiply(BigInteger val) {
    // TODO: Implement actual multiplication logic
    return null; // Stub: returns null
  }

  public BigInteger divide(BigInteger val) {
    // TODO: Implement actual division logic
    return null; // Stub: returns null
  }

  public int compareTo(BigInteger val) {
    // TODO: Implement actual comparison logic
    // Return 0 if equal, <0 if this < val, >0 if this > val
    return 0; // Stub: returns 0 (equal)
  }

  public BigInteger subtract(BigInteger val) {
    // TODO: Implement actual subtraction logic
    return null; // Stub: returns null
  }

  public BigInteger mod(BigInteger m) {
    // TODO: Implement actual modulo logic
    return BigInteger.ZERO; // Stub: returns a dummy BigInteger
  }

  public BigInteger mod(int p) {
    // TODO: Implement actual power logic
    return BigInteger.ONE; // Stub: returns a dummy BigInteger
  }

  public int intValue() {
    // TODO: Implement
    return 0;
  }

  public double doubleValue() {
    // TODO: Implement
    return 0d;
  }

  @Override
  public String toString() {
    // TODO: Implement
    return this.value;
  }
}
