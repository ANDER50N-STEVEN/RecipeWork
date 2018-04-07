package cs246.recipe;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DisplayRecipe extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<Ingredient> ingredients;
    IngredientArrayAdapter adapter;
    FirebaseUser user;
    String userID;
    String recipeName;

    TextView recipeNameEdit;
    ListView ingredientsList;
    TextView instructionsField;
    Ingredient input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        recipeNameEdit = findViewById(R.id.R_recipeName);
        ingredientsList = findViewById(R.id.R_ingredientList);
        instructionsField = findViewById(R.id.R_instructionsField);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Intent intent = getIntent();
        recipeName = intent.getStringExtra("recipeName");
        recipeNameEdit.setText(recipeName);

        adapter = new IngredientArrayAdapter(getApplicationContext());
        ingredientsList.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        ingredients = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataCapture", "checking for data to capture");
                if (dataSnapshot.child("users").hasChild(userID)) {
                    Log.d("dataCapture", "Has child userID");
                    if (dataSnapshot.child("users").child(userID).hasChild("Cookbook")) {
                        Log.d("dataCapture", "Has child Cookbook");
                        if (dataSnapshot.child("users").child(userID).child("Cookbook").hasChild(recipeName)) {
                            Log.d("dataCapture", "Has child recipe name");
                            DataSnapshot ds = dataSnapshot.child("users").child(userID).child("Cookbook").child(recipeName);

                            if (ds.hasChild("instructions")) {
                                Log.d("dataCapture", "Has child instructions");
                                instructionsField.setText(String.valueOf(ds.child("instructions").getValue()));
                            }

                            if (ds.hasChild("ingredients")) {
                                Log.d("dataCapture", "Has child ingredients");
                                if (ds.child("ingredients").hasChildren()) {
                                    Log.d("dataCapture", "ingredients has children");
                                    for (DataSnapshot ingredient : ds.child("ingredients").getChildren()) {
                                        String units = "Error";
                                        Integer whole = 0;
                                        Integer denominator = 0;
                                        Integer numerator = 0;
                                        MixedFraction measurement;

                                        String ingredientString = "Error";

                                        if (ingredient.hasChild("units")) {
                                            Log.d("dataCapture", "Has child units");
                                            units = String.valueOf(ingredient.child("units").getValue());
                                        }
                                        if (ingredient.hasChild("ingredient")) {
                                            Log.d("dataCapture", "Has child ingredient");
                                            ingredientString = String.valueOf(ingredient.child("ingredient").getValue());
                                        }

                                        if (ingredient.hasChild("measurement")) {
                                            if (ingredient.child("measurement").hasChildren()) {
                                                if (ingredient.child("measurement").hasChild("whole")) {
                                                    Log.d("dataCapture", "Has child whole");
                                                    whole = Integer.valueOf(String.valueOf(ingredient.child("measurement").child("whole").getValue()));
                                                }
                                                if (ingredient.child("measurement").hasChild("numerator")) {
                                                    Log.d("dataCapture", "Has child numerator");
                                                    numerator = Integer.valueOf(String.valueOf(ingredient.child("measurement").child("numerator").getValue()));
                                                }
                                                if (ingredient.child("measurement").hasChild("denominator")) {
                                                    Log.d("dataCapture", "Has child denominator");
                                                    denominator = Integer.valueOf(String.valueOf(ingredient.child("measurement").child("denominator").getValue()));
                                                }
                                            }
                                        }

                                        measurement = new MixedFraction(whole, numerator, denominator);
                                        Ingredient ingredientObject = new Ingredient(ingredientString, units, measurement);
                                        adapter.add(ingredientObject);
                                    }
                                }
                            }
                        }
                    }
                    if (dataSnapshot.child("users").child(userID).hasChild("ShoppingList")) {
                        Log.d("dataCapture", "Has child ShoppingList");
                        for(DataSnapshot ds: dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
                            String ingredient = "";
                            String units = "";
                            String display = "";
                            MixedFraction measurement = new MixedFraction();
                            if (ds.hasChild("ingredient")) {
                                ingredient = String.valueOf(ds.child("ingredient").getValue());
                            }
                            if (ds.hasChild("units")) {
                                units = String.valueOf(ds.child("units").getValue());
                            }
                            if (ds.hasChild("measurement")) {
                                int whole = 0;
                                int numerator = 0;
                                int denominator = 0;
                                if (ds.child("measurement").hasChild("whole")) {
                                    try {
                                        whole = Integer.valueOf(String.valueOf(ds.child("whole").getValue()));
                                    } catch (Exception e) {
                                        whole = 0;
                                    }
                                }
                                if (ds.child("measurement").hasChild("numerator")) {
                                    try {
                                        numerator = Integer.valueOf(String.valueOf(ds.child("numerator").getValue()));
                                    } catch (Exception e) {
                                        whole = 0;
                                    }
                                }
                                if (ds.child("measurement").hasChild("denominator")) {
                                    try {
                                        denominator = Integer.valueOf(String.valueOf(ds.child("denominator").getValue()));
                                    } catch (Exception e) {
                                        whole = 0;
                                    }
                                }
                                measurement = new MixedFraction(whole, numerator, denominator);
                            }

                            Ingredient uInfo = new Ingredient(ingredient, units, measurement);
                            assert uInfo != null;
                            ingredients.add(uInfo);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void R_goBackClick(View view) {
        finish();
    }

    public void R_make(View view) {
        List<Ingredient> ingredients = new ArrayList<>();

        for (Ingredient in : adapter.getIngredientList()) {
            ingredients.add(in);
        }
        for (final Ingredient in : ingredients) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.child("users").child(userID).child("ShoppingList").hasChild(in.getIngredient())) {
                        Ingredient oldInfo = snapshot.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).getValue(Ingredient.class);
                       convertData(in, "add", oldInfo);
                        reference.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).setValue(input);
                    }else
                        reference.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).setValue(in);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            reference.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).setValue(in);
        }

        Toast.makeText(this, "Ingredients Added to Shopping List", Toast.LENGTH_SHORT).show();
    }


