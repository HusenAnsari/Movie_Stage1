package husenansari.com.movie_stage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Movie_Detail extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title = (TextView) findViewById(R.id.title);
        TextView overview = (TextView) findViewById(R.id.overview);
        TextView release_date = (TextView) findViewById(R.id.release);

        title.setText("" + getIntent().getExtras().getString("name"));
        overview.setText("" + getIntent().getExtras().getString("overview"));
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        String year = getIntent().getExtras().getString("release_date");
        Matcher dateMatcher = pattern.matcher(year);
        if (dateMatcher.find()) {
            year = dateMatcher.group(1);
        }
        release_date.setText("" + year);
        TextView ratings = (TextView) findViewById(R.id.ratings);
        ratings.setText("" + getIntent().getExtras().getDouble("ratings") + "/10");
        ImageView poster = (ImageView) findViewById(R.id.poster);
        String final_url = "http://image.tmdb.org/t/p/w185/" + getIntent().getExtras().getString("url");
        Picasso.with(this).load(final_url).into(poster);

    }


}
