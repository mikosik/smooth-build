package org.smoothbuild.vm.job.algorithm;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class SelectAlgorithm extends Algorithm {
  public SelectAlgorithm(TypeB outputT) {
    super(outputT);
  }

  @Override
  public Hash hash() {
    return AlgorithmHashes.selectAlgorithmHash();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    var vals = input.vals();
    checkArgument(vals.size() == 2);
    var tuple = selectable(vals);
    var index = index(vals);
    return new Output(tuple.get(index.toJ().intValue()), nativeApi.messages());
  }

  private TupleB selectable(ImmutableList<ValB> vals) {
    return (TupleB) vals.get(0);
  }

  private IntB index(ImmutableList<ValB> vals) {
    return (IntB) vals.get(1);
  }
}
