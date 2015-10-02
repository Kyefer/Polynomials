package kyefer.polynomials.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String SAVED_DATA = "kyefer.polynomials.app.SAVEDDATA";
    public static final String COEFSTRINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private SharedPreferences savedData;

    private int degree;

    private EditText degreeEditText;
    private TableLayout coefTableScrollView;
    private Button degreeButton;
    private Button findRootsButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        savedData = getSharedPreferences(SAVED_DATA, MODE_PRIVATE);

        degreeEditText = (EditText) findViewById(R.id.degreeEditText);
        coefTableScrollView = (TableLayout) findViewById(R.id.coefTableScrollView);
        degreeButton = (Button) findViewById(R.id.degreeButton);
        findRootsButton = (Button) findViewById(R.id.findRootsButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        degree = savedData.getInt("DEGREE", 1);

        degreeButton.setOnClickListener(degreeButtonListener);
        findRootsButton.setOnClickListener(findRootsButtonListener);
        clearButton.setOnClickListener(clearButtonListener);
        updateSavedCoefList();
    }

    private void updateSavedCoefList() {
        coefTableScrollView.removeAllViews();
        degreeEditText.setText("" + degree);
        for (int i = 0; i <= degree; i++) {
            int keyValue = savedData.getInt(COEFSTRINGS.charAt(i) + "", 0);
            insertCoefInScrollView(i, keyValue);
        }
    }

    private void saveCoef(int newCoefWithNum, int pos) {
        SharedPreferences.Editor preferenceEditor = savedData.edit();
        preferenceEditor.putInt(COEFSTRINGS.charAt(pos) + "", newCoefWithNum);
        preferenceEditor.apply();
    }

    private void insertCoefInScrollView(int position, final int coefWithNum) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newCoefRow = inflater.inflate(R.layout.coef_row, null);

        TextView newCoefTextView = (TextView) newCoefRow.findViewById(R.id.coefTextView);

        EditText newCoefEditText = (EditText) newCoefRow.findViewById(R.id.coefEditText);

        newCoefTextView.setText(COEFSTRINGS.charAt(position) + "");
        newCoefEditText.setText(coefWithNum + "");
        newCoefEditText.setSaveEnabled(false);
        newCoefEditText.addTextChangedListener(new CustomTextWatcher(newCoefEditText, position));

        coefTableScrollView.addView(newCoefRow);
    }

    public OnClickListener degreeButtonListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            if (degreeEditText.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_box_title);
                builder.setPositiveButton(R.string.ok_button, null);
                builder.setMessage(R.string.no_degree_text);
                builder.create().show();
            } else if (Integer.parseInt(degreeEditText.getText().toString()) > 25 || Integer.parseInt(degreeEditText.getText().toString()) < 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_box_title);
                builder.setPositiveButton(R.string.ok_button, null);
                builder.setMessage(R.string.wrong_degree);
                builder.create().show();
                degreeEditText.setText(savedData.getInt("DEGREE", 1) + "");
            } else {
                degree = Integer.parseInt(degreeEditText.getText().toString());

                SharedPreferences.Editor preferenceEditor = savedData.edit();
                preferenceEditor.putInt("DEGREE", degree);
                preferenceEditor.apply();

                updateSavedCoefList();
            }
        }
    };

    public OnClickListener findRootsButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            boolean allZeros = true;
            for (int i = 0; i <= degree; i++) {
                if (savedData.getInt(COEFSTRINGS.charAt(i) + "", 0) != 0)
                    allZeros = false;
            }

            if (allZeros) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_box_title);
                builder.setPositiveButton(R.string.ok_button, null);
                builder.setMessage(R.string.no_coef);
                AlertDialog theAlertDialog = builder.create();
                theAlertDialog.show();
            } else {
                Intent intent = new Intent(MainActivity.this, AnswersActivity.class);
                startActivity(intent);
            }
        }
    };

    public OnClickListener clearButtonListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.clear_dbox_title);
            builder.setPositiveButton(R.string.yes_button, deleteAllData);
            builder.setNegativeButton(R.string.no_button, null);
            builder.setMessage(R.string.clear_dbox_text);
            AlertDialog theAlertDialog = builder.create();
            theAlertDialog.show();
        }

    };

    public DialogInterface.OnClickListener deleteAllData = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            coefTableScrollView.removeAllViews();
            SharedPreferences.Editor preferenceEditor = savedData.edit();
            preferenceEditor.clear();
            preferenceEditor.apply();
            degreeEditText.setText("");
        }
    };

    private class CustomTextWatcher implements TextWatcher {
        private EditText mEditText;
        private int pos;

        public CustomTextWatcher(EditText e, int pos) {
            mEditText = e;
            this.pos = pos;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String numString = mEditText.getText().toString();

            if (numString.matches("[-+]?[0-9]+")) {
                saveCoef(Integer.parseInt(numString), pos);
            } else {
                saveCoef(0, pos);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}