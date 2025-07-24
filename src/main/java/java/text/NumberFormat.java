package java.text;

class NumberFormat { // extends Format? May not be needed.
	public final static NumberFormat getInstance() {
		return new NumberFormat();
	}

	protected int maximumFractionDigits = 3;
    protected int minimumFractionDigits = 0;

    // Internal JSweet NumberFormat instance, will be null until first format call or when invalidated
    protected def.dom.intl.NumberFormat jsweetNumberFormat;

	public void setMaximumFractionDigits(int newValue) {
        maximumFractionDigits = Math.max(0,newValue);
        if (maximumFractionDigits < minimumFractionDigits) {
            minimumFractionDigits = maximumFractionDigits;
        }
        // Invalidate the jsweetNumberFormat
        this.jsweetNumberFormat = null;
    }

    public void setMinimumFractionDigits(int newValue) {
        minimumFractionDigits = Math.max(0,newValue);
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
}