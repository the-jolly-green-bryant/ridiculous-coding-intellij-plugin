package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledException;
import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.bryantjames.ridiculouscoding.gamification.Experience;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;


public class TypingHandler extends TypedHandlerDelegate {

  @Override
  public @NotNull Result charTyped(
    char c,
    @NotNull Project project,
    @NotNull Editor editor,
    @NotNull PsiFile file
  ) {

    PluginDisabledGuard.run(() -> powerType(
      editor,
      "" + c
    ));

    return Result.CONTINUE;
  }

  public static void powerType(
    @Nullable Editor editor,
    String text
  ) {
    PluginDisabledException.requirePluginEnabled();
    PluginDisabledException.requireNotNull(editor);
    PowerMode.getInstance().increaseHeatup(null);
    if (text != null && !text.isEmpty()) {
      Experience.modExperience(editor, 1);
    }

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
