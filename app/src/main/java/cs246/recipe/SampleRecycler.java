package cs246.recipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Extends the RecyclerView and retrieves the picture in our imageView
 */
public class SampleRecycler extends RecyclerView.Adapter<SampleHolder> {

    private Context mContext;
    private List<UploadImageObject> mUploads;

    /**
     * non default constructor that sets the list and context
     * @param context set of name/object pairs
     * @param uploads list of our Object class that has the imageUrl and name as our variable
     */
    public SampleRecycler (Context context, List<UploadImageObject> uploads)
    {
        mContext = context;
        mUploads = uploads;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View
     * @return passes the view(imageView) and list of object to the SamplerHolder class
     */
    @NonNull
    @Override
    public SampleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);

        return new SampleHolder(v, mContext);
    }

    /**
     * Retrieves the picture using picasso
     * @param holder The adapter that sets the Image view and the text view
     * @param position position of the list
     */
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

    /**
     * gets the size of the list / how many images does the user have
     * @return size of the list that stores the text and image url
     */
    @Override
    public int getItemCount() {
        return mUploads.size();
    }
}
