package org.smoothbuild.virtualmachine.dagger;

import static org.smoothbuild.common.testing.TestingInitializer.runInitializer;

import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;

public class VmTestContext implements VmTestApi {
  private final VmTestComponent component;

  public VmTestContext() {
    this.component = DaggerVmTestComponent.create();
    runInitializer(component);
  }

  @Override
  public VmTestComponent provide() {
    return component;
  }

  public BExprDb exprDbOther() {
    return new BExprDb(provide().hashedDb(), kindDbOther());
  }

  public BKindDb kindDbOther() {
    return new BKindDb(provide().hashedDb());
  }
}
