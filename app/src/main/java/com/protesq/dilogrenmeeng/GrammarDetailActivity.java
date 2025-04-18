package com.protesq.dilogrenmeeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class GrammarDetailActivity extends AppCompatActivity {
    private TextView tvTenseTitle, tvTenseContent;
    private Button btnStartTest;
    private DatabaseHelper dbHelper;
    private String currentTense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_detail);

        dbHelper = new DatabaseHelper(this);
        tvTenseTitle = findViewById(R.id.tvTenseTitle);
        tvTenseContent = findViewById(R.id.tvTenseContent);
        btnStartTest = findViewById(R.id.btnStartTest);

        currentTense = getIntent().getStringExtra("tense");
        loadTenseContent();

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrammarDetailActivity.this, GrammarTestActivity.class);
                intent.putExtra("tense", currentTense);
                startActivity(intent);
            }
        });
    }

    private void loadTenseContent() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_GRAMMAR,
                new String[]{DatabaseHelper.COLUMN_CONTENT},
                DatabaseHelper.COLUMN_TITLE + "=?",
                new String[]{currentTense},
                null, null, null);

        if (cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
            tvTenseTitle.setText(currentTense);
            tvTenseContent.setText(content);
        }
        cursor.close();
    }
} 