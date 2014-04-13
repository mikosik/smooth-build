package org.smoothbuild.db.hashed;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;

public class HashCodes {
  public static Path toPath(HashCode hash) {
    return path(hash.toString());
  }
}
