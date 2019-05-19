package com.techrums.whatstheprice.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.techrums.whatstheprice.R;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView a,c,r,i,t,h,hh,ee,e,w,tt,p,s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        a=(ImageView)findViewById(R.id.a);
        c=(ImageView)findViewById(R.id.c);
        w=(ImageView)findViewById(R.id.w);
        h=(ImageView)findViewById(R.id.h);
        ee=(ImageView)findViewById(R.id.ee);
        e=(ImageView)findViewById(R.id.e);
       // eee=(ImageView)findViewById(R.id.eee);
        hh=(ImageView)findViewById(R.id.hh);
        i=(ImageView)findViewById(R.id.i);
        a=(ImageView)findViewById(R.id.a);
        p=(ImageView)findViewById(R.id.p);
        tt=(ImageView)findViewById(R.id.tt);
        t=(ImageView)findViewById(R.id.t);
        s=(ImageView)findViewById(R.id.s);
        r=(ImageView)findViewById(R.id.r);

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.setImageResource(R.drawable.ac);
            }
        });


        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.setImageResource(R.drawable.tc);

            }
        });
        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tt.setImageResource(R.drawable.tc);

            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setImageResource(R.drawable.cc);

            }
        });
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setImageResource(R.drawable.pc);

            }
        });
        ee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ee.setImageResource(R.drawable.ec);

            }
        });
     e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setImageResource(R.drawable.ec);

            }
        });
        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.setImageResource(R.drawable.hc);

            }
        });
        hh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hh.setImageResource(R.drawable.hc);

            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.setImageResource(R.drawable.rc);

            }
        });

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.setImageResource(R.drawable.sc);

            }
        }); i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.setImageResource(R.drawable.ic);

            }
        }); w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                w.setImageResource(R.drawable.wc);

            }
        });




    }
}
