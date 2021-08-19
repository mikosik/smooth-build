package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedIntAlgorithmHash;

import java.math.BigInteger;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.spec.IntSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class FixedIntAlgorithm extends Algorithm {
  private final BigInteger bigInteger;

  public FixedIntAlgorithm(IntSpec intSpec, BigInteger bigInteger) {
    super(intSpec);
    this.bigInteger = bigInteger;
  }

  @Override
  public Hash hash() {
    return fixedIntAlgorithmHash(bigInteger);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Obj obj = nativeApi.factory().int_(bigInteger);
    return new Output(obj, nativeApi.messages());
  }
}
