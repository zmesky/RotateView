package com.zme.myrotateview;

import java.util.ArrayList;
import java.util.List;

import com.zme.myrotateview.view.RotateView;
import com.zme.myrotateview.view.RotateView.RotateViewListener;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements RotateViewListener {
    
    private RelativeLayout layout ;
    private RotateView rotate ;
    private ImageView ivClose ;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        layout = (RelativeLayout) findViewById(R.id.rl_layout);
        rotate = (RotateView) findViewById(R.id.rotate_view);
//        rotate = new RotateView(this);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        rotate.setRoateAngle(100);
        rotate.setRotateViewListener(this);
        rotate.setLayoutParams(new RelativeLayout.LayoutParams(400, 80));
        List<String> contents = new ArrayList<String>();
        contents.add("1111111111111111111111111111111111111111111111111");
        contents.add("2222222222222222222222222222222222222222222222222");
        contents.add("3333333333333333333333333333333333333333333333333");
        contents.add("4444444444444444444444444444444444444444444444444");
        
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		ivClose.measure(w, h);
        
        rotate.setShowContent(contents,ivClose.getMeasuredHeight());
        rotate.setTextSize(15);
        rotate.start();
    }
    
//    public View getInfoView(String title){
//        RelativeLayout infoView = new RelativeLayout(getApplicationContext());
//        infoView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//
//        TextView view3 = new TextView(getApplicationContext());
//        view3.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()*9/11,  ivClose.getMeasuredHeight()));
//        rotate.setLayoutParams(new RelativeLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()*9/11,  ivClose.getMeasuredHeight()+5));
//        view3.setText(title);
//        view3.setTextSize(15);
//        view3.setTextColor(Color.parseColor("#666666"));
//        view3.setBackgroundResource(R.drawable.home_page_info_bg);
//        infoView.addView(view3);
//
//        return infoView ;
//    }

    @Override
    public void onCurrentView(int item) {
        // TODO Auto-generated method stub
        
    }
    

}
