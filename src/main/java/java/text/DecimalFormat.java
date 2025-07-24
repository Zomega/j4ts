package java.text;

import java.lang.Double;

// TODO: This class was mostly written by Gemini, needs careful review.
public class DecimalFormat extends NumberFormat {

    private String pattern;
    private DecimalFormatSymbols symbols;
    private int minimumIntegerDigits;
    private boolean useGrouping;

    // Default constructor (if needed, might just call super)
    public DecimalFormat() {
        super();
        this.symbols = new DecimalFormatSymbols(); // Default symbols
        // Initialize with a default pattern, which will set the current*Digits fields
        applyPattern("#,##0.###");
    }

    // Constructor with pattern string
    public DecimalFormat(String pattern) {
        this(); // Call default constructor to initialize symbols and default pattern
        applyPattern(pattern);
    }

    // Constructor with pattern and symbols
    public DecimalFormat(String pattern, DecimalFormatSymbols symbols) {
        this.symbols = symbols;
        applyPattern(pattern);
    }

    // Method to parse the pattern and store configuration parameters
    // TODO: This is nowhere near full featured.
    public void applyPattern(String pattern) {
        this.pattern = pattern;
        // Invalidate the existing jsweetNumberFormat instance
        this.jsweetNumberFormat = null;

        // Reset default values first
        this.minimumFractionDigits = 0;
        this.maximumFractionDigits = 0;
        this.minimumIntegerDigits = 1; // Default from Java's DecimalFormat (e.g. 0.# -> 0.1)
        this.useGrouping = false;

        // Simple pattern parsing logic for common cases:
        this.useGrouping = pattern.contains(",");

        // Find decimal point in pattern
        int decimalPointIndex = pattern.indexOf('.');

        if (decimalPointIndex != -1) {
            // Part after decimal for fractions
            String fractionPart = pattern.substring(decimalPointIndex + 1);

            int minFrac = 0;
            int maxFrac = 0;

            for (char c : fractionPart.toCharArray()) {
                if (c == '0') {
                    minFrac++;
                    maxFrac++;
                } else if (c == '#') {
                    maxFrac++;
                }
            }
            this.minimumFractionDigits = minFrac;
            this.maximumFractionDigits = maxFrac;

            // Handle integer part before decimal for minimum integer digits
            String integerPart = pattern.substring(0, decimalPointIndex);
            int zeroCountInIntegerPart = 0;
            for (char c : integerPart.toCharArray()) {
                if (c == '0') {
                    zeroCountInIntegerPart++;
                }
            }
            if (zeroCountInIntegerPart > 0) {
                 this.minimumIntegerDigits = zeroCountInIntegerPart;
            } else if (!integerPart.contains("#") && integerPart.trim().isEmpty() && pattern.startsWith(".")) {
                 // Case like ".00"
                 this.minimumIntegerDigits = 0;
            }

        } else {
            // No decimal point in pattern (e.g., "#,##0")
            // Max and min fraction digits should be 0.
            this.minimumFractionDigits = 0;
            this.maximumFractionDigits = 0;

            int zeroCountInPattern = 0;
             for (char c : pattern.toCharArray()) {
                if (c == '0') {
                    zeroCountInPattern++;
                }
            }
            if (zeroCountInPattern > 0) {
                 this.minimumIntegerDigits = zeroCountInPattern;
            }
        }
    }

    // Override the format method from NumberFormat
    @Override
    public final String format(double number) {
        // Lazy initialization: create jsweetNumberFormat if it's null
        if (this.jsweetNumberFormat == null) {
            def.dom.intl.NumberFormatOptions options = (def.dom.intl.NumberFormatOptions) new Object();
            options.minimumFractionDigits = this.minimumFractionDigits;
            options.maximumFractionDigits = this.maximumFractionDigits;
            // TODO: WTF! options.minimumIntegerDigits = this.minimumIntegerDigits;
            options.useGrouping = this.useGrouping;

            // TODO: Locale.
            this.jsweetNumberFormat = new def.dom.intl.NumberFormat("en-US", options);
        }
        return this.jsweetNumberFormat.format(number);
    }

    // Implement parse method (simplified)
    // TODO: This is nowhere near full featured.
    public Number parse(String source) throws java.text.ParseException {
        String normalizedSource = source.replace(symbols.getDecimalSeparator(), '.');
        normalizedSource = normalizedSource.replace(String.valueOf(symbols.getGroupingSeparator()), "");

        try {
            return Double.parseDouble(normalizedSource);
        } catch (NumberFormatException e) {
            throw new java.text.ParseException("Unparseable number: \"" + source + "\"", 0);
        }
    }

    public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols) {
        this.symbols = newSymbols;
        // Invalidate the jsweetNumberFormat instance as symbols might affect formatting
        this.jsweetNumberFormat = null;
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return this.symbols;
    }
}