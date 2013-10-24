package org.smoothbuild.plugin;

import org.smoothbuild.message.message.Message;

public interface Sandbox {
  public void report(Message message);

  public FileBuilder fileBuilder();

  public FileSetBuilder fileSetBuilder();
}
