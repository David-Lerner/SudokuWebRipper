/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ehsunbehravesh.asyncwebreader;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author ehsun7b
 */
public class Test1 {

  public static void main(String[] args) {
    WebReader reader1 = new WebReader("http://ehsunbehravesh.com");
    reader1.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        if (arg instanceof String) {
          System.out.println(arg);
        } else {
          System.out.println(((Exception) arg).getMessage());
        }
      }
    });

    WebReader reader2 = new WebReader("http://codetoearn.blogspot.com");
    reader2.addObserver(new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        if (arg instanceof String) {
          System.out.println(arg);
        } else {
          System.out.println(((Exception) arg).getMessage());
        }
      }
    });

    new Thread(reader1).start();
    new Thread(reader2).start();
  }
}
