package org.smoothbuild.common.log.location;

import org.smoothbuild.common.filesystem.base.FullPath;

public final record FileSource(FullPath path) implements CodeSource {
  @Override
  public String description() {
    return path.toString();
  }
}
