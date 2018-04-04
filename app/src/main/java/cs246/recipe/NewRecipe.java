package cs246.recipe;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewRecipe extends AppCompatActivity {

    private ListView ingredientList;

    private EditText instructionsText, mItemEdit, mAmount, recipeName;
    private String units;
    private StorageReference mStorage;
    private FloatingActionButton mSaveButton;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private Uri mImageUri;
    private StorageTask mUploadTask;

    private static final String TAG = "AddToDatabase";
    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String userID;
    private ImageView mImageView;
    int num;
    int den;

    private Spinner spinner;
    private IngredientArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        num = 0;
        den = 1;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //declare StorageReference for uploading photo
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mProgressDialog = new ProgressDialog(this);

        spinner = findViewById(R.id.spinner);
        final String[] measurements = new String[]{"", "tsp", "tbs", "cup", "floz", "box", "can", "lbs"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, measurements);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                units = "";
                if (!adapterView.getItemAtPosition(i).toString().equals(""))
                    units = " ";
                units += adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                units = "";
            }
        });

//        ingredients = new ArrayList<>();
        ingredientList = (ListView) findViewById(R.id.listIngredient);

        /*
         *  Getting all the buttons and list view by their ID's
         */
        mImageView = findViewById(R.id.imageView);
        mItemEdit = findViewById(R.id.addIngredientField); // ingredient input field
        mAmount = findViewById(R.id.amount); // amount of ingredient
        recipeName = findViewById(R.id.recipeName); // name of recipe
        FloatingActionButton mAddButton = findViewById(R.id.add_ingredient_button); // + add image button
        FloatingActionButton mCaptureButton = findViewById(R.id.captureButton); //capture button
        FloatingActionButton mCancelButton = findViewById(R.id.cancelButton); //cancel button
        instructionsText = findViewById(R.id.addInstructionsField); // instructions input field
        mSaveButton = findViewById(R.id.saveButton); // save button

        // setting up ArrayAdapter for populating our list view
        mAdapter = new IngredientArrayAdapter(getApplicationContext());

        ingredientList.setAdapter(mAdapter);

        final SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        /*
         * for our save button
         */
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                String name = recipeName.getText().toString();
                String string = instructionsText.getText().toString();
                RecipeObject newRecipe = new RecipeObject(mAdapter.getIngredientList(), string);
                mDatabaseRef.child("users").child(userID).child("Cookbook").child(name).setValue(newRecipe);

//                if (mUploadTask != null && mUploadTask.isInProgress()) {
//                    Toast.makeText(NewRecipe.this, "Upload in progress", Toast.LENGTH_SHORT).show();
//                } else {
//                    uploadFile();
//                }

                recipeName.setText("");
                instructionsText.setText("");
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                editor.clear();
                editor.commit();

                finish();
            }
        });

        /*
         * populate Ingredient list view
         */
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                String measurementString = mAmount.getText().toString();

                MixedFraction measurement = new MixedFraction(measurementString);
                Ingredient newIngredient = new Ingredient(item, units, measurement);
                mAdapter.add(newIngredient);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");
                mAmount.setText("");

//                for (int i = 0; i < mAdapter.getCount(); ++i){
//                    // This assumes you only have the list items in the SharedPreferences.
//                    try {
////                        editor.putString(String.valueOf(i), gson.toJson(mAdapter.getItem(i)));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                editor.commit();
            }
        });

        /*
         * For our capture button
         * request of choosing where to upload from
         */
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

        /*
         * For our cancel button
         */
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
//        for (int i = 0;; ++i){
//            final String str = prefs.getString(String.valueOf(i), "");
//            if (!str.equals("")){
//                Log.d("Ingredient", str);
//                Ingredient ingredient = gson.fromJson(str, Ingredient.class);
//                mAdapter.add(ingredient);
//            } else {
//                break; // Empty String means the default value was returned.
//            }
//        }
        //-----------------------------------------------------------

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Get the extension of the Image file that is uploaded
     * @param uri the data of the user in the storage
     * @return the extension of the image file
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Uploads Images from gallery of the phone
     * @param requestCode A request code you passed to startActivityForResult()
     * @param resultCode This is either RESULT_OK for successful or RESULT_CANCELED for operation failed
     * @param data Carries the result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK  && data != null && data.getData() != null)
        {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    /**
     * Uploads the Image into the storage and sets its name to the database
     */
    private void uploadFile() {
        if (mImageUri != null) {
            mProgressDialog.setMessage("Uploading....");
            mProgressDialog.show();
            StorageReference fileReference = mStorageRef.child("users").child(userID).child("photos").child(recipeName.getText().toString());
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(NewRecipe.this, "Upload successful", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewRecipe.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressDialog.setProgress((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}