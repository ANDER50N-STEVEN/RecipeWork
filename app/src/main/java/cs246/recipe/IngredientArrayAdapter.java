package cs246.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/24/2018.
 * Custom adapter that is designed to perform all of the functions
 * that an array adapter would but for a list of Ingredient Objects
 */

public class IngredientArrayAdapter extends BaseAdapter {

    private List<Ingredient> ingredientList;
    private Context mContext;

    IngredientArrayAdapter(Context context) {
        ingredientList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public int getCount() {
        return ingredientList.size();
    }

    @Override
    public Ingredient getItem(int i) {
        return ingredientList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.ingredient_view, null);

        TextView amount = (TextView) view.findViewById(R.id.amountView);
        TextView units = (TextView) view.findViewById(R.id.units);
        TextView ingredientView = (TextView) view.findViewById(R.id.ingredient);

        Ingredient ingredient = ingredientList.get(i);
        MixedFraction measurement = ingredient.getMeasurement();

        amount.setText(String.valueOf(measurement.getDisplay()));
        if (String.valueOf(ingredient.getUnits()).equals("")) {
            units.setText(String.valueOf(ingredient.getUnits()));
        } else  {
            units.setText(" " + String.valueOf(ingredient.getUnits()));
        }
        ingredientView.setText(String.valueOf(ingredient.getIngredient()));

        return view;
    }

    public void add(Ingredient ingredient) {
        ingredientList.add(ingredient);
    }

    public void addAll(List<Ingredient> ingredients) {
        ingredientList.addAll(ingredients);
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void clear() {
        ingredientList.clear();
    }

    public void remove(Ingredient ingredient){
        ingredientList.remove(ingredient);
    }

    public void remove(int i){
        ingredientList.remove(i);
    }

    public int indexOf(Ingredient ingredient){
       return ingredientList.indexOf(ingredient);
    }

    public void set(int index, Ingredient ingredient){
        ingredientList.set(index, ingredient);
    }

    public Ingredient find(String ingredient) {
        for (Ingredient ingredient1 : ingredientList){
            if (ingredient1.getIngredient().equals(ingredient))
                return ingredient1;
        }
        return null;
    }
}
