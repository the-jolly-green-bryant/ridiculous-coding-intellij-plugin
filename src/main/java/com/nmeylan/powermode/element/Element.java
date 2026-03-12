package com.nmeylan.powermode.element;


import com.nmeylan.powermode.Power;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.Graphics;

public abstract class Element implements Power {
  protected long initLife;
  protected long life;

  public boolean update(double delta) {
    throw new NotImplementedException("Calling base `update` method on Element.");
  };

  public void render(Graphics g, int dxx, int dyy) {
    throw new NotImplementedException("Calling base `render` method on Element.");
  };

  protected long life() {
    return this.life;
  };

  protected long initLife() {
    return this.initLife;
  };

  protected float lifeFactor() {
    return 1 - ((life() - System.currentTimeMillis()) / (float)initLife());
  }
  
  public boolean isAlive() {
    return life() > System.currentTimeMillis() && powerMode().isEnabled();
  }
}
