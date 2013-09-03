package org.smoothbuild.plugin;

import org.smoothbuild.problem.ProblemsListener;

public interface Sandbox extends ProblemsListener {
  public FileSet resultFileSet();

  public File resultFile(Path path);
}
