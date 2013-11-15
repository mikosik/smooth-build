package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.base.Preconditions;

/**
 * Function that is defined completely in Smooth script using Smooth language
 * (as opposed to {@link org.smoothbuild.lang.function.nativ.NativeFunction} which is
 * implemented completely in java language).
 */
public class DefinedFunction extends AbstractFunction {
  private final Node root;

  public DefinedFunction(Signature signature, Node root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator, Map<String, Result> arguments,
      CodeLocation codeLocation) {
    Preconditions.checkArgument(arguments.isEmpty(),
        "DefinedFunction.generateTask() cannot accept non-empty arguments");
    return root.generateTask(taskGenerator);
  }
}
