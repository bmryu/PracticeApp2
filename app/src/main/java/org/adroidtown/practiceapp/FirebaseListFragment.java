package org.adroidtown.practiceapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static org.adroidtown.practiceapp.R.id.listView;

/**
 * Created by bomeeryu_c on 2017. 5. 22..
 */

public class FirebaseListFragment extends Fragment{
    private DatabaseReference mDatabase;
    Button writeBtn;
    OnPostListener pListener;
    RecyclerView mPostRV;
    Query query;
    FirebaseRecyclerAdapter<FirebaseItem, PostViewHolder> mPostAdapter;
    public interface   OnPostListener {
        void onClick();
    }

    public void setOnPostListener (OnPostListener pListener){
        this.pListener = pListener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_firebase_listview, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://practiceapp-ce6dc.firebaseio.com/post");
       // final ListView listView = (ListView)rootView.findViewById(R.id.listView);

        mPostRV = (RecyclerView)rootView.findViewById(listView);
        mPostRV.setLayoutManager(new LinearLayoutManager(getContext()));
        query = orderByTotal();
        setupAdapter();
        mPostRV.setAdapter(mPostAdapter);

        writeBtn = (Button)rootView.findViewById(R.id.writeBtn);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pListener.onClick();
            }
        });

        final TabLayout tabs = (TabLayout)rootView.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("전체"));
        tabs.addTab(tabs.newTab().setText("내글"));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                if (position == 0) {
                    mPostAdapter.cleanup();
                    mPostRV.removeAllViewsInLayout();
                    query = orderByTotal();
                    setupAdapter();
                    mPostAdapter.notifyDataSetChanged();
                    mPostRV.setAdapter(mPostAdapter);

                } else if (position == 1) {

                    mPostAdapter.cleanup();
                    mPostRV.removeAllViewsInLayout();
                    query = orderByPath();
                    setupAdapter();
                    mPostAdapter.notifyDataSetChanged();
                    mPostRV.setAdapter(mPostAdapter);

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


/*
       firebaseListAdapter = new FirebaseListAdapter(
                getActivity(),
             Object.class,
                R.layout.list_firebase_item,
                mDatabase
        ) {
            @Override
            protected void populateView(View v, Object model, int position) {
                textView = (TextView)v.findViewById(R.id.name);
              //  imageView = (ImageView)v.findViewById(R.id.imageView);

                HashMap<String, Object> data = (HashMap) model;
                String dataText = data.get("content").toString();
                String dataImagePath = data.get("path").toString();
                textView.setText(dataText);
             //   Glide.with(getActivity()).load(Uri.parse(dataImagePath)).override(100,100).into(imageView);
            }
        };

        listView.setAdapter(firebaseListAdapter);

*/


        return rootView;

    }

    private void setupAdapter() {
        mPostAdapter = new FirebaseRecyclerAdapter<FirebaseItem, PostViewHolder> (
                FirebaseItem.class,
                R.layout.list_firebase_item,
                PostViewHolder.class,
                query
        ) {

            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, FirebaseItem model, int position) {
//                viewHolder.imageView.setImageURI(Uri.parse(model.getPath()));
                viewHolder.textView.setText(model.getContent());
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onBindViewHolder(PostViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

            }
        };
    }


    public Query orderByPath() {
//        mDatabase.child("post").orderByTotal("content").addChildEventListener()
        query = mDatabase.orderByChild("path");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Service123","orderByPath-onChildAdded : " + dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.d("Service123","orderByPath-onChildAdded : " + dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.d("Service123","orderByPath-onChildAdded : " + dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                Log.d("Service123","orderByPath-onChildAdded : " + dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            });
        return query;
        }




    public Query orderByTotal(){
        Log.d("Service123","mDatabase.orderByTotal(\"post\") : " + mDatabase.orderByChild("post"));
        query = mDatabase.orderByChild("content");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Service123","orderByTotal-onChildAdded : " + dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.d("Service123","orderByTotal-onChildAdded : " + dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.d("Service123","orderByTotal-onChildAdded : " + dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                Log.d("Service123","orderByTotal-onChildAdded : " + dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
        return query;
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public PostViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.recycleContent);
            imageView = (ImageView)itemView.findViewById(R.id.recycleImage);
        }

    }

}
