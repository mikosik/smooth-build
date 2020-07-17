package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.comp.AlgorithmHashes.ReadTupleElementAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.type.BinaryType;

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
    Tuple tuple = (Tuple) objects.get(0);
    return new Output(tuple.get(accessor.fieldIndex()), nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
