package org.smoothbuild.run.eval.report;

import static org.smoothbuild.compile.fs.lang.base.location.Locations.unknownLocation;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.define.TraceS;
import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.execute.TraceB;

public class BsTraceTranslator {
  private final BsMapping bsMapping;

  public BsTraceTranslator(BsMapping bsMapping) {
    this.bsMapping = bsMapping;
  }

  public TraceS translate(TraceB traceB) {
    var elements = traceB.elements();
    if (elements == null) {
      return new TraceS();
    } else {
      var raw = translateRaw(elements);
      var called = elements.called();
      return new TraceS(nameFor(called), locFor(called), raw);
    }
  }

  public TraceS translateRaw(TraceB.Element elements) {
    if (elements == null) {
      return new TraceS();
    } else {
      var tailB = elements.tail();
      var tailS = translateRaw(tailB);
      var name = tailB == null ? "" : nameFor(tailB.called());
      var location = locFor(elements.call());
      return new TraceS(name, location, tailS);
    }
  }

  private String nameFor(Hash funcHash) {
    return bsMapping.nameMapping().getOrDefault(funcHash, "???");
  }

  private Location locFor(Hash hash) {
    return bsMapping.locMapping().getOrDefault(hash, unknownLocation());
  }
}
