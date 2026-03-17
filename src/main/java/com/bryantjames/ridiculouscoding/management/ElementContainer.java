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
  private final List<Pair<BaseElement, Point>> elements;
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
    elements.add(new Pair<>(
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

    elements.add(new Pair<>(
      new AnimatedImageBaseElement(
        "fire/animated/256",
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

    elements.add(new Pair<>(
      new AnimatedImageBaseElement(
        "fire/animated/256",
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

  private void addReticule(Point point) {
    float base = 1.0f;
    int maxSize = 96;
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
        "reticule",
        point.x - mod_centerReticule + mod_fontWidth,
        point.y + mod_fontSize - mod_centerReticule,
        wh,
        wh,
        375,
        null
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

    elements.removeIf(p -> {
      p.first().update((deltaa / db));
      return !p
        .first()
        .isAlive();
    });
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

    addSparks(point);
    doShake(shakeComponents);
    repaint();
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
