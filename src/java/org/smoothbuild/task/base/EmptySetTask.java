package org.smoothbuild.task.base;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.base.Constants.SET_TASK_NAME;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

public class EmptySetTask extends AbstractTask {
  public EmptySetTask(CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation));
  }

  @Override
  public void execute(Sandbox sandbox) {
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

    FileSet emptySet = sandbox.fileSetBuilder().build();
    setResult(emptySet);
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return Empty.taskList();
  }
}
