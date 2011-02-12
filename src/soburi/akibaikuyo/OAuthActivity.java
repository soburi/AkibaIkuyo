
package soburi.akibaikuyo;

import soburi.akibaikuyo.R;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class OAuthActivity extends Activity
{
    class WebViewTransitHandler extends WebViewClient
    {

        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if(url.startsWith("oauth://request"))
                onTransitToCallbackPage(Uri.parse(url));
            return true;
        }
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(0x7f030002);
        WebView wv = (WebView)findViewById(R.id.authweb);
        wv.setWebViewClient(new WebViewTransitHandler());
        twitter = (new TwitterFactory()).getInstance();
    }

    public void onResume()
    {
        super.onResume();
        try
        {
            String consumertoken = getIntent().getStringExtra(QUERY_CONSUMER_TOKEN);
            String consumersecret = getIntent().getStringExtra(QUERY_CONSUMER_SECRET);
            twitter.setOAuthConsumer(consumertoken, consumersecret);
            RequestToken requestToken = twitter.getOAuthRequestToken("oauth://request.com");
            WebView wv = (WebView)findViewById(R.id.authweb);
            wv.loadUrl(requestToken.getAuthorizationURL());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            setResult(-11, getIntent());
            finish();
        }
    }

    protected void onTransitToCallbackPage(Uri uri)
    {
        if(uri == null || !uri.toString().startsWith("oauth://request"))
            return;

        String verifier = uri.getQueryParameter("oauth_verifier");
        try
        {
            AccessToken accessToken = twitter.getOAuthAccessToken(verifier);
            getIntent().putExtra(QUERY_ACCESS_TOKEN, accessToken.getToken());
            getIntent().putExtra(QUERY_ACCESS_SECRET, accessToken.getTokenSecret());
            setResult(-1, getIntent());
            finish();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            setResult(-11, getIntent());
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Cancel");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getTitle().equals("Cancel"))
        {
            setResult(0, getIntent());
            finish();
        }
        return true;
    }

    Twitter twitter;
    static final String QUERY_CONSUMER_TOKEN = "consumertoken";
    static final String QUERY_CONSUMER_SECRET = "consumersecret";
    static final String QUERY_ACCESS_TOKEN = "accesstoken";
    static final String QUERY_ACCESS_SECRET = "accesssecret";
}
