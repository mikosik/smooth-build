package org.smoothbuild.task.exec.save.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

import com.google.common.base.Joiner;

public class DuplicatePathsInFileArrayArtifactError extends Message {

  public DuplicatePathsInFileArrayArtifactError(Name name, Iterable<Path> duplicates) {
    super(ERROR, createMessage(name, duplicates));
  }

  private static String createMessage(Name name, Iterable<Path> duplicates) {
    String separator = "\n  ";
    String list = separator + Joiner.on(separator).join(duplicates);
    return "Can't store result of " + name + " as it contains files with duplicated paths:" + list;
  }
}
