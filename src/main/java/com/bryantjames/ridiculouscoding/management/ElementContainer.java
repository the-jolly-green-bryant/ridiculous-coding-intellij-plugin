package com.bryantjames.ridiculouscoding.management;

import com.bryantjames.ridiculouscoding.*;
import com.bryantjames.ridiculouscoding.element.*;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.bryantjames.ridiculouscoding.listeners.CaretHandler;
import com.bryantjames.ridiculouscoding.util.Pair;
import com.bryantjames.ridiculouscoding.util.Util;
import com.intellij.ui.JBColor;

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
  private List<Pair<BaseElement, Point>> elements;
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
      .addCaretListener(new CaretHandler());

    SwingUtilities.invokeLater(this::onOpenEditor);
    setVisible(true);
  }

  private void onOpenEditor() {
    // TODO - I like this concept but didn't like the effects. Come back to this.
  }

  private void addSparks(Point point) {
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

  private void renderImageAtCharacter(String imagePath, Point point, float base, int maxSize) {
    int wh = (int) (
      (
        maxSize * base + (
          (
            Math.random() * maxSize * (1 - base)
          ) * powerMode().valueFactor()
        )
      )
    );

    float mod_fontSize = (float) this.editor.getColorsScheme().getEditorFontSize();
    float mod_centerReticule = (float) wh / 2;
    FontMetrics metrics = this.editor.getFontMetrics(Font.PLAIN);
    float mod_fontWidth = metrics.charWidth(' ');

    elements.add(new Pair<>(
      new AnimatedImageBaseElement(
        imagePath,
        point.x - mod_centerReticule + mod_fontWidth,
        point.y + mod_fontSize - mod_centerReticule,
        wh,
        wh,
        375
      ),
      getScrollPosition()
    ));
  }

  private void addReticule(Point point) {
    float base = 1.0f;
    int maxSize = 96;
    renderImageAtCharacter("reticule", point, base, maxSize);
  }

  private void addExplosion(Point point) {
    float base = 1.0f;
    int maxSize = 32;
    renderImageAtCharacter("explosion", point, base, maxSize);
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
    float mod_fontSize = (float) this.editor.getColorsScheme().getEditorFontSize() / 2;

    float dx = (float) (
      ((Math.random() * 2) - 1) * powerMode().getSparkVelocityFactor() * 2.2
    );

    float dy = (float) (
      ((Math.random() * 2) - 1) * powerMode().getSparkVelocityFactor() * 1.6
    );

    int size = (int) ((Math.random() * powerMode().getSparkSize()) + 3);
    int life = 1500;
    elements.add(new Pair<>(
      new ParticleBaseElement(
        x,
        y + mod_fontSize,
        dx,
        dy,
        size,
        life,
        ParticleBaseElement.getColor()
      ),
      getScrollPosition()
    ));
  }

  private float[] getBrightColor() {
    Color[] palette = new Color[] {
      new JBColor(new Color(80, 255, 255), new Color(80, 255, 255)),
      new JBColor(new Color(255, 80, 220), new Color(255, 80, 220)),
      new JBColor(new Color(255, 240, 80), new Color(255, 240, 80)),
      new JBColor(new Color(120, 255, 120), new Color(120, 255, 120)),
      JBColor.WHITE
    };

    Color color = palette[(int) (Math.random() * palette.length)];

    return new float[] {
      color.getRed() / 255f,
      color.getGreen() / 255f,
      color.getBlue() / 255f,
      1.0f
    };
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

    List<Pair<BaseElement, Point>> next = new ArrayList<>(elements.size());

    for (Pair<BaseElement, Point> p : elements) {
      BaseElement element = p.first();
      element.update(delta);

      if (element.isAlive()) {
        next.add(p);
      }
    }

    elements = next;
    repaint();
  }

  public void initializeAnimation(
    String text,
    Point point
  ) {
    this.setBounds(getMyBounds());

    // Add our character falling.
    if (!text.isEmpty()) {
      addReticule(point);
      addCharacter(
        point,
        text
      );
    }

    if (text.equals("\n")) {
      addReticule(point);
      addCharacter(
        point,
        "ENTER"
      );
    }

    if (text.equals(" ")) {
      addCharacter(
        point,
        "SPACE"
      );
    }

    if (text.equalsIgnoreCase("backspace")) {
      addExplosion(point);
    }

    if (!text.equalsIgnoreCase("backspace")) {
      addSparks(point);
    }

    doShake(shakeComponents);
  }

  private void addCharacter(
    Point point,
    String text
  ) {
    if (text.isEmpty()) {
      return;
    }

    addCharacter(point.x, point.y, text);
  }

  private void addCharacter(
    int x,
    int y,
    String text
  ) {
    float startX = x + (float) ((Math.random() * 8) - 4);
    float startY = y + (float) ((Math.random() * 4) - 2);

    int size = 14 + (int) (Math.random() * 6);

    int baseLife = 1000;
    int life = (int) (baseLife * (0.75 + (Math.random() * 0.35)));

    elements.add(new Pair<>(
      new StringBaseElement(
        startX,
        startY,
        size,
        life,
        getBrightColor(),
        text
      ),
      getScrollPosition()
    ));
  }

  public void componentResized(ComponentEvent e) {
    setBounds(getMyBounds());
    PowerMode.logger().debug("Resized");
  }

  public void componentMoved(ComponentEvent e) {
    setBounds(getMyBounds());
    PowerMode.logger().debug("Moved");
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
    PluginDisabledGuard.run(() -> {
      super.paintComponent(g);
      PluginDisabledException.requirePluginEnabled();
      if (shakeData != null
        && shakeData.size() >= 2
        && System.currentTimeMillis() - lastShake > 100
        && Math.abs(shakeData.get(0).x) < 50
        && Math.abs(shakeData.get(1).y) < 50) {
        doShake(List.of(editor.getComponent()));
      }

      renderElementsOfPower(g);
    });
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
      x = shakeData.getFirst().x;
      y = shakeData.getFirst().y;
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
