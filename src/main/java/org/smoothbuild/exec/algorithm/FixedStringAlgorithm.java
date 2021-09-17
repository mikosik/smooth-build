package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.exec.compute.TaskInfo.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class FixedStringAlgorithm extends Algorithm {
  private final String string;
  private final String shortedString;

  public FixedStringAlgorithm(StrSpec strSpec, String string) {
    super(strSpec);
    this.string = string;
    this.shortedString = escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }

  public String shortedString() {
    return shortedString;
  }

  @Override
  public Hash hash() {
    return fixedStringAlgorithmHash(string);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Str str = nativeApi
        .factory()
        .string(string);
    return new Output(str, nativeApi.messages());
  }
}
