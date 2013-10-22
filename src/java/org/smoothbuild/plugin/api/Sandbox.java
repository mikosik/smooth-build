package org.smoothbuild.plugin.api;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;

public interface Sandbox {
  public void report(Message message);

  public FileBuilder fileBuilder();

  public FileSetBuilder fileSetBuilder();
}
