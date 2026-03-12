package com.bryantjames.ridiculouscoding.ui;

import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.color.MultiGradientPanel;

/**
 * Created by nyxos on 05.07.16.
 */
public class ColorViewController {
  private final PowerMode powerMode;
  double c = 0;
  double f = 60.0;
  //    double x = (255 / 5000.0) * f;
  int dir = 1;

  public ColorViewController(
    MultiGradientPanel colorView,
    PowerMode powerMode
  ) {
    this.powerMode = powerMode;
    Thread thread = new Thread(() -> {
      while (colorView.isVisible()) {
        long t0 = System.currentTimeMillis();
        c = Math.max(
          Math.min(
            (c + genX()),
            powerMode.getBlueTo()
          ),
          powerMode.getBlueFrom()
        );
        if (c >= powerMode.getBlueTo()) {
          c = powerMode.getBlueTo();
          dir *= -1;
        }
        if (c <= powerMode.getBlueFrom()) {
          c = powerMode.getBlueFrom();
          dir *= -1;
        }
        colorView.doUpdate(c);
        colorView.repaint();
        try {
          Thread.sleep((long) (
            Math.max(
              0,
              (1000 / f) - (System.currentTimeMillis() - t0)
            )
          ));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }

  public double genX() {
    return dir * ((powerMode.getBlueTo() - powerMode.getBlueFrom()) / 5000.0) * f;
  }
}
