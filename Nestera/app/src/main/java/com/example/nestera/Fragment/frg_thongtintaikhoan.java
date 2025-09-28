package com.example.nestera.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nestera.Adapter.ThongTinTaiKhoan_Adapter;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class frg_thongtintaikhoan extends Fragment {


    public frg_thongtintaikhoan() {
        // Required empty public constructor
    }
    nguoiThueDao dao;
    ArrayList<NguoiThue> list;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    ListView lstThongTin;
    ThongTinTaiKhoan_Adapter thongTinTaiKhoan_adapter;
    ArrayList<NguoiThue> list_nt = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_frg_thongtintaikhoan, container, false);
        lstThongTin=v.findViewById(R.id.lstThongTin);
        dao = new nguoiThueDao(getActivity());
        list = (ArrayList<NguoiThue>) dao.getAll();
        thongTinTaiKhoan_adapter = new ThongTinTaiKhoan_Adapter(getActivity(), this,list);
        lstThongTin.setAdapter(thongTinTaiKhoan_adapter);

        Bundle i= getArguments();
        if (i!=null){
            String user=i.getString("key");
            list_nt = dao.getNguoiThueByUser(user);
            thongTinTaiKhoan_adapter = new ThongTinTaiKhoan_Adapter(getActivity(),this,list_nt);
            lstThongTin.setAdapter(thongTinTaiKhoan_adapter);
        }


        return v;
    }
}