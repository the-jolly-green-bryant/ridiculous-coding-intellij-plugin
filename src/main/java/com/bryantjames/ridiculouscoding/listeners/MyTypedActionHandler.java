package com.bryantjames.ridiculouscoding.listeners;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.bryantjames.ridiculouscoding.Power;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;


public class MyTypedActionHandler implements TypedActionHandler, Power {

  private final TypedActionHandler typedActionHandler;

  public MyTypedActionHandler(TypedActionHandler typedActionHandler) {
    this.typedActionHandler = typedActionHandler;
  }

  @Override
  public void execute(
    @NotNull Editor editor,
    char c,
    @NotNull DataContext dataContext
  ) {
    powerType(
      editor,
      c,
      dataContext
    );
    try {
      typedActionHandler.execute(
        editor,
        c,
        dataContext
      );
    } catch (IllegalStateException | IndexOutOfBoundsException x) {
      PowerMode
        .logger()
        .info(
          x.getMessage(),
          x
        );
    }
  }

  public void powerType(
    @NotNull Editor editor,
    char c,
    @NotNull DataContext dataContext
  ) {
    // TODO - Bet we could replace this with a throwable. This style of guarding is very
    //  common and we'd only need to catch it in a couple places.
    if (!powerMode().isEnabled()) {
      return;
    }

    powerMode().increaseHeatup(null);
    initializeAnimationByTypedAction(
      editor,
      c
    );
  }

  public void initializeAnimationByTypedAction(
    Editor editor,
    char c
  ) {
    // TODO - Same as above.
    boolean isActualEditor = Util.isActualEditor(editor);
    if (!isActualEditor || powerMode().getElementContainerManager() == null) {
      return;
    }

    Set<Point> positions = getEditorCaretPositions(editor);
    positions.forEach(pos -> {
      powerMode()
        .getElementContainerManager()
        .initializeAnimation(
          editor,
          c,
          pos
        )
      ;
    });
  }

  public Set<Point> getEditorCaretPositions(Editor editor) {
    return editor
      .getCaretModel()
      .getAllCarets()
      .stream()
      .map(c -> Util.getCaretPosition(
        editor,
        c
      ))
      .collect(Collectors.toSet());
  }
}
