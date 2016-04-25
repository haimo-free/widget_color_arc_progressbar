
package com.example.arcprogressbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private ColorArcProgressBar progressbar;

    private EditText progressbarMax;
    private EditText progressbarCurrent;
    private TextView spaceUsedView;

    private Button button1;
    private ColorArcProgressBar bar1;
    private Button button2;
    private ColorArcProgressBar bar2;
    private Button button3;
    private ColorArcProgressBar bar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressbar = (ColorArcProgressBar) findViewById(R.id.progressbar);
        progressbar.setOnProgressUpdateListener(new ColorArcProgressBar.OnProgressUpdateListener() {

            @Override
            public void onProgressUpdate(float value) {
                spaceUsedView.setText(String.format("%.0fG", value));
            }
        });
        spaceUsedView = (TextView) findViewById(R.id.header_space_used);

        progressbarMax = (EditText) findViewById(R.id.progressbar_max);
        progressbarCurrent = (EditText) findViewById(R.id.progressbar_current);

        findViewById(R.id.ok).setOnClickListener(this);

        bar1 = (ColorArcProgressBar) findViewById(R.id.bar1);
        button1 = (Button) findViewById(R.id.button1);
        bar2 = (ColorArcProgressBar) findViewById(R.id.bar2);
        button2 = (Button) findViewById(R.id.button2);
        bar3 = (ColorArcProgressBar) findViewById(R.id.bar3);
        button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar1.getCurrentValue() >= 100) {
                    bar1.setCurrentValue(0);
                } else {
                    bar1.setCurrentValue(100);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar2.getCurrentValue() >= 100) {
                    bar2.setCurrentValue(0);
                } else {
                    bar2.setCurrentValue(100);
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar3.getCurrentValue() >= 50) {
                    bar3.setCurrentValue(10);
                } else {
                    bar3.setCurrentValue(77);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok: {
                doOk();
                break;
            }
        }
    }

    private void doOk() {
        int max = Integer.parseInt(progressbarMax.getText().toString());
        int current = Integer.parseInt(progressbarCurrent.getText().toString());
        progressbar.setMaxValue(max);
        progressbar.setCurrentValue(current);

        /*
        if (progressbar.getCurrentValue() / progressbar.getMaxValue() >= 0.5f) {
            progressbar.setCurrentValue(progressbar.getMaxValue() / 3);
        } else {
            progressbar.setCurrentValue(progressbar.getMaxValue() * 2 / 3);
        }
        */
    }
}
