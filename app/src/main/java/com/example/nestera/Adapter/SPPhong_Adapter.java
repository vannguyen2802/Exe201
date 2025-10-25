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
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;

public class SPPhong_Adapter extends ArrayAdapter<PhongTro> {
    private Context context;
    private ArrayList<PhongTro> list;
    TextView txtMaPhong, txtTenPhong;

    public SPPhong_Adapter(@NonNull Context context, ArrayList<PhongTro> list) {
        super(context, 0,list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=convertView;
        if (v==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.item_sp_phong,null);
        }
        final PhongTro item=list.get(position);
        if (item!=null){
            txtMaPhong=v.findViewById(R.id.txtMaPhongSp);
            txtTenPhong=v.findViewById(R.id.txtTenPhongSp);

            txtMaPhong.setText(item.getMaPhong()+". ");
            txtTenPhong.setText(item.getTenPhong());
        }
        return v;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.item_sp_phong,null);
        }
        final PhongTro item=list.get(position);
        if (item!=null){
            txtMaPhong=v.findViewById(R.id.txtMaPhongSp);
            txtTenPhong=v.findViewById(R.id.txtTenPhongSp);

            txtMaPhong.setText(item.getMaPhong()+". ");
            txtTenPhong.setText(item.getTenPhong());
        }
        return v;
    }
}
