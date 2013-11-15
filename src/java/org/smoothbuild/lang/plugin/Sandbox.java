package org.smoothbuild.lang.plugin;

import org.smoothbuild.message.base.Message;

public interface Sandbox {
  public void report(Message message);

  public FileSetBuilder fileSetBuilder();

  public StringSetBuilder stringSetBuilder();

  public FileBuilder fileBuilder();

  public StringValue string(String string);
}
