package com.lightricks.efraim.toolbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lightricks.efraim.toolbar.toolbar.ToolbarItem;
import com.lightricks.efraim.toolbar.toolbar.ToolbarView;
import com.lightricks.efraim.toolbar.util.UriUtils;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = findViewById(R.id.toolbar_1).getLayoutParams();
                layoutParams.height = 900;
                findViewById(R.id.toolbar_1).setLayoutParams(layoutParams);
            }
        });

        ToolbarItem toolbarItem11 = ToolbarItem.createIconToolbarItem("id1", R.string.animate, R.drawable.ic_animate, true,R.drawable.ic_pro_badge);
        ToolbarItem toolbarItem22 = ToolbarItem.createIconToolbarItem("id1", R.string.loop, R.drawable.ic_loop,false, R.drawable.ic_pro_badge);
        ToolbarItem toolbarItem33 = ToolbarItem.createIconToolbarItem("id1", R.string.sky, R.drawable.ic_sky,false, null);
        ToolbarItem toolbarItem44 = ToolbarItem.createIconToolbarItem("id1", R.string.overlay, R.drawable.ic_overlays,false ,null);
        ToolbarItem toolbarItem55 = ToolbarItem.createIconToolbarItem("id1", R.string.camera_fx, R.drawable.ic_camera_fx,false, null);

        ToolbarItem toolbarItem4 = ToolbarItem.createNumberToolbarItem("id1", R.string.overlay, "15",true);
        ToolbarItem toolbarItem5 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.main_orange_50,
                R.drawable.ic_more, UriUtils.getResourceUri(this, R.raw.test_image),true, R.drawable.ic_pro_badge, true, false, R.string.pack1);
        ToolbarItem toolbarItem6 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.main_orange,
                null,  UriUtils.getResourceUri(this, R.raw.test_image), false,null, false, false, null);
        ToolbarItem toolbarItem7 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.main_orange,
                R.drawable.ic_more, UriUtils.getResourceUri(this, R.raw.test_image),true, null, false, true, null);

        ToolbarItem toolbarItem8 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.pnx_white,
                R.drawable.ic_more, UriUtils.getResourceUri(this, R.raw.test_image),true, R.drawable.ic_pro_badge, true, false, R.string.pack2);
        ToolbarItem toolbarItem9 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.pnx_white,
                null, UriUtils.getResourceUri(this, R.raw.test_image),false, null, true, true, null);

        ToolbarItem toolbarItem10 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.pnx_white,
                R.drawable.ic_more, UriUtils.getResourceUri(this, R.raw.test_image),true, R.drawable.ic_pro_badge, true, true, R.string.pack3);
//        ToolbarItem toolbarItem11 = ToolbarItem.createPackToolbarItem(
//                "id", R.string.app_name, R.color.pnx_white, R.color.pnx_gray3,
//                R.drawable.ic_duplicate, R.color.main_orange, UriUtils.getResourceUri(this, R.raw.test_image), R.drawable.ic_round_star_rate_18px, true, false, R.string.app_name);

        ToolbarItem toolbarItem12 = ToolbarItem.createPackToolbarItem(
                "id", R.string.app_name, R.color.pnx_white,
                R.drawable.ic_more, UriUtils.getResourceUri(this, R.raw.test_image),true, R.drawable.ic_pro_badge, true, true, null);


        ToolbarView toolbarView1 = findViewById(R.id.toolbar_1);
//        toolbarView.setToolbarItems(Arrays.asList(toolbarItem9, toolbarItem10, toolbarItem11, toolbarItem12, toolbarItem5, toolbarItem6, toolbarItem6, toolbarItem6,
//                toolbarItem6, toolbarItem6, toolbarItem7, toolbarItem8, toolbarItem6, toolbarItem6, toolbarItem6,
//                toolbarItem6, toolbarItem6, toolbarItem7));
        toolbarView1.setToolbarItems(Arrays.asList(toolbarItem4,toolbarItem11, toolbarItem22, toolbarItem33, toolbarItem44, toolbarItem55,toolbarItem55,toolbarItem55));//, toolbarItem2, toolbarItem3, toolbarItem4));
        toolbarView1.setToolbarItemClickedListener(new ToolbarView.ToolbarItemClickedListener() {
            @Override
            public void onClick(ToolbarItem item) {

                Toast.makeText(MainActivity.this, "I was clicked", Toast.LENGTH_LONG).show();
            }
        });

        ToolbarView toolbarView2 = findViewById(R.id.toolbar_2);
        toolbarView2.setToolbarItems(Arrays.asList(toolbarItem9, toolbarItem10, toolbarItem12, toolbarItem5, toolbarItem6, toolbarItem6, toolbarItem6,
                toolbarItem6, toolbarItem6, toolbarItem7, toolbarItem8, toolbarItem6, toolbarItem6, toolbarItem6,
                toolbarItem6, toolbarItem6, toolbarItem7));
//
//        toolbarView2.setToolbarItemClickedListener(new ToolbarView.ToolbarItemClickedListener() {
//            @Override
//            public void onClick(ToolbarItem item) {
//
//                Toast.makeText(MainActivity.this, "I was clicked", Toast.LENGTH_LONG).show();
//            }
//        });


    }
}
