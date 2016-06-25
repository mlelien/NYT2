package com.example.emilyl.nyt.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.emilyl.nyt.DatePickerFragment;
import com.example.emilyl.nyt.R;
import com.example.emilyl.nyt.SearchFilters;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.etDate) EditText etDate;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.spinner) Spinner spinner;

    SearchFilters searchFilters = new SearchFilters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
    }

    public void dateTextClicked(View view) {
        showDatePickerDialog();
    }

    public void showDatePickerDialog( ) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
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

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.artsBox:
                if (checked) searchFilters.addNewsCategory("Arts");
                else {searchFilters.removeNewsCategory("Arts"); }
                break;
            case R.id.fashionBox:
                if (checked) searchFilters.addNewsCategory("Fashion & Style");
                else searchFilters.removeNewsCategory("Fashion & Style");
                break;
            case R.id.sportsBox:
                if (checked) searchFilters.addNewsCategory("Sports");
                else searchFilters.removeNewsCategory("Sports");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (spinner.getSelectedItem().toString().equals("Newest"))
            searchFilters.setSort("newest");
        else if (spinner.getSelectedItem().toString().equals("Oldest"))
            searchFilters.setSort("oldest");

        Intent intent = new Intent();
        intent.putExtra("filters", Parcels.wrap(searchFilters));
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
