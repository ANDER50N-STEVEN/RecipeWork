package cs246.recipe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PantryActivity extends AppCompatActivity {
    private StorageReference mStorageRef;


    private EditText mItemEdit;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mStorageRef = FirebaseStorage.getInstance().getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        ListView mIngredients = findViewById(R.id.pantryList);
        mItemEdit = findViewById(R.id.item_editText);
        Button mAddButton = findViewById(R.id.add_button);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mIngredients.setAdapter(mAdapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                mAdapter.add(item);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");
            }
        });

    }
}
