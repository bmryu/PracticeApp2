package org.adroidtown.practiceapp;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bomeeryu_c on 2017. 5. 22..
 */

public class FirebaseItem extends RecyclerView.ViewHolder {
    public String content;
    public String path;

    public FirebaseItem(View itemView, String content) {
        super(itemView);
        this.content = content;

    }

    public FirebaseItem(View itemView) {
        super(itemView);
    }

    public FirebaseItem(@Nullable View itemView, String content, String path) {
        super(itemView);
        this.content = content;
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        TextView textView = (TextView)itemView.findViewById(R.id.textView);
        textView.setText(content);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> toMap(){
        HashMap<String, String> result = new HashMap<>();
        result.put("content",content);
        result.put("path",path);

        return result;
    }
}
