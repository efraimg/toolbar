package com.lightricks.efraim.toolbar.toolbar;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ToolbarSpaceDecoration extends RecyclerView.ItemDecoration {
    private final SpaceDecorationAdapter spaceDecorationAdapter;


    public ToolbarSpaceDecoration(SpaceDecorationAdapter spaceDecorationAdapter) {
        this.spaceDecorationAdapter = spaceDecorationAdapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);

        outRect.left = spaceDecorationAdapter.getSpaceBeforeItem(pos);
    }

    public interface SpaceDecorationAdapter {
        int getSpaceBeforeItem(int pos);
    }
}
