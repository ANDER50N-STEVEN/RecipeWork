package cs246.recipe;
/**
 * This ia an object class for the Upload Image
 * Member variables are ImageName and ImageUrl
 */
public class UploadImageObject {
    private String mName;
    private String mImageUrl;

    /**
     * Empty default constructor
     */
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

    /**
     * getter for Image name
     * @return name of the image
     */
    public String getmName() {
        return mName;
    }

    /**
     * setter of the image name
     * @param mName name of the image
     */
    public void setmName(String mName) {
        this.mName = mName;
    }

    /**
     * getter of Image Url
     * @return Image Url
     */
    public String getmImageUrl() {
        return mImageUrl;
    }

    /**
     * setter for Image Url
     * @param mImageUrl Image url
     */
    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
