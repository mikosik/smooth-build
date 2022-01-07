package org.smoothbuild.vm.job.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.job.algorithm.AlgorithmHashes.pickAlgorithmHash;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;

public class PickAlgorithm extends Algorithm {
  public PickAlgorithm(TypeB outputT) {
    super(outputT);
  }

  @Override
  public Hash hash() {
    return pickAlgorithmHash();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    var vals = input.vals();
    checkArgument(vals.size() == 2);
    var array = (ArrayB) vals.get(0);
    var index = (IntB) vals.get(1);

    var elems = array.elems(ValB.class);
    int indexJ = index.toJ().intValue();
    if (indexJ < 0 || elems.size() <= indexJ) {
      nativeApi.log()
          .error("Index (" + indexJ + ") out of bounds. Array size = " + elems.size() + ".");
      return new Output(null, nativeApi.messages());
    }
    return new Output(elems.get(indexJ), nativeApi.messages());
  }
}
