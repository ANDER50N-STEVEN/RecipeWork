package cs246.recipe;


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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity to display the user's shopping list.  Will utilize Firebase Database
 * to save ingredients with the quantity, consisting of value of measurements.
 *
 * @author Steven Anderson
 */

public class ShoppingListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddToDatabase";

    private ListView mShoppingListView;
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

    private ArrayList<String> shoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        spinner = findViewById(R.id.spinner1);
        String[] measurement = new String[]{"tsp", "tbs", "cup", "floz"};
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
                String item = mItemEdit.getText().toString();
                String value = mValueEdit.getText().toString();

                Log.d(TAG, "Ingredient: " + item + "  Value: " + value + "\n");


                //handle the exception if the EditText fields are null
                if (!item.equals("") && !value.equals("")) {
                    Ingredient ingredient = new Ingredient(item, value, units);
                    shoppingList.add(value + " " + units + " - " + item);
                    //    mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingList);
                    mShoppingListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mItemEdit.setText("");
                    mValueEdit.setText("");

                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(ingredient);
                    myRef.child("users").child(userID).child("SavedData").child("ShoppingList").setValue(shoppingList);

//                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(value).child(units);
                    toastMessage("New Information has been saved.");
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
    }

    private void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()){
            Ingredient uInfo = ds.getValue(Ingredient.class);

            //display all the information
            Log.d(TAG, "showData: name: " + uInfo.getIngredient());
            Log.d(TAG, "showData: email: " + uInfo.getValue());
            Log.d(TAG, "showData: phone_num: " + uInfo.getUnits());

            shoppingList.add(uInfo.getValue() + " " + uInfo.getUnits() + " - " + uInfo.getIngredient());

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,shoppingList);
            mShoppingListView.setAdapter(adapter);

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
                    Log.d(TAG, "showData: name: " + uInfo.getIngredient());
                    Log.d(TAG, "showData: email: " + uInfo.getValue());
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