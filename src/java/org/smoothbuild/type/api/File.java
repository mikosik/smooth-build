package org.smoothbuild.type.api;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;

import com.google.common.hash.HashCode;

public interface File {
  public HashCode hash();

  public Path path();

  public InputStream openInputStream();

}
