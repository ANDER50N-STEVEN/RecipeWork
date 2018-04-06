package cs246.recipe;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SampleRecycler extends RecyclerView.Adapter<SampleHolder> {

    private Context mContext;
    private List<UploadImageObject> mUploads;

    public SampleRecycler (Context context, List<UploadImageObject> uploads)
    {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public SampleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);

        return new SampleHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleHolder holder, int position) {
        UploadImageObject uploadCurrent =  mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getmName());
        Picasso.with(mContext)
                .load(uploadCurrent.getmImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
}
