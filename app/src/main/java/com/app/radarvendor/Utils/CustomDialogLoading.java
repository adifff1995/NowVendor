package com.app.radarvendor.Utils;//package com.mtc.dermabit.Utils;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.Window;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//
//import com.mtc.tawerni.R;
//
//import java.util.Objects;
//
//public class CustomDialogLoading extends Dialog {
//    Context context;
//    private ImageView imageView;
//
//    public CustomDialogLoading(Context context, String textView) {
//        super(context);
//        this.context = context;
//    }
//
//    public CustomDialogLoading(Context context) {
//        super(context);
//        this.context = context;
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        setContentView(R.layout.dialog_layout);
////        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        getWindow().setGravity(Gravity.CENTER);
//        setCancelable(false);
//        imageView = findViewById(R.id.image);
//        ImageViewAnimatedChange(context, imageView, R.drawable.logo_icon);
//    }
//
//    public static void ImageViewAnimatedChange(Context c, final ImageView v, final int new_image) {
//        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
//        final Animation anim_in = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
//        anim_in.setRepeatCount(Animation.INFINITE);
//        anim_in.setDuration(1000);
//        anim_out.setDuration(1000);
//        anim_out.setRepeatCount(Animation.INFINITE);
//        anim_out.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                v.setImageResource(new_image);
//                anim_in.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                    }
//                });
//                v.startAnimation(anim_in);
//            }
//        });
//        v.startAnimation(anim_out);
//    }
//}
