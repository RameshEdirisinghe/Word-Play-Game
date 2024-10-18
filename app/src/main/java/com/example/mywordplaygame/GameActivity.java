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
    // Game state variables
    private String randomWord;
    private int score = 100;
    private int attemptsLeft = 10;
    private long timeLeftInMillis = 60000;

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

        // Set initial values for score and attempts
        scoreTextView.setText("Score: " + score);
        attemptsTextView.setText("Attempts Left: " + attemptsLeft);
        newGameButton.setVisibility(View.GONE);

        // Initialize Retrofit and API Service
        WordApiService apiService = Apiclient.getRetrofitClient().create(WordApiService.class);

        // Call the API to get a random word
        Call<String> call = apiService.getRandomWord();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    randomWord = response.body();
                    Log.d("API_RESPONSE", "Random word: " + randomWord);
                    // You can show the length of the word to the player here
                    feedbackTextView.setText("Guess the word! It has " + randomWord.length() + " letters.");
                } else {
                    Log.e("API_ERROR", "Failed to fetch word");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_FAILURE", "API call failed: " + t.getMessage());
            }
        });

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

        letterEditText = findViewById(R.id.letterEditText);
        checkLetterButton = findViewById(R.id.checkLetterButton);
        letterOccurrenceTextView = findViewById(R.id.letterOccurrenceTextView);

        checkLetterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String letter = letterEditText.getText().toString().trim();

                // Ensure input is valid
                if (!letter.isEmpty() && letter.length() == 1) {
                    checkLetterOccurrence(letter);
                } else {
                    letterOccurrenceTextView.setText("Please enter a valid letter.");
                }
            }
        });

        requestClueButton = findViewById(R.id.requestClueButton);
        clueTextView = findViewById(R.id.clueTextView);

        requestClueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provideClue();
            }
        });

        startTimer();
    }
    private void checkGuess(String guess) {
        if (attemptsLeft > 0) {
            if (guess.equalsIgnoreCase(randomWord)) {
                feedbackTextView.setText("Congratulations! You've guessed the word correctly.");
                // Optionally, reset the game
                submitGuessButton.setEnabled(false);
                // Show a new game button
                showNewGameButton();
            } else {
                attemptsLeft--;
                score -= 10;
                feedbackTextView.setText("Wrong guess. Try again!");
                scoreTextView.setText("Score: " + score);
                attemptsTextView.setText("Attempts Left: " + attemptsLeft);

                if (attemptsLeft == 0) {
                    feedbackTextView.setText("Game over! The word was: " + randomWord);
                    // Disable input
                    submitGuessButton.setEnabled(false);
                    // Show a new game button
                    showNewGameButton();
                }
            }
        }
    }
    private void showNewGameButton() {
        Button newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setVisibility(View.VISIBLE);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    private void resetGame() {
        // Reset game state
        score = 100;
        attemptsLeft = 10;
        timeLeftInMillis = 60000;
        scoreTextView.setText("Score: " + score);
        attemptsTextView.setText("Attempts Left: " + attemptsLeft);
        feedbackTextView.setText("");
        guessEditText.setText("");
        letterEditText.setText("");
        clueTextView.setText("");
        letterOccurrenceTextView.setText("");
        newGameButton.setVisibility(View.GONE);
        // Restart timer and get a new word
        startTimer();
        getRandomWord(); // You might need to implement this method to get a new random word
    }

    private void getRandomWord() {
        // Your existing code to fetch a new random word
        WordApiService apiService = Apiclient.getRetrofitClient().create(WordApiService.class);
        Call<String> call = apiService.getRandomWord();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    randomWord = response.body();
                    Log.d("API_RESPONSE", "Random word: " + randomWord);
                    feedbackTextView.setText("Guess the word! It has " + randomWord.length() + " letters.");
                } else {
                    Log.e("API_ERROR", "Failed to fetch word");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_FAILURE", "API call failed: " + t.getMessage());
            }
        });
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
                // Handle timer finish (e.g., game over)
                feedbackTextView.setText("Time's up! The word was: " + randomWord);
                submitGuessButton.setEnabled(false);
            }
        }.start();
    }

    private void updateTimer() {
        int seconds = (int) (timeLeftInMillis / 1000);
        timerTextView.setText("Time: " + String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    private void provideClue() {
        if (attemptsLeft <= 5 && score >= 5) {
            // Call the API to get a rhyming word or similar word (e.g., using Rhyme API)
            WordApiService apiService = Apiclient.getRetrofitClient().create(WordApiService.class);
            Call<List<String>> call = apiService.getRhymeWord(randomWord);// Example API call for rhymes

            call.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (response.isSuccessful()) {
                        List<String> rhymes = response.body();
                        if (rhymes != null && !rhymes.isEmpty()) {
                            clueTextView.setVisibility(View.VISIBLE);
                            clueTextView.setText("Clue: A word that rhymes with the secret word is '" + rhymes.get(0) + "'");
                            score -= 5;
                            scoreTextView.setText("Score: " + score);
                        } else {
                            clueTextView.setText("No rhyming word found.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    clueTextView.setText("Failed to get a clue.");
                }
            });
        } else {
            clueTextView.setText("Clue unavailable or insufficient score.");
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
            score -= 5; // Deduct 5 points for using the letter occurrence feature
            scoreTextView.setText("Score: " + score);

            letterOccurrenceTextView.setText("Letter '" + letter + "' occurs " + count + " times.");
        } else {
            letterOccurrenceTextView.setText("Not enough score to use this feature.");
        }
    }

   

    // Method to provide a hint (can deduct score points)
    private void provideHint() {
        if (score >= 5 && attemptsLeft > 5) {
            // Deduct score and give a hint (e.g., tell the first letter or count of a specific letter)
            score -= 5;
            scoreTextView.setText("Score: " + score);
            feedbackTextView.setText("Hint: The word starts with " + randomWord.charAt(0));
        } else {
            feedbackTextView.setText("No hints available (or insufficient score).");
        }
    }
}


