package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.constructorCallAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ConstructorCallAlgorithm implements Algorithm {
  private final Constructor constructor;

  public ConstructorCallAlgorithm(Constructor constructor) {
    this.constructor = constructor;
  }

  @Override
  public String name() {
    return constructor.name();
  }

  @Override
  public Hash hash() {
    return constructorCallAlgorithmHash(constructor);
  }

  @Override
  public ConcreteType type() {
    return constructor.type();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    StructBuilder builder = nativeApi.factory().structBuilder(constructor.type());
    ImmutableList<Parameter> parameters = constructor.signature().parameters();
    for (int i = 0; i < parameters.size(); i++) {
      builder.set(parameters.get(i).name(), input.objects().get(i));
    }
    return new Output(builder.build(), nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
