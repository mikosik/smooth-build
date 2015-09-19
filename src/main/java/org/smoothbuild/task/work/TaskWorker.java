package org.smoothbuild.task.work;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.compute.Algorithm;
import org.smoothbuild.task.compute.ArrayAlgorithm;
import org.smoothbuild.task.compute.ConstantAlgorithm;
import org.smoothbuild.task.compute.IdentityAlgorithm;
import org.smoothbuild.task.compute.NativeCallAlgorithm;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class TaskWorker {
  private final Algorithm algorithm;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final CodeLocation codeLocation;

  public TaskWorker(Algorithm algorithm, String name, boolean isInternal, boolean isCacheable,
      CodeLocation codeLocation) {
    this.algorithm = algorithm;
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.codeLocation = checkNotNull(codeLocation);
  }

  public HashCode hash() {
    return algorithm.hash();
  }

  public Type resultType() {
    return algorithm.resultType();
  }

  public String name() {
    return name;
  }

  public boolean isInternal() {
    return isInternal;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return algorithm.execute(input, container);
  }

  public static TaskWorker virtualWorker(DefinedFunction function, CodeLocation codeLocation) {
    return new TaskWorker(new IdentityAlgorithm(function.type()), function.name().value(), false,
        true, codeLocation);
  }

  public static TaskWorker nativeCallWorker(NativeFunction function, boolean isInternal,
      CodeLocation codeLocation) {
    return new TaskWorker(new NativeCallAlgorithm(function), function.name().value(), isInternal,
        function.isCacheable(), codeLocation);
  }

  public static TaskWorker arrayWorker(ArrayType arrayType, CodeLocation codeLocation) {
    return new TaskWorker(new ArrayAlgorithm(arrayType), arrayType.name(), true, true,
        codeLocation);
  }

  public static TaskWorker defaultValueWorker(Type type, Value value, CodeLocation codeLocation) {
    return new TaskWorker(new ConstantAlgorithm(value), type.name(), true, true, codeLocation);
  }

  public static TaskWorker constantWorker(Type type, Value value, CodeLocation codeLocation) {
    return new TaskWorker(new ConstantAlgorithm(value), type.name(), true, false, codeLocation);
  }
}
