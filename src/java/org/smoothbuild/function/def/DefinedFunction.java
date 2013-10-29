package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

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
  public Task generateTask(TaskGenerator taskGenerator, Map<String, Result> arguments) {
    Preconditions.checkArgument(arguments.isEmpty(),
        "DefinedFunction.generateTask() cannot accept non-empty arguments");
    return generateTask(taskGenerator);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return root.generateTask(taskGenerator);
  }

  @Override
  public CallLocation callLocation() {
    return root.callLocation();
  }
}
