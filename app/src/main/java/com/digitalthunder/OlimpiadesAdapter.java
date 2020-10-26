package com.digitalthunder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalthunder.ui.events.Olimpiade;

import java.util.ArrayList;

public class OlimpiadesAdapter extends RecyclerView.Adapter<OlimpiadesAdapter.OlimpiadesViewHolder> {

    ArrayList<Olimpiade> olimpiades;
    public OlimpiadesAdapter(ArrayList<Olimpiade> olimpiades) {
        this.olimpiades = olimpiades;
    }

    @NonNull
    @Override
    public OlimpiadesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.olimpiade_list_layout, parent, false);
        return new OlimpiadesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OlimpiadesViewHolder holder, int position) {
        holder.classes.setText(olimpiades.get(position).classes);
        holder.title.setText(olimpiades.get(position).title);
        holder.subTilte.setText(olimpiades.get(position).subTitle);
        holder.description.setText(olimpiades.get(position).description);
    }

    @Override
    public int getItemCount() {
        return olimpiades.size();
    }

    public class OlimpiadesViewHolder extends RecyclerView.ViewHolder {
        TextView classes;
        TextView title;
        TextView subTilte;
        TextView description;

        public OlimpiadesViewHolder(@NonNull View itemView) {
            super(itemView);
            classes = (TextView) itemView.findViewById(R.id.classes);
            title = (TextView) itemView.findViewById(R.id.title);
            subTilte = (TextView) itemView.findViewById(R.id.subtitle);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
