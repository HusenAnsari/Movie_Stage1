package husenansari.com.movie_stage1;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    String LOG_TAG="movie_stage1";
    Movies_Adapter madapter;
    ArrayList<Movies> movies;
    String sort_type="popularity";
    public MainActivityFragment() {
    }
    public static String MOVIE = "movie_details";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie(sort_type);
    }

    private void updateMovie(String sort_type){
        if(isNetworkAvailable()) {
            UpdateMovie updateMovie = new UpdateMovie();
            updateMovie.execute(sort_type);
        }else {
            Toast.makeText(getContext(),"No network available",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment,menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_pop:
                sort_type="popularity";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                updateMovie(sort_type);
                return true;
            case R.id.sort_by_rate:
                sort_type="vote_average";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                updateMovie(sort_type);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movies=new ArrayList<>();
        madapter = new Movies_Adapter(getActivity(), R.layout.list_movies, new ArrayList<Movies>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grids);
        gridView.setAdapter(madapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movies_item = (Movies) parent.getItemAtPosition(position);
                Intent i = new Intent(getContext(), Movie_Detail.class);
                i.putExtra("name", movies_item.getTitle());
                i.putExtra("url", movies_item.image_url);
                i.putExtra("ratings", movies_item.getRatings());
                i.putExtra("release_date", movies_item.getRelease_date());
                i.putExtra("overview", movies_item.getOverview());
                startActivity(i);
            }
        });
        updateMovie(sort_type);
        return  rootView;
    }


    public class UpdateMovie extends AsyncTask<String,Void,Movies[]> {

        HttpURLConnection urlConnection;
        BufferedReader bufferedReader;
        String movies_detail=null;
        @Override
        protected Movies[] doInBackground(String... params) {
            final String BASE_URL="http://api.themoviedb.org/3";
            final String DISCOVER="/discover";
            final String BY_MOVIE="/movie";
            final String SORT_BY="?sort_by="+params[0]+".desc";
            final String SORT_BY_POP="?sort_by=popularity.desc";
            final String SORT_BY_RATINGS="?sort_by=vote_average.desc";
            final String API_KEY="&api_key=API KEY PUT HERE";
            String path=BASE_URL+DISCOVER+BY_MOVIE+SORT_BY+API_KEY;

            try{
                URL url=new URL(path);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                if(inputStream==null){
                    return  null;
                }
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer=new StringBuffer();
                String line;
                while((line=bufferedReader.readLine())!=null){
                    buffer.append(line);
                }
                if(buffer.length()==0){
                    Log.d(LOG_TAG, "No fetched the buffer");
                    return null;
                }
                movies_detail=buffer.toString();
            }catch(Exception e){
                Log.e(LOG_TAG,e.getMessage());
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                try{
                    if(bufferedReader!=null){
                        bufferedReader.close();
                    }
                }catch (Exception e){
                    Log.e(LOG_TAG,"reader didnt close properly");
                }
            }
            return getMovieData(movies_detail);
        }
        private Movies[] getMovieData(String movies_detail) {
            final String TITLE = "original_title";
            final String OVER_VIEW = "overview";
            final String POSTER_PATH = "poster_path";
            final String RELEASE_DATE = "release_date";
            final String RATINGS = "vote_average";
            final String RESULTS = "results";
            Movies[] movies=null;
            try {
                JSONObject movie_json = new JSONObject(movies_detail);
                JSONArray movieArray=movie_json.getJSONArray(RESULTS);
                movies=new Movies[movieArray.length()];
                for(int i=0;i<movieArray.length();i++){
                    JSONObject movieObject=movieArray.getJSONObject(i);
                    Movies temp_movies =new Movies();
                    temp_movies.setTitle(movieObject.getString(TITLE));
                    temp_movies.setImage_base_url(movieObject.getString(POSTER_PATH));
                    temp_movies.setOverview(movieObject.getString(OVER_VIEW));
                    temp_movies.setRatings(movieObject.getDouble(RATINGS));
                    temp_movies.setRelease_date(movieObject.getString(RELEASE_DATE));
                    movies[i]= temp_movies;
                }
            }catch (Exception e){
                Log.e(LOG_TAG,e.getMessage());
            }
            return movies;
        }
        @Override
        protected void onPostExecute(Movies[] all_movies) {
            if(all_movies!=null) {
                movies.clear();
                madapter.clear();
                for(int i=0;i<all_movies.length;i++) {
                    movies.add(all_movies[i]);
                }
                madapter.addAll(movies);
                madapter.notifyDataSetChanged();
            }
        }

    }
}
