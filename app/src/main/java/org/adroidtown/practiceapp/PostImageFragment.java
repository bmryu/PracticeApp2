package org.adroidtown.practiceapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bomeeryu_c on 2017. 5. 15..
 */

public class PostImageFragment extends Fragment {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.postBtn)
    Button completeButton;
    private Unbinder unbinder;
    OnImageClickListener imageClickListener;
    OnPostBtnListener postBtnListener;
    public interface OnImageClickListener {
        void onClick();
    }
    public interface OnPostBtnListener {
        void onClick();
    }

    public void setOnImageClickListener (OnImageClickListener imageClickListener) {this.imageClickListener = imageClickListener;}
    public void setOnPostBtnListener (OnPostBtnListener postBtnListener) {this.postBtnListener = postBtnListener;}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_post_image,container,false);
        ButterKnife.bind(this,rootView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClickListener.onClick();
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postBtnListener.onClick();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
