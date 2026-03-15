package com.bryantjames.ridiculouscoding.management;

import com.bryantjames.ridiculouscoding.element.*;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.bryantjames.ridiculouscoding.Direction;
import com.bryantjames.ridiculouscoding.Power;
import com.bryantjames.ridiculouscoding.element.*;
import com.bryantjames.ridiculouscoding.listeners.MyCaretListener;
import com.bryantjames.ridiculouscoding.util.Pair;
import com.bryantjames.ridiculouscoding.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ElementContainer extends JComponent implements ComponentListener, Power {

  private final EditorImpl editor;

  private final List<JComponent> shakeComponents;
  private final List<Pair<Element, Point>> elements;
  private final List<Point> shakeData;
  private long lastShake;
  private long lastUpdate;

  public ElementContainer(EditorImpl editor) {
    super();
    this.editor = editor;
    this.shakeComponents = Arrays.asList(
      editor.getComponent(),
      editor.getContentComponent()
    );
    this.elements = new ArrayList<>();
    this.shakeData = new ArrayList<>();
    this.lastShake = System.currentTimeMillis();
    this.lastUpdate = System.currentTimeMillis();
    JComponent myParent = editor.getContentComponent();
    myParent.add(this);
    this.setBounds(myParent.getBounds());
    myParent.addComponentListener(this);
    editor
      .getCaretModel()
      .addCaretListener(new MyCaretListener());

    SwingUtilities.invokeLater(() -> onOpenEditor());
    setVisible(true);
  }

  private void onOpenEditor() {
    int dim = editor
      .getScrollPane()
      .getHeight() / 2;
    int w = editor
      .getScrollPane()
      .getWidth();
    int h = editor
      .getScrollPane()
      .getHeight();

    addBam(new Point(w / 2, h / 2 - (dim / 2)));
    addSparks(new Point(w / 4, h / 4));
    addSparks(new Point(w * 3 / 4, h * 3 / 4));
    addSparks(new Point(w / 4, h * 3 / 4));
    addSparks(new Point(w * 3 / 4, h / 4));

    // TODO - Probably an easier way to do this.
    for (
      int i = 0;
      i < h;
      i += 15
    ) {
      addFlames(
        new Point(
          0,
          i
        ),
        Direction.RIGHT
      );
      addFlames(
        new Point(
          w - 75,
          i
        ),
        Direction.LEFT
      );
    }
  }

  private void addBam(Point point) {
    if (!powerMode().isBamEnabled()) {
      return;
    }

    int dim = editor
      .getScrollPane()
      .getHeight() / 2;
    int x = point.x - (dim / 2);
    int y = point.y - (dim / 2);
    elements.add(Pair.with(
      new PowerBam(
        x,
        y,
        dim,
        dim,
        (long) (powerMode().getBamLife() * powerMode().valueFactor())
      ),
      getScrollPosition()
    ));
  }

  private void addSparks(Point point) {
    if (powerMode().isSparksEnabled()) {
      return;
    }

    for (
      int i = 0;
      i < (int) (powerMode().getSparkCount() * powerMode().valueFactor());
      i++
    ) {
      addSpark(
        point.x,
        point.y
      );
    }
  }

  private void addFlames(
    Point point,
    Direction direction
  ) {
    if (!powerMode().isFlamesEnabled()) {
      return;
    }

    float base = 0.3f;
    int wh = (int) (
      (
        powerMode().getMaxFlameSize() * base + (
          (
            Math.random() * powerMode().getMaxFlameSize() * (1 - base)
          ) * powerMode().valueFactor()
        )
      )
    );
    int initLife = (int) (powerMode().getMaxFlameLife() * powerMode().valueFactor());
    if (initLife <= 100) {
      return;
    }

    elements.add(Pair.with(
      new PowerFlame(
        point.x + 5,
        point.y - 1,
        wh,
        wh,
        initLife,
        direction != null ? direction : Direction.UP
      ),
      getScrollPosition()
    ));

    if (direction != null) {
      return;
    }

    elements.add(Pair.with(
      new PowerFlame(
        point.x + 5,
        point.y + 15,
        wh,
        wh,
        initLife,
        Direction.DOWN
      ),
      getScrollPosition()
    ));
  }

  private Point getScrollPosition() {
    return new Point(
      editor
        .getScrollingModel()
        .getHorizontalScrollOffset(),
      editor
        .getScrollingModel()
        .getVerticalScrollOffset()
    );

  }

  private void addSpark(
    int x,
    int y
  ) {
    float dx = (float) (
      (Math.random() * 2)
        * (Math.random() > 0.5 ? -1 : 1)
        * powerMode().getSparkVelocityFactor()
    );
    float dy = (float) (
      ((Math.random() * -3) - 1) * powerMode().getSparkVelocityFactor()
    );
    int size = (int) ((Math.random() * powerMode().getSparkSize()) + 1);
    int life = (int) (
      Math.random() * powerMode().getSparkLife() * powerMode().valueFactor()
    );
    elements.add(Pair.with(
      new PowerSpark(
        x,
        y,
        dx,
        dy,
        size,
        life,
        genNextColor(),
        (float) powerMode().getGravityFactor()
      ),
      getScrollPosition()
    ));
  }

  private float[] getBrightColor() {
    float[] hues = new float[] {
      0.6f,
      0.73f,
      0.53f,
      0.88f
    };

    float baseHue = hues[(int) (Math.random() * hues.length)];
    float hue = baseHue + (((float) Math.random() - 0.5f) * 0.04f);

    if (hue < 0f) hue += 1f;
    if (hue > 1f) hue -= 1f;

    float saturation = 0.70f + ((float) Math.random() * 0.25f);
    float brightness = 0.92f + ((float) Math.random() * 0.08f);
    float alpha = 0.95f;

    Color color = Color.getHSBColor(hue, saturation, brightness);

    return new float[] {
      color.getRed() / 255f,
      color.getGreen() / 255f,
      color.getBlue() / 255f,
      alpha
    };
  }

  private float[] genNextColor() {
    return new float[]{getColorPart(
      powerMode().getRedFrom(),
      powerMode().getRedTo()
    ), getColorPart(
      powerMode().getGreenFrom(),
      powerMode().getGreenTo()
    ), getColorPart(
      powerMode().getBlueFrom(),
      powerMode().getBlueTo()
    ), powerMode().getColorAlpha() / 255f};
  }

  private float getColorPart(int from, int to) {
    return (float) (((Math.random() * (to - from)) + from) / 255);
  }

  public void updateElementsOfPower() {
    long delta = System.currentTimeMillis() - lastUpdate;
    if (delta > (1000.0 / powerMode().getFrameRate()) * 2) {
      delta = 16;
    }

    lastUpdate = System.currentTimeMillis();
    double db = 1000.0 / 16;
    long deltaa = delta;
    if (elements.isEmpty()) {
      return;
    }

    elements.removeIf(p -> {
      p.first().update((deltaa / db));
      return !p
        .first()
        .isAlive();
    });
    repaint();
  }

  public void initializeAnimation(
    char c,
    Point point
  ) {
    this.setBounds(getMyBounds());

    // Add our character falling.
    if (c != '\0') {
      addCharacter(
        point,
        c
      );
    }

    if (c == '\n') {
      addBam(point);
    }

    addSparks(point);
    addFlames(point, null);
    doShake(shakeComponents);
    repaint();
  }

  private void addCharacter(
    Point point,
    char c
  ) {
    if (c == '\0' || Character.isWhitespace(c)) {
      return;
    }

    int count = Character.isLetterOrDigit(c)
      ? 1 + (int) (Math.random() * 2)
      : 1;

    for (int i = 0; i < count; i++) {
      addCharacter(point.x, point.y, c);
    }
  }

  private void addCharacter(
    int x,
    int y,
    char c
  ) {
    float velocityFactor = (float) powerMode().getSparkVelocityFactor();

    float startX = x + (float) ((Math.random() * 6) - 3);
    float startY = y + (float) ((Math.random() * 4) - 2);

    float dx = (float) (((Math.random() * 1.2) - 0.6) * velocityFactor);
    float dy = (float) (-(1.8 + (Math.random() * 2.2)) * velocityFactor);

    int size = 14 + (int) (Math.random() * 6);

    int baseLife = (int) (powerMode().getSparkLife() * powerMode().valueFactor());
    int life = (int) (baseLife * (0.75 + (Math.random() * 0.35)));

    elements.add(Pair.with(
      new PowerCharacter(
        startX,
        startY,
        dx,
        dy,
        size,
        life,
        getBrightColor(),
        (float) powerMode().getGravityFactor(),
        c
      ),
      getScrollPosition()
    ));
  }

  public void componentResized(ComponentEvent e) {
    setBounds(getMyBounds());
    powerMode()
      .logger()
      .debug("Resized")
    ;
  }

  public void componentMoved(ComponentEvent e) {
    setBounds(getMyBounds());
    powerMode()
      .logger()
      .debug("Moved")
    ;
  }

  @Override
  public void componentShown(ComponentEvent e) {

  }

  @Override
  public void componentHidden(ComponentEvent e) {

  }

  private Rectangle getMyBounds() {
    Rectangle area = editor
      .getScrollingModel()
      .getVisibleArea();
    return new Rectangle(
      area.x,
      area.y,
      area.width,
      area.height
    );
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!powerMode().isEnabled()) {
      return;
    }

    if (shakeData != null
      && shakeData.size() >= 2
      && System.currentTimeMillis() - lastShake > 100
      && Math.abs(shakeData.get(0).x) < 50
      && Math.abs(shakeData.get(1).y) < 50) {
      doShake(List.of(editor.getComponent()));
    }

    renderElementsOfPower(g);
  }

  private void doShake(List<JComponent> myShakeComponents) {
    if (!powerMode().isShakeEnabled()) {
      return;
    }

    if (!Util.editorOk(editor, 100)) {
      return;
    }

    int x, y;
    if (!shakeData.isEmpty()) {
      x = shakeData.get(0).x;
      y = shakeData.get(0).y;
      shakeData.clear();
    } else {
      x = generateShakeOffset();
      y = generateShakeOffset();
      int scrollX = editor
        .getScrollingModel()
        .getHorizontalScrollOffset();
      int scrollY = editor
        .getScrollingModel()
        .getVerticalScrollOffset();
      shakeData.add(new Point(
        x,
        y
      ));
      shakeData.add(new Point(
        scrollX,
        scrollY
      ));
    }
    myShakeComponents.forEach(component -> {
      Rectangle bounds = component.getBounds();
      component.setBounds(
        bounds.x + x,
        bounds.y + y,
        bounds.width,
        bounds.height
      );
    });
    lastShake = System.currentTimeMillis();
  }

  private void renderElementsOfPower(Graphics g) {
    ScrollingModel scrollingModel = editor.getScrollingModel();
    Point newElementPosition = new Point(
      scrollingModel.getHorizontalScrollOffset(),
      scrollingModel.getVerticalScrollOffset()
    );

    elements.forEach(elementOfPowerPointPair -> {
      int x = elementOfPowerPointPair.last().x - newElementPosition.x;
      int y = elementOfPowerPointPair.last().y - newElementPosition.y;
      elementOfPowerPointPair
        .first()
        .render(
          g,
          x,
          y
        );
    });

  }

  private int generateShakeOffset() {
    int range = (int) (powerMode().getShakeRange() * powerMode().valueFactor());
    return (int) (range - (Math.random() * 2 * range));
  }
}
