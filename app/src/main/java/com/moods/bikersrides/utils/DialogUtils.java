package com.moods.bikersrides.utils;


import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;


public final class DialogUtils {

    public final Activity activity;
    private DialogInterface.OnClickListener mOnClickYesListener;

    public void setmOnClickYesListener(DialogInterface.OnClickListener mOnClickYesListener) {
        this.mOnClickYesListener = mOnClickYesListener;
    }

    public static int resError = -1;
    public static final String SHAREDPREFS_NAME = "bikersUtils";

    public DialogUtils(final Activity activity) {
        this.activity = activity;
    }

    /**
     * Returns a localized "Error" string if available.
     *
     * @return localized error string if available.
     */
    public String getErrorMsg() {
        return resError == -1 ? "Error" : activity.getString(resError);
    }

    /**
     * Shows a simple yes/no dialog. The dialog does nothing and simply
     * disappears when No is clicked.
     *
     * @param message     the message to show
     * @param yesListener invoked when the Yes button is pressed.
     */
    public void showYesNoDialog(final String message, final DialogInterface.OnClickListener yesListener) {
        showYesNoDialog(null, message, yesListener);
    }

    /**
     * Shows a simple yes/no dialog. The dialog does nothing and simply
     * disappears when No is clicked.
     *
     * @param title       an optional title of the dialog. When null or blank the title will not be shown.
     * @param message     the message to show
     * @param yesListener invoked when the Yes button is pressed.
     */
    public void showYesNoDialog(final String title, final String message, final DialogInterface.OnClickListener yesListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (!MiscUtils.isBlank(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, mOnClickYesListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * Shows an error dialog.
     *
     * @param messageRes the message to show.
     */
    public void showErrorDialog(final int messageRes) {
        showErrorDialog(activity.getString(messageRes));
    }

    /**
     * Shows an error dialog.
     *
     * @param message the message to show.
     */
    public void showErrorDialog(final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setTitle(getErrorMsg());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.create().show();
    }

    /**
     * Shows a quick info toast at the bottom of the screen.
     *
     * @param messageRes the message to show.
     */
    public void showToast(final int messageRes) {
        showToast(activity.getString(messageRes));
    }

    /**
     * Shows a quick info toast.
     *
     * @param message the message to show.
     */
    public void showToast(final String message) {
        final Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }


    /**
     * Shows given string in a dialog, but only once.
     *
     * @param dialogId the ID of the dialog. A dialog with this ID will be shown at most once.
     * @param title    an optional title. If null then no title will be shown.
     * @param message  the string resource ID
     */
    public void showInfoOnce(final String dialogId, final int title, final int message) {
        showInfoOnce(dialogId, title == -1 ? null : activity.getString(title), activity.getString(message));
    }

    /**
     * Clears the tracking of a "dialog was shown" flag. After the method finishes, the dialogs will again be shown when one of the {@link #showInfoOnce(int, java.lang.String) } is invoked.
     */
    public void clearInfoOccurency() {
        final SharedPreferences prefs = activity.getApplication().getSharedPreferences(SHAREDPREFS_NAME, Context.MODE_PRIVATE);
        final Set<String> keys = new HashSet<String>(prefs.getAll().keySet());
        final Editor e = prefs.edit();
        for (final String key : keys) {
            if (key.startsWith("infoonce")) {
                e.remove(key);
            }
        }
        e.commit();
    }

    /**
     * Shows given string in a dialog, but only once.
     *
     * @param dialogId the ID of the dialog. A dialog with this ID will be shown at most once.
     * @param title    an optional title. If null then no title will be shown.
     * @param message  the string resource ID
     */
    public void showInfoOnce(final String dialogId, final String title, final String message) {
        final SharedPreferences prefs = activity.getApplication().getSharedPreferences(SHAREDPREFS_NAME, Context.MODE_PRIVATE);
        final String shownDialog = prefs.getString("infoonce" + dialogId, null);
        if (shownDialog == null) {
            prefs.edit().putString("infoonce" + dialogId, "").commit();
            showInfoDialog(title, message);
        }
    }

    /**
     * Shows an information dialog.
     *
     * @param title   an optional title. If -1 then no title will be shown.
     * @param message the dialog message.
     */
    public void showInfoDialog(final int title, final int message) {
        showInfoDialog(title == -1 ? null : activity.getString(title), activity.getString(message));
    }

    /**
     * Shows an information dialog.
     *
     * @param title   an optional title. If null then no title will be shown.
     * @param message the dialog message.
     */
    public void showInfoDialog(final String title, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.create().show();
    }

    /**
     * Shows an information dialog with button.
     *
     * @param title   an optional title. If -1 then no title will be shown.
     * @param message the dialog message.
     */
    public void showInfoDialogWithButton(final int title, final int message) {
        showCancelInfoDialogWithButton(title == -1 ? null : activity.getString(title), activity.getString(message));
    }

    /**
     * Shows an information dialog with button.
     *
     * @param title   an optional title. If null then no title will be shown.
     * @param message the dialog message.
     */
    public void showCancelInfoDialogWithButton(final String title, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.create().show();
    }
}