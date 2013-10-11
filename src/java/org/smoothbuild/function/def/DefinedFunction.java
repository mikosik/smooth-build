package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;

import com.google.common.base.Preconditions;

/**
 * Function that is defined completely in Smooth script using Smooth language
 * (as opposed to {@link org.smoothbuild.function.nativ.NativeFunction} which is
 * implemented completely in java language).
 */
public class DefinedFunction extends AbstractFunction implements DefinitionNode {
  private final DefinitionNode root;

  public DefinedFunction(Signature signature, DefinitionNode root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  @Override
  public Task generateTask(Map<String, Task> dependencies, CodeLocation codeLocation) {
    Preconditions.checkArgument(dependencies.isEmpty(),
        "DefinedFunction.generateTask() cannot accept non-empty dependencies");
    return generateTask();
  }

  @Override
  public Task generateTask() {
    return root.generateTask();
  }
}
