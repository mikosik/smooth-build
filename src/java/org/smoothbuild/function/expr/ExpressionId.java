package org.smoothbuild.function.expr;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.Path.path;

import org.smoothbuild.plugin.Path;

public class ExpressionId {
  private final String hash;
  private final Path resultDir;

  public ExpressionId(String hash) {
    this.hash = hash;
    this.resultDir = BUILD_DIR.append(path(hash));
  }

  public Path resultDir() {
    return resultDir;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof ExpressionId)) {
      return false;
    }
    ExpressionId that = (ExpressionId) object;
    return this.hash.equals(that.hash);
  }

  @Override
  public final int hashCode() {
    return hash.hashCode();
  }
}
