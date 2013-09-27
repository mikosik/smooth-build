package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class FileParamIsADirError extends Error {
  public FileParamIsADirError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a file but a directory.");
  }
}
