package org.smoothbuild.lang.type;

import java.util.List;
import java.util.function.IntFunction;

import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.base.Signature;

@FunctionalInterface
public interface TypeChooser<T extends Type> {

  public abstract T choose(IntFunction<T> childrenType);

  public static <T extends Type> TypeChooser<T> fixedTypeChooser(T type) {
    return (IntFunction<T> childrenType) -> type;
  }

  public static TypeChooser<ConcreteType> arrayOfFirstChildType() {
    return childrenType -> childrenType.apply(0).increaseCoreDepthBy(1);
  }

  public static TypeChooser<ConcreteType> callEvaluatorType(Signature signature) {
    return (arguments) -> inferCallType(signature.type(), signature.parameters(), arguments);
  }

  public static <T extends Type> T inferCallType(Type resultType,
      List<? extends ParameterInfo> parameters, IntFunction<T> arguments) {
    if (resultType.isGeneric()) {
      int matchingParameter =
          indexOfParameterWithMatchingCoreType(resultType.coreType(), parameters);
      Type actualParameterCoreType = arguments.apply(matchingParameter).decreaseCoreDepthBy(
          parameters.get(matchingParameter).type().coreDepth());
      return (T) resultType.replaceCoreType(actualParameterCoreType);
    } else {
      return (T) resultType;
    }
  }

  static int indexOfParameterWithMatchingCoreType(Type coreType,
      List<? extends ParameterInfo> parameters) {
    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i).type().coreType().equals(coreType)) {
        return i;
      }
    }
    throw new IllegalArgumentException("Cannot find parameter with matching type.");
  }
}
