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
    return new TraceS(translate(traceB.elements()));
  }

  private TraceS.Element translate(TraceB.Element headElement) {
    if (headElement == null) {
      return null;
    } else {
      var name = nameFor(headElement.called());
      var location = locationFor(headElement.call());
      var tailS = translate(headElement.tail());
      return new TraceS.Element(name, location, tailS);
    }
  }

  private String nameFor(Hash funcHash) {
    return bsMapping.nameMapping().getOrDefault(funcHash, "???");
  }

  private Location locationFor(Hash hash) {
    return bsMapping.locMapping().getOrDefault(hash, unknownLocation());
  }
}
