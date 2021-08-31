package com.levent.cryptocurrencyapi.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.levent.cryptocurrencyapi.R;
import com.levent.cryptocurrencyapi.model.CryptoModel;

import java.util.ArrayList;

//RecyclerView Adapter bizden birr ViewHolder İster
//row layout'u istiyor

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder> {

    private ArrayList<CryptoModel> cryptoList;

    private String[] colors = {"#64c9cf","#beb3d7","#c7d6e8","#c7a8b2","#8acae7","#494a65","#ffa700","#765e67"};

    

    public RecyclerViewAdapter(ArrayList<CryptoModel> cryptoList) {

        this.cryptoList = cryptoList;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.row_layout, parent, false);
        return new RowHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {

        holder.bind(cryptoList.get(position),colors,position);

    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public class RowHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textPrice;

        public RowHolder(@NonNull View itemView) {
            super(itemView);


        }

        public void bind(CryptoModel cryptoModel, String[] colors, Integer position) {

            itemView.setBackgroundColor(Color.parseColor(colors[position %8]));

            textName = itemView.findViewById(R.id.textName);
            textPrice =itemView.findViewById(R.id.textPrice);

            textName.setText(cryptoList.get(position).currency);
            textPrice.setText(cryptoList.get(position).price);
        }


    }
}
