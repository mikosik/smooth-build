package org.smoothbuild.plugin.api;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.impl.FileSetBuilder;

public interface Sandbox {
  public void report(Message message);

  public FileSetBuilder fileSetBuilder();

  public MutableFile createFile(Path path);
}
