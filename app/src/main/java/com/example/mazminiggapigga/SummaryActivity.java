package com.example.mazminiggapigga;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvSummary = findViewById(R.id.tvSummary);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvEta = findViewById(R.id.tvEta);
        Button btnPay = findViewById(R.id.btnPay);
        Button btnBack = findViewById(R.id.btnBack);

        Order order = getIntent().getParcelableExtra("order");
        String business = getIntent().getStringExtra("business");

        tvSummary.setText(business + "\nההזמנה שלך: " + order.getQuantity() + " פיצה "
                + order.getSize() + ", בצק " + order.getCrust());
        tvPrice.setText("לתשלום: " + order.getPrice() + " ₪");

        // שעון ספירה לאחור של זמן ההגעה (20 דקות)
        timer = new CountDownTimer(20 * 60 * 1000L, 1000) {
            @Override
            public void onTick(long ms) {
                long sec = ms / 1000;
                tvEta.setText(String.format(Locale.US, "זמן הגעה: %02d:%02d", sec / 60, sec % 60));
            }

            @Override
            public void onFinish() {
                tvEta.setText("השליח בדרך אליכם!");
            }
        }.start();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, PaymentActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
