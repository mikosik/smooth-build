package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Evaluator {
  private final Algorithm algorithm;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final CodeLocation codeLocation;
  private final ImmutableList<Evaluator> dependencies;

  public static Evaluator valueEvaluator(Value value, CodeLocation codeLocation) {
    return new Evaluator(new ValueAlgorithm(value), value.type().name(), true, true, codeLocation,
        ImmutableList.of());
  }

  public static Evaluator arrayEvaluator(ArrayType arrayType, CodeLocation codeLocation,
      List<Evaluator> dependencies) {
    return new Evaluator(new ArrayAlgorithm(arrayType), arrayType.name(), true, true, codeLocation,
        dependencies);
  }

  public static Evaluator nativeCallEvaluator(NativeFunction function, boolean isInternal,
      CodeLocation codeLocation, List<Evaluator> dependencies) {
    return new Evaluator(new NativeCallAlgorithm(function), function.name().value(), isInternal,
        function.isCacheable(), codeLocation, dependencies);
  }

  public static Evaluator virtualEvaluator(DefinedFunction function, CodeLocation codeLocation,
      List<Evaluator> dependencies) {
    return new Evaluator(new IdentityAlgorithm(function.type()), function.name().value(), false,
        true, codeLocation, dependencies);
  }

  public Evaluator(Algorithm algorithm, String name, boolean isInternal, boolean isCacheable,
      CodeLocation codeLocation, List<Evaluator> dependencies) {
    this.algorithm = algorithm;
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.codeLocation = checkNotNull(codeLocation);
    this.dependencies = ImmutableList.copyOf(dependencies);
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

  public ImmutableList<Evaluator> dependencies() {
    return dependencies;
  }

  public Output evaluate(Input input, ContainerImpl container) {
    return algorithm.execute(input, container);
  }
}
