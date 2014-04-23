package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
  public ImmutableList<? extends Node<?>> dependencies(ImmutableMap<String, ? extends Node<?>> args) {
    checkArgument(args.isEmpty(),
        "DefinedFunction.dependencies() cannot accept non-empty arguments");
    return root.dependencies();
  }

  @Override
  public TaskWorker<T> createWorker(ImmutableMap<String, ? extends Node<?>> args,
      CodeLocation codeLocation) {
    checkArgument(args.isEmpty(),
        "DefinedFunction.createWorker() cannot accept non-empty arguments");
    return root.createWorker();
  }
}
