package com.thegads.uliza.interfaces;

import android.view.View;

/**
 * Created by Freddy Genicho on 7/22/2016.
 */
public class RecyclerView_OnClickListener {

    /** Interface for Item Click over Recycler View Items **/
    public interface OnClickListener {
        void OnItemClick(View view, int position);
    }

}
