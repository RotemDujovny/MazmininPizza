package com.example.mazminiggapigga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvPaymentSummary = findViewById(R.id.tvPaymentSummary);
        RadioGroup rgPayment = findViewById(R.id.rgPayment);
        Button btnFinish = findViewById(R.id.btnFinish);

        Order order = getIntent().getParcelableExtra("order");
        tvPaymentSummary.setText(order.getQuantity() + " פיצה " + order.getSize() + ", בצק "
                + order.getCrust() + "\nלתשלום: " + order.getPrice() + " ₪");

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rgPayment.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(PaymentActivity.this, "בחרו אמצעי תשלום", Toast.LENGTH_SHORT).show();
                    return;
                }
                String method = (id == R.id.rbCash) ? "במזומן" : "באשראי";
                Toast.makeText(PaymentActivity.this, "שולם " + method + ". תודה!", Toast.LENGTH_SHORT).show();

                // חוזרים למסך ההזמנה כמסך חדש (מאופס), וסוגרים את שאר המסכים
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
