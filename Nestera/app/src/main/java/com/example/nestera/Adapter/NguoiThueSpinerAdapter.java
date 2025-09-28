package com.example.nestera.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;

import java.util.ArrayList;

public class NguoiThueSpinerAdapter extends ArrayAdapter<NguoiThue> {
    private Context context;
    private ArrayList<NguoiThue> list;
    TextView txtTen,txtMa;

    public NguoiThueSpinerAdapter(@NonNull Context context, ArrayList<NguoiThue> list) {
        super(context, 0,list);
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.spn_nguoithue,null);
        }
        NguoiThue item = list.get(position);
        if(item!=null){
            txtTen=v.findViewById(R.id.txttennd);
            txtTen.setText(item.getTenNguoiThue());
            txtMa=v.findViewById(R.id.txtmand);
            txtMa.setText(item.getMaNguoithue());
            txtMa.setVisibility(View.GONE);
        }
        return v;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.spn_nguoithue,null);
        }
        NguoiThue item = list.get(position);
        if(item!=null){
            txtTen=v.findViewById(R.id.txttennd);
            txtTen.setText(item.getTenNguoiThue());
            txtMa=v.findViewById(R.id.txtmand);
            txtMa.setText(item.getMaNguoithue());
            txtMa.setVisibility(View.GONE);
        }
        return v;
    }
}
