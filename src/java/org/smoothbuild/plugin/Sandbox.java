package org.smoothbuild.plugin;

import org.smoothbuild.message.message.Message;

public interface Sandbox {
  public void report(Message message);

  public FileSetBuilder fileSetBuilder();

  public StringSetBuilder stringSetBuilder();

  public FileBuilder fileBuilder();
}
