package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.record.db.RecordFactory;
import org.smoothbuild.lang.parse.Definitions;

public class ExpressionToTaskConverterProvider {
  private final RecordFactory recordFactory;

  @Inject
  public ExpressionToTaskConverterProvider(RecordFactory recordFactory) {
    this.recordFactory = recordFactory;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(definitions, recordFactory);
  }
}
