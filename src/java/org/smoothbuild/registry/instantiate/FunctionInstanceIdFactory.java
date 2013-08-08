package org.smoothbuild.registry.instantiate;

import static org.smoothbuild.lang.type.Path.path;

import org.smoothbuild.lang.type.Path;

import com.google.common.annotations.VisibleForTesting;

public class FunctionInstanceIdFactory {
  @VisibleForTesting
  static final Path BUILD_ROOT = path(".smooth");

  private int count = 0;

  public FunctionInstanceId createId(String name) {
    // TODO hash should be calculated from function name (in future version
    // together with package) and hashes of function that provide values for
    // parameters.

    String hash = Integer.toString(count++) + name;
    return new FunctionInstanceId(BUILD_ROOT.append(path(hash)));
  }
}
