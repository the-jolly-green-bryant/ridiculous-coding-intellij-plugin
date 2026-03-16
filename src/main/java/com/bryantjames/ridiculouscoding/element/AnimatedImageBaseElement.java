package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.Direction;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.ImageUtil;
import com.bryantjames.ridiculouscoding.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatedImageBaseElement extends BaseElement {

  private static final Map<String, List<BufferedImage>> IMAGE_CACHE = new HashMap<>();
  private float x;
  private float _x;
  private float y;
  private float _y;
  private int width;
  private int _width;
  private int height;
  private int _height;
  private final String cacheKey;
  private final File folder;

  public AnimatedImageBaseElement(
    String folderPath,
    float _x,
    float _y,
    int _width,
    int _height,
    long initLife,
    Direction direction
  ) {
    this.x = _x;
    this.y = _y;
    this.width = 0;
    this.height = 0;
    this._x = _x;
    this._y = _y;
    this._width = _width;
    this._height = _height;
    this.initLife = initLife;
    this.life = System.currentTimeMillis() + initLife;

    this.folder = new File(folderPath);
    this.cacheKey = this.folder.getAbsolutePath();
    findImages();
  }

  private Image findImages() {
    List<BufferedImage> image = IMAGE_CACHE.get(cacheKey);
    if (image != null && !image.isEmpty()) {
      return image.get(0);
    }

    IMAGE_CACHE.clear();

    List<BufferedImage> images = ImageUtil.imagesForPath(this.folder);
    if (images == null || images.isEmpty()) {
      PowerMode
        .logger()
        .warn("No images loaded for: " + this.folder);
      throw new RuntimeException("No images loaded for: " + this.folder);
    }

    IMAGE_CACHE.put(
      cacheKey,
      images
    );

    return images.get(0);
  }

  @Override
  public boolean update(double delta) {
    if (!isAlive()) {
      return true;
    }

    width = _width;
    height = _height;
    return false;
  }

  @Override
  public void render(
    Graphics g,
    int dxx,
    int dyy
  ) {
    if (!isAlive() || IMAGE_CACHE.get(cacheKey) == null) {
      return;
    }

    List<BufferedImage> images = IMAGE_CACHE.get(this.cacheKey);
    int imageCount = images.size();
    float p = progress();
    int frame = Math.min((int) (p * (imageCount - 1)), imageCount - 1);
    Graphics2D g2d = (Graphics2D) g.create();

    g2d.drawImage(
      images.get(frame),
      (int) x + dxx,
      (int) y + dyy,
      width,
      height,
      null
    );
    g2d.dispose();
  }
}
