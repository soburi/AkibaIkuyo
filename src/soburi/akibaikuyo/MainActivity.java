package soburi.akibaikuyo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

	private static final int REQUEST_OAUTH = 0;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	}

	public void onResume() {
		super.onResume();
	}



    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Twitter連携を設定");
        menu.add(0, 1, 0, "位置情報を取得");
        menu.add(0, 2, 0, "About");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getTitle().equals("Twitter連携を設定")) {
        	Intent itt = new Intent();
        	itt.setAction(Intent.ACTION_RUN);
        	this.startActivityForResult(itt, REQUEST_OAUTH);
        }
        if(item.getTitle().equals("位置情報を取得")) {
        }
        if(item.getTitle().equals("About"))
        {
        	Intent itt = new Intent();
        	itt.setAction(Intent.ACTION_RUN);
        	this.startActivity(itt);
        }
        return true;
    }

}
