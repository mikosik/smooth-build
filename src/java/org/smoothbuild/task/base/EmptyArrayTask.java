package org.smoothbuild.task.base;

import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

public class EmptyArrayTask extends Task {

  public EmptyArrayTask(CodeLocation codeLocation) {
    super("Empty*", true, codeLocation);
  }

  @Override
  public SValue execute(SandboxImpl sandbox) {
    /*
     * We cheat here and return empty stringArray. Nobody will ever use this
     * object.
     * 
     * Anyway as ArgumentNodesCreator detects nodes which type is equal to
     * EMPTY_ARRAY and replaces them either with ArrayNode with type element
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

    return sandbox.arrayBuilder(STRING_ARRAY).build();
  }
}
