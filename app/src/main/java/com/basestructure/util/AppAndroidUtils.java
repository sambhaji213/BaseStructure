package com.basestructure.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basestructure.R;
import com.basestructure.base.ApplicationClass;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class AppAndroidUtils {

    public AppAndroidUtils(){}

    static public List<String> getEmailIds(Context context) {
        List<String> emailIds = new ArrayList<String>();

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                emailIds.add(account.name);
            }
        }
        Set<String> set = new HashSet<String>();
        set.addAll(emailIds);
        emailIds.clear();
        emailIds.addAll(set);
        Collections.sort(emailIds);
        return emailIds;
    }

    public static void hideKeyboard(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void showLongToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void hxInformationDlg(Context context, String title) {
        hxInformationDlg(context, title, title);
    }

    public static void hxInformationDlg(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface
                .OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void startFwdFragmentAnimation(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void startFragmentAnimation(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void startFwdAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void startBackAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void startFwdNewActivity(Context context, Class<?> someClass, Bundle
            bundle) {
        Intent intent = new Intent(context, someClass);
        ((Activity)context).overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        context.startActivity(intent);
    }

    public static void startFwdActivity(Context context, Class<?> someClass, Bundle
            bundle) {
        Intent intent = new Intent(context, someClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        context.startActivity(intent);
    }

    public static void startBackwardNewActivity(Context context, Class<?> someClass) {
        startBackwardNewActivity(context, someClass, null);
    }

    public static void startBackwardNewActivity(Context context, Class<?> someClass, Bundle
            bundle) {
        Intent intent = new Intent(context, someClass);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);
        context.startActivity(intent);
    }

    public static void startBackwardActivity(Context context, Class<?> someClass, Bundle
            bundle) {
        Intent intent = new Intent(context, someClass);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        context.startActivity(intent);
    }

   public static SpannableStringBuilder appendSpannableString(SpannableStringBuilder sb, String regularText,
                                                              String clickableText, ClickableSpan clickableSpan) {
       sb.append(regularText);
       String fullString = sb.toString();
       int start= StringUtils.indexOf(fullString, clickableText);
       if(start>0)
           sb.setSpan(clickableSpan, start, start+clickableText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
       return sb;
    }

    public static void makeTextViewSpanable(TextView textView, ClickableSpan clickableSpan,
                                            String textStr, String clickableStr){
        String fullString=textStr;
        int start= StringUtils.indexOf(fullString, clickableStr);
        if(start<1) {
            fullString=textStr+clickableStr;
        }

        SpannableString ss = new SpannableString(fullString);
        start= StringUtils.indexOf(fullString, clickableStr);
        if(start>0)
            ss.setSpan(clickableSpan, start, start+clickableStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static DatePickerDialog getTimePickerDialog(Context context, final EditText editText,
                                                       int maxYear){

        final String defaultDate = editText.getText().toString();

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();

        int year=newCalendar.get(Calendar.YEAR);
        int month= newCalendar.get(Calendar.MONTH);
        int day=newCalendar.get(Calendar.DAY_OF_MONTH);
        try {
            Date date=dateFormatter.parse(defaultDate);
            newCalendar.setTime(date);
            year=newCalendar.get(Calendar.YEAR);
            month= newCalendar.get(Calendar.MONTH);
            day=newCalendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog
                .OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editText.setText(dateFormatter.format(newDate.getTime()));
            }

        },year, month, day);

        Date today= new Date();
        newCalendar.setTime(today);
        newCalendar.add(Calendar.YEAR, maxYear);
        datePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        datePickerDialog.setTitle("Select Date of Birth");
        return datePickerDialog;
    }

    public static void enableDatePicker(final Activity activity,
                                        final EditText editText, final int maxYear){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(activity);
                getTimePickerDialog(activity, editText, maxYear).show();
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(activity);
                    if (editText.getText().toString().isEmpty())
                        getTimePickerDialog(activity, editText, maxYear).show();
                }
            }
        });

    }

    public static boolean isNetWorkAvailable(boolean showMessage) {
        ConnectivityManager connMgr = (ConnectivityManager) ApplicationClass.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else if(showMessage){
            Toast.makeText(ApplicationClass.getAppContext(),
                    ApplicationClass.getAppContext().getString(R.string.hint_networkError),
                    Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public static boolean isNetWorkAvailable() {
        return isNetWorkAvailable(true);
    }

    public static void setOverflowButtonColor(final Activity activity, final int color) {
        final String overflowDescription = activity.getString(R.string
                .abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow=(AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
                removeOnGlobalLayoutListener(decorView,this);
            }
        });
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener
            listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
        else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public static void setBackgroundDrawable(android.support.v7.app.AppCompatActivity  activity,
                                             ImageView image, int drawableId){
        int color= activity.getResources().getColor(R.color.white);
        setBackgroundDrawable(activity, image, drawableId, color);
    }

    public static void setBackgroundDrawable(android.support.v7.app.AppCompatActivity  activity,
                                             ImageView image, int drawableId, int color){
        Drawable[] layers = new Drawable[2];

        ShapeDrawable sd1 = new ShapeDrawable(new OvalShape());
        sd1.getPaint().setColor(color);
        // sd1.getPaint().setStyle(Paint.Style.STROKE);
        Drawable sd2 = ContextCompat.getDrawable(activity, drawableId);
        layers[0] = sd1;
        layers[1] = sd2;
        LayerDrawable composite = new LayerDrawable(layers);
        composite.setLayerInset(1, 2, 2, 2, 2);
        image.setBackgroundDrawable(composite);
    }

    public static void setBackgroundDrawable(Activity activity,
                                             ImageView image, int drawableId, int color){
        Drawable[] layers = new Drawable[2];

        ShapeDrawable sd1 = new ShapeDrawable(new OvalShape());
        sd1.getPaint().setColor(color);
        Drawable sd2 = ContextCompat.getDrawable(activity, drawableId);
        layers[0] = sd1;
        layers[1] = sd2;
        LayerDrawable composite = new LayerDrawable(layers);
        composite.setLayerInset(1,2,2,2,2);
        image.setBackgroundDrawable(composite);
    }

    public static int getActivityWidth(Activity activity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static int getActivityHeight(Activity activity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static int[] getActivityWidthAndHeight(Activity activity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int [] arr = {displaymetrics.widthPixels,displaymetrics.heightPixels};
        return arr;
    }

    public static void changeDrawableColor(Context context, View view,
                                           int drawableResource, int colorResource){
        Drawable sourceDrawable = context.getResources()
                .getDrawable(drawableResource);
        if(sourceDrawable!=null) {
            sourceDrawable.setColorFilter(ContextCompat.getColor(context,
                    colorResource), PorterDuff.Mode.SRC_ATOP);
            view.setBackgroundDrawable(sourceDrawable);
        }
    }
}
