package cs246.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Display individual recipe
 */
public class DisplayRecipe extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference shoppingList;
    DatabaseReference photos;

    List<Ingredient> ingredients;
    IngredientArrayAdapter adapter;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    String userID;
    String recipeName;

    TextView recipeNameEdit;
    ListView ingredientsList;
    TextView instructionsField;
    ImageView recipeImage;

    /**
     * defining variable, buttons and Firebase authentication
     * @param savedInstanceState instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        recipeNameEdit = findViewById(R.id.R_recipeName);
        ingredientsList = findViewById(R.id.R_ingredientList);
        instructionsField = findViewById(R.id.R_instructionsField);
        recipeImage = findViewById(R.id.R_recipeImage);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Intent intent = getIntent();
        recipeName = intent.getStringExtra("recipeName");
        recipeNameEdit.setText(recipeName);

        adapter = new IngredientArrayAdapter(getApplicationContext());
        ingredientsList.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users").child(userID).child("Cookbook").child(recipeName);
        reference = database.getReference();
        ingredients = new ArrayList<>();
        Toolbar mToolBar = findViewById(R.id.toolBarView);
        mToolBar.setBackground(getResources().getDrawable(R.color.blueOfficial ));

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.beet_it_logo_white))
                )
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName(R.string.title_home).withIcon(R.drawable.ic_home_black_24dp);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.shopping_cart).withIcon(R.drawable.ic_shopping_cart_black_24dp);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.pantry_button).withIcon(R.drawable.ic_shopping_basket_black_24dp);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.add_recipe).withIcon(R.drawable.ic_library_add_black_24dp);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.about_us).withIcon(R.drawable.ic_info_outline_black_24dp);
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.sign_out).withIcon(R.drawable.ic_power_settings_new_black_24dp);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(mToolBar)
                .addDrawerItems(
                        item1, item2, item3, item4, new DividerDrawerItem(), item5, item6
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch(position)
                        {
                            case 1:
                                Intent intent = new Intent(DisplayRecipe.this, CookBookActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(DisplayRecipe.this, ShoppingListActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(DisplayRecipe.this, PantryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(DisplayRecipe.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(DisplayRecipe.this, AboutUs.class);
                                startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(DisplayRecipe.this, LoginActivity.class);
                                startActivity(intent);
                                mAuth.signOut();
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("© Beet It").withIcon(R.drawable.beet_it_blue));


        reference.addValueEventListener(new ValueEventListener() {
            /**
             * retrieves data into adapter from firebase database
             * @param dataSnapshot instance contains data from a Firebase Database location
             */
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
                                                                                Integer denominator = 1;
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
                                        if (dataSnapshot.child("users").child(userID).hasChild("ShoppingList")) {
                                                Log.d("dataCapture", "Has child ShoppingList");
                                                for(DataSnapshot ds: dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
                                                        String ingredient = "";
                                                        String units = "";
                                                        MixedFraction measurement = new MixedFraction();
                                                        if (ds.hasChild("ingredient")) {
                                                                ingredient = String.valueOf(ds.child("ingredient").getValue());
                                                            }
                                                        if (ds.hasChild("units")) {
                                                                units = String.valueOf(ds.child("units").getValue());
                                                            }
                                                        if (ds.hasChild("measurement")) {
                                                                int whole = 0;
                                                                int numerator = 0;
                                                                int denominator = 0;
                                                                if (ds.child("measurement").hasChild("whole")) {
                                                                       try {
                                                                                whole = Integer.valueOf(String.valueOf(ds.child("whole").getValue()));
                                                                            } catch (Exception e) {
                                                                                whole = 0;
                                                                            }
                                                                  }
                                                                if (ds.child("measurement").hasChild("numerator")) {
                                                                        try {
                                                                                numerator = Integer.valueOf(String.valueOf(ds.child("numerator").getValue()));
                                                                            } catch (Exception e) {
                                                                                whole = 0;
                                                                            }
                                                                    }
                                                                if (ds.child("measurement").hasChild("denominator")) {
                                                                        try {
                                                                                denominator = Integer.valueOf(String.valueOf(ds.child("denominator").getValue()));
                                                                            } catch (Exception e) {
                                                                                whole = 0;
                                                                            }
                                                                   }
                                                                measurement = new MixedFraction(whole, numerator, denominator);
                                                            }

                                                                                            Ingredient uInfo = new Ingredient(ingredient, units, measurement);
                                                        assert uInfo != null;
                                                        ingredients.add(uInfo);
                                                    }
                                            }
                                    }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ingredients = new ArrayList<>();
        shoppingList = database.getReference().child("users").
                child(userID).child("ShoppingList");
        shoppingList.addValueEventListener(new ValueEventListener() {
            /**
             * retrieves data from firebase
             * @param dataSnapshot instance contains data from a Firebase Database location
             */
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

        photos = database.getReference().child("users").child(userID).child("Photos").child(recipeName);
        photos.addValueEventListener(new ValueEventListener() {
            @Override
            /**
             * loads the picture using picasso
             */
            public void onDataChange(DataSnapshot dataSnapshot) {
                UploadImageObject upload = dataSnapshot.getValue(UploadImageObject.class);
                if (upload == null)
                    return;

                    Picasso.with(DisplayRecipe.this)
                            .load(upload.getmImageUrl())
                            .fit()
                            .centerCrop()
                            .into(recipeImage);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * ends the page
     * @param view view that is clicked on
     */
    public void R_goBackClick(View view) {
        finish();
    }

    /**
     * generates a list of ingredients from recipe and adds them to shopping list
     * @param view view that is clicked on
     */
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
                    } else if (in2.getUnits().equals(in.getUnits())) {
                        int index = ingredients.indexOf(in2);
                        MergeIngredients mergeIngredients = new MergeIngredients();
                        ingredients.set(index, mergeIngredients.simpleMerge(in2, in));
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