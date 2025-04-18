package com.protesq.dilogrenmeeng;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.protesq.dilogrenmeeng.R;

import java.util.ArrayList;
import java.util.List;

public class VocabularyActivity extends AppCompatActivity {
    private static final String TAG = "VocabularyActivity";
    private RecyclerView recyclerView;
    private WordAdapter adapter;
    private DatabaseHelper dbHelper;
    private Button btnStartTest;
    private List<WordPair> wordPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_vocabulary);

            dbHelper = new DatabaseHelper(this);
            recyclerView = findViewById(R.id.recyclerViewWords);
            btnStartTest = findViewById(R.id.btnStartTest);

            // RecyclerView'ı tam olarak yapılandırma
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true); // Performans için
            
            adapter = new WordAdapter();
            recyclerView.setAdapter(adapter);

            loadWords();

            btnStartTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (wordPairs != null && !wordPairs.isEmpty()) {
                            Intent intent = new Intent(VocabularyActivity.this, VocabularyTestActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VocabularyActivity.this, 
                                "Kelimeler yüklenemedi. Lütfen tekrar deneyin.", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting test: " + e.getMessage());
                        Toast.makeText(VocabularyActivity.this,
                            "Test başlatılırken bir hata oluştu. Lütfen tekrar deneyin.",
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this,
                "Uygulama başlatılırken bir hata oluştu. Lütfen tekrar deneyin.",
                Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWords() {
        try {
            wordPairs = new ArrayList<>();
            Cursor cursor = dbHelper.getAllWords();
            
            if (cursor == null) {
                Log.e(TAG, "Cursor is null in loadWords");
                Toast.makeText(this, "Veritabanı hatası", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String englishWord = "";
            String turkishWord = "";
            
            int count = 0;
            while (cursor.moveToNext()) {
                count++;
                String word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
                String language = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LANGUAGE));
                
                Log.d(TAG, "Loaded word: " + word + ", language: " + language);
                
                if (language.equals("English")) {
                    englishWord = word;
                } else if (language.equals("Turkish")) {
                    turkishWord = word;
                    if (!englishWord.isEmpty()) {
                        wordPairs.add(new WordPair(englishWord, turkishWord));
                        Log.d(TAG, "Added word pair: " + englishWord + " - " + turkishWord);
                        englishWord = "";
                    }
                }
            }
            
            Log.d(TAG, "Total cursor rows: " + count);
            Log.d(TAG, "Loaded " + wordPairs.size() + " word pairs");
            
            cursor.close();
            
            if (wordPairs.isEmpty()) {
                Log.w(TAG, "No word pairs loaded!");
                Toast.makeText(this, "Kelime bulunamadı. Uygulama düzgün çalışmayabilir.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, wordPairs.size() + " kelime yüklendi", Toast.LENGTH_SHORT).show();
                
                // Adapter'a kelimeleri ata ve UI'ı güncelle
                adapter.setWords(wordPairs);
                
                // Adapter güncellendi mi kontrol et
                Log.d(TAG, "Adapter item count: " + adapter.getItemCount());
                
                // RecyclerView'ı yenile
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(0);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading words: " + e.getMessage(), e);
            Toast.makeText(this, "Kelimeler yüklenirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class WordPair {
        String english;
        String turkish;

        WordPair(String english, String turkish) {
            this.english = english;
            this.turkish = turkish;
        }
    }

    private class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
        private List<WordPair> words = new ArrayList<>();

        void setWords(List<WordPair> words) {
            this.words = words;
            notifyDataSetChanged();
            Log.d(TAG, "setWords called with " + words.size() + " items");
        }

        @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder called");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WordViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder called for position " + position);
            
            if (position >= 0 && position < words.size()) {
                WordPair word = words.get(position);
                holder.tvEnglishWord.setText(word.english);
                holder.tvTurkishWord.setText(word.turkish);
                
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            String textToCopy = word.english + " - " + word.turkish;
                            android.content.ClipboardManager clipboard = 
                                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            android.content.ClipData clip = 
                                android.content.ClipData.newPlainText("word pair", textToCopy);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(VocabularyActivity.this, 
                                "Kelime kopyalandı: " + textToCopy, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error copying word: " + e.getMessage());
                        }
                        return true;
                    }
                });
            } else {
                Log.e(TAG, "Invalid position: " + position + ", words.size: " + words.size());
            }
        }

        @Override
        public int getItemCount() {
            return words.size();
        }

        class WordViewHolder extends RecyclerView.ViewHolder {
            TextView tvEnglishWord;
            TextView tvTurkishWord;

            WordViewHolder(View itemView) {
                super(itemView);
                tvEnglishWord = itemView.findViewById(R.id.tvEnglishWord);
                tvTurkishWord = itemView.findViewById(R.id.tvTurkishWord);
            }
        }
    }
} 