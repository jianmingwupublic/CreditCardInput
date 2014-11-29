package com.jameswu.creditcardinput;

import android.util.Log;

/**
 * class CardUtil 
 * 	The utility class to validate a credit card number.
 * @author jianmingwu
 *
 */
public class CardUtil {
	public final static int PREFIX_LENGTH = 6;
	public final static int MIN_CARD_LENGTH = 15;
	public final static int CARD_LENGTH_15 = 15;
	public final static int CARD_LENGTH_16 = 16;
	public final static int PIN_LENGTH_AMEX = 4;
	public final static int PIN_LENGTH_DEFAULT = 3;
	public final static String TAG = "CardInput";

	public enum CARDTYPE {
		Amex, Discover, JCB, MasterCard, Visa, UNKNOWN
	};

	/*
	 * luhnValidation use luhn algorithms to validate an input card number
	 * 
	 * @number input card numbers
	 * 
	 * @return true if pass the rule
	 */
	public static boolean luhnValidation(String number) {
        int s1 = 0, s2 = 0;
        String reverseNum = new StringBuffer(number).reverse().toString();
        for(int i = 0 ;i < reverseNum.length();i++){
            int digit = Character.digit(reverseNum.charAt(i), 10);
            if(i % 2 == 0){//odd
                s1 += digit;
            }else{//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit;
                if(digit >= 5){
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }
	/**
	 * checkCardNumPrefix, check the prefix digits are correct. Limit the input
	 * if incorrect set the card image view
	 * 
	 * @param s
	 *  text sequences
	 */
	public static CARDTYPE checkCardNumPrefix(CharSequence s) {
		if (s.length() < PREFIX_LENGTH)
			return CARDTYPE.UNKNOWN;

		CARDTYPE company = CARDTYPE.UNKNOWN;
		if (s.charAt(0) == '4') { // visa
			company = CARDTYPE.Visa;
		} else if (s.charAt(0) == '5' && s.charAt(1) >= '1'
				&& s.charAt(1) <= '5') { // master 51-55
			company = CARDTYPE.MasterCard;
		} else if (s.charAt(0) == '3'
				&& (s.charAt(1) == '4' || s.charAt(1) == '7')) { // Amex 34-37
			company = CARDTYPE.Amex;
		} else if (s.charAt(0) == '6') {// Discover
			// 6011, 622126-622925, 644-649, 65
			if ((s.charAt(1) == '0' && s.charAt(2) == '1' && s.charAt(3) == '1') // 6011
					|| (s.charAt(1) == '2' && s.charAt(2) == '2'
							&& s.charAt(3) >= '1' && s.charAt(4) >= '2'
							&& s.charAt(5) >= '6' && s.charAt(3) <= '9'
							&& s.charAt(4) <= '2' && s.charAt(5) <= '5') // 622126-622925
					|| (s.charAt(1) == '4' && s.charAt(2) >= '4' && s.charAt(2) <= '9') // 644-649
					|| (s.charAt(1) == '5')) {
				company = CARDTYPE.Discover;
			}
		} else if (s.charAt(0) == '3' && s.charAt(1) == '5'
				&& (s.charAt(2) >= 2 && s.charAt(2) <= 8)
				&& (s.charAt(2) >= 8 && s.charAt(2) <= 9)) { // JCB:3528-3589
			company = CARDTYPE.JCB;
		}
		return company;
	}
	/**
	 * getPinLimit
	 * @company card type
	 * @return card number length required
	 */
	public static int getLimit(CARDTYPE company) {
		if (company == CARDTYPE.Amex) {
			return CARD_LENGTH_15;
		} else if (company == CARDTYPE.UNKNOWN) {
			return PREFIX_LENGTH;
		}
		return CARD_LENGTH_16;
	}

	/**
	 * getPinLimit
	 * @company card type
	 * @return pin length required
	 */
	public static int getPinLimit(CARDTYPE company) {
		if (company == CARDTYPE.Amex) {
			return PIN_LENGTH_AMEX;
		}
		return PIN_LENGTH_DEFAULT;
	}

	public static void testCard() {
		String[] tests = { 
				"4556504603244485", 
				"4731106405763221",
				"4731106405763221", 
				"5339148774836496", 
				"6011996220269545",
				"6011707199415997", 
				"377675306247846", 
				"347395315685996",
				"370695998400211" };
		for (int i = 0; i < tests.length; i++) {
			String card = tests[i];
			CharSequence ch = card;
			boolean result = luhnValidation(card);
			CARDTYPE com = checkCardNumPrefix(ch);
			Log.v(TAG, "number: " + card + " com=" + com + " validation="
					+ result);
		}

	}
}
