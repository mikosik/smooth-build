package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class DecodeSpecNodeException extends DecodeSpecException {
  public DecodeSpecNodeException(Hash hash, SpecKind spec, String path, String message) {
    super(buildMessage(hash, spec, path, message));
  }

  public DecodeSpecNodeException(Hash hash, SpecKind spec, String path) {
    super(buildMessage(hash, spec, path, null));
  }

  public DecodeSpecNodeException(Hash hash, SpecKind spec, String path, int index, Throwable e) {
    this(hash, spec, path + "[" + index + "]", e);
  }

  public DecodeSpecNodeException(Hash hash, SpecKind spec, String path, Throwable e) {
    super(buildMessage(hash, spec, path, null), e);
  }

  private static String buildMessage(Hash hash, SpecKind spec, String path, String message) {
    return "Cannot decode " + spec.name() + " spec at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
