package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readTupleElementAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadTupleElementAlgorithm implements Algorithm {
  private final int elementIndex;
  private final Spec spec;

  public ReadTupleElementAlgorithm(int elementIndex, Spec spec) {
    this.elementIndex = elementIndex;
    this.spec = spec;
  }

  @Override
  public Hash hash() {
    return readTupleElementAlgorithmHash(elementIndex);
  }

  @Override
  public Spec outputSpec() {
    return spec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Record> records = input.records();
    checkArgument(records.size() == 1);
    Tuple tuple = (Tuple) records.get(0);
    return new Output(tuple.get(elementIndex), nativeApi.messages());
  }
}
