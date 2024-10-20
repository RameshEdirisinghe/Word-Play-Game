package com.example.mywordplaygame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {

    // UI Elements
    private TextView welcomeUserTextView;
    private EditText guessEditText;
    private Button submitGuessButton;
    private TextView feedbackTextView;
    private TextView scoreTextView;
    private TextView attemptsTextView;
    private Button hintButton;
    private TextView timerTextView;
    private EditText letterEditText;
    private Button checkLetterButton;
    private TextView letterOccurrenceTextView;
    private Button requestClueButton;
    private TextView clueTextView;
    private CountDownTimer countDownTimer;
    private Button newGameButton;
    private Button tryAgainButton;

    // Game state variables
    private String randomWord;
    private int score = 100;
    private int attemptsLeft = 10;
    private long timeLeftInMillis = 60000; // 1 minute
    private String clue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize UI Elements
        welcomeUserTextView = findViewById(R.id.welcomeUserTextView);
        guessEditText = findViewById(R.id.guessEditText);
        submitGuessButton = findViewById(R.id.submitGuessButton);
        feedbackTextView = findViewById(R.id.feedbackTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        attemptsTextView = findViewById(R.id.attemptsTextView);
        hintButton = findViewById(R.id.hintButton);
        timerTextView = findViewById(R.id.timerTextView);
        newGameButton = findViewById(R.id.newGameButton);
        tryAgainButton = findViewById(R.id.tryAgainButton);
        letterEditText = findViewById(R.id.letterEditText);
        checkLetterButton = findViewById(R.id.checkLetterButton);
        letterOccurrenceTextView = findViewById(R.id.letterOccurrenceTextView);
        requestClueButton = findViewById(R.id.requestClueButton);
        clueTextView = findViewById(R.id.clueTextView);

        // Set initial values for score and attempts
        scoreTextView.setText("Score: " + score);
        attemptsTextView.setText("Attempts Left: " + attemptsLeft);

        // Hide the new game and try again buttons initially
        newGameButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);

        // Fetch a random word using the API
        getRandomWord();

        // Handle the guess submission
        submitGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userGuess = guessEditText.getText().toString().trim();
                if (!userGuess.isEmpty()) {
                    checkGuess(userGuess);
                }
            }
        });

        // Hint button logic
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provideHint();
            }
        });

        // Handle letter occurrence check
        checkLetterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String letter = letterEditText.getText().toString().trim();
                if (!letter.isEmpty() && letter.length() == 1) {
                    checkLetterOccurrence(letter);
                } else {
                    letterOccurrenceTextView.setText("Please enter a valid letter.");
                }
            }
        });

        // Handle request clue button
        requestClueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provideClue();
            }
        });

        // Handle Try Again button
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        // Start the timer for the game
        startTimer();
    }

    private void getRandomWord() {
        WordApiService apiService = Apiclient.getRetrofitClient().create(WordApiService.class);
        Call<List<String>> call = apiService.getRandomWord();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    randomWord = response.body().get(0);
                    clue = "This word has " + randomWord.length() + " letters.";
                    Log.d("API_RESPONSE", "Random word: " + randomWord);
                    feedbackTextView.setText("Guess the word! It has " + randomWord.length() + " letters.");
                } else {
                    Log.e("API_ERROR", "Failed to fetch word");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("API_FAILURE", "API call failed: " + t.getMessage());
            }
        });
    }

    private void checkGuess(String guess) {
        if (attemptsLeft > 0) {
            if (guess.equalsIgnoreCase(randomWord)) {
                feedbackTextView.setText("Congratulations! You've guessed the word correctly.");
                submitGuessButton.setEnabled(false);
                showTryAgainButton();
            } else {
                attemptsLeft--;
                score -= 10;
                feedbackTextView.setText("Wrong guess. Try again!");
                scoreTextView.setText("Score: " + score);
                attemptsTextView.setText("Attempts Left: " + attemptsLeft);

                if (attemptsLeft == 0 || score <= 0) {
                    feedbackTextView.setText("Game over! The word was: " + randomWord);
                    submitGuessButton.setEnabled(false);
                    showTryAgainButton();
                }
            }
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                feedbackTextView.setText("Time's up! The word was: " + randomWord);
                submitGuessButton.setEnabled(false);
                showTryAgainButton();
            }
        }.start();
    }

    private void updateTimer() {
        int seconds = (int) (timeLeftInMillis / 1000);
        timerTextView.setText("Time: " + String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    private void provideHint() {
        if (score >= 5 && attemptsLeft > 5) {
            score -= 5;
            scoreTextView.setText("Score: " + score);
            feedbackTextView.setText("Hint: The word starts with " + randomWord.charAt(0));
        } else {
            feedbackTextView.setText("No hints available (or insufficient score).");
        }
    }

    private void checkLetterOccurrence(String letter) {
        if (score >= 5) {
            int count = 0;
            for (char c : randomWord.toCharArray()) {
                if (String.valueOf(c).equalsIgnoreCase(letter)) {
                    count++;
                }
            }
            score -= 5;
            scoreTextView.setText("Score: " + score);
            letterOccurrenceTextView.setText("Letter '" + letter + "' occurs " + count + " times.");
        } else {
            letterOccurrenceTextView.setText("Not enough score for this action.");
        }
    }

    private void provideClue() {
        if (randomWord != null) {
            clueTextView.setText("Clue: " + clue);
        } else {
            clueTextView.setText("No clue available.");
        }
    }

    private void resetGame() {
        // Reset game variables
        score = 100;
        attemptsLeft = 10;
        timeLeftInMillis = 60000; // Reset to 1 minute

        // Update UI elements
        scoreTextView.setText("Score: " + score);
        attemptsTextView.setText("Attempts Left: " + attemptsLeft);
        feedbackTextView.setText("");
        letterOccurrenceTextView.setText("");
        clueTextView.setText("");

        // Re-enable buttons
        submitGuessButton.setEnabled(true);
        guessEditText.setText("");
        letterEditText.setText("");

        // Restart timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startTimer();

        // Fetch a new random word
        getRandomWord();

        // Hide Try Again button
        tryAgainButton.setVisibility(View.GONE);
        newGameButton.setVisibility(View.GONE);
    }

    private void showTryAgainButton() {
        tryAgainButton.setVisibility(View.VISIBLE);

    }
}







