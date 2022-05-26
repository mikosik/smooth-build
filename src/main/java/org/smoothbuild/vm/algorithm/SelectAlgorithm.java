package org.smoothbuild.vm.algorithm;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;
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
  public Output run(TupleB input, NativeApi nativeApi) {
    var vals = input.items();
    checkArgument(vals.size() == 2);
    var tuple = selectable(vals);
    var index = index(vals);
    return new Output(tuple.get(index.toJ().intValue()), nativeApi.messages());
  }

  private TupleB selectable(ImmutableList<CnstB> cnsts) {
    return (TupleB) cnsts.get(0);
  }

  private IntB index(ImmutableList<CnstB> cnsts) {
    return (IntB) cnsts.get(1);
  }
}
