package cs246.recipe;
/**
 * This ia an object class for the Upload Image
 * Member variables are Name and Url
 */
public class UploadImageObject {
    private String mName;
    private String mImageUrl;

    public UploadImageObject() {
        //empty constructor needed
        //don't delete
    }

    /**
     * Non default constructor for the Upload Image class
     * @param mName will take in the name of the uploaded image
     * @param mImageUrl url of the uploaded image
     */
    public UploadImageObject(String mName, String mImageUrl) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
