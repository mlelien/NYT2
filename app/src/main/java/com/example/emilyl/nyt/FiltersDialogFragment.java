package com.example.emilyl.nyt;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;

/**
 * Created by emilyl on 6/24/16.
 */
public class FiltersDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.etDate) EditText etDate;
    SearchFilters searchFilters = new SearchFilters();

    public FiltersDialogFragment() {}

    public static FiltersDialogFragment newInstance(String title) {
        FiltersDialogFragment fragment = new FiltersDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogue, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void dateTextClicked(View view) {
        showDatePickerDialog();
    }

    public void showDatePickerDialog( ) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(c.getTime());

        String month = "";
        switch(c.get(Calendar.MONTH)) {
            case 0: date = "January"; month = "01"; break;
            case 1: date = "February"; month = "02"; break;
            case 2: date = "March"; month = "03"; break;
            case 3: date = "April"; month = "04"; break;
            case 4: date = "May"; month = "05"; break;
            case 5: date = "June"; month = "06"; break;
            case 6: date = "July"; month = "07"; break;
            case 7: date = "August"; month = "08"; break;
            case 8: date = "September"; month = "09"; break;
            case 9: date = "October";month = "10";  break;
            case 10: date = "November"; month = "11"; break;
            case 11: date = "December"; month = "12"; break;
        }
        String numericalDate = Integer.toString(c.get(Calendar.YEAR)) + month;

        int day = c.get(Calendar.DAY_OF_MONTH);
        if (day < 10)
            numericalDate = numericalDate + "0" + Integer.toString(day);
        else
            numericalDate = Integer.toString(day);
        searchFilters.setDate(numericalDate);

        date = date + ", " + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + " " + Integer.toString(c.get(Calendar.YEAR));
        etDate.setText(date);
    }

}
