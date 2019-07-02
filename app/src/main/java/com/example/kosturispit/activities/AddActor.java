package com.example.kosturispit.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.kosturispit.models.Film;
import com.example.kosturispit.models.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

public class AddActor extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "Dozvola";

    private EditText et_first_name;
    private EditText et_last_name;
    private EditText et_bio;
    private EditText et_datum_rodjenja;
    private EditText et_rating;

    private Button btn_add_actor;
    private Glumac glumac;
    private Film movie;
    private ImageButton imageButton;
    private String imagePath;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_actor);

        imageButton = findViewById(R.id.ib_add_actor);

        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_bio = findViewById(R.id.et_biography);
        et_datum_rodjenja = findViewById(R.id.et_date_birth);
        et_rating = findViewById(R.id.et_rating);


        //dodavanje editovanog glumca uz proveru polja na button click
        btn_add_actor = findViewById(R.id.btn_add_actor);
        btn_add_actor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validacija unosa svih polja sem za film i rating
                if(validateActor(et_first_name)
                        && validateActor(et_last_name)
                        && validateActor(et_datum_rodjenja)
                        && validateActor(et_bio)) {
                    add();

                    if(proveraPrefsPodesavanjaZaToast()) {
                        Toast.makeText(AddActor.this, "Toast da je upisano u bazi", Toast.LENGTH_SHORT).show();
                    }
                    if(proveraPrefsPodesavanjaZaNotifikacije()) {
                        Toast.makeText(AddActor.this, "Notifikacija da je upisano u bazi", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(AddActor.this,MainActivity.class);

                    startActivity(intent);
                }

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
                Log.d(TAG, "onClick: na imagebutton");
            }
        });

    }
    //upisuje sve nove podatke u glumca
    private void add(){
        glumac = new Glumac();
        glumac.setIme(et_first_name.getText().toString().trim());
        glumac.setPrezime(et_last_name.getText().toString().trim());
        glumac.setBiografija(et_bio.getText().toString().trim());
        glumac.setDateBirth(et_datum_rodjenja.getText().toString().trim());
        glumac.setOcena(et_rating.getText().toString().trim());
        glumac.setImageId(imagePath);
//        glumac.setImageId("https://m.media-amazon.com/images/M/MV5BMjE0MDkzMDQwOF5BMl5BanBnXkFtZTgwOTE1Mjg1MzE@._V1_UY317_CR2,0,214,317_AL_.jpg");


        try {
            getDatabaseHelper().getGlumacDao().create(glumac);
            getDatabaseHelper().getmMovieDao().create(movie);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //provera prefs settinga za checkbox notifikacije
    private boolean proveraPrefsPodesavanjaZaNotifikacije(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checkBox = prefs.getBoolean("notifications", true);
        return checkBox;
    }
    //provera prefs settingsa za checkbox toast
    private boolean proveraPrefsPodesavanjaZaToast(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checkBox = prefs.getBoolean("toast", true);
        return checkBox;
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


    //validacija unosa za filma
    private boolean validateActor(EditText editText) {
        String titleInput = editText.getText().toString().trim();

        if (titleInput.isEmpty()) {
            editText.setError("Field can't be empty");
            return false;
        }else {
            editText.setError(null);
            return true;
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
