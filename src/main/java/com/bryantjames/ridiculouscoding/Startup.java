package com.bryantjames.ridiculouscoding;

import com.bryantjames.ridiculouscoding.listeners.BackspaceHandler;
import com.bryantjames.ridiculouscoding.listeners.HotkeyHandler;
import com.bryantjames.ridiculouscoding.listeners.TabHandler;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;

public final class Startup implements ProjectActivity {

  private static boolean started = false;

  @Override
  public Object execute(
    @NotNull Project project,
    @NotNull Continuation<? super Unit> continuation
  ) {

    if (!started) {
      started = true;

      PowerMode.getInstance().initialize();
      HotkeyHandler.initialize();
      EditorActionManager manager = EditorActionManager.getInstance();

      EditorActionHandler tabHandler =
        manager.getActionHandler(IdeActions.ACTION_EDITOR_TAB);

      manager.setActionHandler(
        IdeActions.ACTION_EDITOR_TAB,
        new TabHandler(tabHandler)
      );

      EditorActionHandler backspaceHandler =
        manager.getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE);

      manager.setActionHandler(
        IdeActions.ACTION_EDITOR_BACKSPACE,
        new BackspaceHandler(backspaceHandler)
      );
    }

    return Unit.INSTANCE;
  }
}