package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.base.Preconditions;

/**
 * Function that is defined completely in Smooth script using Smooth language
 * (as opposed to {@link org.smoothbuild.lang.function.nativ.NativeFunction}
 * which is implemented completely in java language).
 */
public class DefinedFunction<T extends SValue> extends AbstractFunction<T> {
  private final Node<T> root;

  public DefinedFunction(Signature<T> signature, Node<T> root) {
    super(signature);
    this.root = checkNotNull(root);
  }

  @Override
  public Task<T> generateTask(TaskGenerator taskGenerator,
      Map<String, ? extends Result<?>> arguments, CodeLocation codeLocation) {
    Preconditions.checkArgument(arguments.isEmpty(),
        "DefinedFunction.generateTask() cannot accept non-empty arguments");
    return root.generateTask(taskGenerator);
  }
}
