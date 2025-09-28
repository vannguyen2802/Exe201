package com.example.nestera.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.nestera.Activity.suCo_Activity;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.Dao.suCoDao;
import com.example.nestera.R;
import com.example.nestera.model.PhongTro;
import com.example.nestera.model.suCo;

import java.util.ArrayList;

public class SuCo_Adapter extends ArrayAdapter<suCo> {
    private Context context;
    suCo_Activity suCo_activity;
    private ArrayList<suCo> list;
    suCoDao sCDao;
    phongTroDao ptDao;
    ImageView imgXN;

    TextView txtSuCo, txtMoTa,txtPhong_SuCo, txtTinhTrang_SuCo;


    public SuCo_Adapter(@NonNull Context context, suCo_Activity suCo_activity, ArrayList<suCo> list) {
        super(context,0,list);
        this.context = context;
        this.suCo_activity = suCo_activity;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if( v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_suco, null);
        }
        final suCo suCo = list.get(position);
        SharedPreferences preferences = context.getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        if(suCo != null){
            txtSuCo = v.findViewById(R.id.txtSuCo);
            txtMoTa = v.findViewById(R.id.txtMoTa);
            txtPhong_SuCo = v.findViewById(R.id.txtPhong_SuCo);
            txtTinhTrang_SuCo = v.findViewById(R.id.txtTinhTrang_SuCo);
            imgXN=v.findViewById(R.id.imgXN);

            sCDao = new suCoDao(context);

            txtSuCo.setText("Loại sự cố: " + suCo.getTenSuCo());
            txtMoTa.setText("Mô tả: " + suCo.getNoiDung());

            ptDao = new phongTroDao(context);
            PhongTro phongTro=ptDao.getID(String.valueOf(suCo.getMaPhong()));
            txtPhong_SuCo.setText("Phòng: " +phongTro.getTenPhong());
            if(suCo.getTrangThai() == 0){
                txtTinhTrang_SuCo.setText("Chưa sửa chữa");
                txtTinhTrang_SuCo.setTextColor(Color.RED);
            }else{
                txtTinhTrang_SuCo.setText("Đã sửa chữa");
                imgXN.setVisibility(View.GONE);
                txtTinhTrang_SuCo.setTextColor(Color.GREEN);
            }

            if (username.equalsIgnoreCase("admin")){
                imgXN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Cảnh báo");
                        builder.setIcon(R.drawable.baseline_warning_24);
                        builder.setMessage("Bạn có chắc chắn xác nhận đã sửa chữa");
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sCDao.updateTrangThaiHoaDon(suCo.getMaSuCo(),1);
                                suCo.setTrangThai(1);
                                notifyDataSetChanged();
                                dialogInterface.cancel();
                                Toast.makeText(context, "Đã xác nhận ", Toast.LENGTH_SHORT).show();
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
                });

            }else {
                imgXN.setVisibility(View.GONE);
            }
        }
        return v;
    }
}
