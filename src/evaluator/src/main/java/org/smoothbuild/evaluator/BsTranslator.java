package org.smoothbuild.evaluator;

import static org.smoothbuild.common.log.location.Locations.unknownLocation;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerbackend.BsMapping;
import org.smoothbuild.compilerfrontend.lang.define.STrace;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace.Line;

@Singleton
public class BsTranslator {
  private volatile BsMapping bsMapping;

  @Inject
  public BsTranslator() {}

  public void setBsMapping(BsMapping bsMapping) {
    this.bsMapping = bsMapping;
  }

  public STrace translate(BTrace bTrace) {
    return new STrace(translate(bTrace.topLine()));
  }

  private STrace.Line translate(Line headLine) {
    if (headLine == null) {
      return null;
    } else {
      var name = nameFor(headLine.called());
      var location = locationFor(headLine.call());
      var next = translate(headLine.next());
      return new STrace.Line(name, location, next);
    }
  }

  public String nameFor(Hash bExprHash) {
    return bsMapping.nameMapping().getOrDefault(bExprHash, "???");
  }

  private Location locationFor(Hash hash) {
    return bsMapping.locMapping().getOrDefault(hash, unknownLocation());
  }
}
