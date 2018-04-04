package cs246.recipe;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
                                        Integer whole = 0;
                                        Integer denominator = 0;
                                        Integer numerator = 0;
                                        MixedFraction measurement;

                                        String ingredientString = "Error";

                                        if (ingredient.hasChild("units")) {
                                            Log.d("dataCapture", "Has child units");
                                            units = String.valueOf(ingredient.child("units").getValue());
                                        }
                                        if (ingredient.hasChild("ingredient")) {
                                            Log.d("dataCapture", "Has child ingredient");
                                            ingredientString = String.valueOf(ingredient.child("ingredient").getValue());
                                        }

                                        if (ingredient.hasChild("measurement")) {
                                            if (ingredient.child("measurement").hasChildren()) {
                                                if (ingredient.child("measurement").hasChild("whole")) {
                                                    Log.d("dataCapture", "Has child whole");
                                                    whole = Integer.valueOf(String.valueOf(ingredient.child("measurement").child("whole").getValue()));
                                                }
                                                if (ingredient.child("measurement").hasChild("numerator")) {
                                                    Log.d("dataCapture", "Has child numerator");
                                                    numerator = Integer.valueOf(String.valueOf(ingredient.child("measurement").child("numerator").getValue()));
                                                }
                                                if (ingredient.child("measurement").hasChild("denominator")) {
                                                    Log.d("dataCapture", "Has child denominator");
                                                    denominator = Integer.valueOf(String.valueOf(ingredient.child("measurement").child("denominator").getValue()));
                                                }
                                            }
                                        }

                                        measurement = new MixedFraction(whole, numerator, denominator);
                                        Ingredient ingredientObject = new Ingredient(ingredientString, units, measurement);
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

    public void R_goBackClick(View view) {
        finish();
    }

    public void R_make(View view) {
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Ingredient> ingredients = new ArrayList<>();
//                for(DataSnapshot ds: dataSnapshot.child("users").child(userID).child("Pantry").getChildren()) {
////                    String ingredient = "";
////                    String units = "";
////                    String display = "";
////                    MixedFraction measurement = new MixedFraction();
////                    if (ds.hasChild("ingredient")) {
////                        ingredient = String.valueOf(ds.child("ingredient").getValue());
////                    }
////                    if (ds.hasChild("units")) {
////                        units = String.valueOf(ds.child("units").getValue());
////                    }
////                    if (ds.hasChild("display")) {
////                        display = String.valueOf(ds.child("display").getValue());
////                    }
////                    if (ds.hasChild("measurement")) {
////                        int whole = 0;
////                        int numerator = 0;
////                        int denominator = 0;
////                        if (ds.child("measurement").hasChild("whole")) {
////                            whole = Integer.valueOf(String.valueOf(ds.child("whole").getValue()));
////                        }
////                        if (ds.child("measurement").hasChild("numerator")) {
////                            numerator = Integer.valueOf(String.valueOf(ds.child("numerator").getValue()));
////                        }
////                        if (ds.child("measurement").hasChild("denominator")) {
////                            denominator = Integer.valueOf(String.valueOf(ds.child("denominator").getValue()));
////                        }
////                        measurement = new MixedFraction(whole, numerator, denominator);
////                    }
////
////                    Ingredient uInfo = new Ingredient(ingredient, units, measurement, display);
////                    assert uInfo != null;
////                    ingredients.add(uInfo);
//                }
//                for (Ingredient in : adapter.getIngredientList()) {
//                    ingredients.add(in);
//                }
//                for (Ingredient in : ingredients) {
//                    reference.child("users").child(userID).child("Pantry").child(in.getIngredient()).setValue(in);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        List<Ingredient> ingredients = new ArrayList<>();

        for (Ingredient in : adapter.getIngredientList()) {
            ingredients.add(in);
        }
        for (Ingredient in : ingredients) {
            reference.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).setValue(in);
        }
    }
}

