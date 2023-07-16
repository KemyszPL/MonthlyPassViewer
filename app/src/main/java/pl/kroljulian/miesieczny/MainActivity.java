package pl.kroljulian.miesieczny;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button choosePictureButton;
    ImageView ticketImageView;
    //expected result code to compare actual result code to
    int SELECT_PICTURE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //register button and view
        choosePictureButton = findViewById(R.id.choosePictureButton);
        ticketImageView = findViewById(R.id.ticketImageView);
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        String mString = mPrefs.getString("imageUriVariable", "../../../../res/mipmap-hdpi/ic_launcher.webp");
        ticketImageView.setImageURI(Uri.parse(mString));
    }

    void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: enable cropping of picture while selecting
        if(resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                SharedPreferences mPrefs = getSharedPreferences("label", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("imageUriVariable", String.valueOf(selectedImageUri)).commit();
                if (null != selectedImageUri) {
                    ticketImageView.setImageURI(selectedImageUri);
                }
            }
        }
    }
}