package com.levent.landmarkbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.levent.landmarkbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    ArrayList<Landmark> landmarkArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        landmarkArrayList = new ArrayList<>();

        Landmark pisa = new Landmark("pisa","Italy",R.drawable.pisatower);
        Landmark colosseum = new Landmark("colosseum","Italy",R.drawable.colosseum);
        Landmark londonBridge = new Landmark("londonBridge","",R.drawable.londonbridge);
        Landmark eiffelTower = new Landmark("eiffelTower","France",R.drawable.eiffeltower);

        landmarkArrayList.add(pisa);
        landmarkArrayList.add(colosseum);
        landmarkArrayList.add(londonBridge);
        landmarkArrayList.add(eiffelTower);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LandmarkAdapter landmarkAdapter = new LandmarkAdapter(landmarkArrayList);

        binding.recyclerView.setAdapter(landmarkAdapter);

    }
}