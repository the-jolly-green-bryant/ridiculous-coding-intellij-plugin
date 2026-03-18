package com.bryantjames.ridiculouscoding.element;


import com.bryantjames.ridiculouscoding.Power;
import com.bryantjames.ridiculouscoding.PowerMode;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class BaseElement implements Power {
  protected long initLife;
  protected long life;
  protected Point2D.Float origin;

  public void update(double delta) {
    PowerMode.logger().warn("Calling base `update` method on Element.");
  }

  public void render(
    Graphics g,
    int dxx,
    int dyy
  ) {
    PowerMode.logger().warn("Calling base `render` method on Element.");
  }

  protected float lifeFactor() {
    return 1 - ((life() - System.currentTimeMillis()) / (float) initLife());
  }

  protected long life() {
    return this.life;
  }

  protected long initLife() {
    return this.initLife;
  }

  protected float progress() {
    long remaining = this.life - System.currentTimeMillis();
    if (this.initLife <= 0) {
      return 1f;
    }

    float p = 1f - ((float) remaining / (float) this.initLife);
    return Math.max(0f, Math.min(1f, p));
  }

  public boolean isAlive() {
    return life() > System.currentTimeMillis() && powerMode().isEnabled();
  }
}
