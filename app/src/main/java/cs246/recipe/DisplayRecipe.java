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

public class DisplayRecipe extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference shoppingList;
    DatabaseReference photos;

    List<Ingredient> ingredients;
    IngredientArrayAdapter adapter;
    FirebaseUser user;
    String userID;
    String recipeName;

    TextView recipeNameEdit;
    ListView ingredientsList;
    TextView instructionsField;
    ImageView recipeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        recipeNameEdit = findViewById(R.id.R_recipeName);
        ingredientsList = findViewById(R.id.R_ingredientList);
        instructionsField = findViewById(R.id.R_instructionsField);
        recipeImage = findViewById(R.id.R_recipeImage);

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
                        new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.beet_it_blue))
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
                            case 5:
                                intent = new Intent(DisplayRecipe.this, AboutUs.class);
                                startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(DisplayRecipe.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("© Beet It").withIcon(R.drawable.beet_it_blue));

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

        photos = database.getReference().child("users").child(userID).child("Photos").child(recipeName);
        photos.addValueEventListener(new ValueEventListener() {
            @Override
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

