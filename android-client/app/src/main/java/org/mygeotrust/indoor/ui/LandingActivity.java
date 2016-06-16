package org.mygeotrust.indoor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import java.io.File;

public class LandingActivity extends AppCompatActivity {

    private MapsforgeMapView mapsforgeMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());

        setContentView(R.layout.activity_landing);

        // setting the map view
        mapsforgeMapView = (MapsforgeMapView) findViewById(R.id.map_view);
        mapsforgeMapView.setMapFile(new File(""));
        mapsforgeMapView.setMapPosition(60.161397, 24.738347);
        mapsforgeMapView.setZoomLevel((byte) 16);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.settings:
                break;
            case R.id.about:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
