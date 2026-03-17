package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledException;
import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
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


public class TypingHandler implements TypedActionHandler, Power {

  private final TypedActionHandler typedActionHandler;

  public TypingHandler(TypedActionHandler typedActionHandler) {
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
        "" + c
      );
    });
  }

  public static void powerType(
    @Nullable Editor editor,
    String text
  ) {
    PluginDisabledException.requirePluginEnabled();
    PluginDisabledException.requireNotNull(editor);
    PowerMode.getInstance().increaseHeatup(null);
    initializeAnimationByTypedAction(
      editor,
      text
    );
  }

  public static void initializeAnimationByTypedAction(
    Editor editor,
    String text
  ) {
    // TODO - Same as above.
    boolean isActualEditor = Util.isActualEditor(editor);
    if (!isActualEditor || PowerMode.getInstance().getElementContainerManager() == null) {
      return;
    }

    Set<Point> positions = getEditorCaretPositions(editor);
    positions.forEach(pos -> PowerMode.getInstance()
      .getElementContainerManager()
      .initializeAnimation(
        editor,
        text,
        pos
      ));
  }

  public static Set<Point> getEditorCaretPositions(Editor editor) {
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
