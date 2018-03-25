package cs246.recipe;

import java.util.List;

/**
 * Created by Admin on 3/23/2018.
 */

public class RecipeObject {
    private List<Ingredient> ingredients;
    private String instructions;

    RecipeObject(List<Ingredient> ingredients, String instructions) {
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
