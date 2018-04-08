package cs246.recipe;

/**
 * Created by Steven Anderson on 3/20/2018.
 *  Class that contains the name of an ingredient,
 *  the units that the ingredient is in, the number
 *  as a mixedFraction object and how it will display
 */

public class Ingredient {

    private String ingredient;
    private String units;
    private String display;
    private MixedFraction measurement;

    public Ingredient() {}

    public Ingredient(String ingredient, String units, MixedFraction measurement){
        setIngredient(ingredient);
        setUnits(units);
        setMeasurement(measurement);
    }

    public Ingredient(String ingredient, String units, MixedFraction measurement, String display){
        setIngredient(ingredient);
        setUnits(units);
        setMeasurement(measurement);
        getMeasurement().setDisplay(display);
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void setMeasurement(MixedFraction measurement) {
        this.measurement = measurement;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public MixedFraction getMeasurement() {
        return measurement;
    }

    public String getDisplay() {
        return display;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getUnits() {
        return units;
    }
}
