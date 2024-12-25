package pl.kroljulian.miesieczny;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        //LEGAL JUSTIFICATION: According to https://intercom.help/icons8-7fb7577e8170/en/articles/4725508-where-do-i-add-the-link, if the app does not have an "About" section, a link on the app's page is enough.
        //Toast.makeText(getApplication().getBaseContext(), "Icon by icons8 https://icons8.com/icon/Y8iLfEJeABbG/bus",
        //        Toast.LENGTH_SHORT).show();

        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        String mString = mPrefs.getString("imageUriVariable", "../../../../res/mipmap-hdpi/ic_launcher.webp");
        ticketImageView.setImageURI(Uri.parse(mString));
    }

    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Copy the image to the app's cache directory
                    File copiedFile = copyImageToCache(selectedImageUri);

                    if (copiedFile != null) {
                        // Save the path of the copied file to SharedPreferences
                        SharedPreferences mPrefs = getSharedPreferences("label", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        mEditor.putString("imageFilePath", copiedFile.getAbsolutePath()).apply();

                        // Load the image into the ImageView
                        ticketImageView.setImageURI(Uri.fromFile(copiedFile));
                    }
                }
            }
        }
    }

    // Method to copy the image from the URI to the app's cache directory
    private File copyImageToCache(Uri uri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File cachedImageFile = null;

        try {
            // Get input stream for the URI
            inputStream = getContentResolver().openInputStream(uri);

            // Create a file in the cache directory
            cachedImageFile = new File(getCacheDir(), "cached_image_" + System.currentTimeMillis() + ".jpg");

            // Create an output stream to write to the cache file
            outputStream = new FileOutputStream(cachedImageFile);

            // Copy the input stream to the output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Flush and close the streams
            outputStream.flush();

            return cachedImageFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load the image from SharedPreferences when the app resumes
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String imagePath = mPrefs.getString("imageFilePath", null);

        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                // Set the image from the cached file
                ticketImageView.setImageURI(Uri.fromFile(imgFile));
            } else {
                // If the file doesn't exist, log it and set a placeholder
                Log.e("ImageChooser", "Image file doesn't exist when resuming");
                ticketImageView.setImageResource(R.drawable.ic_launcher_background);  // Placeholder image
            }
        }
    }
}