//    public void R_made(View view) {
//
 //   boolean canMake;
//        List<Ingredient> ingredients = new ArrayList<>();
//
//        for (Ingredient in : adapter.getIngredientList()) {
//            ingredients.add(in);
//        }
//        for (final Ingredient in : ingredients) {
//            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    if (snapshot.child("users").child(userID).child("ShoppingList").hasChild(in.getIngredient())) {
//                       canMake = true;
//                    }else{
    //                   canMake = false;
    //        Toast.makeText(this, "You do not have all of the Ingredients", Toast.LENGTH_SHORT).show();
    //                   return;
    //                   }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            });
//            reference.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).setValue(in);
//        }
    //        for (final Ingredient in : ingredients) {
//            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    if (canMake) {
//                       makeData(in);
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            });
//            reference.child("users").child(userID).child("ShoppingList").child(in.getIngredient()).setValue(in);
//        }
//
//        Toast.makeText(this, "Ingredients Removed from Pantry", Toast.LENGTH_SHORT).show();
//    }



    private int gcd(int a, int b){
        if(a == b)
            return a;
        if(a == 0)
            return b;
        return gcd((b%a), a);
    }

    private void convertData(final Ingredient in, final String function, final Ingredient oldInfo){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
         //       for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").child(in.getIngredient())) {
                    Ingredient iInfo = oldInfo;
                Log.d("dataCapture", String.valueOf(iInfo));
                    input = in;
                    if(iInfo.getMeasurement().getDenominator() == 0) {
                        iInfo.getMeasurement().setDenominator(1);
                    }
                    if(input.getMeasurement().getDenominator() == 0)
                        input.getMeasurement().setDenominator(1);
                    int multiplier = 1;
                    int multiplier2 = 1;
                    if(!Objects.equals(input.getMeasurement().getDenominator(), iInfo.getMeasurement().getDenominator())){
                        multiplier2 = (input.getMeasurement().getDenominator() / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
                        multiplier = (iInfo.getMeasurement().getDenominator() / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
                    }
                    switch(iInfo.getUnits()){
                        case "tsp":
                            iInfo.getMeasurement().setNumerator(iInfo.getMeasurement().getNumerator() + (iInfo.getMeasurement().getWhole() * iInfo.getMeasurement().getDenominator()));
                            if(Objects.equals(input.getUnits(), "tsp")) {
                                input.getMeasurement().setNumerator((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator());

                            }
                            if(Objects.equals(input.getUnits(), "tbs")) {
                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 3);
                            }
                            if(Objects.equals(input.getUnits(), "cup")) {
                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 48);
                            }
                            input.getMeasurement().setDenominator((input.getMeasurement().getDenominator() * iInfo.getMeasurement().getDenominator()) / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
                            input.getMeasurement().setNumerator((input.getMeasurement().getNumerator() * multiplier));
                            input.setUnits("tsp");
                            break;
                        case "tbs":
                            iInfo.getMeasurement().setNumerator((iInfo.getMeasurement().getWhole() * 3 * iInfo.getMeasurement().getDenominator()) + (iInfo.getMeasurement().getNumerator() * 3));
                            if(Objects.equals(input.getUnits(), "tsp")) {
                                input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() + (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
                            }
                            if(Objects.equals(input.getUnits(), "tbs")){
                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 3);
                            }
                            if(Objects.equals(input.getUnits(), "cup")){
                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 48);
                            }
                            input.getMeasurement().setDenominator((input.getMeasurement().getDenominator() * iInfo.getMeasurement().getDenominator()) / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
                            input.getMeasurement().setNumerator((input.getMeasurement().getNumerator() * multiplier));
                            input.setUnits("tsp");
                            break;
                        case "cup":
                            iInfo.getMeasurement().setNumerator((iInfo.getMeasurement().getWhole() * 48 * iInfo.getMeasurement().getDenominator()) + (iInfo.getMeasurement().getNumerator() * 48));
                            if(Objects.equals(input.getUnits(), "tsp")){
                                input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() + (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
                            }
                            if(Objects.equals(input.getUnits(), "tbs")){
                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 3);
                            }
                            if(Objects.equals(input.getUnits(), "cup")){
                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 48);
                            }
                            input.getMeasurement().setDenominator((input.getMeasurement().getDenominator() * iInfo.getMeasurement().getDenominator()) / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
                            input.getMeasurement().setNumerator((input.getMeasurement().getNumerator() * multiplier));
                            input.setUnits("tsp");
                            break;
                        default:
                            input.getMeasurement().setWhole(iInfo.getMeasurement().getWhole() + 1);
                            break;
                    }
                    if (Objects.equals(function, "add"))
                        addData(input, iInfo, multiplier2);
            //        if (Objects.equals(function, "make"))
//                        makeData(in, iInfo, multiplier2);
                }

//            }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addData(Ingredient input, Ingredient iInfo, int multiplier2) {
        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() + (iInfo.getMeasurement().getNumerator() * multiplier2));
    }



