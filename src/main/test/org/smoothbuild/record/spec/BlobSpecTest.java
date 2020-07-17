package org.smoothbuild.record.spec;

import org.smoothbuild.record.db.RecordDb;

public class BlobSpecTest extends AbstractRecordSpecTestCase {
  @Override
  protected Spec getSpec(RecordDb recordDb) {
    return recordDb.blobSpec();
  }
}
