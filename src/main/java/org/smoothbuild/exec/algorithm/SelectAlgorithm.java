package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class SelectAlgorithm extends Algorithm {
  private final int itemIndex;

  public SelectAlgorithm(int itemIndex, TypeV outputType) {
    super(outputType);
    this.itemIndex = itemIndex;
  }

  @Override
  public Hash hash() {
    return selectAlgorithmHash(itemIndex);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Val> objects = input.vals();
    checkArgument(objects.size() == 1);
    Tuple tuple = (Tuple) objects.get(0);
    return new Output(tuple.get(itemIndex), nativeApi.messages());
  }
}
