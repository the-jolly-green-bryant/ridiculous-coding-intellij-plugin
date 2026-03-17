package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledException;
import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.esotericsoftware.kryo.kryo5.util.Null;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.bryantjames.ridiculouscoding.Power;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    PluginDisabledGuard.run(() -> {
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

      powerType(
        editor,
        "" + c,
        dataContext
      );
    });
  }

  public void powerType(
    @Nullable Editor editor,
    String text,
    @NotNull DataContext dataContext
  ) {
    PluginDisabledException.requirePluginEnabled();
    PluginDisabledException.requireNotNull(editor);
    powerMode().increaseHeatup(null);
    initializeAnimationByTypedAction(
      editor,
      text
    );
  }

  public void initializeAnimationByTypedAction(
    Editor editor,
    String text
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
          text,
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
