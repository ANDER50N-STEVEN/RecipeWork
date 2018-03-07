package cs246.recipe;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    private ListView mShoppingListView;
    private EditText mItemEdit;
    private ArrayAdapter<String> mAdapter;
    ArrayList<String> shoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        mShoppingListView = findViewById(R.id.shoppingList);
        mItemEdit = findViewById(R.id.item_editText);
        Button mAddButton = findViewById(R.id.add_button);
        Button mCheckoutButton = findViewById(R.id.checkout);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
        mShoppingListView.setAdapter(mAdapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                shoppingList.add(item);
            //    mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingList);
                mShoppingListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");
            }
        });

        mCheckoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                shoppingList = null;
                Toast.makeText(getApplicationContext(), "Your shopping cart is now empty!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }



}
