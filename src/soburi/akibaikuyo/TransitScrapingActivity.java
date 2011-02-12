package soburi.akibaikuyo;

import java.text.SimpleDateFormat;
import java.util.Date;

import soburi.akibaikuyo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TransitScrapingActivity extends Activity {

	final static String QUERY_LONGITUDE  = "longitude";
	final static String QUERY_LATITUDE = "latitude";
	final static String QUERY_REQUIRE_TIME = "RequireTime";

	GestureDetector gestureDetector;

	String reqTime = "";

    class JsInterface {
    	public void storeValue(String value) {
    		receivedRequiredTime(value);
    	}
    }

	class OnPageFinishedHandler extends WebViewClient {
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	super.onPageFinished(view, url);
	    	String script =
	    		"javascript:" +
	    		"spans = document.all.tags(\"span\");" +
	    		"timecost = spans[0].innerHTML;" +
	    		"time = timecost.split(\"-\");" +
	    		"timestr = time[0].replace(\" \", \"\");" +
	    		"app.storeValue(timestr);";

	    	view.loadUrl(script);
	    }
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transitscrapingactivity);

        WebView wv = (WebView)findViewById(R.id.transitweb);

        wv.setWebViewClient(new OnPageFinishedHandler() );
        wv.getSettings().setJavaScriptEnabled(true);
    }

    @Override
	public void onResume() {
		super.onResume();

		double longitude = getIntent().getDoubleExtra(QUERY_LONGITUDE, 0f);
		double latitude  = getIntent().getDoubleExtra(QUERY_LATITUDE, 0f);

		String str_longitude = Location.convert(longitude, Location.FORMAT_DEGREES);
		String str_latitude  = Location.convert(latitude, Location.FORMAT_DEGREES);

    	try {
	        WebView wv = (WebView)findViewById(R.id.transitweb);

	        Date d = new Date();
	        SimpleDateFormat dayform = new SimpleDateFormat("yyyy/MM/dd");

	        SimpleDateFormat timeform = new SimpleDateFormat("mm:ss");
	        String datestr = dayform.format(d);
	        String timestr = timeform.format(d);

	        Uri.Builder bldr = new Uri.Builder();
	        bldr.scheme("http");
	        bldr.authority("www.google.co.jp");
	        bldr.appendPath("m");
	        bldr.appendPath("directions");
	        bldr.appendQueryParameter("dirflg", "r");
	        bldr.appendQueryParameter("saddr",  str_latitude + "," + str_longitude);
	        bldr.appendQueryParameter("daddr", "秋葉原");
	        bldr.appendQueryParameter("time", timestr);
	        bldr.appendQueryParameter("date", datestr);
	        bldr.appendQueryParameter("sort", "time");
	        bldr.appendQueryParameter("ttype", "dep");
	        bldr.appendQueryParameter("oi", "nojs");

			wv.loadUrl( bldr.build().toString() );

			wv.addJavascriptInterface(new JsInterface(), "app");


    	}
    	catch(Exception e) {
    		e.printStackTrace();

			setResult(-11, getIntent() );
			finish();
    	}
	}

	public boolean finishThisActivity(int code) {

		if(code == RESULT_OK) {
			getIntent().putExtra(QUERY_REQUIRE_TIME, reqTime);
		}

		setResult(code, getIntent() );
		finish();
		return true;
	}

    public void receivedRequiredTime(String reqTimeInfo) {
    	int funn = reqTimeInfo.indexOf("分");
    	int ji   = reqTimeInfo.indexOf("時");
    	if(funn < 0 && ji < 0) {
    		finishThisActivity(-11);
    		return;
    	}


    	reqTime = reqTimeInfo;

    	AlertDialog.Builder bldr = new AlertDialog.Builder(this);

    	bldr.setTitle("あと" + reqTimeInfo + "で秋葉原に到着します。");
    	bldr.setMessage("所要時間をTweetに反映しますか？");
        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
    	bldr.setPositiveButton("はい",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	finishThisActivity(RESULT_OK);
                    }
                });

    	bldr.setNegativeButton("いいえ",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	finishThisActivity(RESULT_CANCELED);
                    }
                });

    	bldr.setOnCancelListener(
    			new OnCancelListener() {
    				@Override
    				public void onCancel(DialogInterface dialog) {
    					finishThisActivity(RESULT_CANCELED);
    				}
    			});

        // アラートダイアログのキャンセルが可能かどうかを設定します
    	bldr.setCancelable(true);

    	AlertDialog confirmApplyToTweet = bldr.create();

        confirmApplyToTweet.show();
    }
}


