package cs246.recipe;

import android.content.Intent;
import android.provider.ContactsContract;
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

    TextView recipeNameEdit;
    ListView ingredientsList;
    TextView instructionsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        recipeNameEdit = findViewById(R.id.R_recipeName);
        ingredientsList = findViewById(R.id.R_ingredientList);
        instructionsField = findViewById(R.id.R_instructionsField);

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
                                if (ds.child("ingredients").hasChildren()) {
                                    Log.d("dataCapture", "ingredients has children");
                                    for (DataSnapshot ingredient : ds.child("ingredients").getChildren()) {
                                        String units = "Error";
                                        Integer value = 0;
                                        Integer denominator = 0;
                                        Integer numerator = 0;

                                        String ingredientString = "Error";

                                        if (ingredient.hasChild("units")) {
                                            Log.d("dataCapture", "Has child units");
                                            units = String.valueOf(ingredient.child("units").getValue());
                                        }
                                        if (ingredient.hasChild("value")) {
                                            Log.d("dataCapture", "Has child value");
                                            value = Integer.valueOf(String.valueOf(ingredient.child("value").getValue()));
                                        }
                                        if (ingredient.hasChild("ingredient")) {
                                            Log.d("dataCapture", "Has child ingredient");
                                            ingredientString = String.valueOf(ingredient.child("ingredient").getValue());
                                        }
                                        if (ingredient.hasChild("numerator")) {
                                            Log.d("dataCapture", "Has child numerator");
                                            numerator = Integer.valueOf(String.valueOf(ingredient.child("numerator")));
                                        }
                                        if (ingredient.hasChild("denominator")) {
                                            Log.d("dataCapture", "Has child denominator");
                                            denominator = Integer.valueOf(String.valueOf(ingredient.child("denominator")));
                                        }
                                        Ingredient ingredientObject = new Ingredient(ingredientString, value, numerator, denominator, units);
                                        adapter.add(ingredientObject);
                                    }
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

