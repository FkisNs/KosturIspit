package com.example.kosturispit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kosturispit.R;
import com.example.kosturispit.adapter.RVadapter;
import com.example.kosturispit.dialog.AboutDialog;
import com.example.kosturispit.models.DatabaseHelper;
import com.example.kosturispit.models.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RVadapter.OnRVItemClick {

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    List<String> drawerItems;
    ListView drawerList;
    private RecyclerView recyclerView;
    private RVadapter rvAdapterNotClass;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        fillData();
        setupDrawer();
        setupRV();
    }

    //Pocetak setup-a za toolbar
    // action bar prikazuje opcije iz meni.xml
    //uneti u action main.xml AppBarLayout i onda Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // onOptionsItemSelected method is called whenever an item in the Toolbar is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                Intent intent = new Intent(this,AddActor.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Intent intent1 = new Intent(this,SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_about:
                Intent intent2 = new Intent(this,AboutDialog.class);
                startActivity(intent2);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //setuje toolbar
    private void setupToolbar(){
        toolbar = findViewById(R.id.toolbar);
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
        drawerList = findViewById(R.id.left_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
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
                        Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(settings);
                        title = "SettingsActivity";
                        setTitle(title);
                        break;
                    case 2:
                        AboutDialog dialog = new AboutDialog(MainActivity.this);
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

    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
    //stavljanj RV adatera
    private void setupRV(){
        recyclerView = findViewById(R.id.rvLista);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(rvAdapterNotClass == null) {

            try {
                rvAdapterNotClass = new RVadapter(getDatabaseHelper().getGlumacDao().queryForAll(), this);
                recyclerView.setAdapter(rvAdapterNotClass);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            rvAdapterNotClass.notifyDataSetChanged();
        }
    }
    //interfejs sa RVAdapterListaGlumaca
    @Override
    public void onRVItemclick(Glumac glumac) {
        Intent intent = new Intent(this,DetailsActivity.class);
        intent.putExtra("glumac_id",glumac.getId());
        startActivity(intent);
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
