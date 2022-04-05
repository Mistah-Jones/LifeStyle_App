package com.example.myapplication.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentHikeBinding;

public class HikesFragment extends Fragment {

    //private HikeViewModel notificationsViewModel;
    private FragmentHikeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        notificationsViewModel =
//                new ViewModelProvider(this).get(HikeViewModel.class);

        binding = FragmentHikeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        
        // Search for Hikes nearby
        Uri hikesUri = Uri.parse("geo:0,0?q=hikes");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, hikesUri);
        startActivity(mapIntent);
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}