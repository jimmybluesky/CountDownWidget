package com.sharyuke.countdowndemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.sharyuke.countdownview.CountDownView;
import com.sharyuke.countdownview.SplitModel;
import com.sharyuke.myapplication.R;

public class MainActivity extends ActionBarActivity {

  CountDownView mcdv;
  EditText et;
  Button bt1, bt2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mcdv = (CountDownView) findViewById(R.id.test);
    bt1 = (Button) findViewById(R.id.start);
    bt2 = (Button) findViewById(R.id.stop);
    et = (EditText) findViewById(R.id.count);
    //mcdv.setUnits("天", "时", "分", "秒");
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(86400).setSplitStr("天").build());
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(3600).setSplitStr("时").build());
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(60).setSplitStr("分").build());
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(1).setSplitStr("秒").build());
    //两种效果一样的 setNormalFormat() 与上面的设置

    mcdv.setNormalFormat().setOnCountOverListener(new CountDownView.CountOver() {
      @Override
      public void onCountOver() {
        Toast.makeText(MainActivity.this, "回调了！！！", Toast.LENGTH_LONG).show();
      }
    });
  }

  public void click(View v) {
    switch (v.getId()) {
      case R.id.start:

        mcdv.setCountTime(et.getText().toString());
        mcdv.startCount();
        break;

      case R.id.stop:
        mcdv.stopCount();
        break;
    }
  }
}
