package fr.igm.robotmissions.objects.utils;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

public class Utils {
    public static String formatDateString(OffsetDateTime dateTime) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(dateTime);
    }

    public static int[] floatArrayToIntArray(float[] arr) {
        if (arr == null)
            return null;
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            res[i] = (int) arr[i];
        return res;
    }
}
