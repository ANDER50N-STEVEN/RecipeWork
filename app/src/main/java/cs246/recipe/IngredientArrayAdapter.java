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

    List<Ingredient> ingredientList;
    Context mContext;

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
        TextView ingredient = (TextView) view.findViewById(R.id.ingredient);

        amount.setText(ingredientList.get(i).getValue());
        units.setText(ingredientList.get(i).getUnits());
        ingredient.setText(ingredientList.get(i).getIngredient());

        return view;
    }

    public void add(Ingredient ingredient) {
        ingredientList.add(ingredient);
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void clear() {
        ingredientList.clear();
    }
}
