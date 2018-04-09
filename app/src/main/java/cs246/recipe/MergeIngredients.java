package cs246.recipe;

import android.util.Log;

import java.util.Locale;

/**
 * Created by Eriqua Eisele on 4/7/2018.
 *  MergeIngredients:
 *      Combines ingredient amounts of same type ingredient.
 */

public class MergeIngredients {

    /**
     * simpleMerge:
     *      Constructor for simple merges.
     * @param ingredient1
     * @param ingredient2
     * @return
     */
    public Ingredient simpleMerge(Ingredient ingredient1, Ingredient ingredient2) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredient(ingredient1.getIngredient());
        ingredient.setUnits(ingredient1.getUnits());

        MixedFraction mixedFraction1 = ingredient1.getMeasurement();
        MixedFraction mixedFraction2 = ingredient2.getMeasurement();

        MixedFraction newMixedFraction = new MixedFraction();
        newMixedFraction = mergeFractions(mixedFraction1, mixedFraction2);

        newMixedFraction = simplifyFraction(newMixedFraction);
        newMixedFraction = new MixedFraction(newMixedFraction.getWhole(), newMixedFraction.getNumerator(), newMixedFraction.getDenominator());
        ingredient.setMeasurement(newMixedFraction);

        return ingredient;
    }

    /**
     * merge:
     *      Merges ingredients of different units together.
     * @param ingredient1
     * @param ingredient2
     * @return
     */
    public Ingredient merge(Ingredient ingredient1, Ingredient ingredient2) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredient(ingredient1.getIngredient());

        MixedFraction mixedFraction1 = ingredient1.getMeasurement();
        MixedFraction mixedFraction2 = ingredient2.getMeasurement();

        mixedFraction1 = getConvertedFraction(mixedFraction1, ingredient1.getUnits(), "tsp");
        display(mixedFraction1);
        mixedFraction2 = getConvertedFraction(mixedFraction2, ingredient2.getUnits(), "tsp");
        display(mixedFraction2);

        MixedFraction newMixedFraction = new MixedFraction();
        newMixedFraction = mergeFractions(mixedFraction1, mixedFraction2);
        if(( newMixedFraction.getNumerator()/ newMixedFraction.getDenominator()) >= 48) {
            newMixedFraction = getConvertedFraction(newMixedFraction, "tsp", "cup");
            ingredient.setUnits("cup");
        }
        else if((newMixedFraction.getNumerator()/newMixedFraction.getDenominator()) >= 3){
            newMixedFraction = getConvertedFraction(newMixedFraction, "tsp", "tbs");
            ingredient.setUnits("tbs");
        }
        else
            ingredient.setUnits("tsp");

        newMixedFraction = simplifyFraction(newMixedFraction);
        newMixedFraction = new MixedFraction(newMixedFraction.getWhole(), newMixedFraction.getNumerator(), newMixedFraction.getDenominator());
        ingredient.setMeasurement(newMixedFraction);


        return ingredient;
    }

    private int gcd(int a, int b) {
        if (a == b)
            return a;
        if (a == 0)
            return b;
        return gcd((b % a), a);
    }

    /**
     * MergeFraction:
     *      adds ingredient amounts together.
     * @param mixedFraction1
     * @param mixedFraction2
     * @return
     */
    private MixedFraction mergeFractions(MixedFraction mixedFraction1, MixedFraction mixedFraction2) {
        MixedFraction mixedFraction = new MixedFraction();
        mixedFraction.setWhole(mixedFraction1.getWhole() + mixedFraction2.getWhole());

        int newDenom = (mixedFraction1.getDenominator() * mixedFraction2.getDenominator());
        mixedFraction1.setNumerator(mixedFraction2.getDenominator() * mixedFraction1.getNumerator());

        mixedFraction2.setNumerator(mixedFraction1.getDenominator() * mixedFraction2.getNumerator());
        mixedFraction2.setDenominator(newDenom);
        mixedFraction1.setDenominator(newDenom);

        mixedFraction.setNumerator(mixedFraction1.getNumerator() + mixedFraction2.getNumerator());
        mixedFraction.setDenominator(newDenom);

        return mixedFraction;
    }


    /**
     * getConvertedFraction:
     *      Converts ingredient unit and amount into different units/ amounts.
     * @param mixedFraction1
     * @param units
     * @return
     */
    private MixedFraction getConvertedFraction(MixedFraction mixedFraction1, String units, String convert) {
        MixedFraction mixedFraction = new MixedFraction();

        if (convert == "tsp") {
            switch (units) {
                case "tsp":
                    mixedFraction = convertUnitsToTsp(mixedFraction1, 1);
                    break;
                case "tbs":
                    mixedFraction = convertUnitsToTsp(mixedFraction1, 3);
                    break;
                case "cup":
                    mixedFraction = convertUnitsToTsp(mixedFraction1, 48);
                    break;

            }
        }

        if (convert == "tbs") {
            switch (units) {
                case "tsp":
                    mixedFraction = convertUnitsToTbs(mixedFraction1, 3);
                    break;
                case "tbs":
                    mixedFraction = convertUnitsToTbs(mixedFraction1, 1);
                    break;

            }
        }


        if (convert == "cup") {
            switch (units) {
                case "tsp":
                    mixedFraction = convertUnitsToCups(mixedFraction1, 48);
                    break;
                case "tbs":
                    mixedFraction = convertUnitsToCups(mixedFraction1, 16);
                    break;
                case "cup":
                    mixedFraction = convertUnitsToCups(mixedFraction1, 1);
                    break;

            }
        }

        return mixedFraction;
    }


    /**
     * convertUnitsToTsp:
     *      Converts measurements into teaspoons.
     * @param mixedFraction
     * @param conversionFactor
     * @return
     */
    private MixedFraction convertUnitsToTsp(MixedFraction mixedFraction, int conversionFactor) {
        MixedFraction mixedFraction1 = new MixedFraction();
        mixedFraction1.setWhole(0);
        mixedFraction1.setNumerator(mixedFraction.getWhole() * mixedFraction.getDenominator() + mixedFraction.getNumerator());
        mixedFraction1.setDenominator(mixedFraction.getDenominator());

        mixedFraction1.setNumerator(mixedFraction1.getNumerator() * conversionFactor);

        return mixedFraction1;
    }

    private MixedFraction convertUnitsToTbs(MixedFraction mixedFraction, int conversionFactor) {
        MixedFraction mixedFraction1 = new MixedFraction();
        mixedFraction1.setWhole(0);
        mixedFraction1.setNumerator(mixedFraction.getWhole() * mixedFraction.getDenominator() + mixedFraction.getNumerator());
        mixedFraction1.setDenominator(mixedFraction.getDenominator());

        mixedFraction1.setDenominator(mixedFraction1.getDenominator() * conversionFactor);

        return mixedFraction1;
    }

        /**
         * convertUnitsToCups:
         *      Converts measurements into cups.
         * @param mixedFraction
         * @param conversionFactor
         * @return
         */
    private MixedFraction convertUnitsToCups(MixedFraction mixedFraction, int conversionFactor) {
        MixedFraction mixedFraction1 = new MixedFraction();
        mixedFraction1.setWhole(0);
        mixedFraction1.setNumerator(mixedFraction.getWhole() * mixedFraction.getDenominator() + mixedFraction.getNumerator());
        mixedFraction1.setDenominator(mixedFraction.getDenominator());

        mixedFraction1.setDenominator(mixedFraction1.getDenominator() * conversionFactor);

        return mixedFraction1;
    }

    /**
     * simplifyFraction:
     *      Simplifies fractions into mixed fractions.
     * @param mixedFraction
     * @return
     */
    private MixedFraction simplifyFraction(MixedFraction mixedFraction) {
        MixedFraction newMixedFraction = mixedFraction;

        int common = gcd(newMixedFraction.getNumerator(), newMixedFraction.getDenominator());
        newMixedFraction.setNumerator(newMixedFraction.getNumerator() / common + newMixedFraction.getWhole() * newMixedFraction.getDenominator());
        newMixedFraction.setDenominator(newMixedFraction.getDenominator() / common);

        newMixedFraction.setWhole(newMixedFraction.getNumerator() / newMixedFraction.getDenominator());
        newMixedFraction.setNumerator(newMixedFraction.getNumerator() % newMixedFraction.getDenominator());

        return newMixedFraction;
    }

    /**
     * Display:
     *      Logs a fraction's data.
     * @param mixedFraction
     */
    private void display(MixedFraction mixedFraction) {
        String display = String.format(Locale.US, "whole: %d numerator: %d denominator: %d",
                mixedFraction.getWhole(), mixedFraction.getNumerator(), mixedFraction.getDenominator());
        Log.d("measure convert", display);
    }
}
