package com.lightricks.efraim.toolbar.toolbar;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Sets space between items.
 */
public class ToolbarSpaceDecorator extends RecyclerView.ItemDecoration {
    private final SpaceDecorationAdapter spaceDecorationAdapter;


    public ToolbarSpaceDecorator(SpaceDecorationAdapter spaceDecorationAdapter) {
        this.spaceDecorationAdapter = spaceDecorationAdapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        outRect.left = spaceDecorationAdapter.getSpaceBeforeItem(pos);
    }

    /**
     * Adapter for {@link ToolbarSpaceDecorator}.
     */
    public interface SpaceDecorationAdapter {
        /**
         * Space to be set before item.
         */
        int getSpaceBeforeItem(int pos);
    }
}
