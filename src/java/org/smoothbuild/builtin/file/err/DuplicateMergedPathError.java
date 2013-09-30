package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class DuplicateMergedPathError extends ErrorMessage {
  public DuplicateMergedPathError(Path path) {
    super("Both parameters ('files' and 'with') contain file with path = " + path + ".");
  }
}
