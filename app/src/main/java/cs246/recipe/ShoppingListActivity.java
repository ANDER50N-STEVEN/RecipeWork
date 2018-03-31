package cs246.recipe;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private boolean wholeNum;
    private boolean fraction;
    private int num;
    private int den;
    private int nValue;
    private String display;


    private ArrayList<String> shoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        spinner = findViewById(R.id.spinner1);
        String[] measurement = new String[]{"   ", "tsp", "tbs", "cup", "floz", "box", "can", "lbs"};
        ArrayAdapter<String> madapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, measurement);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(madapter);
        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        load = true;
        wholeNum = false;
        fraction = false;



        mItemEdit = findViewById(R.id.item_editText);
        mValueEdit = findViewById(R.id.value_editText);
        Button mAddButton = findViewById(R.id.add_button);
        Button mCheckoutButton = findViewById(R.id.checkout);

        mShoppingListView = findViewById(R.id.shoppingList);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
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
                //if(value.contains("/")){
              //      String [] part= value.split("/");

              //  }

                Log.d(TAG, "Ingredient: " + item + "  Value: " + value + "\n");


                //handle the exception if the EditText fields are null
                if (!item.equals("") && !value.equals("")) {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (value.contains("/")) {
                                fraction = true;
                                String[] rat = value.split("/");
                                num = Integer.parseInt(rat[0]);
                                den = Integer.parseInt(rat[1]);
                                nValue = 0;
                                Log.d(TAG, "Ingredient: " + item + "  Value: " + num + " / " + den + "\n");
                            }else{
                                wholeNum = true;
                                nValue = Integer.parseInt(value);
                                Log.d(TAG, "Ingredient after parsing it: " + nValue + "     " + units + "\n");
                                den = 1;
                                num = 0;
                            }

                                if (!snapshot.child("users").child(userID).child("ShoppingList").hasChild(item)) {

                                    if(fraction){
                                            display = (num + "/" + den + " " + units);
                                    }else{
                                        if (wholeNum && fraction) {
                                            display = (nValue + " " + num + "/" + den + " " + units);
                                        }else
                                            display = (nValue + " " + units);
                                    }
                                    MixedFraction measureent = new MixedFraction(nValue, num, den);
                                    Ingredient ingredient = new Ingredient(item, units, measureent, display);
                                    shoppingList.add(display + " - " + item);
                                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(ingredient);
                                    mShoppingListView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    mItemEdit.setText("");
                                    mValueEdit.setText("");
                                    toastMessage("New Information has been saved.");
                                } else {
                                    convertData(Integer.parseInt(snapshot.child("users").child(userID).child("ShoppingList").child(item).child("numerator").getValue().toString()),
                                            Integer.parseInt(snapshot.child("users").child(userID).child("ShoppingList").child(item).child("denominator").getValue().toString()),
                                            Integer.parseInt(snapshot.child("users").child(userID).child("ShoppingList").child(item).child("value").getValue().toString()),
                                            snapshot.child("users").child(userID).child("ShoppingList").child(item).child("units").getValue().toString(),
                                            item);

                                    MixedFraction measurement = new MixedFraction(nValue, num, den);
                                    Ingredient ingredient = new Ingredient(item, units, measurement, display);
                                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(ingredient);
                                    mItemEdit.setText("");
                                    mValueEdit.setText("");
                                    shoppingList.clear();
                                   // mAdapter.notifyDataSetChanged();
                                    load = true;
                                }
                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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
                shoppingList = null;
                mShoppingListView.setAdapter(null);
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
            shoppingList.add(uInfo.getDisplay() + " - " + uInfo.getIngredient());
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,shoppingList);
            mShoppingListView.setAdapter(adapter);

        }
    }

    private int gcd(int oldD, int newD){
        while (newD > 0)
        {
            int temp = newD;
            newD = oldD % newD; // % is remainder
            oldD = temp;
        }
        return oldD;
    }

    private void convertData(int numerator, int denominator, int oldValue, String dUnits, String item){
        Log.d(TAG, "showData: nValue: " + nValue + "\n");
        Log.d(TAG, "showData: nValue: " + oldValue + "\n");
        int multiplier = 1;
        int multiplier2 = 1;
        if(wholeNum)
            num = (nValue * den);
        Log.d(TAG, "showData: num: " + num + "\n");
        Log.d(TAG, "showData: den: " + den + "\n");
        if(oldValue != 0)
            numerator += (oldValue * denominator);
        if(den != denominator){
            multiplier = (gcd(denominator, den) / den);
            multiplier2 = (gcd(denominator, den) / denominator);
            den = gcd(denominator, den);
        }
        num *= multiplier;
        numerator *= multiplier2;
        wholeNum = false;
        fraction = true;

        switch(dUnits){
            case "tsp":
                if(Objects.equals(units, "tsp")) {
                    break;
                }
                if(Objects.equals(units, "tbs")) {
                    den *= 3;
                    num *= 3;
                }
                if(Objects.equals(units, "cup")) {
                    den *= 48;
                    num *= 48;
                }
                den = gcd(denominator, den);
                num += numerator;
                units = "tsp";
                break;
            case "tbs":
                numerator *= 3;
                if(Objects.equals(units, "tsp")) {
                   break;
                }
                if(Objects.equals(units, "tbs")){
                    num *= 3;
                }
                if(Objects.equals(units, "cup")){
                    num *= 48;
                }
                den = gcd(denominator, den);
                num += numerator;
                units = "tsp";
                break;
            case "cup":
                numerator *= 48;
                if(Objects.equals(units, "tsp")){
                    break;
                }
                if(Objects.equals(units, "tbs")){
                   num *= 3;
                }
                if(Objects.equals(units, "cup")){
                    num *= 48;
                }
                den = gcd(denominator, den);
                num += numerator;
                units = "tsp";
                break;
            default:
                nValue = oldValue + 1;
                break;
        }
        int tempV;
        int tempN;
        int tempD;
        Log.d(TAG, "showData: before convert nValue: " + nValue + "\n");
        if(((num / 48) > 0) && (Objects.equals(units, "tsp"))) {
            Toast.makeText(getApplicationContext(), "changing to cups",
                    Toast.LENGTH_LONG).show();
            units = "cup";
            nValue = (num / (den * 48));
            Log.d(TAG, "showData: after convert nValue: " + nValue + "\n");
            num = (num % 48);
            den = 48;
            if (num != 0) {
                if ((num / 3) > 0) {
                    Log.d(TAG, "showData: needs to be tbs: " + num + "\n");
                    tempV = (num / (den / 3));
                    tempN = (num % 3);
                    tempD = 3;
                    if (tempN != 0)
                        display = (nValue + " cup " + tempV + " " + tempN + "/" + tempD + " tbs");
                    else
                        display = (tempV + " tbs" + tempN + " tsp");
                }
                else
                    display = (nValue + " " + num + "/" + den + " " + units);
            }
            else
                display = (nValue + " " + units);
            wholeNum = true;

        }else if (((num / 3) > 0) && (Objects.equals(units, "tsp"))) {
            Toast.makeText(getApplicationContext(), "changing to tbs",
                    Toast.LENGTH_LONG).show();
            nValue = (num /(den * 3));
            num = (num % 3);
            den = 3;
            if (num != 0)
                display = (nValue + " " + num + "/" + den + " tbs");
            else
                display = (nValue + " tbs");
            wholeNum = true;
        }else{
            if (num != 0)
                display = (num + "/" + den + " tsp");
            else
                myRef.child("users").child(userID).child("ShoppingList").child(item).removeValue();
        }
        if(num == 0) {
            fraction = false;
            den = 1;
            Log.d(TAG, "showData: n = 0 nValue: " + nValue + "\n");
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
                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
                    Ingredient uInfo = ds.getValue(Ingredient.class);
                    // Ingredient ingredient = new Ingredient(uInfo.getIngredient(), uInfo.getValue(), uInfo.getUnits());
                    assert uInfo != null;
                    Log.d(TAG, "showData: name: " + uInfo.getIngredient());
                    Log.d(TAG, "showData: value: " + uInfo.getMeasurement().getDisplay());
                    Log.d(TAG, "showData: phone_num: " + uInfo.getUnits());
                    myRef.child("users").child(userID).child("Pantry").child(uInfo.getIngredient()).setValue(uInfo);
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