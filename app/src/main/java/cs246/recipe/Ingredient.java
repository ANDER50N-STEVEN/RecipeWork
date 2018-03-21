package cs246.recipe;

/**
 * Created by Steven Anderson on 3/20/2018.
 */

public class Ingredient {

    private String ingredient;
    private String value;
    public Ingredient(){}
    public Ingredient(String ingredient, String value){
        this.ingredient = ingredient;
        this.value = value;
    }
    public String getIngredient(){
        return ingredient;
    }
    public String getValue(){
        return value;
    }
    public void setIngredient(String ingredient){
         this.ingredient = ingredient;
    }
    public void setValue(String value){
        this.value = value;
    }
}