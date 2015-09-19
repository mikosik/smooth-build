package org.smoothbuild.task.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class Computer {
  private final Algorithm algorithm;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final CodeLocation codeLocation;

  public static Computer constantComputer(Type type, Value value, CodeLocation codeLocation) {
    return new Computer(new ConstantAlgorithm(value), type.name(), true, false, codeLocation);
  }

  public static Computer defaultValueComputer(Type type, Value value, CodeLocation codeLocation) {
    return new Computer(new ConstantAlgorithm(value), type.name(), true, true, codeLocation);
  }

  public static Computer arrayComputer(ArrayType arrayType, CodeLocation codeLocation) {
    return new Computer(new ArrayAlgorithm(arrayType), arrayType.name(), true, true, codeLocation);
  }

  public static Computer nativeCallComputer(NativeFunction function, boolean isInternal,
      CodeLocation codeLocation) {
    return new Computer(new NativeCallAlgorithm(function), function.name().value(), isInternal,
        function.isCacheable(), codeLocation);
  }

  public static Computer virtualComputer(DefinedFunction function, CodeLocation codeLocation) {
    return new Computer(new IdentityAlgorithm(function.type()), function.name().value(), false,
        true, codeLocation);
  }

  public Computer(Algorithm algorithm, String name, boolean isInternal, boolean isCacheable,
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
}
