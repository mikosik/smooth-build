package org.smoothbuild.plugin.api;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.type.api.MutableFile;

public interface Sandbox {
  public void report(Message message);

  public FileBuilder fileBuilder();

  public FileSetBuilder fileSetBuilder();

  public MutableFile createFile(Path path);
}
