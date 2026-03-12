package com.nmeylan.powermode.listeners;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.nmeylan.powermode.Power;
import com.nmeylan.powermode.PowerMode;
import com.nmeylan.powermode.util.Util;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;
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
    if (powerMode().isEnabled()) {
      powerMode().increaseHeatup(
        Optional.of(dataContext),
        null
      );
      initializeAnimationByTypedAction(
        editor,
        c
      );
    }
  }

  public void initializeAnimationByTypedAction(
    Editor editor,
    char c
  ) {
    boolean isActualEditor = Util.isActualEditor(editor);
    if (isActualEditor && powerMode().getElementContainerManager() != null) {
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
