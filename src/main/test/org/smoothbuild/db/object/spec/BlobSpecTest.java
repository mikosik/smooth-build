package org.smoothbuild.db.object.spec;

import org.smoothbuild.db.object.db.ObjectDb;

public class BlobSpecTest extends AbstractObjectSpecTestCase {
  @Override
  protected Spec getSpec(ObjectDb objectDb) {
    return objectDb.blobSpec();
  }
}
