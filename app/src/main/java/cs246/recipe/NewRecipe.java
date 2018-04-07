package cs246.recipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class NewRecipe extends AppCompatActivity {

    private ListView ingredientList;

    private EditText instructionsText, mItemEdit, mAmount, recipeName;
    private String units;
    private StorageReference mStorage;
    private Button mSaveButton;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;
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

        Toolbar mToolBar = findViewById(R.id.toolBarView);
        mToolBar.setBackground(getResources().getDrawable(R.color.blueOfficial ));

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("").withIcon(getResources().getDrawable(R.drawable.beet_it_blue))
                )
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName(R.string.title_home).withIcon(R.drawable.ic_home_black_24dp);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.shopping_cart).withIcon(R.drawable.ic_shopping_cart_black_24dp);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.pantry_button).withIcon(R.drawable.ic_shopping_basket_black_24dp);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.about_us).withIcon(R.drawable.ic_info_outline_black_24dp);
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.sign_out).withIcon(R.drawable.ic_power_settings_new_black_24dp);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(mToolBar)
                .addDrawerItems(
                        item1, item2, item3, new DividerDrawerItem(), item5, item6
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch(position)
                        {
                            case 1:
                                Intent intent = new Intent(NewRecipe.this, CookBookActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(NewRecipe.this, ShoppingListActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(NewRecipe.this, PantryActivity.class);
                                startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(NewRecipe.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(NewRecipe.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("© Beet It").withIcon(R.drawable.beet_it_blue));

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
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference();
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
//                units = "";
//                if (!adapterView.getItemAtPosition(i).toString().equals(""))
//                    units = " ";
                units = adapterView.getItemAtPosition(i).toString();
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
        ImageButton mAddButton = findViewById(R.id.add_ingredient_button); // + add image button
        Button mCaptureButton = findViewById(R.id.captureButton); //capture button
        Button mCancelButton = findViewById(R.id.cancelButton); //cancel button
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

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(NewRecipe.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

                recipeName.setText("");
                instructionsText.setText("");
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                editor.clear();
                editor.commit();

                //finish();
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
                measurement.setDisplay(measurementString + " " + units);
                Ingredient newIngredient = new Ingredient(item, units, measurement);
                mAdapter.add(newIngredient);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");
                mAmount.setText("");
                spinner.setSelection(0);

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
            Picasso.with(this).load(mImageUri).into(mImageView);
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

                            String name = taskSnapshot.getMetadata().getName();
                            String url = taskSnapshot.getDownloadUrl().toString();

                            Log.e(TAG, "Uri: " + url);
                            Log.e(TAG, "Name: " + name);

                            writeNewImageInfoToDB(name, url);
                            finish();
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

    /**
     * Stores image name and url to the database
     * @param name name of the image
     * @param url url of the image
     */
    private void writeNewImageInfoToDB(String name, String url) {
        UploadImageObject info = new UploadImageObject(name, url);

        //String key = mDatabaseRef2.push().getKey();
        mDatabaseRef2.child("users").child(userID).child("Photos").child(name).setValue(info);
    }

}