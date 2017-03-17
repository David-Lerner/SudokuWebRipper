package com.ehsunbehravesh.asyncwebreader;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Test2 {

  public static void main(String[] args) throws Exception {
    AsyncWebReader webReader = new AsyncWebReader(5, new String[]{
              "http://www.google.com",
              "http://www.yahoo.com",
              "http://www.live.com",
              "http://www.wikipedia.com",
              "http://www.facebook.com",
              "http://www.khorasannews.com",
              "http://www.fcbarcelona.com",
              "http://www.khorasannews.com",
            });

    webReader.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        if (arg instanceof Exception) {
          Exception ex = (Exception) arg;
          System.out.println(ex.getMessage());
        } /*else if (arg instanceof List) {
          List<String> vals = (List<String>) arg;
          System.out.println(vals.get(0) + ": " + vals.get(1));
        } */else if (arg instanceof Object[]) {
          Object[] objects = (Object[]) arg;
          HashMap<String, String> result = (HashMap<String, String>) objects[0];
          String[] success = (String[]) objects[1];
          String[] fail = (String[]) objects[2];

          System.out.println("Failds");
          for (int i = 0; i < fail.length; i++) {
            String string = fail[i];
            System.out.println(string);
          }

          System.out.println("-----------");
          System.out.println("success");
          for (int i = 0; i < success.length; i++) {
            String string = success[i];
            System.out.println(string);
          }
          
          System.out.println("\n\nresult of Google: ");
          System.out.println(result.remove("http://www.google.com"));
        }
      }
    });
    Thread t = new Thread(webReader);
    t.start();
    t.join();
  }
}
