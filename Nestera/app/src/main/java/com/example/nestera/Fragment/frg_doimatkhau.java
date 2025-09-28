package com.example.nestera.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;
import com.google.android.material.textfield.TextInputEditText;

public class frg_doimatkhau extends Fragment {
    TextInputEditText txtMkc, txtMkm, txtNlMkm;
    Button btnXN;
    nguoiThueDao dao;
    public frg_doimatkhau() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_frg_doimatkhau, container, false);
        txtMkc = v.findViewById(R.id.edtPassCu);
        txtMkm = v.findViewById(R.id.edtPassMoi_dmk);
        txtNlMkm = v.findViewById(R.id.edtNhapLaiPass_dmk);
        btnXN = v.findViewById(R.id.btnXacNhan);
        dao = new nguoiThueDao(getActivity());

        btnXN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("USER_FILE", MODE_PRIVATE);
                String user = preferences.getString("USERNAME", "");
                if (validate() > 0) {
                   NguoiThue nt = dao.getID(user);
                    nt.setMatKhauNT(txtMkm.getText().toString());
                    dao.update(nt);
                    if (dao.update(nt)>0) {
                        Toast.makeText(getActivity(), "Đổi mật khẩu thành công ", Toast.LENGTH_SHORT).show();
                        txtMkc.setText("");
                        txtMkm.setText("");
                        txtNlMkm.setText("");

                    } else {
                        Toast.makeText(getActivity(), "Đổi mật khẩu thất bại ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return v;

    }
    public int validate() {
        int check = 1;
        if (txtMkc.getText().length() == 0 || txtMkm.getText().length() == 0 || txtNlMkm.getText().length() == 0) {
            Toast.makeText(getActivity(), "Hãy nhập đầy đủ ", Toast.LENGTH_SHORT).show();
            check = -1;
        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("USER_FILE", MODE_PRIVATE);
            String passOld = preferences.getString("PASSWORD", "");
            String passNew = txtMkm.getText().toString();
            String RepassNew = txtNlMkm.getText().toString();
            if (!passOld.equals(txtMkc.getText().toString())) {
                Toast.makeText(getActivity(), "Mật khẩu cũ sai ", Toast.LENGTH_SHORT).show();
                check = -1;
            }
            if (!passNew.equals(RepassNew)) {
                Toast.makeText(getActivity(), "Mật khẩu mới không trùng ", Toast.LENGTH_SHORT).show();
                check = -1;
            }
        }
        return check;
    }
}