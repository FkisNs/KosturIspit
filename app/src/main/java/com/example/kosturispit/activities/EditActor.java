package com.example.kosturispit.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kosturispit.R;
import com.example.kosturispit.models.DatabaseHelper;
import com.example.kosturispit.models.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

public class EditActor extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "Dozvola";
    private String imagePath;

    private EditText firstName;
    private EditText lastName;
    private EditText rating;
    private EditText biography;
    private EditText birthdate;
    private ImageButton imageButton;
    private Button btn_save_changes;
    private Glumac glumac;
    private int glumac_id;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_actor);

        setupViews();
        preuzmiPodatkeIzIntenta();
        btn_save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

    }

    private void setupViews(){
        firstName = findViewById(R.id.et_edit_first_name);
        birthdate = findViewById(R.id.et_edit_date_birth);
        lastName = findViewById(R.id.et_edit_last_name);
        rating = findViewById(R.id.et_edit_rating);
        biography = findViewById(R.id.et_edit_biography);
        btn_save_changes = findViewById(R.id.btn_edit_actor);
        imageButton = findViewById(R.id.ib__edit_actor);
    }

    private void preuzmiPodatkeIzIntenta(){
        glumac_id = getIntent().getIntExtra("glumac_id",-1);
        if(glumac_id < 0){
            Toast.makeText(this, "Greska! Glumac_id " +glumac_id+" ne pstoji", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            glumac = getDatabaseHelper().getGlumacDao().queryForId(glumac_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        firstName.setText(glumac.getIme());
        lastName.setText(glumac.getPrezime());
        rating.setText(glumac.getOcena());
        biography.setText(glumac.getBiografija());
        birthdate.setText( glumac.getDateBirth());
        imageButton.setImageBitmap(BitmapFactory.decodeFile(glumac.getImageId()));
    }


    private void saveChanges(){
        glumac.setIme(firstName.getText().toString());
        glumac.setPrezime(lastName.getText().toString());
        glumac.setBiografija(biography.getText().toString());
        glumac.setOcena(rating.getText().toString());
        glumac.setDateBirth(birthdate.getText().toString());
        glumac.setImageId(imagePath);

        try {
            getDatabaseHelper().getGlumacDao().createOrUpdate(glumac);
            Toast.makeText(this, "Glumac " +glumac + " updated", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(EditActor.this, DetailsActivity.class);
        intent.putExtra("glumac_id",glumac_id);
        startActivity(intent);
    }


    /**
     * Da bi dobili pristup Galeriji slika na uredjaju
     * moramo preko URI-ja pristupiti delu baze gde su smestene
     * slike uredjaja. Njima mozemo pristupiti koristeci sistemski
     * ContentProvider i koristeci URI images/* putanju
     *
     * Posto biramo sliku potrebno je da pozovemo aktivnost koja icekuje rezultat
     * Kada dobijemo rezultat nazad prikazemo sliku i dobijemo njenu tacnu putanju
     * */
    //proverava dozvolu za biranje iz galerije
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }
    //ako je dozvoljeno bira se slika iz galerije
    private void selectPicture(){
        if (isStoragePermissionGranted()) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    imagePath = picturePath; //uzima image path i stavlja ga da bude dostupno
                    cursor.close();

                    // String picturePath contains the path of selected Image
//
//                    Dialog dialog = new Dialog(MainActivity.this);
//                    dialog.setContentView(R.layout.image);
//                    dialog.setTitle("Image dialog");

//                    ImageView imageButton = (ImageView) dialog.findViewById(R.id.image);
                    imageButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//
//                    dialog.show();

                    Toast.makeText(this, picturePath,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // nakon rada sa bazo podataka potrebno je obavezno
        //osloboditi resurse!
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
