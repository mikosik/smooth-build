package org.smoothbuild.lang.base;

import org.smoothbuild.io.fs.base.Path;

public interface FileBuilder {

  public void setPath(Path path);

  public void setContent(SBlob content);

  public SFile build();
}
