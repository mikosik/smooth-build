package org.smoothbuild.task.base;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

public class EmptySetTask implements Task {

  @Override
  public Value execute(Sandbox sandbox) {
    /*
     * We cheat here and return empty fileSet. Nobody will ever use this object
     * 
     * Anyway as ArgumentNodesCreator detects nodes which type is equal to
     * EMPTY_SET and replaces them either with StringSetNode or FileSetNode
     * depending on the type of parameter to which argument is assigned.
     * 
     * The only way this code could be execute is when you define smooth
     * function like this
     * 
     * empty : [];
     * 
     * and request smooth to execute it directly. In that case it won't be
     * passed as argument to any other function so workaround in
     * ArgumentNodesCreator won't kick in.
     */

    return sandbox.fileSetBuilder().build();
  }
}
