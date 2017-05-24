package org.adroidtown.practiceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bomeeryu_c on 2017. 5. 10..
 */

public class ListFragment extends Fragment implements View.OnClickListener {

    ListView listView;
    Context context;
    ListAdapter listAdapter;
    TextView allPostBtn;
    TextView newPostBtn;
    Button writeBtn;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    OnPostListener pListener;
    public interface   OnPostListener {
        void onClick();
    }
    public void setOnPostListener (OnPostListener pListener){
        this.pListener = pListener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list_new, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        allPostBtn = (TextView) rootView.findViewById(R.id.allPostBtn);
        newPostBtn = (TextView) rootView.findViewById(R.id.newPostBtn);
        writeBtn = (Button) rootView.findViewById(R.id.writeBtn);
        button1 = (Button) rootView.findViewById(R.id.button1);
        button2 = (Button) rootView.findViewById(R.id.button2);
        button3 = (Button) rootView.findViewById(R.id.button3);
        button4 = (Button) rootView.findViewById(R.id.button4);
        button5 = (Button) rootView.findViewById(R.id.button5);
        button6 = (Button) rootView.findViewById(R.id.button6);

        listAdapter = new ListAdapter(context);
        String nameT = "이름";
        String contentT = "내용";

        for (int i = 1; i < 21; i++) {
            listAdapter.addItem(new ListItem(R.drawable.profile, nameT + i, contentT + i, i));
        }

        listView.setAdapter(listAdapter);

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pListener.onClick();
            }
        });
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

        final TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("전체"));
        tabs.addTab(tabs.newTab().setText("내글"));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                if (position == 0) {
                    listAdapter.reverse(false);
                    listView.setAdapter(listAdapter);
                } else if (position == 1) {
                    listAdapter.reverse(true);
                    listView.setAdapter(listAdapter);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    public void setFalse() {
        button1.setSelected(false);
        button2.setSelected(false);
        button3.setSelected(false);
        button4.setSelected(false);
        button5.setSelected(false);
        button6.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        setFalse();
        v.setSelected(true);
    }
}

class ListAdapter extends BaseAdapter {
    Context context;
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    boolean state;

    public ListAdapter(Context context) {
        this.context = context;
    }

    public void addItem(ListItem item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void reverse(boolean isdescending) {
        if (isdescending == true && state == false) {
            Collections.reverse(items);
            state = true;
        } else if (isdescending == true && state == true) {
            state = true;
            //어레이리스트 그대로 쓰기
        } else if (isdescending == false && state == true) {
            Collections.reverse(items);
            state = false;
        } else if (isdescending == false && state == false) {
            //어레이리스트 그대로 쓰기
            state = false;
        }

    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemHolder itemHolder;

        if (convertView == null) { //한번도 inflate되지 않은 뷰라면 null로 전달될 수도 있으니 체크
            LayoutInflater mInflater = LayoutInflater.from(context); //context로부터 layout인플레이터 정보를 받아 초기활르 해준다
            convertView = mInflater.inflate(R.layout.list_item, null); //convertView에 list_item을 인플레이트 해준다 //null은 뷰그룹 여
            itemHolder = new ItemHolder(); //Itemholder 객체 생성
            itemHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView); //현재 가져온 컨버트뷰에 이미지뷰를 인플레이트 해주고 아이템홀더에 넣어
            itemHolder.nameTV = (TextView) convertView.findViewById(R.id.name);
            itemHolder.contentTV = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(itemHolder); //만든 뷰 홀더를 이용할 수 있게 아이디 부여
        } else {
            itemHolder = (ItemHolder) convertView.getTag(); //만들어둔 뷰홀더를 불러온다
        }

//
//        itemHolder.imageView.setImageResource(items.get(position).getResID());
//        itemHolder.nameTV.setText(items.get(position).getName());
//        itemHolder.contentTV.setText(items.get(position).getName());

        return convertView;
    }

}
