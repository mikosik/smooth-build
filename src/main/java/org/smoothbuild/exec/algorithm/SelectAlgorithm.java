package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class SelectAlgorithm extends Algorithm {
  private final IntH index;

  public SelectAlgorithm(IntH index, TypeH outputType) {
    super(outputType);
    this.index = index;
  }

  @Override
  public Hash hash() {
    return selectAlgorithmHash(index);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<ValueH> objects = input.vals();
    checkArgument(objects.size() == 1);
    TupleH tuple = (TupleH) objects.get(0);
    return new Output(tuple.get(index.jValue().intValue()), nativeApi.messages());
  }
}
