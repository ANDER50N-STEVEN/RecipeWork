package cs246.recipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * to display list of recipes
 * and has button to "add new recipe"
 *
 * @author Eriqua Eisele
 */

public class CookBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book);
    }

    public void addNewRecipe(View view) {
        Intent intent = new Intent(this, NewRecipe.class);
        startActivity(intent);
    }

}
