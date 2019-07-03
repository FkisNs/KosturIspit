package com.example.kosturispit.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kosturispit.R;
import com.example.kosturispit.dialog.AboutDialog;
import com.example.kosturispit.models.DatabaseHelper;
import com.example.kosturispit.models.Film;
import com.example.kosturispit.models.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;
    private TextView tv_first_name;
    private TextView tv_last_name;
    private TextView tv_rodjen;
    private TextView tv_bio;
    private TextView tv_ocena;
    private ImageButton imageButton;
    private TextView rating;
    private Glumac glumac;
    private Button btn_add_movie;
    private Button btn_edit;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    List<String> drawerItems;
    ListView drawerList;

    public static final int notificationID = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupToolbar();
        getAndSetData(getIntent());
        fillData();
        setupDrawer();
    }

    //Pocetak setup-a za toolbar
    // action bar prikazuje opcije iz meni.xml
    //uneti u action main.xml AppBarLayout i onda Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // onOptionsItemSelected method is called whenever an item in the Toolbar is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_movie:
                addMovie();

                if(proveraPrefsPodesavanjaZaToast()){
                    Toast.makeText(this, "Add new movie to the actor", Toast.LENGTH_SHORT).show();
                }
                if(proveraPrefsPodesavanjaZaNotifikacije()){
                    setupNotification(glumac, " add movie to the actor "+ glumac);
                }

                break;
            case R.id.action_edit:
                editActor();
                break;
            case R.id.action_delete:
                deleteActor();
                break;
            case R.id.action_about:
                Intent intent3 = new Intent(this,AboutDialog.class);
                startActivity(intent3);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //setuje toolbar
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_hamburger);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }
    }
    //kraj setup-a za toolbar

    //popunjava listu za drawer
    private void fillData(){
        drawerItems = new ArrayList<>();
        drawerItems.add("Lista glumaca");
        drawerItems.add("Settings");
        drawerItems.add("O aplikaciji");
    }

    //podesavanje Drawera
    private void setupDrawer(){
        drawerList = findViewById(R.id.left_drawer_detail);
        drawerLayout = findViewById(R.id.drawer_layout_details);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i){
                    case 0:
                        title = "Lista glumaca";

                        break;
                    case 1:
                        Intent settings = new Intent(DetailsActivity.this,SettingsActivity.class);
                        startActivity(settings);
                        title = "SettingsActivity";
                        setTitle(title);
                        break;
                    case 2:
                        AboutDialog dialog = new AboutDialog(DetailsActivity.this);
                        dialog.show();
                        title = "O aplikaciji";
                        break;
                    default:
                        break;
                }
                //drawerList.setItemChecked(i, true);
                setTitle(title);
                drawerLayout.closeDrawer(drawerList);
            }

        });
        drawerToggle = new ActionBarDrawerToggle(
                this,                           /* host Activity */
                drawerLayout,                   /* DrawerLayout object */
                toolbar,                        /* nav drawer image to replace 'Up' caret */
                R.string.app_name,           /* "open drawer" description for accessibility */
                R.string.app_name           /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle("");
                invalidateOptionsMenu();        // Creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                getSupportActionBar().setTitle("");
                invalidateOptionsMenu();        // Creates call to onPrepareOptionsMenu()
            }
        };
    }


    private void editActor(){
        Intent intent = new Intent(DetailsActivity.this, EditActor.class);
        intent.putExtra("glumac_id",glumac.getId());
        startActivity(intent);
    }
    //brise glumca i sve filmove koji su vezani za id tog glumca
    private void deleteActor() {

        try {
            List<Film> filmovi = getDatabaseHelper().getmMovieDao().queryForEq("glumac_id", glumac.getId());

            getDatabaseHelper().getGlumacDao().delete(glumac);
            for(Film film:filmovi) {
                getDatabaseHelper().getmMovieDao().delete(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(proveraPrefsPodesavanjaZaToast()) {
            Toast.makeText(this, "Actor " + glumac + " deleted.", Toast.LENGTH_SHORT).show();
        }
        if(proveraPrefsPodesavanjaZaNotifikacije()){
            setupNotification(glumac, " deleted from the list");
            Toast.makeText(this, "Notify - Actor " + glumac + " deleted.", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
        startActivity(intent);
    }
    private void addMovie(){
        int glumac_id = glumac.getId();
        Intent intent = new Intent(this, AddMovie.class);
        intent.putExtra("glumac_id",glumac_id);
        startActivity(intent);
    }
    //na osnovu intenta vadi glumac_id i povraci iz baze podatke za tog glumca i popunjava polja
    private void getAndSetData(Intent intent){
        int glumac_id = intent.getIntExtra("glumac_id",1);
        List<Film> movies;

        ListView listaFilmova = findViewById(R.id.lv_filmovi_f_detalji_glumca);
        try {
            glumac = getDatabaseHelper().getGlumacDao().queryForId(glumac_id);
            movies = getDatabaseHelper().getmMovieDao().queryForEq("glumac_id",glumac_id);
            ArrayAdapter<Film> adapterFilmova = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,movies);
            listaFilmova.setAdapter(adapterFilmova);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tv_first_name = findViewById(R.id.et_details_ime);
        tv_last_name = findViewById(R.id.et_details_prezime);
        tv_rodjen = findViewById(R.id.et_details_DOB);
        tv_bio = findViewById(R.id.et_details_bio);
        tv_ocena = findViewById(R.id.et_details_ocena);
        imageButton = findViewById(R.id.et_details_imageButtonActor);


        tv_first_name.setText(glumac.getIme());
        tv_last_name.setText(glumac.getPrezime());
        tv_bio.setText(glumac.getBiografija());
        tv_rodjen.setText(glumac.getDateBirth());
        tv_ocena.setText(glumac.getOcena());
        imageButton.setImageBitmap(BitmapFactory.decodeFile(glumac.getImageId()));

//        Picasso.get().load(glumac.getImageId()).into(imageButton);

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
    private void setupNotification(Glumac glumac, String text){
        String textForTheNotification = text;
        Notification builder = new Notification.Builder(this)
                .setContentTitle("Notification")
                .setContentText(glumac.toString() + textForTheNotification)
                .setSmallIcon(R.drawable.ic_action_delete)
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, builder);

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
