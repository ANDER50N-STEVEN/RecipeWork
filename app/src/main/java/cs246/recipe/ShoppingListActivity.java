package cs246.recipe;


import android.annotation.SuppressLint;
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
import java.util.Objects;

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
    private ArrayAdapter<String> mAdapter;


    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Spinner spinner;
    private String units;
    private String userID;
    private boolean load;
    private String display;
    private MixedFraction input;


    private ArrayList<String> shoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        spinner = findViewById(R.id.spinner1);
        final String[] measurement = new String[]{"   ", "tsp", "tbs", "cup", "floz", "box", "can", "lbs"};
        final ArrayAdapter<String> madapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, measurement);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(madapter);
        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        load = true;

        mItemEdit = findViewById(R.id.item_editText);
        mValueEdit = findViewById(R.id.value_editText);
        Button mAddButton = findViewById(R.id.add_button);
        Button mCheckoutButton = findViewById(R.id.checkout);

        mShoppingListView = findViewById(R.id.shoppingList);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
        mShoppingListView.setAdapter(mAdapter);

        Toolbar mToolBar = findViewById(R.id.toolBarView);
        mToolBar.setBackground(getResources().getDrawable(R.color.blueOfficial));

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.beet_it_blue))
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
                                intent = new Intent(ShoppingListActivity.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(ShoppingListActivity.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("© Beet It").withIcon(R.drawable.beet_it_blue));

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
                if(load) {
                    //loads the info from the database on initialization.
                    showData(dataSnapshot);
                    load = false;
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item = mItemEdit.getText().toString();
                final String value = mValueEdit.getText().toString();
                input = new MixedFraction(value);

                Log.d(TAG, "Ingredient: " + item + "  Value: " + value + "\n");

                //handle the exception if the EditText fields are null
                if (!item.equals("") && !value.equals("")) {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                                if (!snapshot.child("users").child(userID).child("ShoppingList").hasChild(item)) {

                                    if(input.getWhole() == 0){
                                            input.setDisplay(input.getNumerator() + "/" + input.getDenominator() + " " + units);
                                    }else{
                                        if (input.getNumerator() != 0) {
                                            input.setDisplay(input.getWhole() + " " + input.getNumerator() + "/" + input.getDenominator() + " " + units);
                                        }else
                                            input.setDisplay(input.getWhole() + " " + units);
                                    }
                                    Ingredient ingredient = new Ingredient(item, units, input, input.getDisplay());
                                    shoppingList.add(input.getDisplay() + " - " + item);
                                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(ingredient);
                                    mShoppingListView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    mItemEdit.setText("");
                                    mValueEdit.setText("");
                                    toastMessage("New Information has been saved.");
                                } else {
//                                    MixedFraction old = snapshot.child("users").child(userID).child("ShoppingList").child(item).getValue(MixedFraction.class);
//                                    Convert convert = new Convert(item, old, snapshot.child("users").child(userID).child("ShoppingList").child(item).child("units").getValue().toString(),
//                                            input, units, display);
                                    convertData(Integer.parseInt(snapshot.child("users").child(userID).child("ShoppingList").child(item).child("measurement").child("numerator").getValue().toString()),
                                            Integer.parseInt(snapshot.child("users").child(userID).child("ShoppingList").child(item).child("measurement").child("denominator").getValue().toString()),
                                            Integer.parseInt(snapshot.child("users").child(userID).child("ShoppingList").child(item).child("measurement").child("whole").getValue().toString()),
                                            snapshot.child("users").child(userID).child("ShoppingList").child(item).child("units").getValue().toString());
//                                    Log.d(TAG, "TTTTTTTTTTTTTEEEEEEEEEEEEEESSSSSSSSSSSSSTTTTTTTTTTTTT\n");
//                                    convert.convertData();
//                                    Ingredient ingredient = new Ingredient(convert.getIng(), convert.getNewUni(), convert.getNewFrac(), convert.getDisplay());
                                    Ingredient ingredient = new Ingredient(item, units, input);
                                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(ingredient);
                                    mItemEdit.setText("");
                                    mValueEdit.setText("");
                                    shoppingList.clear();
                                    load = true;
                                }
                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    spinner.setSelection(0);
                } else {
                    toastMessage("Fill out all the fields");
                }
            }

        });

        mCheckoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                copyRecord();
                myRef.child("users").child(userID).child("ShoppingList").removeValue();
                Toast.makeText(getApplicationContext(), "Your shopping cart is now empty!",
                        Toast.LENGTH_LONG).show();
                shoppingList.clear();
