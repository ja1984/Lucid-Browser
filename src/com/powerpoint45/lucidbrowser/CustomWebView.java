package com.powerpoint45.lucidbrowser;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class CustomWebView extends WebView{

	private ProgressBar PB;
	private boolean videoPlaying;
	VideoEnabledWebChromeClient chromeClient;
	
	public CustomWebView(Context context,AttributeSet set, String url) {
		super(context,set);
		this.setId(R.id.browser_page);
		if (url==null)
			this.loadUrl(MainActivity.mPrefs.getString("browserhome", "http://www.google.com/"));
		else
			this.loadUrl(url);
			
		this.getSettings().setPluginState(PluginState.ON);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setBuiltInZoomControls(true);
		this.getSettings().setDisplayZoomControls(false);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setSaveFormData(true);
		this.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		((Activity) MainActivity.activity).registerForContextMenu(this);
		this.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	           return false;
		    }
	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        	if (PB==null)
	            	try{PB = (ProgressBar) MainActivity.webLayout.findViewById(R.id.webpgbar);}catch(Exception e){};
	        	if (view.getVisibility()==View.VISIBLE)
	        		if (PB!=null && PB.getVisibility()!=View.VISIBLE)
	        			PB.setVisibility(ProgressBar.VISIBLE);
	        	ImageButton IB = (ImageButton) MainActivity.bar.findViewById(R.id.browser_refresh);
	    		if (IB!=null){
	    		IB.setImageResource(R.drawable.btn_toolbar_stop_loading_normal);
	    	    }
	        }
	        public void onPageFinished(WebView view, String url) {
                // do your stuff here
               if (PB==null)
             	   PB = (ProgressBar) MainActivity.webLayout.findViewById(R.id.webpgbar);
               if (MainActivity.browserListViewAdapter!=null)
         	   MainActivity.browserListViewAdapter.notifyDataSetChanged();
               
               CustomWebView WV = (CustomWebView) MainActivity.webLayout.findViewById(R.id.browser_page);
               
	               if (WV==CustomWebView.this){//check if this webview is being currently shown/used
		         	   if (((EditText)((Activity) MainActivity.activity).findViewById(R.id.browser_searchbar))!=null)
		         		   if (!((EditText)((Activity) MainActivity.activity).findViewById(R.id.browser_searchbar)).isFocused())
		         			   if (view!=null)
		         				  if (view.getUrl()!=null && view.getUrl().compareTo("about:blank")!=0)
		         					  ((EditText)((Activity) MainActivity.activity).findViewById(R.id.browser_searchbar)).setText(view.getUrl().replace("http://", "").replace("https://", ""));
		                PB.setVisibility(ProgressBar.INVISIBLE);
		                
		                ImageButton IB = (ImageButton) MainActivity.bar.findViewById(R.id.browser_refresh);
			    		if (IB!=null){
			    		IB.setImageResource(R.drawable.btn_toolbar_reload_normal);
			    	    }
			    		
			    		ImageButton BI = (ImageButton) MainActivity.bar.findViewById(R.id.browser_bookmark);
			    		if (BI!=null){
			    			int numBooks=MainActivity.mPrefs.getInt("numbookmarkedpages", 0);
			    			boolean isBook = false;
			    			for (int i=0;i<numBooks;i++){
			    				if (CustomWebView.this!=null)
			    					if (CustomWebView.this.getUrl()!=null)
					    				if (MainActivity.mPrefs.getString("bookmark"+i, "").compareTo(CustomWebView.this.getUrl())==0){
					    					BI.setImageResource(R.drawable.btn_omnibox_bookmark_selected_normal);
					    					isBook=true;
					    					break;
					    				}
			    			}
			    			if (!isBook)
			    				BI.setImageResource(R.drawable.btn_omnibox_bookmark_normal);
			    	   }
	               }
	        }
		});
		
		
		chromeClient =new VideoEnabledWebChromeClient(this);
	    this.setWebChromeClient(chromeClient);
	    
		this.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                    String contentDisposition, String mimetype,
                    long contentLength) {

			   if (MainActivity.isDownloadManagerAvailable(MainActivity.ctxt)){
				   DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
				   
				   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) 
				       request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
				   else 
				       request.setShowRunningNotification(true);
				   
				   if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
					   request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				   else
					   request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
				   
				   
				        
				   
				   request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				   request.allowScanningByMediaScanner();
				   request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url.substring( url.lastIndexOf('/')+1, url.length() ));
				   DownloadManager manager = (DownloadManager) MainActivity.ctxt.getSystemService(Context.DOWNLOAD_SERVICE);
				   manager.enqueue(request);
			   }
		    }
		});

		
	}
	
	
	public CustomWebView(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub
	}
	 
    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public boolean isVideoPlaying(){
    	return videoPlaying;
    }
    
    public void setVideoPlaying(boolean b){
    	videoPlaying = b;
    }
    
    public VideoEnabledWebChromeClient getChromeClient(){
    	return chromeClient;
    }

}
