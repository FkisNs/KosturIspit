package com.example.kosturispit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kosturispit.R;
import com.example.kosturispit.models.Glumac;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RVadapter extends RecyclerView.Adapter<RVadapter.MyViewHolder> {
    private List<Glumac> listaGlumaca;


    public OnRVItemClick listenerListaGlumaca;

    public interface OnRVItemClick{
        void onRVItemclick(Glumac glumac);
    }

    public RVadapter(List<Glumac> listaGlumaca, OnRVItemClick listenerListaGlumaca) {
        this.listaGlumaca = listaGlumaca;
        this.listenerListaGlumaca = listenerListaGlumaca;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirstName;
        TextView tvLastName;
        ImageView imageView;
        TextView rating;
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.iv_single_item);
            tvFirstName = itemView.findViewById(R.id.tv_recycler_ime_glumca);
            tvLastName = itemView.findViewById(R.id.tv_recycler_prezime_glumca);
            rating = itemView.findViewById(R.id.tv_rating_single_item);
        }

        public void bind(final Glumac glumac, final OnRVItemClick listener){
            tvFirstName.setText(glumac.getIme());
            tvLastName.setText(glumac.getPrezime());
            rating.setText("    rating: " +glumac.getOcena());

            Picasso.get()
                    .load("http://i.imgur.com/DvpvklR.png")
                    .fit()
                    .centerInside()
                    .into(imageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRVItemclick(glumac);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.rv_single_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.bind(listaGlumaca.get(i),listenerListaGlumaca);
    }

    @Override
    public int getItemCount() {
        return listaGlumaca.size();
    }

    //dodavanje i refresh rv liste
    public void setNewData(List<Glumac> listaGlumaca){
        this.listaGlumaca.clear();
        this.listaGlumaca.addAll(listaGlumaca);
        notifyDataSetChanged();
    }

}