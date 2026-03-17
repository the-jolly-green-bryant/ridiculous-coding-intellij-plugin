package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public class BackspaceHandler extends EditorActionHandler {

  private final EditorActionHandler original;

  public BackspaceHandler(EditorActionHandler original) {
    this.original = original;
  }

  @Override
  protected void doExecute(Editor editor, Caret caret, DataContext dataContext) {
    PluginDisabledGuard.run(() -> {

      if (original != null) {
        original.execute(editor, caret, dataContext);
      }

      MyTypedActionHandler.powerType(editor, "backspace");

    });
  }
}