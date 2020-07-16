package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.comp.AlgorithmHashes.ReadTupleElementAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.BinaryType;
import org.smoothbuild.lang.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadTupleElementAlgorithm implements Algorithm {
  private final Accessor accessor;
  private final BinaryType type;

  public ReadTupleElementAlgorithm(Accessor accessor, BinaryType type) {
    this.accessor = accessor;
    this.type = type;
  }

  @Override
  public Hash hash() {
    return ReadTupleElementAlgorithmHash(accessor);
  }

  @Override
  public BinaryType type() {
    return type;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<SObject> objects = input.objects();
    checkArgument(objects.size() == 1);
    Struct struct = (Struct) objects.get(0);
    return new Output(struct.get(accessor.fieldIndex()), nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
