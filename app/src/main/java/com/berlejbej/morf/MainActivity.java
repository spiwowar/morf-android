package com.berlejbej.morf;

import android.app.ActionBar;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.berlejbej.morf.sphere.SphereRenderer;
import com.berlejbej.morf.sphere.SphereView;

import org.rajawali3d.surface.IRajawaliSurface;

/**
 * Created by Szymon on 2016-07-08.
 */
public class MainActivity extends AppCompatActivity {

    private SphereRenderer sphereRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SphereView sphereView = new SphereView(this);
        sphereView.setFrameRate(60.0);
        sphereView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        sphereView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        sphereView.getHolder().setFormat(PixelFormat.RGBA_8888);

        // Add sphereView to your root view
        addContentView(sphereView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

        sphereRenderer = new SphereRenderer(this);
        sphereView.setSurfaceRenderer(sphereRenderer);
    }

    // TODO: probably not needed - no main menu (maybe later???)
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}