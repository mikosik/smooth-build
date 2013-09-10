package org.smoothbuild.plugin.api;

import org.smoothbuild.problem.ProblemsListener;

public interface Sandbox extends ProblemsListener {
  public MutableFileSet resultFileSet();

  public MutableFile createFile(Path path);
}
