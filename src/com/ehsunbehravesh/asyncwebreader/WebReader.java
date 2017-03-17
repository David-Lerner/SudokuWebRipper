package com.ehsunbehravesh.asyncwebreader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.HashMap;

/**
 * http://codetoearn.blogspot.com
 *
 * Modified by David Lerner to allow adding request headers
 * @author ehsun7b
 */
public class WebReader extends Observable implements Runnable {

  private String urlAddress;
  private HashMap<String, String> headers; //new  
  private boolean finished;

  public WebReader(String urlAddress) {
    this(urlAddress, new HashMap<>());
  }

  public WebReader(String urlAddress, HashMap<String, String> headers) {
    this.urlAddress = urlAddress;
    this.headers = headers;
  }
  
  @Override
  public void run() {
    finished = false;
    InputStreamReader isr = null;
    try {
      URL url = new URL(urlAddress);
      URLConnection yc = url.openConnection();
      //modification to allow adding headers to request
      for (String headerName : headers.keySet()) {
          yc.setRequestProperty(headerName, headers.get(headerName));
      }
      isr = new InputStreamReader(yc.getInputStream());

      char[] buffer = new char[1024];
      int read = 0;
      StringBuilder doc = new StringBuilder();
      while ((read = isr.read(buffer)) > 0) {
        doc.append(buffer, 0, read);
      }
      String content = doc.toString();
      setChanged();
      notifyObservers(content);
    } catch (Exception e) {
      setChanged();
      notifyObservers(e);
    } finally {
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException ex) {
          setChanged();
          notifyObservers(ex);
        }
      }
      finished = true;
    }
  }

  public String getUrlAddress() {
    return urlAddress;
  }    

  public boolean isFinished() {
    return finished;
  }    
}
