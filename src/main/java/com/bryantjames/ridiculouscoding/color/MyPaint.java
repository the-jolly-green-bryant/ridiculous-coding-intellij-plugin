package com.bryantjames.ridiculouscoding.color;

import java.awt.*;

public class MyPaint {
  private int r;
  private int g;
  private int b;
  private int a;


  public MyPaint(
    int r,
    int g,
    int b,
    int a
  ) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public MyPaint withRed(int r) {
    this.setR(r);
    return this;
  }

  public void setR(int r) {
    this.r = r;
  }

  public MyPaint withGreen(int g) {
    this.setG(g);
    return this;
  }

  public void setG(int g) {
    this.g = g;
  }

  public MyPaint withBlue(int b) {
    this.setB(b);
    return this;
  }

  public void setB(int b) {
    this.b = b;
  }

  public MyPaint withAlpha(int a) {
    this.setA(a);
    return this;
  }

  public void setA(int a) {
    this.a = a;
  }

  public Color color() {
    return new Color(
      r,
      g,
      b,
      a
    );
  }
}
