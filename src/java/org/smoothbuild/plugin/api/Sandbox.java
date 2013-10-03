package org.smoothbuild.plugin.api;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;

public interface Sandbox extends MessageListener {
  public MutableFileSet resultFileSet();

  public MutableFile createFile(Path path);
}
