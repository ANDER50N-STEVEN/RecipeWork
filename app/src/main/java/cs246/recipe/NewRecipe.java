package cs246.recipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class NewRecipe extends AppCompatActivity {

    List<String> ingredients;
    ListView ingredientList;

    private EditText input2;
    private Button saveButton;
    private ListView newIngredient;
    private EditText mItemEdit;
    private ArrayAdapter<String> mAdapter;
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //declare StorageReference for uploading photo
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgressDialog = new ProgressDialog(this);
        ingredients = new ArrayList<>();
        ingredientList = (ListView) findViewById(R.id.listIngredient);

        /* ****************************
        *  Getting all the buttons and list view by their ID's
        ***************************** */
        newIngredient = findViewById(R.id.listIngredient); //list view
        mItemEdit = findViewById(R.id.addIngredientField); // ingredient input field
        ImageButton mAddButton = findViewById(R.id.add_ingredient_button); // + add image button
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

        /**
         *
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
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
        //-----------------------------------------------------------

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
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            mProgressDialog.setMessage("Uploading....");
            mProgressDialog.show();
            Uri uri = data.getData();
            //creates a folder called photos in our firebase console(storage)
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
