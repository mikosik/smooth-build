package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.recAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateRecAlgorithm extends Algorithm {
  public CreateRecAlgorithm(RecSpec recSpec) {
    super(recSpec);
  }

  @Override
  public Hash hash() {
    return recAlgorithmHash(outputSpec());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Rec rec = nativeApi.factory().rec(((RecSpec) outputSpec()), input.objects());
    return new Output(rec, nativeApi.messages());
  }
}
