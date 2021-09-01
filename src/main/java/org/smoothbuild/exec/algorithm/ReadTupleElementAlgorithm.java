package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readTupleElementAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadTupleElementAlgorithm extends Algorithm {
  private final int elementIndex;
  private final Spec spec;

  public ReadTupleElementAlgorithm(int elementIndex, Spec outputSpec) {
    super(outputSpec);
    this.elementIndex = elementIndex;
    this.spec = outputSpec;
  }

  @Override
  public Hash hash() {
    return readTupleElementAlgorithmHash(elementIndex);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Obj> objects = input.objects();
    checkArgument(objects.size() == 1);
    Tuple tuple = (Tuple) objects.get(0);
    return new Output(tuple.get(elementIndex), nativeApi.messages());
  }
}
