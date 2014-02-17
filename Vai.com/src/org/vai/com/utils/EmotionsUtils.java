package org.vai.com.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import org.vai.com.R;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class EmotionsUtils {
	public static String[] mEmoText = new String[] { ":poop:", "(n)", "(y)", "(finger)", ":))", ":)", ":((", ":(",
			";))", ";)", ":-\"", ":|", ":\">", ":*", ":-*", "=))", "B-)", ":d", ":D", ":-ss", ":-s", ":-S", ":-o",
			":-O", ":-?", ":-W", ":-w", ">:P", ">:p", ":P", ":p", "=P~", "=p~", ":-B", "8-}", ":x", ":X", "x(", "X(",
			":-<", ":-/", ":v", "<3", ":3", "3:)", "3:-)", ":'(", "B)", "B|", ":\\", ":/", ":o", ":O", ":0", "^_^",
			"-_-", "@@", "$$", "o.O", "O.o", "o.0", "0.o", ">:(" };

	public static HashMap<String, Integer> emoticons = new HashMap<String, Integer>();
	static {
		emoticons.put(mEmoText[0], R.drawable.poop);
		emoticons.put(mEmoText[1], R.drawable.thumbsdown);
		emoticons.put(mEmoText[2], R.drawable.thumbsup);
		emoticons.put(mEmoText[3], R.drawable.finger);
		emoticons.put(mEmoText[4], R.drawable.laugh);
		emoticons.put(mEmoText[5], R.drawable.smile);
		emoticons.put(mEmoText[6], R.drawable.bawling);
		emoticons.put(mEmoText[7], R.drawable.frown);
		emoticons.put(mEmoText[8], R.drawable.playful);
		emoticons.put(mEmoText[9], R.drawable.wink);
		emoticons.put(mEmoText[10], R.drawable.whistling);
		emoticons.put(mEmoText[11], R.drawable.bored);
		emoticons.put(mEmoText[12], R.drawable.shy);
		emoticons.put(mEmoText[13], R.drawable.kiss);
		emoticons.put(mEmoText[14], R.drawable.kiss);
		emoticons.put(mEmoText[15], R.drawable.roflmao);
		emoticons.put(mEmoText[16], R.drawable.cool);
		emoticons.put(mEmoText[17], R.drawable.biggrin);
		emoticons.put(mEmoText[18], R.drawable.biggrin);
		emoticons.put(mEmoText[19], R.drawable.nailbiting);
		emoticons.put(mEmoText[20], R.drawable.grumpy);
		emoticons.put(mEmoText[21], R.drawable.grumpy);
		emoticons.put(mEmoText[22], R.drawable.eek);
		emoticons.put(mEmoText[23], R.drawable.eek);
		emoticons.put(mEmoText[24], R.drawable.o_o);
		emoticons.put(mEmoText[25], R.drawable.meh);
		emoticons.put(mEmoText[26], R.drawable.meh);
		emoticons.put(mEmoText[27], R.drawable.yuck);
		emoticons.put(mEmoText[28], R.drawable.yuck);
		emoticons.put(mEmoText[29], R.drawable.tongue);
		emoticons.put(mEmoText[30], R.drawable.tongue);
		emoticons.put(mEmoText[31], R.drawable.hungry);
		emoticons.put(mEmoText[32], R.drawable.hungry);
		emoticons.put(mEmoText[33], R.drawable.geek);
		emoticons.put(mEmoText[34], R.drawable.confused);
		emoticons.put(mEmoText[35], R.drawable.inlove);
		emoticons.put(mEmoText[36], R.drawable.inlove);
		emoticons.put(mEmoText[37], R.drawable.banghead);
		emoticons.put(mEmoText[38], R.drawable.banghead);
		emoticons.put(mEmoText[39], R.drawable.frown);
		emoticons.put(mEmoText[40], R.drawable.unsure);
		emoticons.put(mEmoText[41], R.drawable.wtf);
		emoticons.put(mEmoText[42], R.drawable.heart);
		emoticons.put(mEmoText[43], R.drawable.x3);
		emoticons.put(mEmoText[44], R.drawable.devil);
		emoticons.put(mEmoText[45], R.drawable.devil);
		emoticons.put(mEmoText[46], R.drawable.cry);
		emoticons.put(mEmoText[47], R.drawable.cool);
		emoticons.put(mEmoText[48], R.drawable.cool);
		emoticons.put(mEmoText[49], R.drawable.unsure);
		emoticons.put(mEmoText[50], R.drawable.unsure);
		emoticons.put(mEmoText[51], R.drawable.eek);
		emoticons.put(mEmoText[52], R.drawable.eek);
		emoticons.put(mEmoText[53], R.drawable.eek);
		emoticons.put(mEmoText[54], R.drawable.kiki);
		emoticons.put(mEmoText[55], R.drawable.shifty);
		emoticons.put(mEmoText[56], R.drawable.wideyed);
		emoticons.put(mEmoText[57], R.drawable.greedy);
		emoticons.put(mEmoText[58], R.drawable.confused);
		emoticons.put(mEmoText[59], R.drawable.confused);
		emoticons.put(mEmoText[60], R.drawable.confused);
		emoticons.put(mEmoText[61], R.drawable.confused);
		emoticons.put(mEmoText[62], R.drawable.grumpy);
	}

	public static Spannable getSmiledText(Context context, String text) {

		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		int index;

		for (index = 0; index < builder.length(); index++) {
			for (Entry<String, Integer> entry : emoticons.entrySet()) {
				int length = entry.getKey().length();
				if (index + length > builder.length()) continue;
				if (builder.subSequence(index, index + length).toString().equals(entry.getKey())) {
					builder.setSpan(new ImageSpan(context, entry.getValue()), index, index + length,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					index += length - 1;
					break;
				}
			}
		}
		return builder;
	}
}
