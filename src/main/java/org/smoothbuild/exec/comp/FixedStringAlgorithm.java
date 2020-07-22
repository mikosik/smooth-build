package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.exec.task.base.Task.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.spec.Spec;
import org.smoothbuild.record.spec.StringSpec;

public class FixedStringAlgorithm implements Algorithm {
  private final StringSpec stringSpec;
  private final String string;
  private final String shortedString;

  public FixedStringAlgorithm(StringSpec stringSpec, String string) {
    this.stringSpec = stringSpec;
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
  public Spec type() {
    return stringSpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    SString sstring = nativeApi
        .factory()
        .string(string);
    return new Output(sstring, nativeApi.messages());
  }
}
