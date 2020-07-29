package org.smoothbuild.db.record.spec;

import org.smoothbuild.db.record.db.RecordDb;

public class StringArraySpecTest extends AbstractRecordSpecTestCase {
  @Override
  protected Spec getSpec(RecordDb recordDb) {
    return recordDb.arraySpec(recordDb.stringSpec());
  }
}
