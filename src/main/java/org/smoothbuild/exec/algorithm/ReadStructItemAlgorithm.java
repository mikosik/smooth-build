package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readStructItemAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadStructItemAlgorithm extends Algorithm {
  private final int itemIndex;

  public ReadStructItemAlgorithm(int itemIndex, ValType outputType) {
    super(outputType);
    this.itemIndex = itemIndex;
  }

  @Override
  public Hash hash() {
    return readStructItemAlgorithmHash(itemIndex);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Val> objects = input.vals();
    checkArgument(objects.size() == 1);
    Struc_ struct = (Struc_) objects.get(0);
    return new Output(struct.get(itemIndex), nativeApi.messages());
  }
}
