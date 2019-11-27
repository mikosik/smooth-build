package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.constructorCallComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.ConcreteType;

import com.google.common.collect.ImmutableList;

public class ConstructorCallComputation implements Computation {
  private final Constructor constructor;

  public ConstructorCallComputation(Constructor constructor) {
    this.constructor = constructor;
  }

  @Override
  public Hash hash() {
    return constructorCallComputationHash(constructor);
  }

  @Override
  public ConcreteType type() {
    return constructor.type();
  }

  @Override
  public Output execute(Input input, Container container) {
    StructBuilder builder = container.factory().structBuilder(constructor.type());
    ImmutableList<Parameter> parameters = constructor.signature().parameters();
    for (int i = 0; i < parameters.size(); i++) {
      builder.set(parameters.get(i).name(), input.objects().get(i));
    }
    return new Output(builder.build(), emptyMessageArray(container));
  }
}
