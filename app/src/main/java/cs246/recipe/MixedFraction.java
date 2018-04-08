package cs246.recipe;

/**
 * MixedFraction:
 *      Stores data for a mixed fraction.
 */
public class MixedFraction {
    Integer whole;
    Integer numerator;
    Integer denominator;
    String display;

    /**
     * save data to class
     * @param whole the whole number of ingredient
     * @param numerator the numerator of the whole number
     * @param denominator the denominator of the whole number
     */
    MixedFraction(Integer whole, Integer numerator, Integer denominator) {
        setWhole(whole);
        setNumerator(numerator);
        setDenominator(denominator);
        createDisplay();
    }

    /**
     * constructor to set the values with two param
     * @param numerator the numerator of the whole number
     * @param denominator the denominator of the whole number
     */
    MixedFraction(Integer numerator, Integer denominator) {
        setNumerator(numerator);
        setDenominator(denominator);
        setWhole(0);
        createDisplay();
    }

    /**
     * constructor to set the values with one param
     * @param whole whole measurement of the ingredient
     */
    MixedFraction(Integer whole) {
        setWhole(whole);
        setNumerator(0);
        setDenominator(1);
        createDisplay();
    }

    /**
     * calls convert and display method
     * @param measurement the measurement of the ingredient
     */
    MixedFraction(String measurement) {
        convertFromString(measurement);
        createDisplay();
    }

    /**
     * default constructor to set initial value
     */
    MixedFraction() {
        setWhole(0);
        setNumerator(0);
        setDenominator(1);
        createDisplay();
    }

    /**
     * setter of denominator
     * @param denominator sets denominator
     */
    public void setDenominator(Integer denominator) {
        this.denominator = denominator;
    }

    /**
     * setter of numerator
     * @param numerator sets numerator
     */
    public void setNumerator(Integer numerator) {
        this.numerator = numerator;
    }

    /**
     * setter of whole number
     * @param whole sets whole number
     */
    public void setWhole(Integer whole) {
        this.whole = whole;
    }

    /**
     * setter of diplay
     * @param display sets display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * getters of denominator
     * @return denominator
     */
    public Integer getDenominator() {
        return denominator;
    }

    /**
     * getters of numerator
     * @return numerator
     */
    public Integer getNumerator() {
        return numerator;
    }

    /**
     * getters of whole number
     * @return whole number
     */
    public Integer getWhole() {
        return whole;
    }

    /**
     * getters of display
     * @return display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * ConvertFromString:
     *      Converts a string into a measurement.
     * @param measurement
     */
    private void convertFromString(String measurement) {
        setWhole(0);
        setNumerator(0);
        setDenominator(1);

        if (measurement.contains("-") || measurement.contains(" ")) {
            String[] mixed = measurement.split("-");
            setWhole(Integer.valueOf(mixed[0]));

            String[] fraction = mixed[1].split("/");
            setNumerator(Integer.valueOf(fraction[0]));
            setDenominator(Integer.valueOf(fraction[1]));
        } else if (measurement.contains("/")) {
            String[] fraction = measurement.split("/");
            setNumerator(Integer.valueOf(fraction[0]));
            setDenominator(Integer.valueOf(fraction[1]));
        } else {
            setWhole(Integer.valueOf(measurement));
        }
    }

    /**
     * createDisplay:
     *      Creates a display for the measurement.
     */
    private void createDisplay() {
        String display = "";
        if (whole != 0)
            display += whole;
        if (whole != 0 && numerator != 0)
            display += " ";
        if (numerator != 0) {
            display += numerator + "/" + denominator;
        }
        setDisplay(display);
    }
}
