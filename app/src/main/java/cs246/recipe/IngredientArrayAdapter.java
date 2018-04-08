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
 * Custom adapter that is designed to perform all of the functions
 * that an array adapter would but for a list of Ingredient Objects
 */

public class IngredientArrayAdapter extends BaseAdapter {

    private List<Ingredient> ingredientList;
    private Context mContext;

    /**
     * constructor to set initial value
     * @param context list of names/object
     */
    IngredientArrayAdapter(Context context) {
        ingredientList = new ArrayList<>();
        mContext = context;
    }

    /**
     * get list size
     * @return size of our list
     */
    @Override
    public int getCount() {
        return ingredientList.size();
    }

    /**
     * index of list
     * @param i index of list
     * @return every element of list
     */
    @Override
    public Ingredient getItem(int i) {
        return ingredientList.get(i);
    }

    /**
     * gets id
     * @param i index of list
     * @return zero
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * ges the view
     * @param i index
     * @param view item that is clicked on
     * @param viewGroup data group
     * @return item that is clicked on
     */
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

    /**
     * adds new ingredient in list
     * @param ingredient individual ingredient
     */
    public void add(Ingredient ingredient) {
        ingredientList.add(ingredient);
    }

    /**
     * adds all ingredient to the list
     * @param ingredients individual ingredient
     */
    public void addAll(List<Ingredient> ingredients) {
        ingredientList.addAll(ingredients);
    }

    /**
     * gets Ingredient fromlist
     * @return individual ingredient in list
     */
    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    /**
     * clears the list
     */
    public void clear() {
        ingredientList.clear();
    }

    /**
     * remove an item
     * @param ingredient individual ingredient
     */
    public void remove(Ingredient ingredient){
        ingredientList.remove(ingredient);
    }

    /**
     * deletes individual ingredient
     * @param i index of list
     */
    public void remove(int i){
        ingredientList.remove(i);
    }

    /**
     *
     * @param ingredient individual ingredient
     * @return index of individual ingredient
     */
    public int indexOf(Ingredient ingredient){
       return ingredientList.indexOf(ingredient);
    }

    /**
     *
     * @param index index of list
     * @param ingredient individual ingredient
     */
    public void set(int index, Ingredient ingredient){
        ingredientList.set(index, ingredient);
    }

    /**
     * matched and searches for ingredient that match
     * @param ingredient individual ingredient
     * @return ingreient that matches
     */
    public Ingredient find(String ingredient) {
        for (Ingredient ingredient1 : ingredientList){
            if (ingredient1.getIngredient().equals(ingredient))
                return ingredient1;
        }
        return null;
    }
}