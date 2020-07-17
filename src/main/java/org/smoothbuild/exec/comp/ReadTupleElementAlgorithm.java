package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.comp.AlgorithmHashes.ReadTupleElementAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.spec.Spec;

import com.google.common.collect.ImmutableList;

public class ReadTupleElementAlgorithm implements Algorithm {
  private final Accessor accessor;
  private final Spec spec;

  public ReadTupleElementAlgorithm(Accessor accessor, Spec spec) {
    this.accessor = accessor;
    this.spec = spec;
  }

  @Override
  public Hash hash() {
    return ReadTupleElementAlgorithmHash(accessor);
  }

  @Override
  public Spec type() {
    return spec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Record> records = input.objects();
    checkArgument(records.size() == 1);
    Tuple tuple = (Tuple) records.get(0);
    return new Output(tuple.get(accessor.fieldIndex()), nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
