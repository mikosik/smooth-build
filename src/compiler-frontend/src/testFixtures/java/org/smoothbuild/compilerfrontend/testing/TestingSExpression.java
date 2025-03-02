package org.smoothbuild.compilerfrontend.testing;

import static org.smoothbuild.compilerfrontend.lang.name.Name.typeName;

import java.util.ArrayList;
import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class TestingSExpression {
  public static java.util.List<SType> typesToTest() {
    return nonCompositeTypes().stream()
        .flatMap(t -> compositeTypeSFactories().stream().map(f -> f.apply(t)))
        .toList();
  }

  public static List<SType> nonCompositeTypes() {
    return List.<SType>list(new STypeVar(typeName("A")))
        .addAll(STypes.baseTypes().add());
  }

  public static java.util.List<Function<SType, SType>> compositeTypeSFactories() {
    var context = new FrontendCompilerTestContext();
    java.util.List<Function<SType, SType>> simpleFactories = java.util.List.of(
        context::sArrayType,
        context::sFuncType,
        t -> context.sFuncType(t, context.sIntType()),
        context::sTupleType,
        context::sStructType,
        context::sInterfaceType);
    java.util.List<Function<SType, SType>> factories = new ArrayList<>();
    factories.addAll(simpleFactories);
    for (var simpleFactory : simpleFactories) {
      for (var simpleFactory2 : simpleFactories) {
        Function<SType, SType> compositeFactory = t -> simpleFactory.apply(simpleFactory2.apply(t));
        factories.add(compositeFactory);
      }
    }
    return factories;
  }
}
