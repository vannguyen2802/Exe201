package com.example.nestera.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nestera.Adapter.NganHang_Adapter;
import com.example.nestera.Dao.NganHangDao;
import com.example.nestera.R;
import com.example.nestera.model.NganHang;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class frg_NganHang extends Fragment {

    ListView lstNganHang;
    FloatingActionButton fab;
    Dialog dialog;
    EditText edtTenTKNganHang, edtTenNganHang,edtSTK,edtID;
    ImageView imgAnhQR;
    Button btnChonAnh,btnXacNhan,btnHuy;
    ArrayList<NganHang> list;
    NganHang item;
    NganHangDao nganHangDao;
    NganHang_Adapter nganHangAdapter;
    byte[] hinhAnh;
    final int REQUEST_CODE_FOLDER = 456;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_frg__ngan_hang, container, false);
        fab=v.findViewById(R.id.fltadd);
        lstNganHang=v.findViewById(R.id.lstNganHang);
        nganHangDao=new NganHangDao(getActivity());
        capNhatLv();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(getActivity(),0);
            }
        });
        lstNganHang.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                item = list.get(i);
                openDialog(getActivity(),1);
                return false;
            }
        });



        return v;
    }

    protected void openDialog(final Context context, final int type){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_nganhang);
        btnChonAnh=dialog.findViewById(R.id.btnChonAnh);
        btnXacNhan=dialog.findViewById(R.id.btnXacNhan);
        btnHuy=dialog.findViewById(R.id.btnHuy);
        imgAnhQR=dialog.findViewById(R.id.imgAnhQR);
        edtTenTKNganHang=dialog.findViewById(R.id.edtTenTKNganHang);
        edtTenNganHang=dialog.findViewById(R.id.edtTenNganHang);
        edtSTK=dialog.findViewById(R.id.edtSTK);
        edtID=dialog.findViewById(R.id.edtID);

        edtID.setVisibility(View.GONE);



        if (type!=0){
            edtID.setText(String.valueOf(item.getId()));
            edtTenTKNganHang.setText(item.getTenTKNganHang());
            edtTenNganHang.setText(item.getTenNganHang());
            edtSTK.setText(item.getSTK());
        }

        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_FOLDER);
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edtTenTKNganHang.getText().toString()) || TextUtils.isEmpty(edtTenNganHang.getText().toString()) || TextUtils.isEmpty(edtSTK.getText().toString())) {
                    Toast.makeText(context, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgAnhQR.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                hinhAnh = byteArrayOutputStream.toByteArray();

                item=new NganHang();
                item.setTenTKNganHang(edtTenTKNganHang.getText().toString());
                item.setTenNganHang(edtTenNganHang.getText().toString());
                item.setSTK(edtSTK.getText().toString());
                item.setHinhAnh(hinhAnh);

                if (type==0){
                    if (nganHangDao.insert(item)>0){
                        Toast.makeText(context, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Thêm Thất Bại", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    item.setId(Integer.parseInt(edtID.getText().toString()));
                    if (nganHangDao.update(item)>0){
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                capNhatLv();
                dialog.dismiss();

            }
        });

        dialog.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAnhQR.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        switch (requestCode){
//            case REQUEST_CODE_FOLDER:
//                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    startActivityForResult(intent,REQUEST_CODE_FOLDER);
//                }else {
//                    Toast.makeText(getContext(), "Bạn không cho phép truy cập thư viện ảnh", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//        }
//
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    public void capNhatLv() {
        list = (ArrayList<NganHang>) nganHangDao.getAll();
        nganHangAdapter = new NganHang_Adapter(getActivity(),list,this);
        lstNganHang.setAdapter(nganHangAdapter);
    }

    public void xoa(String Id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cảnh báo");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setMessage("Bạn có chắc chắn muốn xoá");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nganHangDao.delete(Id);
                capNhatLv();
                dialogInterface.cancel();
                Toast.makeText(getContext(), "Xóa thành công ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        builder.show();
    }
}