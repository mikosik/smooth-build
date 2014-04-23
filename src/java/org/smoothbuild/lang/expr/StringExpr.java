package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.STRING;

import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.StringWorker;
import org.smoothbuild.task.base.TaskWorker;
import org.smoothbuild.util.Empty;

public class StringExpr extends Expr<SString> {
  private final SString string;

  public StringExpr(SString string, CodeLocation codeLocation) {
    super(STRING, Empty.exprList(), codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public TaskWorker<SString> createWorker() {
    return new StringWorker(string, codeLocation());
  }
}
