package husenansari.com.movie_stage1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Movies_Adapter extends ArrayAdapter {
    LayoutInflater inflater;
    Context context;

    Movies_Adapter(Context context, int id, ArrayList<Movies> images) {
        super(context, id, images);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movies movies = (Movies) getItem(position);
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_movies,parent,false);
            Log.d("movie_stage1","inflate");
        }
        ImageView image1=(ImageView)convertView.findViewById(R.id.image1);
        Log.d("movie_stage1", movies.getImage_url());
        Picasso.with(context).load(movies.getImage_url()).into(image1);
        return convertView;
    }
}