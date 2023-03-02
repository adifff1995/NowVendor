package com.app.radarvendor.Utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.radarvendor.Module.WorkingDay;
import com.app.radarvendor.R;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressParams;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class GlobalMethods {
    public static int getAccountType() {
        return Hawk.get(Constant.accountType, 1);
    }

    public static List<WorkingDay> getWorkingDays(Context context) {
        List<WorkingDay> workingDayList = new ArrayList<>();
        workingDayList.add(new WorkingDay("1", context.getString(R.string.saturday), 0));
        workingDayList.add(new WorkingDay("2", context.getString(R.string.sunday), 0));
        workingDayList.add(new WorkingDay("3", context.getString(R.string.monday), 0));
        workingDayList.add(new WorkingDay("4", context.getString(R.string.tuesday), 0));
        workingDayList.add(new WorkingDay("5", context.getString(R.string.wednesday), 0));
        workingDayList.add(new WorkingDay("6", context.getString(R.string.thursday), 0));
        workingDayList.add(new WorkingDay("7", context.getString(R.string.friday), 0));
        return workingDayList;
    }

    public static void showErrorToast(Activity context, String msg) {
        MotionToast.Companion.createColorToast(context,
                "",
                msg,
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(context, R.font.almarai));
    }

    public static void showSuccessToast(Activity context, String msg) {
        MotionToast.Companion.createColorToast(context,
                "",
                msg,
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(context, R.font.almarai));
    }

    public static void showWarningToast(Activity context, String msg) {
        MotionToast.Companion.createColorToast(context,
                "",
                msg,
                MotionToastStyle.WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(context, R.font.almarai));
    }


    //    static CustomDialogLoading customDialogLoading;
    public static CircularProgressDrawable getPlaceHolder(Context context) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
//        circularProgressDrawable.setBackgroundColor(context.getColor(R.color.app));
        return circularProgressDrawable;
    }

    //
//    public static String getToken() {
//        if (Session.getInstanse().isUserLogIn()) {
//            return Session.getInstanse().getCustomer().getToken();
//        }
//        return "";
//    }
//    public static LookUpData getLookUp() {
//        return Hawk.get(Constant.lookUpData);
//    }
    public static ApiInterface getApiInterface() {
        return ApiClient.getRetrofitInstance().create(ApiInterface.class);
    }

    //
    public static boolean setEditTextEmptyError(Context context, EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(context.getString(R.string.required_field));
            return false;
        } else {
            return true;
        }
    }

    public static Map<String, Object> getHeaders() {
        Map<String, Object> params = new HashMap<>();
//        Hawk.put(Constant.token, "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiOTNlOGEyY2VhMmJlNTczZWI3NjdmNTlkMTQ4NDUwYWY2MjViZjU2OTI0YmE0OTExYzNkNTFlZjUzNTY0YTkwZWE5MjY4MjkzZDkxMjUzZTYiLCJpYXQiOjE2NjAzMDg4NzcuNTcxMDcsIm5iZiI6MTY2MDMwODg3Ny41NzEwNzEsImV4cCI6MTY5MTg0NDg3Ny41NjcwMDYsInN1YiI6IjEiLCJzY29wZXMiOltdfQ.D-2ka5O1TeglCJ7khgvLJu9FTXKKVlA-dosGL9GDwFS8SLjTgzEku-n9hf_j07tFfgAB7PJTnUE_YBKUSeUHofsFa6CLY0Q1KOq5KvVlBrGAr-lWWpkAyLSc6XlDl9MsnIXDXPdmahSVudJzk4w3vYeeSWXdkLcMs5wtCmzQ4sHakGO_2T1NTlHMERpVG8HAXObxZRPYg7Hu_m4czVfdsFuf8CsT0S8Jcmui_Uxnf0O7NxYY0JZ6JoFS1JKzkT2UZQIjLAobv1M1IhlHGcPJMloA9UYMPIoTxhiruG7SaxDUZ4ya1aBuMHZ5AnZ793I7GSjA00DpeF8rQivlVyZtUgQuMpRHJEFwAGA1sZNTENCAcDYi7-shku3GPr-JbIR1g4rFZTEOdUzwkT9jjIfCOiCOFHKaMbBffCcnQvSoUv9UN7XKpcgNtPH_0fPsELPOoVxKJMc4TLkw4XuEO7bDRsQcpZB50Gtl7tKjxXZiWv_HWvMhWQqoMOjN_gF_-UVHB_1GPPfDLu3h6aGvNTOXPmR2Nk8CSDvc4Eb9U9YJ2zEBB4sUOZTRw8QaxPTSfcMWGk3D94Il5CfIpr3S6jly6LV0YrQfq7lpET24xQTUsqv0WtnMl74FEVVTsk6GawzavTI71RkvFrlsj_mktNXh_OBQCSafOpEfymdL0SPwOj0");

        Log.e("getHeaders: ", Hawk.get(Constant.token));
        if (Hawk.contains(Constant.token))
            params.put("Authorization", "Bearer " + Hawk.get(Constant.token) + "");
        else
            params.put("Authorization", "");
        return params;
    }

    public static String getFCMToken() {
        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            Log.e("getFCMToken: ", s);
            token[0] = s;
        });

        return token[0];
    }

    //
