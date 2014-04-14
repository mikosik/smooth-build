package org.smoothbuild.lang.base;

import org.smoothbuild.io.fs.base.Path;

public interface FileBuilder {

  public FileBuilder setPath(Path path);

  public FileBuilder setContent(SBlob content);

  public SFile build();
}
