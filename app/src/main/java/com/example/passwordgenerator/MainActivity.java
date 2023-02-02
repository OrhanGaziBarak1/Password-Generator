package com.example.passwordgenerator;


import androidx.appcompat.app.AppCompatActivity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import com.example.passwordgenerator.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


/*User is able to generate a strong password for your accounts and user can copy them to clipboard
* then use it in everywhere.*/
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding; //Declaring view binding.


    //Declaring global variables.
    private final String [] lowerLetters = {
            "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h",
            "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
            "u" , "v" , "w" , "x" , "y" , "z"
    };
    private final String [] upperLetters = {
            "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H",
            "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
            "U" , "V" , "W" , "X" , "Y" , "Z"
    };
    private final String [] specialCharacters = {
            "!" , "#" , "'" , "+" , "%" , "(" , ")" , "&" ,
            "=" , "?" , "*" , "-" , "$" , "{" , "}"
    };
    private final String [] numbers = {
            "0" , "1" , "2" , "3" , "4" , "5" , "6" ,
            "7" , "8" , "9"
    };

    private String[] wordsSplit = new String [200];

    private String password = "";
    private String passwordCopy = "";
    private final String messageSuccess = "Password Copied Successfully.";
    private final String messageErrorCopy = "Password Can't Copied.";

    //These boolean variables are for deciding checkboxes are checked or not.
    private Boolean upper = true;
    private Boolean lower = true;
    private Boolean special = true;
    private Boolean number = true;
    private Boolean word = true;

    //In text file I have some meaningful words.
    //This function read the text file and assign to them to words array.
    private void readTextFile() {
        String words;
        try {
            InputStream inputStream = getAssets().open("words.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            //noinspection ResultOfMethodCallIgnored
            inputStream.read(buffer);
            words = new String(buffer);
            wordsSplit = words.split(" ");

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This function generate a password for user.
    //User is able to determine password's length and the content.
    /*If user want a meaningful word in their password function assign to word first and then
    complete the password's length randomly.*/
    @SuppressWarnings("StringConcatenationInLoop")
    private void generate () {
        Random random = new Random();

        int character;
        int lengthPassword;
        int situation;

        String lengthPasswordStr;

        Editable editablePassword;

        editablePassword = binding.lengthInputBox.getText();

        lengthPasswordStr = editablePassword.toString();
        lengthPassword = Integer.parseInt(lengthPasswordStr);

        while ((lengthPassword <= 16) && (lengthPassword >= 8) && (password.length() < lengthPassword)) {

            if (word) {
                character = random.nextInt((wordsSplit.length - 1));

                if(wordsSplit[character].length() <= lengthPassword) {
                    password += wordsSplit[character];
                    word = false;
                }
            }

            if (password.length() < lengthPassword) {
                situation = random.nextInt(5);

                switch (situation) {
                    case 1:
                        if(lower) {
                            character = random.nextInt((lowerLetters.length - 1));
                            password += lowerLetters[character];
                        }
                        break;

                    case 2:
                        if(upper) {
                            character = random.nextInt((upperLetters.length - 1));
                            password += upperLetters[character];
                        }
                        break;

                    case 3:
                        if (special) {
                            character = random.nextInt((specialCharacters.length - 1));
                            password += specialCharacters[character];
                        }
                        break;

                    case 4:
                        if (number) {
                            character = random.nextInt((numbers.length - 1));
                            password += numbers[character];
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //An object for manage the clipboard on phone.
        ClipboardManager copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        readTextFile();

        //If user click the checkBoxes runs here and user change the password's content.
        binding.upperCheck.setOnClickListener(view -> upper = !upper);

        binding.lowerCheck.setOnClickListener(view -> lower = !lower);

        binding.specialCheck.setOnClickListener(view -> special = !special);

        binding.numberCheck.setOnClickListener(view -> number = !number);

        binding.meaningfulWordCheck.setOnClickListener(v -> word = !word);

        //If user click the generate password button application generating a password.
        binding.button.setOnClickListener(view -> {
            generate();

            binding.text.setText(password);
            passwordCopy = password;
            password = "";

            if(binding.meaningfulWordCheck.isChecked()) {
                word = true;
            }
        });

        //User is able to copy the generated password to own clipboard and can use everywhere.
        binding.copy.setOnClickListener(view -> {
            CharSequence passwordChar = new StringBuffer(passwordCopy);

            ClipData copyData = ClipData.newPlainText("copiedPassword" , passwordChar);

            copy.setPrimaryClip(copyData);

            if (copy.hasPrimaryClip()) {
                Toast.makeText(getApplicationContext(), messageSuccess, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), messageErrorCopy, Toast.LENGTH_SHORT).show();
            }
        });
    }
}