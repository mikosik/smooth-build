package org.smoothbuild.plugin;

import org.smoothbuild.problem.ProblemsListener;

public interface Sandbox extends ProblemsListener {
  public FileList resultFileList();

  public File resultFile(Path path);
}
