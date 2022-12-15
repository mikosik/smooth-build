package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;

public class DecodeCatNodeExc extends DecodeCatExc {
  public DecodeCatNodeExc(Hash hash, CategoryKindB kind, String path, String message) {
    super(buildMessage(hash, kind, path, message));
  }

  public DecodeCatNodeExc(Hash hash, CategoryKindB kind, String path) {
    super(buildMessage(hash, kind, path, null));
  }

  public DecodeCatNodeExc(Hash hash, CategoryKindB kind, String path, int index, Throwable e) {
    this(hash, kind, path + "[" + index + "]", e);
  }

  public DecodeCatNodeExc(Hash hash, CategoryKindB kind, String path, Throwable e) {
    super(buildMessage(hash, kind, path, null), e);
  }

  private static String buildMessage(Hash hash, CategoryKindB kind, String path, String message) {
    return "Cannot decode " + kind.name() + " category at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
