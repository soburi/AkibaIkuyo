package soburi.akibaikuyo;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.webkit.WebView;
import java.io.InputStream;

public class AboutActivity extends Activity
{

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        WebView wv = (WebView)findViewById(R.id.AboutView);
        try
        {
            wv.loadData(readAboutAsset(), "text/html", "utf-8");
        }
        catch(Exception exception) { }
    }

    public String readAboutAsset()
    {
        try
        {
            AssetManager as = getResources().getAssets();
            InputStream is = as.open("about.html");
            byte buf[] = new byte[is.available()];
            is.read(buf);
            String str = new String(buf);
            return str;
        }
        catch(Exception exception)
        {
            return "";
        }
    }
}
