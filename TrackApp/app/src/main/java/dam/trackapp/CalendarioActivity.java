package dam.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.Calendar;

public class CalendarioActivity extends AppCompatActivity {
    private long fecha = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        final CalendarView calendarView = findViewById(R.id.calendarView);

        Button guardarButton = findViewById(R.id.calendario_guardar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 0);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fecha = cal.getTimeInMillis();
            }
        });

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.putExtra(TareaEventoDetalleActivity.ACTIVITY_RESULT_CALENDARIO_VALUE, fecha);
                setResult(TareaEventoDetalleActivity.ACTIVITY_RESULT_CALENDARIO, intent);

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
