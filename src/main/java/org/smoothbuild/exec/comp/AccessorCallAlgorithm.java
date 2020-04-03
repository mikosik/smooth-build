package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.comp.AlgorithmHashes.accessorCallAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class AccessorCallAlgorithm implements Algorithm {
  private final Accessor accessor;

  public AccessorCallAlgorithm(Accessor accessor) {
    this.accessor = accessor;
  }

  @Override
  public String name() {
    return accessor.name();
  }

  @Override
  public Hash hash() {
    return accessorCallAlgorithmHash(accessor);
  }

  @Override
  public ConcreteType type() {
    return accessor.type();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<SObject> objects = input.objects();
    checkArgument(objects.size() == 1);
    Struct struct = (Struct) objects.get(0);
    return new Output(struct.get(accessor.fieldName()), nativeApi.messages());
  }
}
