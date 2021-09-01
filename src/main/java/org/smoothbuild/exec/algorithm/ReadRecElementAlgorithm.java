package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readRecElementAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ReadRecElementAlgorithm extends Algorithm {
  private final int elementIndex;
  private final Spec spec;

  public ReadRecElementAlgorithm(int elementIndex, Spec outputSpec) {
    super(outputSpec);
    this.elementIndex = elementIndex;
    this.spec = outputSpec;
  }

  @Override
  public Hash hash() {
    return readRecElementAlgorithmHash(elementIndex);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ImmutableList<Obj> objects = input.objects();
    checkArgument(objects.size() == 1);
    Rec rec = (Rec) objects.get(0);
    return new Output(rec.get(elementIndex), nativeApi.messages());
  }
}
