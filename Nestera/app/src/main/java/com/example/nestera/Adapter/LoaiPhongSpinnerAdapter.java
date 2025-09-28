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
import com.example.nestera.model.LoaiPhong;

import java.util.ArrayList;

public class LoaiPhongSpinnerAdapter extends ArrayAdapter<LoaiPhong> {
    private Context context;
    private ArrayList<LoaiPhong> list;
    TextView txtMa,txtTen;

    public LoaiPhongSpinnerAdapter(@NonNull Context context, ArrayList<LoaiPhong> list) {
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
            v=inflater.inflate(R.layout.spn_loaiphong,null);
        }
        LoaiPhong item = list.get(position);
        if(item!=null){
            txtMa = v.findViewById(R.id.txtmaloai);
            txtTen=v.findViewById(R.id.txttenloai);
            txtMa.setText(item.getMaLoaiPhong()+".");
            txtTen.setText(item.getTenLoaiPhong());
        }
        return v;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.spn_loaiphong,null);
        }
        LoaiPhong item = list.get(position);
        if(item!=null){
            txtMa = v.findViewById(R.id.txtmaloai);
            txtTen=v.findViewById(R.id.txttenloai);
            txtMa.setText(item.getMaLoaiPhong()+".");
            txtTen.setText(item.getTenLoaiPhong());
        }
        return v;
    }
}
