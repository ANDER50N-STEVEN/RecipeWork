package cs246.recipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayRecipe extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;

    IngredientArrayAdapter adapter;
    FirebaseUser user;
    String userID;
    String recipeName;
    int num;
    int den;

    TextView recipeNameEdit;
    ListView ingredientsList;
    TextView instructionsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        recipeNameEdit = (TextView) findViewById(R.id.R_recipeName);
        ingredientsList = (ListView) findViewById(R.id.R_ingredientList);
        instructionsField = (TextView) findViewById(R.id.R_instructionsField);
        num = 0;
        den = 1;

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Intent intent = getIntent();
        recipeName = intent.getStringExtra("recipeName");
        recipeName = "Crepe";
        recipeNameEdit.setText(recipeName);

        adapter = new IngredientArrayAdapter(getApplicationContext());
        ingredientsList.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataCapture", "checking for data to capture");
                if (dataSnapshot.child("users").hasChild(userID)) {
                    Log.d("dataCapture", "Has child userID");
                    if (dataSnapshot.child("users").child(userID).hasChild("Cookbook")) {
                        Log.d("dataCapture", "Has child Cookbook");
                        if (dataSnapshot.child("users").child(userID).child("Cookbook").hasChild(recipeName)) {
                            Log.d("dataCapture", "Has child recipe name");
                            DataSnapshot ds = dataSnapshot.child("users").child(userID).child("Cookbook").child(recipeName);

                            if (ds.hasChild("instructions")) {
                                Log.d("dataCapture", "Has child instructions");
                                instructionsField.setText(String.valueOf(ds.child("instructions").getValue()));
                            }

                            if (ds.hasChild("ingredients")) {
                                Log.d("dataCapture", "Has child ingredients");
                                for (DataSnapshot instructions : dataSnapshot.child("ingredients").getChildren()) {
                                    String units = String.valueOf(instructions.child("units").getValue());
                                    int value = Integer.parseInt(instructions.child("value").getValue().toString());
                                    String ingredientString = String.valueOf(instructions.child("ingredient").getValue());
                                    Ingredient ingredient = new Ingredient(ingredientString, value, num, den, units);
                                    adapter.add(ingredient);
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
