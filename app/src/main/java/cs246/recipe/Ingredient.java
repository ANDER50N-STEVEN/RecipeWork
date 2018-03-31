package cs246.recipe;

/**
 * Created by Steven Anderson on 3/20/2018.
 */

public class Ingredient {

    private String ingredient;
    private int value;
    private String units;
    private int numerator;
    private int denominator;
    private String display;
    public Ingredient(String ingredientString, String value, String units){}
    public Ingredient(String ingredient, int value, int numerator, int denominator, String units){
        this.ingredient = ingredient;
        this.value = value;
        this.numerator = numerator;
        this.denominator = denominator;
        this.units = units;
    }
    public Ingredient(String ingredient, int value, int numerator, int denominator, String units, String display){
        this.ingredient = ingredient;
        this.numerator = numerator;
        this.value = value;
        this.denominator = denominator;
        this.units = units;
        this.display = display;
    }
    public String getIngredient(){
        return ingredient;
    }
    public int getValue(){
        return value;
    }
    public String getUnits() {return units; }
    public int getNumerator() {return numerator;}
    public int getDenominator() {return denominator;}
    public String getDisplay() {return display;}
    public void setIngredient(String ingredient){
         this.ingredient = ingredient;
    }
    public void setValue(int value){
        this.value = value;
    }
    public void setUnits(String units) {this.units = units; }
    public void setNumerator(int numerator) {this.numerator = numerator;}
    public void setDenominator(int denominator) {this.denominator = denominator;}
    public void setDisplay(String display) {this.display = display;}
}
