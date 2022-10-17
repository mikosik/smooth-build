package org.smoothbuild.vm.execute;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.compile.lang.base.Trace;
import org.smoothbuild.util.collect.Duo;

public class TraceB extends Trace<Duo<Hash, Hash>> {
  public TraceB(Hash enclosingEvaluable, Hash calledEvaluable) {
    this(enclosingEvaluable, calledEvaluable, null);
  }

  public TraceB(Hash enclosingEvaluable, Hash calledEvaluable, TraceB chain) {
    super(new Duo<>(enclosingEvaluable, calledEvaluable), chain);
  }
}
