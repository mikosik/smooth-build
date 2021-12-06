package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class SelectAlgorithm extends Algorithm {
  public SelectAlgorithm(TypeH outputT) {
    super(outputT);
  }

  @Override
  public Hash hash() {
    return selectAlgorithmHash();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    var vals = input.vals();
    checkArgument(vals.size() == 2);
    var tuple = selectable(vals);
    var index = index(vals);
    return new Output(tuple.get(index.toJ().intValue()), nativeApi.messages());
  }

  private TupleH selectable(ImmutableList<ValH> vals) {
    return (TupleH) vals.get(0);
  }

  private IntH index(ImmutableList<ValH> vals) {
    return (IntH) vals.get(1);
  }
}
