package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.location.Locations.unknownLocation;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.log.report.BsMapping;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public class BsTranslatorTest extends FrontendCompilerTestContext {
  private static final Hash HASH1 = Hash.of(1);
  private static final Hash HASH2 = Hash.of(2);
  private static final Hash HASH3 = Hash.of(3);
  private static final Hash HASH4 = Hash.of(4);
  private static final Hash UNKNOWN_HASH = Hash.of(17);

  @Test
  void empty_trace() {
    var bsMapping = new BsMapping();
    var bsTraceTranslator = newBsTranslator(bsMapping);
    assertThat(bsTraceTranslator.translate(new BTrace())).isEqualTo(sTrace());
  }

  @Test
  void one_elem_trace() {
    var bsTraceTranslator = newBsTranslator(bsMapping());
    var trace = bTrace(HASH1, HASH2);
    assertThat(bsTraceTranslator.translate(trace)).isEqualTo(sTrace("name2", location(1)));
  }

  @Test
  void two_elem_trace() {
    var bsTraceTranslator = newBsTranslator(bsMapping());
    var trace = bTrace(HASH3, HASH4, bTrace(HASH1, HASH2));
    assertThat(bsTraceTranslator.translate(trace))
        .isEqualTo(sTrace("name4", location(3), "name2", location(1)));
  }

  @Test
  void trace_with_unknown_name() {
    var bsTraceTranslator = newBsTranslator(bsMapping());
    var trace = bTrace(HASH3, HASH4, bTrace(HASH1, UNKNOWN_HASH));
    assertThat(bsTraceTranslator.translate(trace))
        .isEqualTo(sTrace("name4", location(3), "???", location(1)));
  }

  @Test
  void trace_with_unknown_loc() {
    var bsTraceTranslator = newBsTranslator(bsMapping());
    var trace = bTrace(HASH3, HASH4, bTrace(UNKNOWN_HASH, HASH2));
    assertThat(bsTraceTranslator.translate(trace))
        .isEqualTo(sTrace("name4", location(3), "name2", unknownLocation()));
  }

  private BsMapping bsMapping() {
    Map<Hash, String> names = map(
        HASH1, "name1",
        HASH2, "name2",
        HASH3, "name3",
        HASH4, "name4");
    Map<Hash, Location> locations = map(
        HASH1, location(1),
        HASH2, location(2),
        HASH3, location(3),
        HASH4, location(4));
    return new BsMapping(names, locations);
  }

  private static BsTranslator newBsTranslator(BsMapping bsMapping) {
    var bsTranslator = new BsTranslator();
    bsTranslator.setBsMapping(bsMapping);
    return bsTranslator;
  }
}
