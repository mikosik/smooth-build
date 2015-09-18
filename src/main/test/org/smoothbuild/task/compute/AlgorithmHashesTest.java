package org.smoothbuild.task.compute;

import static org.smoothbuild.task.compute.AlgorithmHashes.arrayAlgorithmHash;
import static org.smoothbuild.task.compute.AlgorithmHashes.constantAlgorithmHash;
import static org.smoothbuild.task.compute.AlgorithmHashes.identityAlgorithmHash;
import static org.smoothbuild.task.compute.AlgorithmHashes.nativeCallAlgorithmHash;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.HashSet;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class AlgorithmHashesTest {
  private HashSet<HashCode> hashes;
  private NativeFunction function;
  private Value value;

  @Test
  public void each_algorithm_has_different_hash() {
    given(hashes = new HashSet<>());
    given(function = mock(NativeFunction.class));
    given(willReturn(Hash.integer(0)), function).hash();
    given(value = mock(Value.class));
    given(willReturn(Hash.integer(0)), value).hash();
    given(hashes.add(constantAlgorithmHash(value)));
    given(hashes.add(arrayAlgorithmHash()));
    given(hashes.add(identityAlgorithmHash()));
    given(hashes.add(nativeCallAlgorithmHash(function)));
    when(hashes).size();
    thenReturned(4);
  }
}
