package org.smoothbuild.plugin.api;

import org.smoothbuild.problem.MessageListener;

public interface Sandbox extends MessageListener {
  public MutableFileSet resultFileSet();

  public MutableFile createFile(Path path);
}
