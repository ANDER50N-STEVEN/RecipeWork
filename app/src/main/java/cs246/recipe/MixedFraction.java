package cs246.recipe;

import java.util.MissingFormatArgumentException;

/**
 * Created by Admin on 3/30/2018.
 */

public class MixedFraction {
    Integer whole;
    Integer numerator;
    Integer denominator;
    String display;

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
