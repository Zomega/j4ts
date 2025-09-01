package java.text;

import java.util.Locale;
import java.lang.Number;

class NumberFormat { // extends Format? May not be needed.
  public static final NumberFormat getInstance() {
    return new NumberFormat();
  }

  protected int maximumFractionDigits = 3;
  protected int minimumFractionDigits = 0;

  // Internal JSweet NumberFormat instance, will be null until first format call or when invalidated
  protected def.dom.intl.NumberFormat jsweetNumberFormat;

  public void setMaximumFractionDigits(int newValue) {
    maximumFractionDigits = Math.max(0, newValue);
    if (maximumFractionDigits < minimumFractionDigits) {
      minimumFractionDigits = maximumFractionDigits;
    }
    // Invalidate the jsweetNumberFormat
    this.jsweetNumberFormat = null;
  }

  public void setMinimumFractionDigits(int newValue) {
    minimumFractionDigits = Math.max(0, newValue);
    if (maximumFractionDigits < minimumFractionDigits) {
      maximumFractionDigits = minimumFractionDigits;
    }
    // Invalidate the jsweetNumberFormat
    this.jsweetNumberFormat = null;
  }

  public String format(double number) {
    // Lazy initialization: create jsweetNumberFormat if it's null
    if (this.jsweetNumberFormat == null) {
      def.dom.intl.NumberFormatOptions options = (def.dom.intl.NumberFormatOptions) new Object();
      options.minimumFractionDigits = this.minimumFractionDigits;
      options.maximumFractionDigits = this.maximumFractionDigits;

      // TODO: Locale.
      this.jsweetNumberFormat = new def.dom.intl.NumberFormat("en-US", options);
    }
    return this.jsweetNumberFormat.format(number);
  }

  public void setGroupingUsed(boolean newValue) {
    // TODO: Implement
  }


  // Implement parse method (simplified)
  // TODO: This is nowhere near full featured.
  public Number parse(String source) throws java.text.ParseException {
    try {
      return Double.parseDouble(source);
    } catch (NumberFormatException e) {
      throw new java.text.ParseException("Unparseable number: \"" + source + "\"", 0);
    }
  }

  public Number parse(String source, ParsePosition pos) throws java.text.ParseException {
    return parse(source);
  }

  public static final NumberFormat getIntegerInstance() {
    return NumberFormat.getInstance(); // TODO: Implement
  }

  public static final NumberFormat getIntegerInstance(Locale l) {
    return NumberFormat.getCurrencyInstance();
  }

  public static final NumberFormat getCurrencyInstance() {
    return NumberFormat.getInstance(); // TODO: Implement
  }

  public static final NumberFormat getCurrencyInstance(Locale l) {
    return NumberFormat.getCurrencyInstance();
  }

  public static final NumberFormat getPercentInstance() {
    return NumberFormat.getInstance(); // TODO: Implement
  }

  public static final NumberFormat getPercentInstance(Locale l) {
    return NumberFormat.getCurrencyInstance();
  }
}
