package org.vai.com.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private HashMap<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();
	private Context mContext;
	private SpannableStringBuilder mSpannable;

	private void addPattern() {
		addPattern(emoticons, mEmoText[0], R.drawable.poop);
		addPattern(emoticons, mEmoText[1], R.drawable.thumbsdown);
		addPattern(emoticons, mEmoText[2], R.drawable.thumbsup);
		addPattern(emoticons, mEmoText[3], R.drawable.finger);
		addPattern(emoticons, mEmoText[4], R.drawable.laugh);
		addPattern(emoticons, mEmoText[5], R.drawable.smile);
		addPattern(emoticons, mEmoText[6], R.drawable.bawling);
		addPattern(emoticons, mEmoText[7], R.drawable.frown);
		addPattern(emoticons, mEmoText[8], R.drawable.playful);
		addPattern(emoticons, mEmoText[9], R.drawable.wink);
		addPattern(emoticons, mEmoText[10], R.drawable.whistling);
		addPattern(emoticons, mEmoText[11], R.drawable.bored);
		addPattern(emoticons, mEmoText[12], R.drawable.shy);
		addPattern(emoticons, mEmoText[13], R.drawable.kiss);
		addPattern(emoticons, mEmoText[14], R.drawable.kiss);
		addPattern(emoticons, mEmoText[15], R.drawable.roflmao);
		addPattern(emoticons, mEmoText[16], R.drawable.cool);
		addPattern(emoticons, mEmoText[17], R.drawable.biggrin);
		addPattern(emoticons, mEmoText[18], R.drawable.biggrin);
		addPattern(emoticons, mEmoText[19], R.drawable.nailbiting);
		addPattern(emoticons, mEmoText[20], R.drawable.grumpy);
		addPattern(emoticons, mEmoText[21], R.drawable.grumpy);
		addPattern(emoticons, mEmoText[22], R.drawable.eek);
		addPattern(emoticons, mEmoText[23], R.drawable.eek);
		addPattern(emoticons, mEmoText[24], R.drawable.o_o);
		addPattern(emoticons, mEmoText[25], R.drawable.meh);
		addPattern(emoticons, mEmoText[26], R.drawable.meh);
		addPattern(emoticons, mEmoText[27], R.drawable.yuck);
		addPattern(emoticons, mEmoText[28], R.drawable.yuck);
		addPattern(emoticons, mEmoText[29], R.drawable.tongue);
		addPattern(emoticons, mEmoText[30], R.drawable.tongue);
		addPattern(emoticons, mEmoText[31], R.drawable.hungry);
		addPattern(emoticons, mEmoText[32], R.drawable.hungry);
		addPattern(emoticons, mEmoText[33], R.drawable.geek);
		addPattern(emoticons, mEmoText[34], R.drawable.confused);
		addPattern(emoticons, mEmoText[35], R.drawable.inlove);
		addPattern(emoticons, mEmoText[36], R.drawable.inlove);
		addPattern(emoticons, mEmoText[37], R.drawable.banghead);
		addPattern(emoticons, mEmoText[38], R.drawable.banghead);
		addPattern(emoticons, mEmoText[39], R.drawable.frown);
		addPattern(emoticons, mEmoText[40], R.drawable.unsure);
		addPattern(emoticons, mEmoText[41], R.drawable.wtf);
		addPattern(emoticons, mEmoText[42], R.drawable.heart);
		addPattern(emoticons, mEmoText[43], R.drawable.x3);
		addPattern(emoticons, mEmoText[44], R.drawable.devil);
		addPattern(emoticons, mEmoText[45], R.drawable.devil);
		addPattern(emoticons, mEmoText[46], R.drawable.cry);
		addPattern(emoticons, mEmoText[47], R.drawable.cool);
		addPattern(emoticons, mEmoText[48], R.drawable.cool);
		addPattern(emoticons, mEmoText[49], R.drawable.unsure);
		addPattern(emoticons, mEmoText[50], R.drawable.unsure);
		addPattern(emoticons, mEmoText[51], R.drawable.eek);
		addPattern(emoticons, mEmoText[52], R.drawable.eek);
		addPattern(emoticons, mEmoText[53], R.drawable.eek);
		addPattern(emoticons, mEmoText[54], R.drawable.kiki);
		addPattern(emoticons, mEmoText[55], R.drawable.shifty);
		addPattern(emoticons, mEmoText[56], R.drawable.wideyed);
		addPattern(emoticons, mEmoText[57], R.drawable.greedy);
		addPattern(emoticons, mEmoText[58], R.drawable.confused);
		addPattern(emoticons, mEmoText[59], R.drawable.confused);
		addPattern(emoticons, mEmoText[60], R.drawable.confused);
		addPattern(emoticons, mEmoText[61], R.drawable.confused);
		addPattern(emoticons, mEmoText[62], R.drawable.grumpy);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile, int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	private SpannableStringBuilder getSmiledText(SpannableStringBuilder spannable) {

		// For set emotions.
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				if (!set) continue;
				for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start() && spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					spannable.setSpan(new ImageSpan(mContext, entry.getValue()), matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return spannable;
	}

	public EmotionsUtils(Context context) {
		mContext = context;
		addPattern();
	}

	public void setSpannableText(SpannableStringBuilder spannable) {
		mSpannable = spannable;
	}

	public SpannableStringBuilder getSmileText() {
		return getSmiledText(mSpannable);
	}
}
