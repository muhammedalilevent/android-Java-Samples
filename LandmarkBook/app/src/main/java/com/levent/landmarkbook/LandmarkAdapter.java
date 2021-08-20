package com.levent.landmarkbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.levent.landmarkbook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkHolder> {

    ArrayList<Landmark> landmarkArrayList;

    public LandmarkAdapter(ArrayList<Landmark> landmarkArrayList) {
        this.landmarkArrayList = landmarkArrayList;
    }

    @NonNull
    @Override
    public LandmarkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //XML'in bağlandığı yer

        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new LandmarkHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LandmarkAdapter.LandmarkHolder holder,int position) {
    //Değerleri Vereceğiz

        holder.binding.recyclerViewTextView.setText(landmarkArrayList.get(position).name);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(),DetailsActivity.class);
            //intent.putExtra("landmark",landmarkArrayList.get(position));
            Singleton singleton = Singleton.getInstance();
            singleton.setChosenLandmark(landmarkArrayList.get(position));
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        //Kaç tane olduğunu yazcaz
        return landmarkArrayList.size();
    }

    public class LandmarkHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public LandmarkHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
