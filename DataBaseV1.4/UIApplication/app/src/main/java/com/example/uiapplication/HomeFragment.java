package com.example.uiapplication;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View homeview = inflater.inflate(R.layout.fragment_home, container, false);
        Button bnt_query_voice = homeview.findViewById(R.id.bnt_query_voice);
        Button bnt_query_local = homeview.findViewById(R.id.bnt_query_local);
        View line= homeview.findViewById(R.id.line_home);


        bnt_query_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), voiceQueryActivity.class);
                startActivity(intent);
            }
        });

        bnt_query_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), localQueryActivity.class);
                startActivity(intent);
            }
        });


        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(line,  "alpha", 0f, 1f);
        alphaAnimator.setDuration(1000); // 设置动画持续时间，单位为毫秒
        alphaAnimator.start();

        alphaAnimator = ObjectAnimator.ofFloat(bnt_query_voice,  "translationX", -1000f, 0f);
        alphaAnimator.setDuration(1000); // 设置动画持续时间，单位为毫秒
        alphaAnimator.start();

        alphaAnimator = ObjectAnimator.ofFloat(bnt_query_local,  "translationX", 1000f, 0f);
        alphaAnimator.setDuration(1000); // 设置动画持续时间，单位为毫秒
        alphaAnimator.start();

        line.setVisibility(View.VISIBLE);
        return homeview;
    }
}