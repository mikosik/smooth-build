package org.smoothbuild.lang.base;

import java.io.OutputStream;

public interface BlobBuilder {

  public OutputStream openOutputStream();

  public SBlob build();
}
