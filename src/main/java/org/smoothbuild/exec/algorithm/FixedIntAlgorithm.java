package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedIntAlgorithmHash;

import java.math.BigInteger;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.val.IntSpec;
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
    Val val = nativeApi.factory().intValue(bigInteger);
    return new Output(val, nativeApi.messages());
  }
}
