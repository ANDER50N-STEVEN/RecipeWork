package cs246.recipe;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleHolder extends RecyclerView.ViewHolder {

    public TextView textViewName;
    public ImageView imageView;

    public SampleHolder(View itemView) {
        super(itemView);

        textViewName = itemView.findViewById(R.id.text_view_name);
        imageView = itemView.findViewById(R.id.image_view_upload);
    }
}
