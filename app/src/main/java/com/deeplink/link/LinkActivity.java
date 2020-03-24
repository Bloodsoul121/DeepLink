package com.deeplink.link;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deeplink.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkActivity extends AppCompatActivity {

    @BindView(R.id.data)
    TextView mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Uri data = getIntent().getData();
        if (data != null) {
            String scheme = data.getScheme();
            String host = data.getHost();
            List<String> pathSegments = data.getPathSegments();
            String info = scheme + " " + host + " " + pathSegments;
            mData.setText(info);
        }
    }
}
