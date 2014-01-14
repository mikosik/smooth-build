package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.task.base.Result;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

/**
 * Calculates hash of given function call (function + its arguments) so it can
 * be used as a key for referencing its cached result in {@link TaskDb}.
 */
public class CallHasher {
  private final Function function;
  private final Map<String, Result> args;

  public CallHasher(NativeFunction function, Map<String, Result> args) {
    this.function = function;
    this.args = args;
  }

  public HashCode hash() {

    //
    // Hash of given function call is calculated as a hash of byte array
    // created by concatenating byte representation of following hashes:
    // - hash of function being called
    // - for each argument (sorted lexicographically by parameter name):
    // ---- hash of parameter name
    // ---- hash of argument value
    //

    Hasher hasher = Hash.function().newHasher();
    hasher.putBytes(Hash.string(function.signature().name().value()).asBytes());

    // function.params() are sorted lexicographically
    for (Param param : function.params().values()) {
      Result argument = args.get(param.name());
      if (argument != null) {
        HashCode paramNameHash = param.nameHash();
        HashCode valueHash = argument.value().hash();

        hasher.putBytes(paramNameHash.asBytes());
        hasher.putBytes(valueHash.asBytes());
      }
    }
    return hasher.hash();
  }
}
