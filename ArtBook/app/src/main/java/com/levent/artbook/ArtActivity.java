package com.levent.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.levent.artbook.databinding.ActivityArtBinding;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")) {
            //new Art
            binding.nameText.setText("");
            binding.nameText.setText("");
            binding.yearText2.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.selectimage);

            Bitmap selectImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.selectimage);
            binding.imageView.setImageBitmap(selectImage);

        } else {
            int artId = intent.getIntExtra("artId", 1);
            binding.button.setVisibility(View.INVISIBLE);

            try {

                System.out.println("ArtId "+artId);

                Cursor cursor = database.rawQuery("SELECT * from arts WHERE id = ?", new String[]{String.valueOf(artId)});
                int artNameIx = cursor.getColumnIndex("artname");
                int artistNameIx = cursor.getColumnIndex("artistname");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()) {
                    binding.nameText.setText(cursor.getString(artNameIx));
                    binding.artistText.setText(cursor.getString(artistNameIx));
                    binding.yearText2.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    binding.imageView.setImageBitmap(bitmap);
                }

                cursor.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public void save(View view) {

        String artName = binding.nameText.getText().toString();
        String artistName = binding.artistText.getText().toString();
        String year = binding.yearText2.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {


            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year int,image BLOB )");

            String sqlString = "INSERT INTO  arts (artname,artistname,year,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, artName);
            sqLiteStatement.bindString(2, artistName);
            sqLiteStatement.bindString(3, year);
            sqLiteStatement.bindBlob(4, byteArray);
            sqLiteStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ArtActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    public Bitmap makeSmallerImage(Bitmap image, int maxSize) {

        float width = image.getWidth();
        float height = image.getHeight();
        float bitmapRatio = width / height;

        if (bitmapRatio > 1) {
            //landscape Image

            width = maxSize;
            height = width / bitmapRatio;

        } else {
            //portrait

            height = maxSize;
            width = height * bitmapRatio;

        }

        return image.createScaledBitmap(image, (int) width, (int) (height), true);
    }

    public void selectImage(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Snackbar.make(view, "Permission Needed for Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();

            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);


            }
        } else {
            //Go Gallery

            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);


        }

    }

    private void registerLauncher() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {

                    Intent intentFromResult = result.getData();

                    if (intentFromResult != null) {

                        Uri imageData = intentFromResult.getData();

                        //binding.imageView.setImageURI(imageData);

                        //BİTMAP'e Çeviriyoruz çünkü Db ye kaydedeceğiz
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(ArtActivity.this.getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(), imageData);
                            }
                            binding.imageView.setImageBitmap(selectedImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    activityResultLauncher.launch(intentToGallery);
                } else {
                    Toast.makeText(ArtActivity.this, "Permission Needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}