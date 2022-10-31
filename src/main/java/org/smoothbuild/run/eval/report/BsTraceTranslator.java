package org.smoothbuild.run.eval.report;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.vm.execute.TraceB;

public class BsTraceTranslator {
  private final BsMapping bsMapping;

  public BsTraceTranslator(BsMapping bsMapping) {
    this.bsMapping = bsMapping;
  }

  public TraceS translate(TraceB traceB) {
    if (traceB == null) {
      return null;
    } else {
      var raw = translateRaw(traceB);
      var called = traceB.called();
      return new TraceS(nameFor(called), locFor(called), raw);
    }
  }

  public TraceS translateRaw(TraceB trace) {
    if (trace == null) {
      return null;
    } else {
      var tailB = trace.tail();
      var tailS = translateRaw(tailB);
      var tag = tailB == null ? "" : nameFor(tailB.called());
      var loc = locFor(trace.call());
      return new TraceS(tag, loc, tailS);
    }
  }

  private String nameFor(Hash funcHash) {
    return bsMapping.nameMapping().getOrDefault(funcHash, "???");
  }

  private Loc locFor(Hash hash) {
    return bsMapping.locMapping().getOrDefault(hash, Loc.unknown());
  }
}
