package cs246.recipe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PantryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddToDatabase";

    private ListView mPantryListView;
    private EditText mItemEdit;
    private EditText mValueEdit;
    private ArrayAdapter<String> mAdapter;
    ArrayList<String> pantryList = new ArrayList<>();
    private int num;
    private int den;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Spinner spinner;
    private String units;
    private String userID;
    private boolean load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        num = 0;
        den = 1;

        spinner = findViewById(R.id.spinner1);
        String[] measurement = new String[]{"   ", "tsp", "tbs", "cup", "floz", "box", "can", "lbs"};
        ArrayAdapter<String> madapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, measurement);
        madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(madapter);
        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        load = true;

        mPantryListView = findViewById(R.id.pantryList);
        mItemEdit = findViewById(R.id.item_editText);
        mValueEdit = findViewById(R.id.value_editText);
        Button mAddButton = findViewById(R.id.add_button);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pantryList);
        mPantryListView.setAdapter(mAdapter);


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
                final int nValue = Integer.parseInt(value);

                Log.d(TAG, "Ingredient: " + item + "  Value: " + value + "\n");


                //handle the exception if the EditText fields are null
                if (!item.equals("") && !value.equals("")) {
                    Ingredient ingredient = new Ingredient(item, nValue, num, den, units);
                    pantryList.add(value + " " + units + " - " + item);
                    //    mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingList);
                    mPantryListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mItemEdit.setText("");
                    mValueEdit.setText("");

                    myRef.child("users").child(userID).child("Pantry").child(item).setValue(ingredient);

//                    myRef.child("users").child(userID).child("ShoppingList").child(item).setValue(value).child(units);
                    toastMessage("New Information has been saved.");
                } else {
                    toastMessage("Fill out all the fields");
                }
            }

        });

    }

    private void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("Pantry").getChildren()){
            Ingredient uInfo = ds.getValue(Ingredient.class);

            //display all the information
            Log.d(TAG, "showData: name: " + uInfo.getIngredient());
            Log.d(TAG, "showData: email: " + uInfo.getValue());
            Log.d(TAG, "showData: phone_num: " + uInfo.getUnits());

            pantryList.add(uInfo.getValue() + " " + uInfo.getUnits() + " - " + uInfo.getIngredient());

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,pantryList);
            mPantryListView.setAdapter(adapter);

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

