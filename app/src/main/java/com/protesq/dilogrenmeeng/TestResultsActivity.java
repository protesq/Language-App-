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

public class TestResultsActivity extends AppCompatActivity {
    private static final String TAG = "TestResultsActivity";
    private TextView tvScore;
    private TextView tvResultTitle;
    private RecyclerView recyclerView;
    private WrongAnswersAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<WordPair> testWords;
    private ArrayList<Integer> wrongAnswers;
    private String testType;
    private String tense;
    private ArrayList<String> englishWords;
    private ArrayList<String> turkishWords;
    private ArrayList<String> grammarQuestions;
    private ArrayList<String> grammarAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_test_results);

            dbHelper = new DatabaseHelper(this);
            tvScore = findViewById(R.id.tvScore);
            tvResultTitle = findViewById(R.id.tvResultTitle);
            recyclerView = findViewById(R.id.recyclerViewWrongAnswers);
            Button btnBackToMain = findViewById(R.id.btnBackToMain);

            int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
            int totalQuestions = getIntent().getIntExtra("totalQuestions", 10);
            wrongAnswers = getIntent().getIntegerArrayListExtra("wrongAnswers");
            testType = getIntent().getStringExtra("testType");
            tense = getIntent().getStringExtra("tense");
            
            // Get vocabulary test words if available
            englishWords = getIntent().getStringArrayListExtra("englishWords");
            turkishWords = getIntent().getStringArrayListExtra("turkishWords");
            
            // Get grammar questions and answers if available
            grammarQuestions = getIntent().getStringArrayListExtra("grammarQuestions");
            grammarAnswers = getIntent().getStringArrayListExtra("grammarAnswers");

            tvScore.setText(String.format("Skor: %d/%d", correctAnswers, totalQuestions));

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WrongAnswersAdapter();
            recyclerView.setAdapter(adapter);

            if (testType != null && testType.equals("grammar")) {
                tvResultTitle.setText("Gramer Testi Sonuçları");
                showGrammarResults();
            } else {
                tvResultTitle.setText("Kelime Testi Sonuçları");
                loadTestWords();
                showVocabularyResults();
            }

            btnBackToMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TestResultsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Sonuçlar görüntülenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadTestWords() {
        try {
            testWords = new ArrayList<>();
            
            // If we have the words from the intent, use them
            if (englishWords != null && turkishWords != null && 
                englishWords.size() == turkishWords.size()) {
                for (int i = 0; i < englishWords.size(); i++) {
                    testWords.add(new WordPair(englishWords.get(i), turkishWords.get(i)));
                }
            } else {
                // Otherwise, load from database
                Cursor cursor = dbHelper.getAllWords();
                String englishWord = "";
                String turkishWord = "";
                
                while (cursor.moveToNext()) {
                    String word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
                    String language = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LANGUAGE));
                    
                    if (language.equals("English")) {
                        englishWord = word;
                    } else if (language.equals("Turkish")) {
                        turkishWord = word;
                        if (!englishWord.isEmpty()) {
                            testWords.add(new WordPair(englishWord, turkishWord));
                            englishWord = "";
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading test words: " + e.getMessage());
            Toast.makeText(this, "Kelimeler yüklenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showVocabularyResults() {
        try {
            List<WordPair> wrongWords = new ArrayList<>();
            List<WordPair> correctWords = new ArrayList<>();
            
            if (wrongAnswers != null && testWords != null) {
                // Add wrong answers
                for (Integer index : wrongAnswers) {
                    if (index < testWords.size()) {
                        wrongWords.add(testWords.get(index));
                    }
                }
                
                // Add correct answers
                for (int i = 0; i < testWords.size(); i++) {
                    if (!wrongAnswers.contains(i)) {
                        correctWords.add(testWords.get(i));
                    }
                }
            }
            
            adapter.setWords(wrongWords, correctWords);
        } catch (Exception e) {
            Log.e(TAG, "Error showing vocabulary results: " + e.getMessage());
            Toast.makeText(this, "Kelime sonuçları gösterilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGrammarResults() {
        try {
            List<GrammarQuestion> wrongQuestions = new ArrayList<>();
            List<GrammarQuestion> correctQuestions = new ArrayList<>();
            
            if (grammarQuestions != null && grammarAnswers != null && 
                wrongAnswers != null && grammarQuestions.size() == grammarAnswers.size()) {
                
                for (int i = 0; i < grammarQuestions.size(); i++) {
                    GrammarQuestion question = new GrammarQuestion(
                        grammarQuestions.get(i),
                        grammarAnswers.get(i),
                        null,
                        0
                    );
                    
                    if (wrongAnswers.contains(i)) {
                        wrongQuestions.add(question);
                    } else {
                        correctQuestions.add(question);
                    }
                }
            }
            
            // If no grammar questions were passed, show a message
            if (grammarQuestions == null || grammarQuestions.isEmpty()) {
                Toast.makeText(this, "Gramer soruları yüklenemedi.", Toast.LENGTH_SHORT).show();
            }
            
            adapter.setGrammarQuestions(wrongQuestions, correctQuestions);
        } catch (Exception e) {
            Log.e(TAG, "Error showing grammar results: " + e.getMessage());
            Toast.makeText(this, "Gramer sonuçları gösterilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
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

    private static class GrammarQuestion {
        String question;
        String correctAnswer;
        String[] options;
        int correctIndex;

        GrammarQuestion(String question, String correctAnswer, String[] options, int correctIndex) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private class WrongAnswersAdapter extends RecyclerView.Adapter<WrongAnswersAdapter.WrongAnswerViewHolder> {
        private List<WordPair> wrongWords = new ArrayList<>();
        private List<WordPair> correctWords = new ArrayList<>();
        private List<GrammarQuestion> wrongGrammarQuestions = new ArrayList<>();
        private List<GrammarQuestion> correctGrammarQuestions = new ArrayList<>();
        private boolean isGrammarTest = false;

        void setWords(List<WordPair> wrongWords, List<WordPair> correctWords) {
            this.wrongWords = wrongWords;
            this.correctWords = correctWords;
            this.wrongGrammarQuestions.clear();
            this.correctGrammarQuestions.clear();
            this.isGrammarTest = false;
            notifyDataSetChanged();
        }

        void setGrammarQuestions(List<GrammarQuestion> wrongQuestions, List<GrammarQuestion> correctQuestions) {
            this.wrongGrammarQuestions = wrongQuestions;
            this.correctGrammarQuestions = correctQuestions;
            this.wrongWords.clear();
            this.correctWords.clear();
            this.isGrammarTest = true;
            notifyDataSetChanged();
        }

        @Override
        public WrongAnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new WrongAnswerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WrongAnswerViewHolder holder, int position) {
            if (isGrammarTest) {
                if (position < wrongGrammarQuestions.size()) {
                    // Show wrong grammar questions first
                    GrammarQuestion question = wrongGrammarQuestions.get(position);
                    holder.textView.setText("❌ " + question.question + " - Doğru cevap: " + question.correctAnswer);
                } else {
                    // Then show correct grammar questions
                    int correctIndex = position - wrongGrammarQuestions.size();
                    if (correctIndex < correctGrammarQuestions.size()) {
                        GrammarQuestion question = correctGrammarQuestions.get(correctIndex);
                        holder.textView.setText("✅ " + question.question + " - Doğru cevap: " + question.correctAnswer);
                    }
                }
            } else {
                // Show wrong vocabulary words first, then correct vocabulary words
                if (position < wrongWords.size()) {
                    WordPair word = wrongWords.get(position);
                    holder.textView.setText("❌ " + word.english + " - " + word.turkish);
                } else {
                    int correctIndex = position - wrongWords.size();
                    if (correctIndex < correctWords.size()) {
                        WordPair word = correctWords.get(correctIndex);
                        holder.textView.setText("✅ " + word.english + " - " + word.turkish);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            if (isGrammarTest) {
                return wrongGrammarQuestions.size() + correctGrammarQuestions.size();
            } else {
                return wrongWords.size() + correctWords.size();
            }
        }

        class WrongAnswerViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            WrongAnswerViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
} 