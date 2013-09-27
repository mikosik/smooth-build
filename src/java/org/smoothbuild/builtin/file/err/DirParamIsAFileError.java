package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DirParamIsAFileError extends Error {
  public DirParamIsAFileError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a directory but a file.");
  }
}
