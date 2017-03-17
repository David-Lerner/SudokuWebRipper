package com.ehsunbehravesh.asyncwebreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class AsyncWebReader extends Observable implements Runnable, Observer {
  
  private int coundOfThreads;
  private WebReader[] readers;
  private int indicator;
  private String[] urlAddresses;
  private HashMap<String, String> headers; //new
  private ArrayList<String> successfulAddresses;
  private ArrayList<String> failedAddresses;
  private HashMap<String, String> result;
  
  private boolean running;
  
  public AsyncWebReader(int coundOfThreads, String[] urlAddresses) throws Exception {
    this(coundOfThreads, urlAddresses, new HashMap<>());
  }
  
  public AsyncWebReader(int coundOfThreads, String[] urlAddresses, HashMap<String, String> headers) throws Exception {
    this.coundOfThreads = coundOfThreads;
    this.urlAddresses = urlAddresses;
    this.headers = headers;
    if (coundOfThreads <= 0) {
      throw new Exception("Count of threads should be at least 1!");
    }
    indicator = 0;
  }
  
  
  @Override
  public void run() {
    successfulAddresses = new ArrayList<>();
    failedAddresses = new ArrayList<>();
    result = new HashMap<>();
    running = true;
    readers = new WebReader[Math.min(coundOfThreads, urlAddresses.length)];
    
    for (int i = 0; i < readers.length; i++) {
      readers[i] = new WebReader(urlAddresses[indicator++], headers);
      readers[i].addObserver(this);
      new Thread(readers[i]).start();
    }
    
    /* wait until all urls get fetched */
    
    while (running && 
            successfulAddresses.size() + failedAddresses.size() 
            < urlAddresses.length) {
      try {        
        Thread.sleep(500);        
      } catch (InterruptedException ex) {
        setChanged();
        notifyObservers(ex);
      }
    }
        
    
    String[] successfulAddressesArray = new String[successfulAddresses.size()];
    successfulAddressesArray = successfulAddresses.toArray(successfulAddressesArray);
    
    String[] failedAddressesArray = new String[failedAddresses.size()];
    failedAddressesArray = failedAddresses.toArray(failedAddressesArray);
    
    setChanged();
    notifyObservers(new Object[] {result, successfulAddressesArray, failedAddressesArray});    
  }
  
  @Override
  public synchronized void update(Observable o, Object arg) {
    WebReader reader = (WebReader) o;
    if (arg instanceof Exception) {      
      failedAddresses.add(reader.getUrlAddress());
    } else if (arg instanceof String) {      
      String content = (String) arg;
      String urlAddress = reader.getUrlAddress();
      result.put(urlAddress, content);
      successfulAddresses.add(urlAddress);
      setChanged();
      List<String> list = new ArrayList<>();
      list.add(urlAddress);
      list.add(content);
      notifyObservers(list);
    }
    
    if (indicator < urlAddresses.length) {
      for (int i = 0; i < readers.length; i++) {
        WebReader currentReader = readers[i];
        if (currentReader == reader) {
          readers[i] = new WebReader(urlAddresses[indicator++], headers);
          readers[i].addObserver(this);
          new Thread(readers[i]).start();
          break;
        }
      }
    }
  }
  
  public void stop() {
    running = false;
  }  
}
