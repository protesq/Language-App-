package com.protesq.dilogrenmeeng;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.protesq.dilogrenmeeng.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VocabularyTestActivity extends AppCompatActivity {
    private static final String TAG = "VocabularyTestActivity";
    private TextView tvQuestionNumber, tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons;
    private Button btnNext;
    private DatabaseHelper dbHelper;
    private List<WordPair> wordPairs;
    private List<WordPair> testWords;
    private int currentQuestion = 0;
    private int correctAnswers = 0;
    private List<Integer> wrongAnswers = new ArrayList<>();
    private boolean[] isEnglishToTurkish; // true if question is English to Turkish, false if Turkish to English
    private List<WordPair> allTestWords = new ArrayList<>(); // Store all test words for results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_vocabulary_test);

            dbHelper = new DatabaseHelper(this);
            tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
            tvQuestion = findViewById(R.id.tvQuestion);
            radioGroup = findViewById(R.id.radioGroup);
            btnNext = findViewById(R.id.btnNext);

            radioButtons = new RadioButton[4];
            radioButtons[0] = findViewById(R.id.radioButton1);
            radioButtons[1] = findViewById(R.id.radioButton2);
            radioButtons[2] = findViewById(R.id.radioButton3);
            radioButtons[3] = findViewById(R.id.radioButton4);

            loadWords();
            
            if (wordPairs == null || wordPairs.isEmpty()) {
                Toast.makeText(this, "Kelimeler yüklenemedi. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            setupTest();

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        checkAnswer();
                        if (currentQuestion < 9) {
                            currentQuestion++;
                            setupQuestion();
                        } else {
                            showResults();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error on next button click: " + e.getMessage());
                        Toast.makeText(VocabularyTestActivity.this, "Bir hata oluştu. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Uygulama başlatılırken bir hata oluştu. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadWords() {
        try {
            wordPairs = new ArrayList<>();
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
                        wordPairs.add(new WordPair(englishWord, turkishWord));
                        englishWord = "";
                    }
                }
            }
            cursor.close();
            
            // Log the number of words loaded
            Log.d(TAG, "Loaded " + wordPairs.size() + " word pairs from database");
            
            if (wordPairs.isEmpty()) {
                Toast.makeText(this, "Veritabanında kelime bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading words: " + e.getMessage());
            Toast.makeText(this, "Kelimeler yüklenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            wordPairs = new ArrayList<>();
        }
    }

    private void setupTest() {
        try {
            if (wordPairs.isEmpty()) {
                // If no words are loaded, show an error message and return to previous activity
                Toast.makeText(this, "Test için kelime bulunamadı.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            if (wordPairs.size() < 10) {
                // If fewer than 10 words, just use what we have
                Toast.makeText(this, "Uyarı: Sadece " + wordPairs.size() + " kelime bulundu.", Toast.LENGTH_SHORT).show();
            }
            
            Collections.shuffle(wordPairs);
            
            // Ensure we don't try to get more words than exist
            int testSize = Math.min(10, wordPairs.size());
            testWords = wordPairs.subList(0, testSize);
            allTestWords = new ArrayList<>(testWords); // Store all test words for results
            
            currentQuestion = 0;
            correctAnswers = 0;
            wrongAnswers.clear();
            
            // Determine which questions will be English to Turkish and which will be Turkish to English
            isEnglishToTurkish = new boolean[testSize];
            for (int i = 0; i < testSize; i++) {
                isEnglishToTurkish[i] = i < (testSize / 2); // First half questions are English to Turkish, second half are Turkish to English
            }
            
            setupQuestion();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up test: " + e.getMessage());
            Toast.makeText(this, "Test hazırlanırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupQuestion() {
        try {
            if (currentQuestion >= testWords.size()) {
                // Safety check to avoid index out of bounds
                showResults();
                return;
            }
            
            WordPair currentWord = testWords.get(currentQuestion);
            tvQuestionNumber.setText("Soru " + (currentQuestion + 1) + "/" + testWords.size());
            
            List<String> options = new ArrayList<>();
            String correctAnswer;
            
            if (isEnglishToTurkish[currentQuestion]) {
                // English to Turkish question
                tvQuestion.setText("İngilizce kelime: " + currentWord.english);
                correctAnswer = currentWord.turkish;
                options.add(currentWord.turkish);
            } else {
                // Turkish to English question
                tvQuestion.setText("Türkçe kelime: " + currentWord.turkish);
                correctAnswer = currentWord.english;
                options.add(currentWord.english);
            }

            // Add random wrong answers
            List<WordPair> remainingWords = new ArrayList<>(wordPairs);
            remainingWords.removeAll(testWords);
            Collections.shuffle(remainingWords);
            
            // If we don't have enough remaining words, use testWords again
            if (remainingWords.size() < 3) {
                remainingWords = new ArrayList<>(wordPairs);
                Collections.shuffle(remainingWords);
            }
            
            int addedOptions = 0;
            for (WordPair word : remainingWords) {
                if (addedOptions >= 3) break;
                
                String option;
                if (isEnglishToTurkish[currentQuestion]) {
                    option = word.turkish;
                } else {
                    option = word.english;
                }
                
                if (!options.contains(option)) {
                    options.add(option);
                    addedOptions++;
                }
            }

            // Ensure we have exactly 4 options
            while (options.size() < 4 && !remainingWords.isEmpty()) {
                WordPair word = remainingWords.get(0);
                remainingWords.remove(0);
                
                String option;
                if (isEnglishToTurkish[currentQuestion]) {
                    option = word.turkish;
                } else {
                    option = word.english;
                }
                
                if (!options.contains(option)) {
                    options.add(option);
                }
            }

            Collections.shuffle(options);
            for (int i = 0; i < Math.min(4, options.size()); i++) {
                radioButtons[i].setText(options.get(i));
            }

            radioGroup.clearCheck();
            btnNext.setEnabled(false);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    btnNext.setEnabled(true);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up question: " + e.getMessage());
            Toast.makeText(this, "Soru hazırlanırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void checkAnswer() {
        try {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedButton = findViewById(selectedId);
                String selectedAnswer = selectedButton.getText().toString();
                String correctAnswer;
                
                if (isEnglishToTurkish[currentQuestion]) {
                    correctAnswer = testWords.get(currentQuestion).turkish;
                } else {
                    correctAnswer = testWords.get(currentQuestion).english;
                }

                if (selectedAnswer.equals(correctAnswer)) {
                    correctAnswers++;
                } else {
                    wrongAnswers.add(currentQuestion);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking answer: " + e.getMessage());
            // Don't show toast here as it might disrupt the flow
        }
    }

    private void showResults() {
        try {
            Intent intent = new Intent(this, TestResultsActivity.class);
            intent.putExtra("correctAnswers", correctAnswers);
            intent.putExtra("totalQuestions", testWords.size());
            intent.putIntegerArrayListExtra("wrongAnswers", new ArrayList<>(wrongAnswers));
            intent.putExtra("testType", "vocabulary");
            
            // Pass all test words to the results activity
            ArrayList<String> englishWords = new ArrayList<>();
            ArrayList<String> turkishWords = new ArrayList<>();
            for (WordPair word : allTestWords) {
                englishWords.add(word.english);
                turkishWords.add(word.turkish);
            }
            intent.putStringArrayListExtra("englishWords", englishWords);
            intent.putStringArrayListExtra("turkishWords", turkishWords);
            
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error showing results: " + e.getMessage());
            Toast.makeText(this, "Sonuçlar gösterilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            
            // Try to go back to main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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
} 