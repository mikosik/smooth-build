package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.constructorCallAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.plugin.NativeApi;

public class ConstructorCallAlgorithm implements Algorithm {
  private final Constructor constructor;
  private final StructType constructedType;

  public ConstructorCallAlgorithm(Constructor constructor, StructType constructedType) {
    this.constructor = constructor;
    this.constructedType = constructedType;
  }

  @Override
  public String name() {
    return constructor.name();
  }

  @Override
  public Hash hash() {
    return constructorCallAlgorithmHash(constructedType);
  }

  @Override
  public ConcreteType type() {
    return constructedType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Struct struct = nativeApi.factory().struct(constructedType, input.objects());
    return new Output(struct, nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
