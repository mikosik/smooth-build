package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class SelectAlgorithm extends Algorithm {
  private final IntH index;

  public SelectAlgorithm(IntH index, TypeH outputT) {
    super(outputT);
    this.index = index;
  }

  @Override
  public Hash hash() {
    return selectAlgorithmHash(index);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    var vals = input.vals();
    checkArgument(vals.size() == 1);
    var tuple = (TupleH) vals.get(0);
    return new Output(tuple.get(index.toJ().intValue()), nativeApi.messages());
  }
}
