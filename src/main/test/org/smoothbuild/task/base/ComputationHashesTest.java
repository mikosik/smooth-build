package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.task.base.ComputationHashes.arrayComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.identityComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.nativeCallComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.valueComputationHash;
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

public class ComputationHashesTest {
  private HashSet<HashCode> hashes;
  private NativeFunction function;
  private NativeFunction function2;
  private Value value;
  private Value value2;

  @Test
  public void each_computation_has_different_hash() {
    given(hashes = new HashSet<>());
    given(function = mock(NativeFunction.class));
    given(willReturn(Hash.integer(0)), function).hash();
    given(value = mock(Value.class));
    given(willReturn(Hash.integer(0)), value).hash();
    given(hashes.add(valueComputationHash(value)));
    given(hashes.add(arrayComputationHash()));
    given(hashes.add(identityComputationHash()));
    given(hashes.add(nativeCallComputationHash(function)));
    when(hashes).size();
    thenReturned(4);
  }

  @Test
  public void value_computation_has_different_hash_for_different_values() throws Exception {
    given(value = mock(Value.class));
    given(willReturn(Hash.integer(1)), value).hash();
    given(value2 = mock(Value.class));
    given(willReturn(Hash.integer(2)), value2).hash();
    when(valueComputationHash(value));
    thenReturned(not(valueComputationHash(value2)));
  }

  @Test
  public void native_call_computation_has_different_hash_for_different_functions()
      throws Exception {
    given(function = mock(NativeFunction.class));
    given(willReturn(Hash.integer(1)), function).hash();
    given(function2 = mock(NativeFunction.class));
    given(willReturn(Hash.integer(2)), function2).hash();
    when(nativeCallComputationHash(function));
    thenReturned(not(nativeCallComputationHash(function2)));
  }
}
