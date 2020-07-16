package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.exec.task.base.Task.NAME_LENGTH_LIMIT;
import static org.smoothbuild.exec.task.base.TaskKind.LITERAL;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.type.BinaryType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.plugin.NativeApi;

public class FixedStringAlgorithm implements Algorithm {
  private final StringType stringType;
  private final String string;
  private final String shortedString;

  public FixedStringAlgorithm(StringType stringType, String string) {
    this.stringType = stringType;
    this.string = string;
    this.shortedString = escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }

  @Override
  public TaskKind kind() {
    return LITERAL;
  }

  public String shortedString() {
    return shortedString;
  }

  @Override
  public Hash hash() {
    return fixedStringAlgorithmHash(string);
  }

  @Override
  public BinaryType type() {
    return stringType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    SString sstring = nativeApi
        .factory()
        .string(string);
    return new Output(sstring, nativeApi.messages());
  }
}
