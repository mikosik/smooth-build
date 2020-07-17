package org.smoothbuild.record.spec;

import org.smoothbuild.record.db.RecordDb;

public class BoolSpecTest extends AbstractRecordSpecTestCase {
  @Override
  protected Spec getSpec(RecordDb recordDb) {
    return recordDb.boolSpec();
  }
}
