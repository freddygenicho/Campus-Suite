package com.thegads.uliza.anim;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Freddy Genicho on 7/11/2016.
 */
public class AnimationUtils {
    public static void animateView(RecyclerView.ViewHolder holder, boolean goesDown) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translateY", goesDown ? 100 : -100, 0);
        animatorTranslateY.setDuration(1000);
        animatorTranslateY.start();
    }
}
