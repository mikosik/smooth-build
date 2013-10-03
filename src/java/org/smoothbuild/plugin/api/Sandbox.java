package org.smoothbuild.plugin.api;

import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;
import org.smoothbuild.type.api.Path;

public interface Sandbox extends MessageListener {
  public MutableFileSet resultFileSet();

  public MutableFile createFile(Path path);
}
