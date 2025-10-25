package com.example.nestera.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nestera.Fragment.frg_thongkebieudo;
import com.example.nestera.Fragment.frg_thongketheongay;

public class viewpage_adapter extends FragmentStateAdapter {
    public viewpage_adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new frg_thongkebieudo();
            case 1: return new frg_thongketheongay();
        }
        return new frg_thongkebieudo();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
