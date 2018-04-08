package cs246.recipe;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * to display list of recipes
 * and has button to "add new recipe"
 */

public class CookBookActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SampleRecycler sampleRecycler;
    private DatabaseReference mDatabaseRef;
    private ProgressBar mProgressCircle;
    private List<UploadImageObject> mUploads;

    private static final String TAG = "AddToDatabase";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        mAuth = FirebaseAuth.getInstance();
        //FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid().toString();

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


        Toolbar mToolBar = findViewById(R.id.toolBarView);
        mToolBar.setBackground(getResources().getDrawable(R.color.blueOfficial));

        final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                TextView textView = viewHolder.itemView.findViewById(R.id.text_view_name);
                String name = textView.getText().toString();
                Log.d("deleteRecie", name);
                mDatabaseRef.child("users").child(userID).child("Cookbook").child(name).removeValue();
                mDatabaseRef.child("users").child(userID).child("Photos").child(name).removeValue();


            }
        };

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("").withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.beet_it_blue))
                )
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName(R.string.title_home).withIcon(R.drawable.ic_home_black_24dp);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.shopping_cart).withIcon(R.drawable.ic_shopping_cart_black_24dp);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.pantry_button).withIcon(R.drawable.ic_shopping_basket_black_24dp);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.add_recipe).withIcon(R.drawable.ic_library_add_black_24dp);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.about_us).withIcon(R.drawable.ic_info_outline_black_24dp);
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.sign_out).withIcon(R.drawable.ic_power_settings_new_black_24dp);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(mToolBar)
                .addDrawerItems(
                        item1, item2, item3, item4, new DividerDrawerItem(), item5, item6
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 1:
                                Intent intent = new Intent(CookBookActivity.this, CookBookActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(CookBookActivity.this, ShoppingListActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(CookBookActivity.this, PantryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(CookBookActivity.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(CookBookActivity.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(CookBookActivity.this, NewRecipe.class);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new PrimaryDrawerItem().withName("© Beet It").withIcon(R.drawable.beet_it_blue));

        mDatabaseRef.child("users").child(userID).child("Photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadImageObject upload = postSnapshot.getValue(UploadImageObject.class);
                    mUploads.add(upload);
                }

                sampleRecycler = new SampleRecycler(CookBookActivity.this, mUploads);

                mRecyclerView.setAdapter(sampleRecycler);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(mRecyclerView);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CookBookActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


        FloatingActionButton mAddNewRecipe = findViewById(R.id.addNewRecipe);

        mAddNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CookBookActivity.this, NewRecipe.class);
                mUploads.clear();
                startActivity(intent);
            }
        });

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
            }
        };


    }

}