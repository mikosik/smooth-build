package org.smoothbuild.record.spec;

import org.smoothbuild.record.db.RecordDb;

public class StringArrayArraySpecTest extends AbstractRecordSpecTestCase {
  @Override
  protected Spec getSpec(RecordDb recordDb) {
    return recordDb.arraySpec(recordDb.arraySpec(recordDb.stringSpec()));
  }
}
