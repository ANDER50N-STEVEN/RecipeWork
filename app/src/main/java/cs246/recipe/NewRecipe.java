package cs246.recipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewRecipe extends AppCompatActivity {

    private ListView ingredientList;

    private EditText instructionsText, mItemEdit, mAmount, recipeName;
    private String units;
    private Button saveButton;
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;

    private static final String TAG = "AddToDatabase";
    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String userID;
    int num;
    int den;

    private Spinner spinner;
    private IngredientArrayAdapter mAdapter;

//    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        num = 0;
        den = 1;

//        gson = new Gson();

        myDatabase = FirebaseDatabase.getInstance();
        myRef = myDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        user = mAuth.getCurrentUser();
        userID = user.getUid();

        spinner = findViewById(R.id.spinner);
        String[] measurement = new String[]{"tsp", "tbs", "cup", "floz", ""};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, measurement);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                units = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                units = "tsp";
            }
        });

        ingredientList = (ListView) findViewById(R.id.listIngredient);

        /* ****************************
        *  Getting all the buttons and list view by their ID's
        ***************************** */
        mItemEdit = findViewById(R.id.addIngredientField); // ingredient input field
        mAmount = findViewById(R.id.amount); // amount of ingredient
        recipeName = findViewById(R.id.recipeName); // name of recipe
        final ImageButton mAddButton = findViewById(R.id.add_ingredient_button); // + add image button
        Button mCaptureButton = findViewById(R.id.captureButton); //capture button
        Button mCancelButton = findViewById(R.id.cancelButton); //cancel button
        instructionsText = findViewById(R.id.addInstructionsField); // instructions input field
        saveButton = findViewById(R.id.saveButton); // save button

        // setting up ArrayAdapter for populating our list view
        mAdapter = new IngredientArrayAdapter(getApplicationContext());

        ingredientList.setAdapter(mAdapter);

        final SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        /* ****************************
         * for our save button
         ***************************** */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                String name = recipeName.getText().toString();
                String string = instructionsText.getText().toString();
                RecipeObject newRecipe = new RecipeObject(mAdapter.getIngredientList(), string);
                myRef.child("users").child(userID).child("Cookbook").child(name).setValue(newRecipe);

                recipeName.setText("");
                instructionsText.setText("");
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                editor.clear();
                editor.commit();
            }
        });

        /* ****************************
         * populate Ingredient list view
         ***************************** */
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                int amount = Integer.parseInt(mAmount.getText().toString());
                Ingredient newIngredient = new Ingredient(item, amount, num, den, units);
                mAdapter.add(newIngredient);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");
                mAmount.setText("");

                for (int i = 0; i < mAdapter.getCount(); ++i){
                    // This assumes you only have the list items in the SharedPreferences.
                    try {
//                        editor.putString(String.valueOf(i), gson.toJson(mAdapter.getItem(i)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, GALLERY_INTENT);
            }
        });

         /* ****************************
         * For our cancel button
         ***************************** */
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName.setText("");
                instructionsText.setText("");

                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                editor.clear();
                editor.commit();
            }
        });

        //--------------------------------------------------
        for (int i = 0;; ++i){
            final String str = prefs.getString(String.valueOf(i), "");
            if (!str.equals("")){
                Log.d("Ingredient", str);
                try {
//                    Ingredient ingredient = gson.fromJson(str, Ingredient.class);
//                    mAdapter.add(ingredient);
//                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                break; // Empty String means the default value was returned.
            }
        }
        //-----------------------------------------------------------

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            mProgressDialog.setMessage("Uploading....");
            mProgressDialog.show();
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(NewRecipe.this, "Upload done", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }

}