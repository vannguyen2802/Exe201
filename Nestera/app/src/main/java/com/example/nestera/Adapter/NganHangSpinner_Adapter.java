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
import com.example.nestera.model.NganHang;

import java.util.ArrayList;

public class NganHangSpinner_Adapter extends ArrayAdapter<NganHang> {

    private Context context;
    private ArrayList<NganHang> list;
    TextView txtMa,txtTen;

    public NganHangSpinner_Adapter(@NonNull Context context, ArrayList<NganHang> list) {
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
            v=inflater.inflate(R.layout.spn_nganhang,null);
        }
        NganHang item = list.get(position);
        if (item!=null){
            txtMa=v.findViewById(R.id.txtmaNganHang);
            txtTen=v.findViewById(R.id.txtTenNganHang);
            txtMa.setText(item.getId()+"");
            txtTen.setText(item.getTenNganHang());
        }

        return v;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.spn_nganhang,null);
        }
        NganHang item = list.get(position);
        if (item!=null){
            txtMa=v.findViewById(R.id.txtmaNganHang);
            txtTen=v.findViewById(R.id.txtTenNganHang);
            txtMa.setText(item.getId()+"");
            txtTen.setText(item.getTenNganHang());
        }

        return v;
    }
}
