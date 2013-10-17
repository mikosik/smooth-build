package org.smoothbuild.object;

import static org.smoothbuild.fs.base.Path.path;

import org.smoothbuild.fs.base.Path;

import com.google.common.hash.HashCode;

public class HashCodes {
  public static Path toPath(HashCode hash) {
    return path(hash.toString());
  }
}
