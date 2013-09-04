package org.smoothbuild.plugin;

import org.smoothbuild.problem.ProblemsListener;

public interface Sandbox extends ProblemsListener {
  public File createFile(Path path);
}
