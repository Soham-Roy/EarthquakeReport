package com.example.quakereport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ViewHolder extends RecyclerView.ViewHolder{

    TextView magnitude;
    TextView direction;
    TextView location;
    TextView date;
    TextView time;

    public ViewHolder(View itemView) {
        super(itemView);

        magnitude = itemView.findViewById(R.id.magnitude);
        direction = itemView.findViewById(R.id.direction);
        location = itemView.findViewById(R.id.location);
        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);
    }
}

interface onItemClicked {
    public void onItemClick(int pos);
}

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final ArrayList<Earthquake> mContent;
    ViewGroup parent;
    onItemClicked listener;

    public MyAdapter( ArrayList<Earthquake> al, onItemClicked listener ){
        mContent = al;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View req;
        this.parent = parent;
        req = LayoutInflater.from( parent.getContext() ).inflate(
                R.layout.list_item, parent, false );

        ViewHolder vh = new ViewHolder(req);

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(vh.getAdapterPosition());
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Earthquake here = mContent.get(position);

        String dir = here.getDirection();
        int direct = dir.indexOf("of ");
        String directionSet;
        String loc;
        if ( direct != -1 ) {
            directionSet = dir.substring(0, direct + 2);
            loc = dir.substring(direct+3);
        }
        else {
            directionSet = "Near the";
            loc = dir;
        }

        holder.direction.setText(directionSet);
        holder.location.setText(loc);

        Date now = new Date(here.getTime());
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat time = new SimpleDateFormat("h:mm a");

        holder.date.setText( date.format(now) );
        holder.time.setText( time.format(now) );

        float mag = here.getMagnitude();
        DecimalFormat dec = new DecimalFormat("0.0");
        holder.magnitude.setText( "" + dec.format(mag) );

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(here.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(parent.getContext(), magnitudeColorResourceId);
    }
}
