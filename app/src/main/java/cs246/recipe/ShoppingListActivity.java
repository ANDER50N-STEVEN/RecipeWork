package cs246.recipe;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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

import java.util.ArrayList;

/**
 * Activity to display the user's shopping list.  Will utilize Firebase Database
 * to save ingredients with the quantity, consisting of value of measurements.
 *
 * @author Steven Anderson
 */

public class ShoppingListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddToDatabase";

    private SwipeMenuListView mShoppingListView;
    private EditText mItemEdit;
    private EditText mValueEdit;
    private IngredientArrayAdapter mAdapter;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Spinner spinner;
    private String units;
    private String userID;
    private boolean load;
    private String display;
    private MixedFraction input;


    private ArrayList<Ingredient> shoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        TextView pageName = findViewById(R.id.pageName);
        pageName.setText("Shopping List");

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        Toolbar mToolBar = findViewById(R.id.toolBarView);
        mToolBar.setBackground(getResources().getDrawable(R.color.blueOfficial ));

        //displays navigation bar on top of page

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
                                Intent intent = new Intent(ShoppingListActivity.this, CookBookActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(ShoppingListActivity.this, ShoppingListActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(ShoppingListActivity.this, PantryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(ShoppingListActivity.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(ShoppingListActivity.this, AboutUs.class);
                                startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(ShoppingListActivity.this, LoginActivity.class);
                                startActivity(intent);
                                mAuth.signOut();
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("© Beet It").withIcon(R.drawable.beet_it_blue));

        spinner = findViewById(R.id.spinner1);
        final String[] measurement = new String[]{"   ", "tsp", "tbs", "cup", "floz", "box", "can", "lbs"};
        final ArrayAdapter<String> madapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, measurement);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(madapter);
        spinner.setOnItemSelectedListener(this);
        final DatabaseReference login = myRef.child("users").child(userID);

        mItemEdit = findViewById(R.id.item_editText);
        mValueEdit = findViewById(R.id.value_editText);
        final Button mAddButton = findViewById(R.id.add_button);
        Button mCheckoutButton = findViewById(R.id.checkout);

        mShoppingListView = findViewById(R.id.shoppingList);
        mAdapter = new IngredientArrayAdapter(this);
        mShoppingListView.setAdapter(mAdapter);

        /**
         * This not only activates when a state change occurs but also when the activity
         * fist starts up
         */

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        // Read from the database

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                    showData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Add new ingredient checks if it already exists and then either adds to it or creates new ingredient

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item = mItemEdit.getText().toString();
                final String value = mValueEdit.getText().toString();
                input = new MixedFraction(value);

                Log.d(TAG, "Ingredient: " + item + "  Value: " + value + "\n");

                //handle the exception if the EditText fields are null
                if (!item.equals("") && !value.equals("")) {
                    Ingredient ingredient = mAdapter.find(item);
                    Ingredient ingredient1 = new Ingredient(item, units, input);
                    if (ingredient == null) {
                        myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(ingredient1);
                    } else {
                        if (ingredient.getIngredient().equals(ingredient1.getIngredient())) {
                            if (!ingredient1.getUnits().equals("") &&
                                    !ingredient1.getUnits().equals("floz") &&
                                    !ingredient.getUnits().equals("") &&
                                    !ingredient.getUnits().equals("floz")) {
                                Log.d("merging", "merging2");
                                int index = mAdapter.indexOf(ingredient);
                                MergeIngredients mergeIngredients = new MergeIngredients();
                                Ingredient merged = mergeIngredients.merge(ingredient, ingredient1);
                                myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(merged);
                            }
                        }
                    }
                    // reset all values
                    mItemEdit.setText("");
                    mValueEdit.setText("");
                    toastMessage("New Information has been saved.");
                    spinner.setSelection(0);
                } else {
                    toastMessage("Fill out all the fields");
                }
            }

        });

        //empties shopping list and moves items to pantry
        mCheckoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                copyRecord();
                myRef.child("users").child(userID).child("ShoppingList").removeValue();
                Toast.makeText(getApplicationContext(), "Your shopping cart is now empty!",
                        Toast.LENGTH_LONG).show();
                shoppingList.clear();
                mShoppingListView.removeAllViewsInLayout();
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();

            }
        });

        // currently only implements swipe to delete but could be implement additional functions
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        getApplicationContext());
                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                // set item width
//                openItem.setWidth(300);
//                // set item title
//                openItem.setTitle("Edit");
//                // set item title fontsize
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(300);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator

        mShoppingListView.setMenuCreator(creator);

        mShoppingListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
//                    case 0:
//                        String info = shoppingList.get(position);
//                        String name = info.substring(info.lastIndexOf(" ")+1);
//                        mItemEdit.setText(name);
//                        mValueEdit.setText(info.substring(0, info.indexOf(" ")));
//                        myRef.child("users").child(userID).child("ShoppingList").child(name).removeValue();
//                        shoppingList.clear();
//                        mAdapter.notifyDataSetChanged();
//                        load = true;
//                        Log.d(TAG, "key     -" + name);
//                        break;
                    case 0:
                        Ingredient key = mAdapter.getItem(position);
                        myRef.child("users").child(userID).child("ShoppingList").child(key.getIngredient()).removeValue();
//                        mAdapter.remove(position);
//                        mAdapter.notifyDataSetChanged();
//                        load = true;
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    // clears and then redisplays data

    private void showData(DataSnapshot dataSnapshot){
        mAdapter.clear();
        if (dataSnapshot.child("users").child(userID).hasChild("ShoppingList")) {
            for (DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
                Ingredient uInfo = ds.getValue(Ingredient.class);
                if (uInfo == null)
                    continue;

                //display all the information
                Log.d(TAG, "showData: database ingredient: " + uInfo.getIngredient());
                Log.d(TAG, "showData: database value: " + uInfo.getMeasurement().getDisplay());
                Log.d(TAG, "showData: database units: " + uInfo.getUnits());
                Log.d(TAG, "showData: trial 1: " + uInfo.getMeasurement().getDisplay());
                mAdapter.add(uInfo);
            }
        }

        mShoppingListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * function called with checkout.  copies everything in shopping list and adds or creates it in pantry
     *
     */

    public void copyRecord() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
                    Ingredient uInfo = ds.getValue(Ingredient.class);
                    if (uInfo == null)
                        continue;
                    Log.d(TAG, "showData: name: " + uInfo.getIngredient());
                    Log.d(TAG, "showData: phone_num: " + uInfo.getUnits());
                    Log.d(TAG, "showData: value: " + uInfo.getMeasurement().getDenominator());
                    Log.d(TAG, "showData: value: " + uInfo.getMeasurement().getNumerator());
                    Log.d(TAG, "showData: value: " + uInfo.getMeasurement().getWhole());
                    if (!dataSnapshot.child("users").child(userID).child("Pantry").hasChild(uInfo.getIngredient())) {
                        myRef.child("users").child(userID).child("Pantry").child(uInfo.getIngredient()).setValue(uInfo);
                    } else {
                            Ingredient dInfo = dataSnapshot.child("users").child(userID).child("Pantry").child(uInfo.getIngredient()).getValue(Ingredient.class);
                            units = dInfo.getUnits();
                            input = dInfo.getMeasurement();
                            MergeIngredients mergeIngredients = new MergeIngredients();
                            Ingredient merged = mergeIngredients.merge(uInfo, dInfo);
                            myRef.child("users").child(userID).child("Pantry").child(uInfo.getIngredient()).setValue(merged);
                    }
                    shoppingList.clear();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

            /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        units = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}