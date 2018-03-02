package cs246.recipe;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecipeCreatedTest {
    @Test
    public void recipe_wasCreated() throws Exception {
        Recipe addRecipe = new Recipe();
        Recipe savedRecipe = new Recipe();

        assertEquals(savedRecipe, addRecipe);
    }
}
