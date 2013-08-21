package org.smoothbuild.expression;

import static org.smoothbuild.lang.type.Path.path;

import org.smoothbuild.lang.type.Path;

import com.google.common.annotations.VisibleForTesting;

public class ExpressionId {
  @VisibleForTesting
  static final Path BUILD_ROOT = path(".smooth");

  private final String hash;
  private final Path resultDir;

  public ExpressionId(String hash) {
    this.hash = hash;
    this.resultDir = BUILD_ROOT.append(path(hash));
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
