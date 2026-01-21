package com.cscorner.app.widgets;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableRecyclerView extends RecyclerView {

    public ExpandableRecyclerView(Context context) { super(context); }
    public ExpandableRecyclerView(Context context, AttributeSet attrs) { super(context, attrs); }
    public ExpandableRecyclerView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }
}
