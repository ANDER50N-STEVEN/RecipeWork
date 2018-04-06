package cs246.recipe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textViewName;
    public ImageView imageView;
    private Context mContext;

    public SampleHolder(View itemView, Context context) {
        super(itemView);

        textViewName = itemView.findViewById(R.id.text_view_name);
        imageView = itemView.findViewById(R.id.image_view_upload);
        mContext = context;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, DisplayRecipe.class);
        intent.putExtra("recipeName", textViewName.getText());
        mContext.startActivity(intent);
    }
}
