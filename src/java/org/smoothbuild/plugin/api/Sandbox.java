package org.smoothbuild.plugin.api;

import org.smoothbuild.message.listen.MessageListener;

public interface Sandbox extends MessageListener {
  public MutableFileSet resultFileSet();

  public MutableFile createFile(Path path);
}
