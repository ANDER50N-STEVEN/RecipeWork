package cs246.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewRecipe extends AppCompatActivity {

    private static final String TAG = "MyFirebase";
    private EditText input2;
    private Button saveButton;
    private ListView newIngredient;
    private EditText mItemEdit;
    private ArrayAdapter<String> mAdapter;

    public static String addIngredient = "flour";
    public static String addInstructions = "Preheat the oven to 325";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        /* ****************************
        *  Getting all the buttons and list view by their ID's
        ***************************** */
        newIngredient = findViewById(R.id.listIngredient); //list view
        mItemEdit = findViewById(R.id.addIngredientField); // ingredient input field
        ImageButton mAddButton = findViewById(R.id.addImageButton); // + add image button
        Button mCaptureButton = findViewById(R.id.captureButton); //capture button
        Button mCancelButton = findViewById(R.id.cancelButton); //cancel button
        input2 = findViewById(R.id.addInstructionsField); // instructions input field
        saveButton = findViewById(R.id.saveButton); // save button

        //setting up ArrayAdapter for populating our list view
        mAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        newIngredient.setAdapter(mAdapter);

        final SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        /* ****************************
         * supposed to be shared preferences thing
         ***************************** */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                editor.clear();
            }
        });

        /* ****************************
         * populate Ingredient list view
         ***************************** */
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                mAdapter.add(item);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");

                for (int i = 0; i < mAdapter.getCount(); ++i){
                    // This assumes you only have the list items in the SharedPreferences.
                    editor.putString(String.valueOf(i), mAdapter.getItem(i));
                }
                editor.commit();

            }
        });

        /* ****************************
         * For our capture button
         ***************************** */
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

         /* ****************************
         * For our cancel button
         ***************************** */
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
            }
        });

        //--------------------------------------------------
        for (int i = 0;; ++i){
            final String str = prefs.getString(String.valueOf(i), "");
            if (!str.equals("")){
                mAdapter.add(str);
            } else {
                break; // Empty String means the default value was returned.
            }
        }
        //----------------------------------------------------
        //write a message to database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.v(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
