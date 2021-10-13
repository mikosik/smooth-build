package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;

public class DecodeStructWrongTupleSpecException extends DecodeObjException {
  public DecodeStructWrongTupleSpecException(Hash hash, StructSpec expected, RecSpec actual) {
    super(buildMessage(hash, expected, actual));
  }

  private static String buildMessage(Hash hash, StructSpec spec, RecSpec actual) {
    return (
        "Cannot decode %s object at %s. Its fields should match %s spec while their spec is %s.")
        .formatted(spec.name(), hash, spec.rec().name(), actual.name());
  }
}
