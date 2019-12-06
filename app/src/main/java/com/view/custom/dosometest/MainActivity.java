package com.view.custom.dosometest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_point);

//        codeAddView();

    }

    private void codeAddView() {
        rl = (RelativeLayout) findViewById(R.id.rl);

        TextView textView = new TextView(this);
        textView.setText("我是一个textview");
        textView.setBackgroundColor(Color.RED);
        textView.setId(R.id.textview);//动态设置id，见https://www.cnblogs.com/codingblock/p/5090441.html
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.leftMargin = 10;
        lp1.setMargins(100, 10, 10, 10);
        lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setLayoutParams(lp1);
        rl.addView(textView);


        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.BELOW, R.id.textview);
        rl.addView(imageView, lp2);
    }
}
