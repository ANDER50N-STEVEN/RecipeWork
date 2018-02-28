package cs246.recipe;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConvertTest {

    @Test
    public void Convert(String measurement, Double Amount){

        Double floz;
        Amount = 2.0;
        measurement = "cup";
        if (measurement == "cup" && Amount == 2.0){
            floz = 8.0;
            assertEquals(16, Amount * floz);
        }
        if (measurement == "tsp" && Amount == 1.0){
            floz = .5;
            assertEquals(.5, Amount * floz);
        }
    }
}