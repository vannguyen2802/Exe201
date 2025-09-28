package com.example.nestera.Fragment;

import android.os.Bundle;


import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



import com.example.nestera.R;


public class frg_trangchu extends Fragment {
    Button btnCoso,btnPhong,btnNguoiThue,btnThongKe,btnHoaDon,btnSuCo;
    Toolbar toolbar;



    public frg_trangchu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v=  inflater.inflate(R.layout.fragment_frg_trangchu, container, false);
        btnCoso=v.findViewById(R.id.btnCoso);
        btnHoaDon=v.findViewById(R.id.btnHoaDon);
        btnPhong=v.findViewById(R.id.btnPhong);
        btnNguoiThue=v.findViewById(R.id.btnNguoiThue);
        btnThongKe=v.findViewById(R.id.btnThongKe);
        btnHoaDon=v.findViewById(R.id.btnHoaDon);

        btnCoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                frg_coso frgcoso = new frg_coso();
//                replaceFrg(frgcoso);

            }
        });



        return v;
    }
    public void replaceFrg(Fragment frg){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frmnav,frg).commit();
    }
}