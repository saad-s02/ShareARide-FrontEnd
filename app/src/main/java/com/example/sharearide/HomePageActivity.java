package com.example.sharearide;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageActivity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePageActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageActivity newInstance(String param1, String param2) {
        HomePageActivity fragment = new HomePageActivity();
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

//    ToggleButton temp_btn, recur_btn;
    Button offer_btn, request_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.home_page, container, false);
//        temp_btn = (ToggleButton) rootView.findViewById(R.id.btn_temp);
//        recur_btn = (ToggleButton) rootView.findViewById(R.id.btn_recur);
        offer_btn = (Button) rootView.findViewById(R.id.btn_offer);
        request_btn = (Button) rootView.findViewById(R.id.btn_request);

        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RequestActivity.class);
                startActivity(intent);
            }
        });

        offer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                startActivity(intent);
//                if (temp_btn.isChecked()) {
//                    Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
//                    startActivity(intent);
//                } else if (recur_btn.isChecked()) {
//                    Intent intent = new Intent(getActivity(), RecurOfferActivity.class);
//                    startActivity(intent);
//                }
            }
        });

//        temp_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (temp_btn.isChecked()) {
//                    recur_btn.setChecked(false);
//                }
//            }
//        });
//
//        recur_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (recur_btn.isChecked()) {
//                    temp_btn.setChecked(false);
//                }
//            }
//        });

        return rootView;
    }
}