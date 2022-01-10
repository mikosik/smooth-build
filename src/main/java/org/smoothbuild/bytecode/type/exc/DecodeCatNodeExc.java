package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.db.Hash;

public class DecodeCatNodeExc extends DecodeCatExc {
  public DecodeCatNodeExc(Hash hash, CatKindB kind, String path, String message) {
    super(buildMessage(hash, kind, path, message));
  }

  public DecodeCatNodeExc(Hash hash, CatKindB kind, String path) {
    super(buildMessage(hash, kind, path, null));
  }

  public DecodeCatNodeExc(Hash hash, CatKindB kind, String path, int index, Throwable e) {
    this(hash, kind, path + "[" + index + "]", e);
  }

  public DecodeCatNodeExc(Hash hash, CatKindB kind, String path, Throwable e) {
    super(buildMessage(hash, kind, path, null), e);
  }

  private static String buildMessage(Hash hash, CatKindB kind, String path, String message) {
    return "Cannot decode " + kind.name() + " type at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}