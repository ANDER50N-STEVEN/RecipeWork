package cs246.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewRecipe extends AppCompatActivity {

    EditText input1, input2;
    Button saveButton;

    public static final String addIngredient = "flour";
    public static final String addInstructions = "Preheat the oven to 325";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        input1=(EditText)findViewById(R.id.addIngredientField);
        input2=(EditText)findViewById(R.id.addInstructionsField);

        saveButton =(Button)findViewById(R.id.button);
        sharedpreferences = getSharedPreferences(addIngredient, Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n  = input1.getText().toString();
                String ph  = input2.getText().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(addIngredient, n);
                editor.putString(addInstructions, ph);
                editor.commit();
                Toast.makeText(NewRecipe.this,"Thanks",Toast.LENGTH_LONG).show();
            }
        });
    }

}
