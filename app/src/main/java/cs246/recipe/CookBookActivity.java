package cs246.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * to display list of recipes
 * and has button to "add new recipe"
 */

public class CookBookActivity extends AppCompatActivity {

    private FloatingActionButton mAddnewRecipe;
//    private ImageView mAddImageView;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book);
//
            mAddnewRecipe = findViewById(R.id.addNewRecipe);
//        mAddImageView = findViewById(R.id.addImageView);

        mStorageRef = FirebaseStorage.getInstance().getReference("photos");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("photos");

        mAddnewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CookBookActivity.this, NewRecipe.class));
            }
        });
    }
}
