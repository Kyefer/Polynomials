package kyefer.polynomials.app;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

public class AnswersActivity extends Activity {

	TextView polynomial;
	int degree;
	ArrayList<Integer> coef = new ArrayList<Integer>();
	ArrayList<Double> roots = new ArrayList<Double>();
	ArrayList<Integer> firstCoefFactors = new ArrayList<Integer>();
	ArrayList<Integer> lastCoefFactors = new ArrayList<Integer>();
	TableLayout rootTableScrollView;
	private SharedPreferences savedData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answers);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}*/

		polynomial = (TextView) findViewById(R.id.equationTextView);
		rootTableScrollView = (TableLayout) findViewById(R.id.rootTableScrollView);
		getPrefs();
		setEquation();
		addRoots();
	}

	private void getPrefs() {
		savedData = getSharedPreferences(MainActivity.SAVED_DATA, MODE_PRIVATE);
		degree = savedData.getInt("DEGREE", 0);
		for (int i = 0; i <= degree; i++) {
			coef.add(
					i,
					savedData.getInt(
							MainActivity.COEFSTRINGS.substring(i, i + 1), 0));

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

		int degreeOffPlacement = degree;
		int noStartZero = 0;

		while ((int) coef.get(degreeOffPlacement) == 0) {
			degreeOffPlacement--;
			if (!roots.contains(0.0)) {
				roots.add(0.0);
			}
		}

		while ((int) coef.get(noStartZero) == 0) {
			noStartZero++;
		}

		for (double i = 1; i <= Math.abs(coef.get(noStartZero)); i++) {
			if (coef.get(noStartZero) % i == 0) {
				firstCoefFactors.add((int) i);
			}
		}

		for (double i = 1; i <= Math.abs(coef.get(degreeOffPlacement)); i++) {

			if (coef.get(degreeOffPlacement) % i == 0) {
				lastCoefFactors.add((int) i);
			}
		}

		for (int i = 0; i < firstCoefFactors.size(); i++) {
			for (int j = 0; j < lastCoefFactors.size(); j++) {

				float fx = 0;
				double root = (double) lastCoefFactors.get(j)
						/ (double) firstCoefFactors.get(i);
				for (int k = degree, l = 0; k >= 0; k--, l++) {
					fx += coef.get(l) * Math.pow(root, k);

				}
				fx = Math.round(fx);
				if ((float)fx == 0 && !roots.contains(root)) {
					roots.add(root);

				}
				fx = 0;

				for (int k = degree, l = 0; k >= 0; k--, l++) {
					fx += (int) coef.get(l) * Math.pow(-root, k);
				}
				fx = Math.round(fx);
				if ((float)fx == 0 && !roots.contains(-root)) {
					roots.add(-root);

				}

			}
		}
	}

	private void addRoots() {
		findRoots();
		for (int i = 0; i < roots.size(); i++) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View newRootRow = inflater.inflate(R.layout.root_row, null);

			TextView rootLabelTextView = (TextView) newRootRow
					.findViewById(R.id.rootLabelTextView);
			TextView rootAnswerTextView = (TextView) newRootRow
					.findViewById(R.id.rootAnswerTextView);

			rootLabelTextView.setText(Html.fromHtml("X" + "<sub><small>"
					+ (i + 1) + "</small></sub>" + ":"));
			rootAnswerTextView.setText(roots.get(i).toString());

			rootTableScrollView.addView(newRootRow);

		}
		if (roots.size() == 0) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View newRootRow = inflater.inflate(R.layout.root_row, null);

			TextView rootLabelTextView = (TextView) newRootRow
					.findViewById(R.id.rootLabelTextView);

			rootLabelTextView.setText("No Rational Roots");

			rootTableScrollView.addView(newRootRow);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}