package cs246.recipe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A way to repeat findViewById
 * provides implementation of the listener interface
 * For the Image view and the text view that goes in the RecyclerView
 */
public class SampleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textViewName;
    public ImageView imageView;
    private Context mContext;

    /**
     * Non - default constructor for SampleHolder class
     * calls setOnClickListener to this class
     * Initializes Image view and Text view and Context
     * @param itemView view that was clicked / set the listener on
     * @param context set of name/object pairs
     */
    public SampleHolder(View itemView, Context context) {
        super(itemView);

        textViewName = itemView.findViewById(R.id.text_view_name);
        imageView = itemView.findViewById(R.id.image_view_upload);
        mContext = context;

        itemView.setOnClickListener(this);
    }

    /**
     * Takes the user to the their individual Recipe page
     * @param v view that was clicked / set the listener on
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, DisplayRecipe.class);
        intent.putExtra("recipeName", textViewName.getText());
        mContext.startActivity(intent);
    }
}
