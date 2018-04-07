package cs246.recipe;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    DatabaseReference shoppingList;

    List<Ingredient> ingredients;
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
        reference = database.getReference().child("users").child(userID).child("Cookbook").child(recipeName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataCapture", "checking for data to capture");
                RecipeObject recipeObject = dataSnapshot.getValue(RecipeObject.class);
                instructionsField.setText(recipeObject.getInstructions());
                adapter.clear();
                adapter.addAll(recipeObject.getIngredients());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ingredients = new ArrayList<>();
        shoppingList = database.getReference().child("users").child(userID).child("ShoppingList");
        shoppingList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ingredients.clear();
                for (DataSnapshot ingredient: dataSnapshot.getChildren()) {
                    if (ingredient == null)
                        continue;
                    Ingredient ingredient1 = ingredient.getValue(Ingredient.class);
                    ingredients.add(ingredient1);
                }
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
        for (Ingredient in : adapter.getIngredientList()) {
            boolean found = false;
            for (Ingredient in2 : ingredients) {
                Log.d("merging", "merging");
                if (in2.getIngredient().equals(in.getIngredient())) {
                    if (!in.getUnits().equals("") &&
                            !in.getUnits().equals("floz") &&
                            !in2.getUnits().equals("") &&
                            !in2.getUnits().equals("floz")) {
                        Log.d("merging", "merging2");
                        int index = ingredients.indexOf(in2);
                        MergeIngredients mergeIngredients = new MergeIngredients();
                        ingredients.set(index, mergeIngredients.merge(in2, in));
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                ingredients.add(in);
            }
        }
        for (Ingredient in : ingredients) {
            shoppingList.child(in.getIngredient()).setValue(in);
        }

        Toast.makeText(this, "Ingredients Added to Shopping List", Toast.LENGTH_SHORT).show();
    }
}

