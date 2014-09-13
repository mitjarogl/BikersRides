package com.moods.bikersrides.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{

	public static final String EMPTY = "";

	public static String padLeft(String value, int places, String replaceChar)
	{
		return String.format("%1$" + places + "s", value).replace(" ", replaceChar);
	}

	public static String padRight(String value, int places, String replaceChar)
	{
		return String.format("%1$-" + places + "s", value).replace(" ", replaceChar);
	}

	public static boolean isNullOrEmpty(String value)
	{
		return value == null || value.length() == 0;
	}

	public static boolean isNullOrWhitespace(String s)
	{
		return s == null || isWhitespace(s);
	}

	private static boolean isWhitespace(String s)
	{
		int length = s.length();
		for (int i = 0; i < length; i++)
		{
			if (s.charAt(i) > ' ')
				return false;
		}
		return true;
	}

	public static int nthOccurrence(String original, String text, int n)
	{
		int position = original.indexOf(text, 0);

		while (--n > 0 && position != -1)
		{
			position = original.indexOf(text, position + 1);
		}

		return position;
	}

	public static int indexOfRegex(String text, String regex)
	{
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		int index = -1;

		if (matcher.find())
		{
			index = matcher.start();
		}

		return index;
	}

	public static int lastIndexOfRegex(String text, String regex)
	{
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		int lastIndex = -1;

		while (matcher.find())
		{
			lastIndex = matcher.start();
		}

		return lastIndex;
	}

	public static int lastIndexOfRegex(String text, String regex, int fromIndex)
	{
		return lastIndexOfRegex(text.substring(0, fromIndex), regex);
	}

	public static String join(Collection<?> data, String separator)
	{
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = data.iterator();

		while (true)
		{
			builder.append(iter.next());
			if (!iter.hasNext())
				break;
			builder.append(separator);
		}

		return builder.toString();
	}

	public static String replaceString(String original, String text, String value, int nthOccurrence)
	{
		int occurrence = StringUtils.nthOccurrence(original, text, nthOccurrence);
		StringBuilder mySql = new StringBuilder(original);
		mySql.replace(occurrence, occurrence + text.length(), value);

		return mySql.toString();
	}
	
}