//    public static CircularProgressDrawable getProgress(Context context) {
//        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
//        circularProgressDrawable.setStrokeWidth(5f);
//        circularProgressDrawable.setCenterRadius(30f);
//        return circularProgressDrawable;
//    }
//
//    public static List<String> getBranchNames(Context context) {
//        List<Branch> branchesList = getLookUp().getBranchesList();
//        ArrayList<String> items = new ArrayList<>();
//        items.add(context.getString(R.string.all));
//        for (int i = 0; i < branchesList.size(); i++) {
//            items.add(branchesList.get(i).getName());
//        }
//        return items;
//    }
//
//    public static List<String> getDepartmentNames() {
//        ArrayList<String> items = new ArrayList<>();
//        for (int i = 0; i < getLookUp().getDepartmentsList().size(); i++) {
//            items.add(getLookUp().getDepartmentsList().get(i).getName());
//        }
//        return items;
//    }
//
//    public static List<String> getBranchNamesForContact(Context context) {
//        List<Branch> branchesList = getLookUp().getBranchesList();
//        ArrayList<String> items = new ArrayList<>();
//        for (int i = 0; i < branchesList.size(); i++) {
//            items.add(branchesList.get(i).getName());
//        }
//        return items;
//    }
//
//    public static List<Branch> getBranches() {
//        return getLookUp().getBranchesList();
//    }
//
//    public static List<CategoriesNews> getNewsCategories() {
//        return getLookUp().getCategoriesNewsList();
//    }
//
//    public static List<String> getNewsCategoriesNames(Context context) {
//        ArrayList<String> items = new ArrayList<>();
//        items.add(context.getString(R.string.all));
//        for (int i = 0; i < getLookUp().getCategoriesNewsList().size(); i++) {
//            items.add(getLookUp().getCategoriesNewsList().get(i).getMainCategory().getCategory_name());
//        }
//        return items;
//    }
//
//    public static List<String> getNewsSubCategoriesNames(Context context, String categoryId) {
//        ArrayList<String> items = new ArrayList<>();
//        items.add(context.getString(R.string.all));
//        List<SubCategory> subCategoryList = getNewsSubCategories(categoryId);
//        for (int i = 0; i < subCategoryList.size(); i++) {
//            items.add(subCategoryList.get(i).getName());
//        }
//
//        return items;
//    }
//
//    public static List<SubCategory> getNewsSubCategories(String categoryId) {
//        ArrayList<SubCategory> subCategoryList = new ArrayList<>();
//        for (int i = 0; i < getLookUp().getCategoriesNewsList().size(); i++) {
//            if (getLookUp().getCategoriesNewsList().get(i).getMainCategory().getCategory_id().equals(categoryId))
//                subCategoryList.addAll(getLookUp().getCategoriesNewsList().get(i).getMainCategory().getSubCategoryList());
//        }
//
//        return subCategoryList;
//    }
//
//    public static List<String> getProductsCategoriesNames() {
//        ArrayList<String> items = new ArrayList<>();
//        for (int i = 0; i < getLookUp().getProductsCategories().size(); i++) {
//            items.add(getLookUp().getProductsCategories().get(i).getName());
//        }
//        return items;
//    }
//
//    public static List<String> getOrderStatusNames() {
//        ArrayList<String> items = new ArrayList<>();
//        for (int i = 0; i < getLookUp().getOrderStatusList().size(); i++) {
//            items.add(getLookUp().getOrderStatusList().get(i).getName());
//        }
//        return items;
//    }
//
//    public static Customer getCustomer() {
//        return Hawk.get(Constant.customer);
//    }
//
//    public static String getToken() {
//        if (Hawk.contains(Constant.customer)) {
//            Customer customer = Hawk.get(Constant.customer);
//            return customer.getToken();
//        } else {
//            return "";
//        }
//    }
//
    public static void printGson(String tag, Object object) {
        Gson gson = new Gson();
        Log.e(tag, gson.toJson(object));
    }

    //
