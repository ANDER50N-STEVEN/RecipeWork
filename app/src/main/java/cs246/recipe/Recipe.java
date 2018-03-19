package cs246.recipe;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//import com.firebase.client.Firebase;

public class Recipe extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
       // Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
