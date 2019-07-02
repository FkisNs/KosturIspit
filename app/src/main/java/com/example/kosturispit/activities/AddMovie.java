package com.example.kosturispit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kosturispit.R;
import com.example.kosturispit.models.DatabaseHelper;
import com.example.kosturispit.models.Film;
import com.example.kosturispit.models.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

public class AddMovie extends AppCompatActivity {

    private EditText title;
    private EditText zanr;
    private EditText year;
    private Button btn_add;
    private int glumac_id;
    private Glumac glumac = null;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        title = findViewById(R.id.et_title_add_movie);
        zanr = findViewById(R.id.et_genre_add_movie);
        year = findViewById(R.id.et_year_add_movie);
        btn_add = findViewById(R.id.btn_add_add_movie);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateMovie(title) && validateMovie(zanr) && validateMovie(year)) {
                    addMovieToTheList();
                }
            }
        });
    }

    //take glumac_id i na osnovu toga ubacuje se film za tog glumca.
    //posle ubacivanja vraca se na DetailActivity
    private void addMovieToTheList() {
        glumac_id =  getIntent().getIntExtra("glumac_id", 1);

        try {
            glumac = getDatabaseHelper().getGlumacDao().queryForId(glumac_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Film film = new Film();
        film.setNaziv(title.getText().toString());
        film.setGenre(zanr.getText().toString());
        film.setGodina(year.getText().toString());
        film.setGlumac(glumac);

        try {
            getDatabaseHelper().getmMovieDao().createIfNotExists(film);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("glumac_id",glumac_id);
        startActivity(intent);

    }

    //validacija unosa za filma
    private boolean validateMovie(EditText editText) {
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

