package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.ReadTupleElementAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.plugin.NativeApi;

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
    ImmutableList<Record> records = input.records();
    checkArgument(records.size() == 1);
    Tuple tuple = (Tuple) records.get(0);
    return new Output(tuple.get(accessor.fieldIndex()), nativeApi.messages());
  }
}
