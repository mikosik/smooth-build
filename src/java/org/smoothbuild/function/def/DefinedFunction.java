package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.TaskGenerator;
import org.smoothbuild.task.base.Task;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

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
  public Task generateTask(TaskGenerator taskGenerator, Map<String, HashCode> arguments,
      CodeLocation codeLocation) {
    Preconditions.checkArgument(arguments.isEmpty(),
        "DefinedFunction.generateTask() cannot accept non-empty arguments");
    return generateTask(taskGenerator);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return root.generateTask(taskGenerator);
  }
}
