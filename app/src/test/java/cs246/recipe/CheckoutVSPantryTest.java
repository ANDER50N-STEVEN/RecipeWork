package cs246.recipe;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CheckoutVSPantryTest {

    @Test
    public void CheckoutVSPantry(){
        List shoppingList = new ArrayList<>();
        List pantryList = new ArrayList<>();

        assertEquals(pantryList, shoppingList);
    }

}
