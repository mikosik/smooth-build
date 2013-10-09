package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

/**
 * Function that is defined completely in Smooth script using Smooth language
 * (as opposed to {@link org.smoothbuild.function.nativ.NativeFunction} which is
 * implemented completely in java language).
 */
public class DefinedFunction extends AbstractFunction {
  private final DefinitionNode root;

  public DefinedFunction(Signature signature, DefinitionNode root) {
    // TODO
    super(signature, HashCode.fromInt(77));
    this.root = checkNotNull(root);
  }

  @Override
  public Task generateTask(Map<String, Task> dependencies, CodeLocation codeLocation) {
    Preconditions.checkArgument(dependencies.isEmpty(),
        "DefinedFunction.generateTask() cannot accept non-empty dependencies");
    return root.generateTask();
  }
}
