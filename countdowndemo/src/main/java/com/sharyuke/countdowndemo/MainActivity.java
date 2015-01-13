package com.sharyuke.countdowndemo;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.sharyuke.countdownview.CountDownView;
import com.sharyuke.countdownview.SplitModel;
import com.sharyuke.myapplication.R;

public class MainActivity extends ActionBarActivity {

  private ListView mListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mListView = (ListView) findViewById(R.id.list);
    mListView.setAdapter(new CoundDownTimeAdapter());
    //mcdv.setUnits("天", "时", "分", "秒");
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(86400).setSplitStr("天").build());
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(3600).setSplitStr("时").build());
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(60).setSplitStr("分").build());
    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(1).setSplitStr("秒").build());
    //两种效果一样的 setNormalFormat() 与上面的设置

    //mcdv.addSplitModel(new SplitModel.Builder().setSplitNum(86400)
    //    .setSplitStr("天")
    //    .setTextColor(Color.WHITE)
    //    .setIsSplitText(true)
    //    .setSplitPic(CountDownView.FLAG_NO_BACKGROUND)
    //    .build())
    //    .addSplitModel(new SplitModel.Builder().setSplitNum(1)
    //        .setSplitStr("秒")
    //        .setTextColor(Color.WHITE)
    //        .setIsSplitText(false)
    //        .setSplitPic(CountDownView.FLAG_NO_BACKGROUND)
    //        .setIsSplitStr(false)
    //        .build())
    //    .addSplitModel(new SplitModel.Builder().setSplitNum(3600)
    //        .setSplitStr("时")
    //        .setTextColor(Color.WHITE)
    //        .setIsSplitText(false)
    //        .setIsSplitStr(true)
    //        .build())
    //    .addSplitModel(new SplitModel.Builder().setSplitNum(60)
    //        .setSplitStr("分")
    //        .setTextColor(Color.WHITE)
    //        .setIsSplitText(true)
    //        .build())
    //    .setCountTime(1000)
    //    .setOnCountOverListener(new CountDownView.CountOver() {
    //      @Override
    //      public void onCountOver() {
    //        Toast.makeText(MainActivity.this, "回调了！！！", Toast.LENGTH_LONG).show();
    //      }
    //    });
    //mcdv.startCount();
  }

  public class CoundDownTimeAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return 10;
    }

    @Override
    public Object getItem(int position) {
      return position;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      ViewHolder vh;
      if (convertView == null) {
        vh = new ViewHolder();
        convertView = getLayoutInflater().inflate(R.layout.item_count, null);
        vh.countDownView = (CountDownView) convertView.findViewById(R.id.count_view);
        convertView.setTag(vh);
      } else {
        vh = (ViewHolder) convertView.getTag();
      }

      vh.countDownView.setCountTime(
          position == 0 ? 50 : position == 1 ? 70 : position == 2 ? 300 : 3700)
          .setNormalFormat()
          .startCount();

      return convertView;
    }

    class ViewHolder {
      CountDownView countDownView;
    }
  }
}
