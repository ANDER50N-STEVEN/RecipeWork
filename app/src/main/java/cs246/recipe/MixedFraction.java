package cs246.recipe;

import java.util.MissingFormatArgumentException;

/**
 * Created by Eriqua Eisele on 3/30/2018.
 * MixedFraction:
 *      Stores data for a mixed fraction.
 */

public class MixedFraction {
    Integer whole;
    Integer numerator;
    Integer denominator;
    String display;

    /**
     * Constructors:
     *      Save data to class.
     */
    MixedFraction(Integer whole, Integer numerator, Integer denominator) {
        setWhole(whole);
        setNumerator(numerator);
        setDenominator(denominator);
        createDisplay();
    }

    MixedFraction(Integer numerator, Integer denominator) {
        setNumerator(numerator);
        setDenominator(denominator);
        setWhole(0);
        createDisplay();
    }

    MixedFraction(Integer whole) {
        setWhole(whole);
        setNumerator(0);
        setDenominator(1);
        createDisplay();
    }

    MixedFraction(String measurement) {
        convertFromString(measurement);
        createDisplay();
    }

    MixedFraction() {
        setWhole(0);
        setNumerator(0);
        setDenominator(1);
        createDisplay();
    }

    /**
     * getters and setters
     */
    public void setDenominator(Integer denominator) {
        this.denominator = denominator;
    }

    public void setNumerator(Integer numerator) {
        this.numerator = numerator;
    }

    public void setWhole(Integer whole) {
        this.whole = whole;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Integer getDenominator() {
        return denominator;
    }

    public Integer getNumerator() {
        return numerator;
    }

    public Integer getWhole() {
        return whole;
    }

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
