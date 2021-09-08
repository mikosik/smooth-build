package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readRecItemAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadRecItemAlgorithm extends Algorithm {
  private final int itemIndex;

  public ReadRecItemAlgorithm(int itemIndex, ValSpec outputSpec) {
    super(outputSpec);
    this.itemIndex = itemIndex;
  }

  @Override
  public Hash hash() {
    return readRecItemAlgorithmHash(itemIndex);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Val> objects = input.vals();
    checkArgument(objects.size() == 1);
    Rec rec = (Rec) objects.get(0);
    return new Output(rec.get(itemIndex), nativeApi.messages());
  }
}
