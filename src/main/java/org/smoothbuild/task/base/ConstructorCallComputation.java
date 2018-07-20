package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.constructorCallComputationHash;

import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.StructBuilder;
import org.smoothbuild.task.exec.Container;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ConstructorCallComputation implements Computation {
  private final Constructor constructor;

  public ConstructorCallComputation(Constructor constructor) {
    this.constructor = constructor;
  }

  @Override
  public HashCode hash() {
    return constructorCallComputationHash(constructor);
  }

  @Override
  public ConcreteType resultType() {
    return constructor.type();
  }

  @Override
  public Output execute(Input input, Container container) {
    StructBuilder builder = container.create().structBuilder(constructor.type());
    ImmutableList<Parameter> parameters = constructor.signature().parameters();
    for (int i = 0; i < parameters.size(); i++) {
      builder.set(parameters.get(i).name(), input.values().get(i));
    }
    return new Output(builder.build());
  }
}
