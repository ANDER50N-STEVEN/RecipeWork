package cs246.recipe;

/**
 * Created by Steven Anderson on 3/20/2018.
 */

public class Ingredient {

    private String ingredient;
    private String value;
    private String units;
    public Ingredient(){}
    public Ingredient(String ingredient, String value, String units){
        this.ingredient = ingredient;
        this.value = value;
        this.units = units;
    }
    public String getIngredient(){
        return ingredient;
    }
    public String getValue(){
        return value;
    }
    public String getUnits() {return units; }
    public void setIngredient(String ingredient){
         this.ingredient = ingredient;
    }
    public void setValue(String value){
        this.value = value;
    }
    public void setUnits(String units) {this.units = units; }
}
