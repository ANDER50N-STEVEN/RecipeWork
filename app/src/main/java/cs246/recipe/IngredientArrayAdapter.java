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
    public Object getItem(int i) {
        return null;
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
}