//    public static String getLanguage(Context context) {
//        return Lingver.getInstance().getLanguage();
////        return LocalHelper.getLanguage(context);
//    }
//
    public static Map<String, Object> getParams(Context context) {
        Map<String, Object> params = new HashMap<>();
//        params.put("lang", getLanguage(context));
//        params.put("api_data", Constant.api_data);
        return params;
    }

    //
////    public static void showLoading(Context context) {
////        customDialogLoading = new CustomDialogLoading(context);
////        customDialogLoading.show();
////    }
////
////    public static void hideLoading() {
////        if (customDialogLoading != null && customDialogLoading.isShowing()) {
////            customDialogLoading.dismiss();
////        }
////    }
//
//    public static void showSnackbar(View v, String msg) {
//        Snackbar.make(v, msg, Snackbar.LENGTH_SHORT).show();
//    }
//
//
//    public static void setLocale(Activity activity, String languageCode) {
//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//        Resources resources = activity.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//    }
//
//    public static void showErrorToast(Activity context, String msg) {
//        MotionToast.Companion.createColorToast(context,
//                "",
//                msg,
//                MotionToastStyle.ERROR,
//                MotionToast.GRAVITY_BOTTOM,
//                MotionToast.SHORT_DURATION,
//                ResourcesCompat.getFont(context, R.font.calibri_regular));
//    }
//
//    public static void showSuccessToast(Activity context, String title, String msg) {
//        MotionToast.Companion.createColorToast(context,
//                title,
//                msg,
//                MotionToastStyle.SUCCESS,
//                MotionToast.GRAVITY_BOTTOM,
//                MotionToast.SHORT_DURATION,
//                ResourcesCompat.getFont(context, R.font.calibri_regular));
//    }
//
//    public static void showWarningToast(Activity context, String msg) {
//        MotionToast.Companion.createColorToast(context,
//                "",
//                msg,
//                MotionToastStyle.WARNING,
//                MotionToast.GRAVITY_BOTTOM,
//                MotionToast.SHORT_DURATION,
//                ResourcesCompat.getFont(context, R.font.calibri_regular));
//    }
//
    public static void showProgress(final Button button) {
        DrawableButtonExtensionsKt.showProgress(button, new Function1<ProgressParams, Unit>() {
            @Override
            public Unit invoke(ProgressParams progressParams) {
                progressParams.setButtonTextRes(R.string.loading);
                progressParams.setProgressColor(Color.WHITE);
                return Unit.INSTANCE;
            }
        });
        button.setEnabled(false);
    }
//
//    public static boolean checkIsVisitor(Activity context) {
//        if (Hawk.contains(Constant.customer)) {
//            return true;
//        } else {
//            showWarningToast(context, context.getString(R.string.login_msg_visitor));
//            return false;
//        }
//    }


//
//    public static boolean checkLogin(Activity context) {
//        if (Session.getInstanse().isUserLogIn()) {
//            return true;
//        } else {
//            Toast.makeText(context, "الرجاء تسجيل الدخول للإستفادة من خدمات التطبيق", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, ContentForLoginActivity.class);
//            intent.putExtra("flag", 1);
//            context.startActivity(intent);
//            context.finish();
//        }
//
//        return false;
//    }


}
