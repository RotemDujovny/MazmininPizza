package com.example.mazminiggapigga;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static final String[] SIZES = {"קטנה", "בינונית", "משפחתית"};
    static final int[] SIZE_PRICES = {35, 45, 62};
    static final String[] CRUSTS = {"דק", "עבה"};
    static final int THICK_EXTRA = 7;
    static final int DEFAULT_SIZE = 1;

    RadioGroup rgSize;
    Button btnCrust;
    SeekBar sbQty;
    TextView tvQtyLabel;
    TextView tvPrice;

    int selectedCrust = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rgSize = findViewById(R.id.rgSize);
        btnCrust = findViewById(R.id.btnCrust);
        sbQty = findViewById(R.id.sbQty);
        tvQtyLabel = findViewById(R.id.tvQtyLabel);
        tvPrice = findViewById(R.id.tvPrice);
        Button btnOrder = findViewById(R.id.btnOrder);
        Button btnClear = findViewById(R.id.btnClear);
        TextView tvTitle = findViewById(R.id.tvTitle);

        // הכפתורים של הגודל נבנים מהמערך
        for (int i = 0; i < SIZES.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(SIZES[i] + " (" + SIZE_PRICES[i] + " ₪)");
            rb.setId(i);
            rgSize.addView(rb);
        }
        rgSize.check(DEFAULT_SIZE);

        rgSize.setOnCheckedChangeListener((group, checkedId) -> updatePrice());

        sbQty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePrice();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnCrust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("בחרו בצק");
                builder.setSingleChoiceItems(CRUSTS, selectedCrust, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCrust = which;
                        btnCrust.setText("בצק: " + CRUSTS[which]);
                        updatePrice();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCrust == -1) {
                    Toast.makeText(MainActivity.this, "בחרו סוג בצק לפני ההזמנה", Toast.LENGTH_SHORT).show();
                    return;
                }

                String size = SIZES[rgSize.getCheckedRadioButtonId()];
                String crust = CRUSTS[selectedCrust];
                int qty = sbQty.getProgress() + 1;
                int price = calcPrice();
                Order order = new Order(size, crust, qty, price);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("אישור הזמנה");
                builder.setMessage("להזמין " + qty + " פיצה " + size + " עם בצק " + crust
                        + "?\nלתשלום: " + price + " ₪");

                builder.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "ההזמנה התקבלה!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
                        intent.putExtra("order", order);
                        intent.putExtra("business", tvTitle.getText().toString());
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("ביטול", null);
                builder.show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean untouched = rgSize.getCheckedRadioButtonId() == DEFAULT_SIZE
                        && selectedCrust == -1
                        && sbQty.getProgress() == 0;
                if (untouched) {
                    Toast.makeText(MainActivity.this, "אין מה לנקות", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("לנקות את כל הבחירות?");
                builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rgSize.check(DEFAULT_SIZE);
                        selectedCrust = -1;
                        btnCrust.setText("בחרו בצק");
                        sbQty.setProgress(0);
                        updatePrice();
                        Toast.makeText(MainActivity.this, "הבחירות נוקו", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("לא", null);
                builder.show();
            }
        });

        updatePrice();
    }

    // מחיר ליחידה: גודל + בצק עבה (בחינם בימי שלישי), כפול הכמות
    int calcPrice() {
        int checked = rgSize.getCheckedRadioButtonId();
        int unit = SIZE_PRICES[checked == -1 ? DEFAULT_SIZE : checked];
        boolean tuesday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;
        if (selectedCrust == 1 && !tuesday) {
            unit += THICK_EXTRA;
        }
        return unit * (sbQty.getProgress() + 1);
    }

    void updatePrice() {
        if (tvPrice == null || sbQty == null) return;
        tvQtyLabel.setText("כמות: " + (sbQty.getProgress() + 1));
        tvPrice.setText("לתשלום: " + calcPrice() + " ₪");
    }
}
