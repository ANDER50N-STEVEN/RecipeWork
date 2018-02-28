package cs246.recipe;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CheckoutVSPantryTest {

    public void CheckoutVSPantry(){
        List shoppingList = new ArrayList<>();
        List pantryList = new ArrayList<>();

        assertEquals(pantryList, shoppingList);
    }

}
