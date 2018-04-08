package cs246.recipe;

import java.util.List;

/**
 *  Stores essential parts of a recipe.
 */

public class RecipeObject {
    private List<Ingredient> ingredients;
    private String instructions;

    /**
     * default constructor
     * purposely left empty
     */
    RecipeObject() {}

    /**
     * non - default constructor
     * @param ingredients string of instructions
     * @param instructions list of ingredient
     */
    RecipeObject(List<Ingredient> ingredients, String instructions) {
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    /**
     *  getter of ingredient
     * @return instruction
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * getter instructions
     * @return instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * inredient setter
     * @param ingredients sets ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * setter of instruction
     * @param instructions sets instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
