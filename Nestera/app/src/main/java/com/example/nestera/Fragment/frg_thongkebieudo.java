package com.example.nestera.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.R;
import com.example.nestera.model.HoaDon;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class frg_thongkebieudo extends Fragment {
    ArrayList<HoaDon> list;
    hoaDonDao hdDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_frg_thongkebieudo, container, false);

        BarChart barChart = v.findViewById(R.id.barChart);
        hdDao = new hoaDonDao(getActivity());
        list = (ArrayList<HoaDon>) hdDao.getAll();

        List<BarEntry> monthlyRevenueEntries = getMonthlyRevenue(list);

        updateBarChart(barChart, monthlyRevenueEntries);

        return v;
    }

    private List<BarEntry> getMonthlyRevenue(ArrayList<HoaDon> hoaDonList) {
        List<BarEntry> entries = new ArrayList<>();
        int[] monthlyRevenue = new int[12];

        for (HoaDon hoaDon : hoaDonList) {
            if (hoaDon.getTrangThai()==2){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(hoaDon.getNgayTao());
                int month = calendar.get(Calendar.MONTH);
                monthlyRevenue[month] += hdDao.getTongTienDien(hoaDon.getMaHoaDon())
                        + hdDao.getTongTienNuoc(hoaDon.getMaHoaDon())
                        + hoaDon.getPhiDichVu() + hoaDon.getTienPhong();
            }

        }

        for (int i = 0; i < monthlyRevenue.length; i++) {
            entries.add(new BarEntry(i, monthlyRevenue[i]));
        }

        return entries;
    }

    private void updateBarChart(BarChart barChart, List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu theo tháng");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);

        // Cấu hình trục x
        String[] months = new String[]{"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelCount(months.length);
        barChart.getXAxis().setTextColor(Color.WHITE);



        // Cấu hình trục y
        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);

        // Đặt dữ liệu lên biểu đồ
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);
        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.WHITE);

        // Cập nhật biểu đồ
        barChart.invalidate();
    }
}