//                mShoppingListView.setAdapter(null);
                mAdapter.notifyDataSetChanged();

            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(300);
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

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
                    case 0:
                        String info = shoppingList.get(position);
                        String name = info.substring(info.lastIndexOf(" ")+1);
                        mItemEdit.setText(name);
                        mValueEdit.setText(info.substring(0, info.indexOf(" ")));
                        myRef.child("users").child(userID).child("ShoppingList").child(name).removeValue();
                        shoppingList.clear();
                        mAdapter.notifyDataSetChanged();
                        load = true;
                        Log.d(TAG, "key     -" + name);
                        break;
                    case 1:
                        String key = shoppingList.get(position);
                        key = key.substring(key.lastIndexOf(" ")+1);
                        myRef.child("users").child(userID).child("ShoppingList").child(key).removeValue();
                        shoppingList.clear();
                        mAdapter.notifyDataSetChanged();
                        load = true;
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()){
            Ingredient uInfo = ds.getValue(Ingredient.class);

            //display all the information
            assert uInfo != null;
            Log.d(TAG, "showData: database ingredient: " + uInfo.getIngredient());
            Log.d(TAG, "showData: database value: " + uInfo.getMeasurement().getDisplay());
            Log.d(TAG, "showData: database units: " + uInfo.getUnits());
            Log.d(TAG, "showData: trial 1: " + uInfo.getMeasurement().getDisplay());
            shoppingList.add(uInfo.getMeasurement().getDisplay() + " - " + uInfo.getIngredient());
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,shoppingList);
            mShoppingListView.setAdapter(adapter);
        }
    }

    private int gcd(int a, int b){
        if(a == b)
            return a;
       if(a == 0)
           return b;
       return gcd((b%a), a);
    }

    private void convertData(int numerator, int denominator, int oldValue, String dUnits){
        if(denominator == 0)
            denominator = 1;
        if(input.getDenominator() == 0)
            input.setDenominator(1);
        int multiplier = 1;
        int multiplier2 = 1;
        if(input.getDenominator() != denominator){
            multiplier2 = (input.getDenominator() / gcd(denominator, input.getDenominator()));
            multiplier = (denominator / gcd(denominator, input.getDenominator()));
        }
        switch(dUnits){
            case "tsp":
                numerator += (oldValue * denominator);
                if(Objects.equals(units, "tsp")) {
                    input.setNumerator((input.getWhole() * input.getDenominator()) + input.getNumerator());
                }
                if(Objects.equals(units, "tbs")) {
                    input.setNumerator(((input.getWhole() * input.getDenominator()) + input.getNumerator()) * 3);
                }
                if(Objects.equals(units, "cup")) {
                    input.setNumerator(((input.getWhole() * input.getDenominator()) + input.getNumerator()) * 48);
                }
                input.setDenominator((input.getDenominator() * denominator) / gcd(denominator, input.getDenominator()));
                input.setNumerator((input.getNumerator() * multiplier) + (numerator * multiplier2));
                units = "tsp";
                break;
            case "tbs":
                numerator = ((oldValue * 3 * denominator) + (numerator * 3));
                if(Objects.equals(units, "tsp")) {
                    input.setNumerator(input.getNumerator() + (input.getWhole() * input.getDenominator()));
                }
                if(Objects.equals(units, "tbs")){
                    input.setNumerator(((input.getWhole() * input.getDenominator()) + input.getNumerator()) * 3);
                }
                if(Objects.equals(units, "cup")){
                    input.setNumerator(((input.getWhole() * input.getDenominator()) + input.getNumerator()) * 48);
                }
                input.setDenominator((input.getDenominator() * denominator) / gcd(denominator, input.getDenominator()));
                input.setNumerator((input.getNumerator() * multiplier) + (numerator * multiplier2));
                units = "tsp";
                break;
            case "cup":
                numerator = ((oldValue * 48 * denominator) + (numerator * 48));
                if(Objects.equals(units, "tsp")){
                    input.setNumerator(input.getNumerator() + (input.getWhole() * input.getDenominator()));
                }
                if(Objects.equals(units, "tbs")){
                    input.setNumerator(((input.getWhole() * input.getDenominator()) + input.getNumerator()) * 3);
                }
                if(Objects.equals(units, "cup")){
                    input.setNumerator(((input.getWhole() * input.getDenominator()) + input.getNumerator()) * 48);
                }
                input.setDenominator((input.getDenominator() * denominator) / gcd(denominator, input.getDenominator()));
                input.setNumerator((input.getNumerator() * multiplier) + (numerator * multiplier2));
                units = "tsp";
                break;
            default:
                input.setWhole(oldValue + 1);
                break;
        }
        if(input.getDenominator() == 0)
            input.setDenominator(1);
        if(((input.getNumerator() / input.getDenominator()) >= 48) && (Objects.equals(units, "tsp"))) {
            units = "cup";
            input.setDenominator(input.getDenominator() * 48);
            input.setWhole((input.getNumerator() / input.getDenominator()));
            input.setNumerator(input.getNumerator() - (input.getWhole() * input.getDenominator()));
            int common = gcd(input.getNumerator(), input.getDenominator());
            input.setNumerator(input.getNumerator()/common);
            input.setDenominator(input.getDenominator()/common);
            if (input.getNumerator() != 0) {
                if ((input.getNumerator() / ((input.getDenominator() * common) / 16)) >= 3) {
                    int tempD = input.getDenominator() * 3;
                    int tempV = ((input.getNumerator() / tempD));
                    int tempN = (input.getNumerator() - (input.getWhole() * tempD));
                    common = gcd(input.getNumerator(), tempD);
                    tempN = (tempN/common);
                    tempD = (tempD/common);
                    if (tempN != 0)
                        input.setDisplay(input.getWhole() + " cup " + tempV + " " + tempN + "/" + tempD + " tbs");
                    else
                        input.setDisplay(input.getWhole() + " tbs" + tempN + " tsp");
                }
                else
                    input.setDisplay(input.getWhole() + " " + input.getNumerator() + "/" + input.getDenominator() + " " + units);
            }
            else
                input.setDisplay(input.getWhole() + " " + units);
        }else if (((input.getNumerator() / input.getDenominator()) >= 3) && (Objects.equals(units, "tsp"))) {
            Log.d(TAG, "showData: tbs \n");
            units = "tbs";
            input.setDenominator(input.getDenominator() * 3);
            input.setWhole(input.getNumerator() / input.getDenominator());
            input.setNumerator(input.getNumerator() - (input.getWhole() * input.getDenominator()));
            int common = gcd(input.getNumerator(), input.getDenominator());
            input.setNumerator(input.getNumerator()/common);
            input.setDenominator(input.getDenominator()/common);
            if (input.getNumerator() != 0)
                input.setDisplay(input.getWhole() + " " + input.getNumerator() + "/" + input.getDenominator() + " tbs");
            else
                input.setDisplay(input.getWhole() + " tbs");
        }else{
            Log.d(TAG, "showData: tsp \n");
            input.setWhole(input.getNumerator() / input.getDenominator());
            input.setNumerator(input.getNumerator() - (input.getWhole() * input.getDenominator()));
            int common = gcd(input.getNumerator(), input.getDenominator());
            input.setNumerator(input.getNumerator()/common);
            input.setDenominator(input.getDenominator()/common);
            if (input.getNumerator() != 0) {
                if (input.getWhole() != 0)
                    input.setDisplay(input.getWhole() + " " + input.getNumerator() + "/" + input.getDenominator() + " tsp");
                else
                    input.setDisplay(input.getNumerator() + "/" + input.getDenominator() + " tsp");
            }else
                input.setDisplay(input.getWhole() + "tsp");
        }
        if(input.getNumerator() == 0) {
            input.setDenominator(1);
        }
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
     * Probably should be its own class
     *
     */

    public void copyRecord() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
                    Ingredient uInfo = ds.getValue(Ingredient.class);
                    assert uInfo != null;
                    Log.d(TAG, "showData: name: " + uInfo.getIngredient());
//                    Log.d(TAG, "showData: value: " + uInfo.getMeasurement().getinput.setDisplay(());
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
                            display = dInfo.getDisplay();
                            Log.d(TAG, "showData: CRRRRRRRRRRRAAAAAAAAAZZZZZZZZZYYYYYYYYYY");
                            convertData(uInfo.getMeasurement().getNumerator(),
                                    uInfo.getMeasurement().getDenominator(),
                                    uInfo.getMeasurement().getWhole(),
                                    uInfo.getUnits());
                            Log.d(TAG, "showData: CRRRRRRRRRRRAAAAAAAAAZZZZZZZZZYYYYYYYYYY2");
                            Ingredient newIngredient = new Ingredient(uInfo.getIngredient(), units, input);
                            myRef.child("users").child(userID).child("Pantry").child(uInfo.getIngredient()).setValue(newIngredient);
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