package cs246.recipe;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    private static final String TAG = "AddToDatabase";

    private ListView mShoppingListView;
    private EditText mItemEdit;
    private ArrayAdapter<String> mAdapter;
    ArrayList<String> shoppingList = new ArrayList<>();
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
       FirebaseUser user = mAuth.getCurrentUser();
       final String userID = user.getUid();

        mShoppingListView = findViewById(R.id.shoppingList);
        mItemEdit = findViewById(R.id.item_editText);
        Button mAddButton = findViewById(R.id.add_button);
        Button mCheckoutButton = findViewById(R.id.checkout);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
        mShoppingListView.setAdapter(mAdapter);

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

                Log.d(TAG, "Ingredient: " + item + "\n");


                //handle the exception if the EditText fields are null
                if (!item.equals("")) {
                    shoppingList.add(item);
                    //    mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingList);
                    mShoppingListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mItemEdit.setText("");

                    myRef.child("users").child(userID).child("ShoppingList").push().setValue(item);
                    toastMessage("New Information has been saved.");
                } else {
                    toastMessage("Fill out all the fields");
                }
            }

        });

        mCheckoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                shoppingList = null;
                myRef.child("users").child(userID).child("ShoppingList").removeValue();
                Toast.makeText(getApplicationContext(), "Your shopping cart is now empty!",
                        Toast.LENGTH_LONG).show();
            }
        });
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

}

