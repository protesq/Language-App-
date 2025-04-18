package com.protesq.dilogrenmeeng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GrammarActivity extends AppCompatActivity {
    private Button btnPresentSimple, btnPastSimple, btnFutureSimple, btnPresentContinuous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar);

        btnPresentSimple = findViewById(R.id.btnPresentSimple);
        btnPastSimple = findViewById(R.id.btnPastSimple);
        btnFutureSimple = findViewById(R.id.btnFutureSimple);
        btnPresentContinuous = findViewById(R.id.btnPresentContinuous);

        View.OnClickListener tenseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tense = "";
                if (v.getId() == R.id.btnPresentSimple) {
                    tense = "Present Simple";
                } else if (v.getId() == R.id.btnPastSimple) {
                    tense = "Past Simple";
                } else if (v.getId() == R.id.btnFutureSimple) {
                    tense = "Future Simple";
                } else if (v.getId() == R.id.btnPresentContinuous) {
                    tense = "Present Continuous";
                }

                Intent intent = new Intent(GrammarActivity.this, GrammarDetailActivity.class);
                intent.putExtra("tense", tense);
                startActivity(intent);
            }
        };

        btnPresentSimple.setOnClickListener(tenseClickListener);
        btnPastSimple.setOnClickListener(tenseClickListener);
        btnFutureSimple.setOnClickListener(tenseClickListener);
        btnPresentContinuous.setOnClickListener(tenseClickListener);
    }
} 