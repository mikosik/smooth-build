package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readRecElementAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadRecElementAlgorithm extends Algorithm {
  private final int elementIndex;

  public ReadRecElementAlgorithm(int elementIndex, ValSpec outputSpec) {
    super(outputSpec);
    this.elementIndex = elementIndex;
  }

  @Override
  public Hash hash() {
    return readRecElementAlgorithmHash(elementIndex);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Val> objects = input.vals();
    checkArgument(objects.size() == 1);
    Rec rec = (Rec) objects.get(0);
    return new Output(rec.get(elementIndex), nativeApi.messages());
  }
}
