package com.protesq.dilogrenmeeng;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrammarTestActivity extends AppCompatActivity {
    private TextView tvQuestionNumber, tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons;
    private Button btnNext;
    private DatabaseHelper dbHelper;
    private String currentTense;
    private List<GrammarQuestion> questions;
    private int currentQuestion = 0;
    private int correctAnswers = 0;
    private List<Integer> wrongAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_test); // Reusing the same layout

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

        currentTense = getIntent().getStringExtra("tense");
        setupGrammarQuestions();
        setupTest();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                if (currentQuestion < 9) {
                    currentQuestion++;
                    setupQuestion();
                } else {
                    showResults();
                }
            }
        });
    }

    private void setupGrammarQuestions() {
        questions = new ArrayList<>();
        
        // Add questions based on the selected tense
        if (currentTense.equals("Present Simple")) {
            questions.add(new GrammarQuestion(
                "I _____ to school every day.",
                "go",
                new String[]{"go", "goes", "going", "went"},
                0
            ));
            questions.add(new GrammarQuestion(
                "She _____ English very well.",
                "speaks",
                new String[]{"speak", "speaks", "speaking", "spoke"},
                1
            ));
            questions.add(new GrammarQuestion(
                "They _____ in London.",
                "live",
                new String[]{"live", "lives", "living", "lived"},
                0
            ));
            questions.add(new GrammarQuestion(
                "He _____ breakfast at 8 AM.",
                "has",
                new String[]{"have", "has", "having", "had"},
                1
            ));
            questions.add(new GrammarQuestion(
                "We _____ to the cinema on weekends.",
                "go",
                new String[]{"go", "goes", "going", "went"},
                0
            ));
            questions.add(new GrammarQuestion(
                "The sun _____ in the east.",
                "rises",
                new String[]{"rise", "rises", "rising", "rose"},
                1
            ));
            questions.add(new GrammarQuestion(
                "My mother _____ dinner every evening.",
                "cooks",
                new String[]{"cook", "cooks", "cooking", "cooked"},
                1
            ));
            questions.add(new GrammarQuestion(
                "I _____ coffee in the morning.",
                "drink",
                new String[]{"drink", "drinks", "drinking", "drank"},
                0
            ));
            questions.add(new GrammarQuestion(
                "The train _____ at 9 AM.",
                "leaves",
                new String[]{"leave", "leaves", "leaving", "left"},
                1
            ));
            questions.add(new GrammarQuestion(
                "They _____ football on Sundays.",
                "play",
                new String[]{"play", "plays", "playing", "played"},
                0
            ));
        } else if (currentTense.equals("Past Simple")) {
            questions.add(new GrammarQuestion(
                "I _____ to school yesterday.",
                "went",
                new String[]{"go", "goes", "going", "went"},
                3
            ));
            questions.add(new GrammarQuestion(
                "She _____ English very well.",
                "spoke",
                new String[]{"speak", "speaks", "speaking", "spoke"},
                3
            ));
            questions.add(new GrammarQuestion(
                "They _____ in London last year.",
                "lived",
                new String[]{"live", "lives", "living", "lived"},
                3
            ));
            questions.add(new GrammarQuestion(
                "He _____ breakfast at 8 AM.",
                "had",
                new String[]{"have", "has", "having", "had"},
                3
            ));
            questions.add(new GrammarQuestion(
                "We _____ to the cinema last weekend.",
                "went",
                new String[]{"go", "goes", "going", "went"},
                3
            ));
            questions.add(new GrammarQuestion(
                "The sun _____ at 6 AM yesterday.",
                "rose",
                new String[]{"rise", "rises", "rising", "rose"},
                3
            ));
            questions.add(new GrammarQuestion(
                "My mother _____ dinner last evening.",
                "cooked",
                new String[]{"cook", "cooks", "cooking", "cooked"},
                3
            ));
            questions.add(new GrammarQuestion(
                "I _____ coffee this morning.",
                "drank",
                new String[]{"drink", "drinks", "drinking", "drank"},
                3
            ));
            questions.add(new GrammarQuestion(
                "The train _____ at 9 AM.",
                "left",
                new String[]{"leave", "leaves", "leaving", "left"},
                3
            ));
            questions.add(new GrammarQuestion(
                "They _____ football last Sunday.",
                "played",
                new String[]{"play", "plays", "playing", "played"},
                3
            ));
        } else if (currentTense.equals("Future Simple")) {
            questions.add(new GrammarQuestion(
                "I _____ to school tomorrow.",
                "will go",
                new String[]{"will go", "will goes", "will going", "will went"},
                0
            ));
            questions.add(new GrammarQuestion(
                "She _____ English very well.",
                "will speak",
                new String[]{"will speak", "will speaks", "will speaking", "will spoke"},
                0
            ));
            questions.add(new GrammarQuestion(
                "They _____ in London next year.",
                "will live",
                new String[]{"will live", "will lives", "will living", "will lived"},
                0
            ));
            questions.add(new GrammarQuestion(
                "He _____ breakfast at 8 AM.",
                "will have",
                new String[]{"will have", "will has", "will having", "will had"},
                0
            ));
            questions.add(new GrammarQuestion(
                "We _____ to the cinema next weekend.",
                "will go",
                new String[]{"will go", "will goes", "will going", "will went"},
                0
            ));
            questions.add(new GrammarQuestion(
                "The sun _____ at 6 AM tomorrow.",
                "will rise",
                new String[]{"will rise", "will rises", "will rising", "will rose"},
                0
            ));
            questions.add(new GrammarQuestion(
                "My mother _____ dinner this evening.",
                "will cook",
                new String[]{"will cook", "will cooks", "will cooking", "will cooked"},
                0
            ));
            questions.add(new GrammarQuestion(
                "I _____ coffee tomorrow morning.",
                "will drink",
                new String[]{"will drink", "will drinks", "will drinking", "will drank"},
                0
            ));
            questions.add(new GrammarQuestion(
                "The train _____ at 9 AM.",
                "will leave",
                new String[]{"will leave", "will leaves", "will leaving", "will left"},
                0
            ));
            questions.add(new GrammarQuestion(
                "They _____ football next Sunday.",
                "will play",
                new String[]{"will play", "will plays", "will playing", "will played"},
                0
            ));
        } else if (currentTense.equals("Present Continuous")) {
            questions.add(new GrammarQuestion(
                "I _____ to school now.",
                "am going",
                new String[]{"am going", "am goes", "am go", "am went"},
                0
            ));
            questions.add(new GrammarQuestion(
                "She _____ English right now.",
                "is speaking",
                new String[]{"is speaking", "is speaks", "is speak", "is spoke"},
                0
            ));
            questions.add(new GrammarQuestion(
                "They _____ in London at the moment.",
                "are living",
                new String[]{"are living", "are lives", "are live", "are lived"},
                0
            ));
            questions.add(new GrammarQuestion(
                "He _____ breakfast now.",
                "is having",
                new String[]{"is having", "is has", "is have", "is had"},
                0
            ));
            questions.add(new GrammarQuestion(
                "We _____ to the cinema.",
                "are going",
                new String[]{"are going", "are goes", "are go", "are went"},
                0
            ));
            questions.add(new GrammarQuestion(
                "The sun _____ now.",
                "is rising",
                new String[]{"is rising", "is rises", "is rise", "is rose"},
                0
            ));
            questions.add(new GrammarQuestion(
                "My mother _____ dinner now.",
                "is cooking",
                new String[]{"is cooking", "is cooks", "is cook", "is cooked"},
                0
            ));
            questions.add(new GrammarQuestion(
                "I _____ coffee right now.",
                "am drinking",
                new String[]{"am drinking", "am drinks", "am drink", "am drank"},
                0
            ));
            questions.add(new GrammarQuestion(
                "The train _____ now.",
                "is leaving",
                new String[]{"is leaving", "is leaves", "is leave", "is left"},
                0
            ));
            questions.add(new GrammarQuestion(
                "They _____ football now.",
                "are playing",
                new String[]{"are playing", "are plays", "are play", "are played"},
                0
            ));
        }
        
        Collections.shuffle(questions);
    }

    private void setupTest() {
        currentQuestion = 0;
        correctAnswers = 0;
        wrongAnswers.clear();
        setupQuestion();
    }

    private void setupQuestion() {
        GrammarQuestion question = questions.get(currentQuestion);
        tvQuestionNumber.setText("Soru " + (currentQuestion + 1) + "/10");
        tvQuestion.setText(question.question);

        for (int i = 0; i < 4; i++) {
            radioButtons[i].setText(question.options[i]);
        }

        radioGroup.clearCheck();
        btnNext.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnNext.setEnabled(true);
            }
        });
    }

    private void checkAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedButton = findViewById(selectedId);
            int selectedIndex = -1;
            for (int i = 0; i < 4; i++) {
                if (selectedButton.getId() == radioButtons[i].getId()) {
                    selectedIndex = i;
                    break;
                }
            }
            
            if (selectedIndex == questions.get(currentQuestion).correctIndex) {
                correctAnswers++;
            } else {
                wrongAnswers.add(currentQuestion);
            }
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("totalQuestions", questions.size());
        intent.putIntegerArrayListExtra("wrongAnswers", new ArrayList<>(wrongAnswers));
        intent.putExtra("testType", "grammar");
        intent.putExtra("tense", currentTense);
        
        // Pass grammar questions and answers to the results activity
        ArrayList<String> questionTexts = new ArrayList<>();
        ArrayList<String> answerTexts = new ArrayList<>();
        
        for (GrammarQuestion question : questions) {
            questionTexts.add(question.question);
            answerTexts.add(question.correctAnswer);
        }
        
        intent.putStringArrayListExtra("grammarQuestions", questionTexts);
        intent.putStringArrayListExtra("grammarAnswers", answerTexts);
        
        startActivity(intent);
        finish();
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
} 