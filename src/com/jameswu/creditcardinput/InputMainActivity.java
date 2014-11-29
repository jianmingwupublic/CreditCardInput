package com.jameswu.creditcardinput;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
/**
 * InputMainActivity 
 * 		Activity to input credit card number, expiration date, and pin.
 * 		The company logo is shown next to the card number.
 * @author jianmingwu
 *
 */
public class InputMainActivity extends Activity {
	private EditText editnums = null;
	private EditText editccvpin = null;
	private Spinner spinnerMM = null;
	private EditText edityear = null;
	private Button btsubmit = null;
	private ImageView imgview = null;
	private final boolean DEBUG = true;
	private final String TAG = CardUtil.TAG;

	// map to CardUtil.CARDTYPE
	String[] imageNames = { "amex", "discover", "jcp", "mastercard", "visa",
			"cvv" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG)
			Log.v(TAG, "Inside of onCreate");

		setContentView(R.layout.activity_input_main);
		editnums = (EditText) findViewById(R.id.editcardnum);
		spinnerMM = (Spinner) findViewById(R.id.mm_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.months_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerMM.setAdapter(adapter);

		edityear = (EditText) findViewById(R.id.edityear);
		editccvpin = (EditText) findViewById(R.id.editccv);
		imgview = (ImageView) findViewById(R.id.imageView1);
		btsubmit = (Button) findViewById(R.id.button1);
		setLengthFilter(CardUtil.getPinLimit(CardUtil.CARDTYPE.UNKNOWN), editccvpin);
		btsubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				validate();
			}
		});

		editnums.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (DEBUG)
					Log.v(TAG,
							"afterTextChanged: editnums=" + editnums.getText());
				if (editnums.length() == CardUtil.PREFIX_LENGTH
						|| editnums.length() == 0) {
					checkCardNumPrefix();
				}
			}
		});
	}

	/**
	 * checkCardNumPrefix, check the prefix digits are correct. Limit the input
	 * if incorrect set the card image view
	 * 
	 * @param s
	 *            , text sequences
	 */
	private void checkCardNumPrefix() {
		if (DEBUG)
			Log.v(TAG, "checkCardNumPrefix starts");
		CharSequence s = editnums.getText().toString();
		CardUtil.CARDTYPE company = CardUtil.checkCardNumPrefix(s);
		// process the result
		int cNums = Color.BLACK;
		if (company == CardUtil.CARDTYPE.UNKNOWN && s != null && s.length() > 0) { // wrong
																					// prefix
			cNums = Color.RED;
		}
		editnums.setTextColor(cNums);
		setLengthFilter(CardUtil.getLimit(company), editnums);
		setCardImage(company);
		// reset ccvpin
		editccvpin.setText("");
		setLengthFilter(CardUtil.getPinLimit(company), editccvpin);
	}

	// string names to save the state, key-value pairs
	private static final String CARDNUMS = "cardnums";
	private static final String MM = "MM";
	private static final String YYYY = "YYYY";
	private static final String CCV = "CCV";

	@Override
	protected void onSaveInstanceState(Bundle state) {
		Log.v(TAG, "Inside of onSaveInstanceState");
		state.putString(CARDNUMS, editnums.getText().toString());
		state.putString(MM, "" + spinnerMM.getSelectedItemPosition());
		state.putString(YYYY, edityear.getText().toString());
		state.putString(CCV, editccvpin.getText().toString());
		super.onSaveInstanceState(state);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.v(TAG, "Inside of onRestoreInstanceState");
		String tmp = savedInstanceState.getString(CARDNUMS);
		if (tmp != null) {
			editnums.setText(tmp);
		}
		tmp = savedInstanceState.getString(YYYY);
		if (tmp != null) {
			edityear.setText(tmp);
		}
		tmp = savedInstanceState.getString(MM);
		if (tmp != null) {
			spinnerMM.setSelection(Integer.valueOf(tmp));
		}
		tmp = savedInstanceState.getString(CCV);
		if (tmp != null) {
			editccvpin.setText(tmp);
		}
	}

	/**
	 * setCardImage, update the imageView of card type
	 * 
	 * @param company
	 *            , card type
	 */
	private void setCardImage(CardUtil.CARDTYPE company) {
		String imgName = imageNames[company.ordinal()];
		Resources res = getResources(); // need this to fetch the drawable
		int resourceId = res.getIdentifier(imgName, "drawable",
				getPackageName());
		Drawable draw = res.getDrawable(resourceId);
		imgview.setImageDrawable(draw);
		if (DEBUG)
			Log.v(TAG, "setCardImage " + company);
	}

	/**
	 * setLengthFilter
	 * 
	 * @param limit
	 *            , limit of the input
	 * @param holder
	 *            , the target editText box
	 */
	private void setLengthFilter(int limit, EditText holder) {
		Log.v(TAG, "setLengthFilter limit =" + limit + " holder=" + holder);
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(limit);
		holder.setFilters(filterArray);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.input_main, menu);
		return true;
	}

	private void showErrorMsg(String title, String msg) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// whatever...
					}
				}).create().show();
	}

	/*
	 * validate validate the input card information, prompt error messages for
	 * invalid information
	 * 
	 * @return true if passed
	 */
	private boolean validate() {
		final String strSuccess = getString(R.string.success);
		final String strFailed = getString(R.string.invalid_card);
		String title = "Done";
		String msg = strSuccess;
		// check input not empty
		if (editnums.getText() == null || editnums.getText().length() < 15
				|| edityear.getText() == null
				|| edityear.getText().length() == 0
				|| spinnerMM.toString().length() < 2
				|| editccvpin.getText() == null
				|| editccvpin.getText().length() < 0) {
			// not enough input
			msg = getString(R.string.empty);
		} else if (CardUtil.luhnValidation(editnums.getText().toString())) {
			msg = strSuccess;
		} else {
			title = "Error";
			msg = strFailed;
		}
		showErrorMsg(title, msg);
		return false;
	}

}
