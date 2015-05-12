package kyefer.polynomials.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class AnswersActivity extends Activity {

    private TextView polynomial;
    private int degree;
    private ArrayList<Integer> coef = new ArrayList<Integer>();
    private ArrayList<Double> roots = new ArrayList<Double>();
    private ArrayList<Integer> firstCoefFactors = new ArrayList<Integer>();
    private ArrayList<Integer> lastCoefFactors = new ArrayList<Integer>();
    private TableLayout rootTableScrollView;
    private SharedPreferences savedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        polynomial = (TextView) findViewById(R.id.equationTextView);
        rootTableScrollView = (TableLayout) findViewById(R.id.rootTableScrollView);
        getCoefs();
        setEquation();
        findRoots();
        populateRootListView();
    }

    private void getCoefs() {
        savedData = getSharedPreferences(MainActivity.SAVED_DATA, MODE_PRIVATE);
        degree = savedData.getInt("DEGREE", 0);
        for (int i = 0; i <= degree; i++) {
            coef.add(i, savedData.getInt(MainActivity.COEFSTRINGS.substring(i, i + 1), 0));
        }
    }

    private void setEquation() {
        String equation = "";
        for (int i = degree; i >= 0; i--) {
            equation += (coef.get(degree - i) == 0) ? "" : (coef
                    .get(degree - i) == 1 && i != 0) ? "+" : (coef.get(degree
                    - i) == Math.abs(coef.get(degree - i))) ? "+"
                    + coef.get(degree - i) : "-"
                    + Math.abs(coef.get(degree - i));
            if (coef.get(degree - i) != 0) {
                equation += (i == 0) ? "" : (i == 1) ? "x" : "x<sup><small>"
                        + i + "</small></sup>";

            }
        }

        if (equation.charAt(0) != '-') {
            equation = equation.substring(1);
        }
        polynomial.setText(Html.fromHtml(equation));

    }

    private void findRoots() {

        int firstCoef = coef.get(0);
        int lastCoef = coef.get(coef.size() - 1);

        while (firstCoef == 0) {
            coef.remove(0);
            firstCoef = coef.get(0);
        }

        while (lastCoef == 0) {
            coef.remove(coef.size() - 1);
            lastCoef = coef.get(coef.size() - 1);
            if (!roots.contains(0.0))
                roots.add(0.0);
        }

        for (double i = 1; i <= Math.abs(firstCoef); i++) {
            if (firstCoef % i == 0) {
                firstCoefFactors.add((int) i);
            }
        }

        for (double i = 1; i <= Math.abs(lastCoef); i++) {
            if (lastCoef % i == 0) {
                lastCoefFactors.add((int) i);
            }
        }

        for (int i = 0; i < firstCoefFactors.size(); i++) {
            for (int j = 0; j < lastCoefFactors.size(); j++) {

                float totalPositive = 0;
                float totalNegative = 0;
                double root = (double) lastCoefFactors.get(j)
                        / (double) firstCoefFactors.get(i);
                for (int k = coef.size() - 1, l = 0; k >= 0; k--, l++) {
                    totalPositive += coef.get(l) * Math.pow(root, k);
                    totalNegative += coef.get(l) * Math.pow(-root, k);

                }
                if (Math.abs(totalPositive) < 1e-8 && !roots.contains(root)) {
                    roots.add(root);
                }

                if (Math.abs(totalNegative) < 1e-8 && !roots.contains(-root)) {
                    roots.add(-root);
                }

            }
        }
    }

    private void populateRootListView() {
        if (roots.size() == 0) {
            TextView rootsTitleTextView = (TextView)  findViewById(R.id.rootsTitleTextView);
            rootsTitleTextView.setText(getString(R.string.no_rational_roots));
            return;
        }
        for (int i = 0; i < roots.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View newRootRow = inflater.inflate(R.layout.root_row, null);

            TextView rootLabelTextView = (TextView) newRootRow.findViewById(R.id.rootLabelTextView);
            TextView rootAnswerTextView = (TextView) newRootRow.findViewById(R.id.rootAnswerTextView);

            rootLabelTextView.setText(Html.fromHtml("X" + "<sub><small>"
                    + (i + 1) + "</small></sub>" + ": "));
            rootAnswerTextView.setText(roots.get(i).toString());

            rootTableScrollView.addView(newRootRow);

        }
    }

}