//    private void makeData(final Ingredient in){
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                input = in;
//                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("ShoppingList").getChildren()) {
//                    Ingredient iInfo = ds.getValue(Ingredient.class);
//                    if(iInfo.getMeasurement().getDenominator() == 0) {
//                        iInfo.getMeasurement().setDenominator(1);
//                    }
//                    if(input.getMeasurement().getDenominator() == 0)
//                        input.getMeasurement().setDenominator(1);
//                    int multiplier = 1;
//                    int multiplier2 = 1;
//                    if(!Objects.equals(input.getMeasurement().getDenominator(), iInfo.getMeasurement().getDenominator())){
//                        multiplier2 = (input.getMeasurement().getDenominator() / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
//                        multiplier = (iInfo.getMeasurement().getDenominator() / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
//                    }
//                    switch(iInfo.getUnits()){
//                        case "tsp":
//                            iInfo.getMeasurement().setNumerator(iInfo.getMeasurement().getNumerator() + (iInfo.getMeasurement().getWhole() * iInfo.getMeasurement().getDenominator()));
//                            if(Objects.equals(input.getUnits(), "tsp")) {
//                                input.getMeasurement().setNumerator((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator());
//                            }
//                            if(Objects.equals(input.getUnits(), "tbs")) {
//                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 3);
//                            }
//                            if(Objects.equals(input.getUnits(), "cup")) {
//                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 48);
//                            }
//                            input.getMeasurement().setDenominator((input.getMeasurement().getDenominator() * iInfo.getMeasurement().getDenominator()) / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
//                            input.getMeasurement().setNumerator((input.getMeasurement().getNumerator() * multiplier) + (iInfo.getMeasurement().getNumerator() * multiplier2));
//                            input.setUnits("tsp");
//                            break;
//                        case "tbs":
//                            iInfo.getMeasurement().setNumerator((iInfo.getMeasurement().getWhole() * 3 * iInfo.getMeasurement().getDenominator()) + (iInfo.getMeasurement().getNumerator() * 3));
//                            if(Objects.equals(input.getUnits(), "tsp")) {
//                                input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() + (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
//                            }
//                            if(Objects.equals(input.getUnits(), "tbs")){
//                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 3);
//                            }
//                            if(Objects.equals(input.getUnits(), "cup")){
//                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 48);
//                            }
//                            input.getMeasurement().setDenominator((input.getMeasurement().getDenominator() * iInfo.getMeasurement().getDenominator()) / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
//                            input.getMeasurement().setNumerator((input.getMeasurement().getNumerator() * multiplier) + (iInfo.getMeasurement().getNumerator() * multiplier2));
//                            input.setUnits("tsp");
//                            break;
//                        case "cup":
//                            iInfo.getMeasurement().setNumerator((iInfo.getMeasurement().getWhole() * 48 * iInfo.getMeasurement().getDenominator()) + (iInfo.getMeasurement().getNumerator() * 48));
//                            if(Objects.equals(input.getUnits(), "tsp")){
//                                input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() + (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
//                            }
//                            if(Objects.equals(input.getUnits(), "tbs")){
//                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 3);
//                            }
//                            if(Objects.equals(input.getUnits(), "cup")){
//                                input.getMeasurement().setNumerator(((input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()) + input.getMeasurement().getNumerator()) * 48);
//                            }
//                            input.getMeasurement().setDenominator((input.getMeasurement().getDenominator() * iInfo.getMeasurement().getDenominator()) / gcd(iInfo.getMeasurement().getDenominator(), input.getMeasurement().getDenominator()));
//                            input.getMeasurement().setNumerator((input.getMeasurement().getNumerator() * multiplier) + (iInfo.getMeasurement().getNumerator() * multiplier2));
//                            input.setUnits("tsp");
//                            break;
//                        default:
//                            input.getMeasurement().setWhole(iInfo.getMeasurement().getWhole() + 1);
//                            break;
//                    }
//                    if(input.getMeasurement().getDenominator() == 0)
//                        input.getMeasurement().setDenominator(1);
//                    if(((input.getMeasurement().getNumerator() / input.getMeasurement().getDenominator()) >= 48) && (Objects.equals(input.getUnits(), "tsp"))) {
//                        input.setUnits("cup");
//                        input.getMeasurement().setDenominator(input.getMeasurement().getDenominator() * 48);
//                        input.getMeasurement().setWhole((input.getMeasurement().getNumerator() / input.getMeasurement().getDenominator()));
//                        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() - (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
//                        int common = gcd(input.getMeasurement().getNumerator(), input.getMeasurement().getDenominator());
//                        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator()/common);
//                        input.getMeasurement().setDenominator(input.getMeasurement().getDenominator()/common);
//                        if (input.getMeasurement().getNumerator() != 0) {
//                            if ((input.getMeasurement().getNumerator() / ((input.getMeasurement().getDenominator() * common) / 16)) >= 3) {
//                                int tempD = input.getMeasurement().getDenominator() * 3;
//                                int tempV = ((input.getMeasurement().getNumerator() / tempD));
//                                int tempN = (input.getMeasurement().getNumerator() - (input.getMeasurement().getWhole() * tempD));
//                                common = gcd(input.getMeasurement().getNumerator(), tempD);
//                                tempN = (tempN/common);
//                                tempD = (tempD/common);
//                                if (tempN != 0)
//                                    input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " cup " + tempV + " " + tempN + "/" + tempD + " tbs");
//                                else
//                                    input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " tbs" + tempN + " tsp");
//                            }
//                            else
//                                input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " " + input.getMeasurement().getNumerator() + "/" + input.getMeasurement().getDenominator() + " " + input.getUnits());
//                        }
//                        else
//                            input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " " + input.getUnits());
//                    }else if (((input.getMeasurement().getNumerator() / input.getMeasurement().getDenominator()) >= 3) && (Objects.equals(input.getUnits(), "tsp"))) {
//                        //     Log.d(TAG, "showData: tbs \n");
//                        input.setUnits("tbs");
//                        input.getMeasurement().setDenominator(input.getMeasurement().getDenominator() * 3);
//                        input.getMeasurement().setWhole(input.getMeasurement().getNumerator() / input.getMeasurement().getDenominator());
//                        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() - (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
//                        int common = gcd(input.getMeasurement().getNumerator(), input.getMeasurement().getDenominator());
//                        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator()/common);
//                        input.getMeasurement().setDenominator(input.getMeasurement().getDenominator()/common);
//                        if (input.getMeasurement().getNumerator() != 0)
//                            input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " " + input.getMeasurement().getNumerator() + "/" + input.getMeasurement().getDenominator() + " tbs");
//                        else
//                            input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " tbs");
//                    }else{
//                        //       Log.d(TAG, "showData: tsp \n");
//                        input.getMeasurement().setWhole(input.getMeasurement().getNumerator() / input.getMeasurement().getDenominator());
//                        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator() - (input.getMeasurement().getWhole() * input.getMeasurement().getDenominator()));
//                        int common = gcd(input.getMeasurement().getNumerator(), input.getMeasurement().getDenominator());
//                        input.getMeasurement().setNumerator(input.getMeasurement().getNumerator()/common);
//                        input.getMeasurement().setDenominator(input.getMeasurement().getDenominator()/common);
//                        if (input.getMeasurement().getNumerator() != 0) {
//                            if (input.getMeasurement().getWhole() != 0)
//                                input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + " " + input.getMeasurement().getNumerator() + "/" + input.getMeasurement().getDenominator() + " tsp");
//                            else
//                                input.getMeasurement().setDisplay(input.getMeasurement().getNumerator() + "/" + input.getMeasurement().getDenominator() + " tsp");
//                        }else
//                            input.getMeasurement().setDisplay(input.getMeasurement().getWhole() + "tsp");
//                    }
//                    if(input.getMeasurement().getNumerator() == 0) {
//                        input.getMeasurement().setDenominator(1);
//                    }
//                }
//
//            }
//
//
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

}

