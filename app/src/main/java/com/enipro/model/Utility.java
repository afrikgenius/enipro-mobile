package com.enipro.model;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.enipro.R;

public class Utility {

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void collapseKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showToast(Context context, @StringRes int text, boolean isLong) {
        showToast(context, context.getString(text), isLong);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks for permission and if not granted, requests permission
     * for the intent called.
     * @param context the context from which the permission is requested
     * @param permission the permission to request for
     * @param requestCode the request code for the permission.
     * @return
     */
    public static boolean checkPermission(final Context context, String permission, int requestCode, CharSequence rationaleText){
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            Log.i(context.getClass().getCanonicalName(), "Permission not granted. Requesting permission.");
            requestPermission(context, permission, requestCode, rationaleText); // Requests the permission specified since the permission has not been granted.
        } else {
            Log.i(context.getClass().getCanonicalName(), permission + " has already been granted. There is no need to request for it.");
            return true;
        }
        return false;
    }


    /**
     * Requests for a permission from the system and grants permission unless permission request was denied then a snackbar with a
     * rationale text is displayed to give user an explanation of why the permission is needed.
     * @param context context where permission is requested.
     * @param permission the permission to request for
     * @param requestCode the request code for the permission
     * @param rationaleText the rationale text to be displayed when permission is denied.
     */
    private static void requestPermission(final Context context, String permission, int requestCode, CharSequence rationaleText){
        Log.i(context.getClass().getCanonicalName(), permission + " has not been granted. Requesting permission..");
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)){
            // Show UI to explain why the permission is needed.
            Log.i(context.getClass().getCanonicalName(), "Providing additional info as to why the permission is needed");
            View rootView = ((Activity)context).getWindow().getDecorView().getRootView();
            Snackbar.make(rootView, rationaleText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, v -> ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode))
                    .show();
        } else {
            // Request permission
            Log.i(context.getClass().getCanonicalName(), "Requesting Permission.");
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
        }
    }


    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }

            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }
}