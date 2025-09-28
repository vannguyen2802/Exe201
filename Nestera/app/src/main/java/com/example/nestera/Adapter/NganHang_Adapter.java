package com.example.nestera.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.Fragment.frg_NganHang;
import com.example.nestera.R;
import com.example.nestera.model.NganHang;

import java.util.ArrayList;

public class NganHang_Adapter extends ArrayAdapter<NganHang> {

    TextView txtTenTKNganHang, txtTenNganHang, txtSTK;
    ImageView btnDelete, imgAnh;
    private Context context;
    private ArrayList<NganHang> list;
    frg_NganHang fragment;
    byte[] hinhAnh;

    public NganHang_Adapter(@NonNull Context context, ArrayList<NganHang> list, frg_NganHang fragment) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v==null){
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.item_nganhang,null);
        }

        final NganHang item = list.get(position);
        if (item!=null){
            txtTenTKNganHang=v.findViewById(R.id.txtTenTKNganHang);
            txtTenNganHang=v.findViewById(R.id.txtTenNganHang);
            txtSTK=v.findViewById(R.id.txtStk);
            imgAnh = v.findViewById(R.id.imgAnh);
            btnDelete = v.findViewById(R.id.btnDelete);

            txtTenTKNganHang.setText("Tên tài khoản: "+item.getTenTKNganHang());
            txtTenNganHang.setText("Tên ngân hàng: "+item.getTenNganHang());
            txtSTK.setText("Số tài khoản: "+item.getSTK());

            hinhAnh = item.getHinhAnh();
            Bitmap bitmap= BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
            imgAnh.setImageBitmap(bitmap);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.xoa(String.valueOf(item.getId()));
                }
            });

        }


        return v;

    }
}
