package com.moods.bikersrides.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ArrayUtils
{

    public static boolean isNullOrEmpty(List<?> list)
    {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(T[] list)
    {
        return list == null || list.length < 1;
    }

    public static boolean isNullOrEmpty(Map<?, ?> map)
    {
        return map == null || map.isEmpty();
    }

    public static boolean isNullOrEmpty(long... array)
    {
        return array == null || array.length < 1;
    }

    public static <T> T firstOrDefault(List<T> list)
    {
        if (isNullOrEmpty(list))
        {
            return null;
        }

        return list.get(0);
    }

    public static <T> T firstOrDefault(T[] list)
    {
        if (isNullOrEmpty(list))
        {
            return null;
        }

        return list[0];
    }

    public static <T, U> Entry<T, U> firstOrDefault(Map<T, U> map)
    {
        if (isNullOrEmpty(map))
        {
            return null;
        }

        return map.entrySet().iterator().next();
    }

    public static <T> List<T> take(List<T> list, int to)
    {
        if (isNullOrEmpty(list) || to > list.size())
        {
            return null;
        }

        return list.subList(0, to);
    }

    public static <T> List<T> skip(List<T> list, int from)
    {
        int size = list.size();

        if (isNullOrEmpty(list) || from > list.size())
        {
            return null;
        }

        return list.subList(from, size);
    }

    public static <T> List<T> takeFirst(List<T> list)
    {
        return take(list, 1);
    }

    public static Double listSum(List<Double> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }

        Double sum = 0.0;

        for (Double value : list)
        {
            sum += value;
        }

        return sum;
    }

    public static Double listAvg(List<Double> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }

        Double listSum = listSum(list);

        return listSum / list.size();
    }

    public static Double listMax(List<Double> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }

        return Collections.max(list);
    }

    public static Double listMin(List<Double> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }

        return Collections.min(list);
    }

    public <T> Map<String, T> listToMap(Collection<T> list)
    {
        Map<String, T> map = new HashMap<String, T>(list.size());

        for (T element : list)
        {
            map.put(element.toString(), element);
        }

        return map;
    }

    public static long[] toPrimitiveArray(List<Long> longList)
    {
        if (ArrayUtils.isNullOrEmpty(longList))
        {
            return new long[0];
        }

        int size = longList.size();
        long[] list = new long[size];

        for (int i = 0; i < size; i++)
        {
            list[i] = longList.get(i);
        }

        return list;
    }

    public static List<Long> toArrayList(long... array)
    {
        if (ArrayUtils.isNullOrEmpty(array))
        {
            return null;
        }
        List<Long> list = new ArrayList<Long>();
        for (Long element : array)
        {
            list.add(element);
        }
        return list;
    }

    public static String printArray(List<?> list)
    {
        if (ArrayUtils.isNullOrEmpty(list))
        {
            return "Empty array!";
        }
        String result = "[" + StringUtils.join(list, ", ") + "]";
        return result;
    }

    public static String printMap(Map<?, ?> map)
    {
        if (ArrayUtils.isNullOrEmpty(map))
        {
            return "Empty Array!";
        }
        String result = "[" + StringUtils.join(map.keySet(), "\n") + "," + StringUtils.join(map.values(), "\n ") + "]";
        return result;
    }

}
