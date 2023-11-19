package org.smoothbuild.run.eval.report;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.frontend.lang.base.location.Locations.unknownLocation;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.backend.BsMapping;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.execute.TraceB;

public class BsTraceTranslatorTest extends TestContext {
  private static final Hash HASH1 = Hash.of(1);
  private static final Hash HASH2 = Hash.of(2);
  private static final Hash HASH3 = Hash.of(3);
  private static final Hash HASH4 = Hash.of(4);
  private static final BsMapping BS_MAPPING = createBsMapping();
  private static final Hash UNKNOWN_HASH = Hash.of(17);

  @Test
  public void empty_trace() {
    var bsMapping = new BsMapping();
    var bsTraceTranslator = new BsTraceTranslator(bsMapping);
    assertThat(bsTraceTranslator.translate(new TraceB())).isEqualTo(traceS());
  }

  @Test
  public void one_elem_trace() {
    var bsTraceTranslator = new BsTraceTranslator(BS_MAPPING);
    var trace = traceB(HASH1, HASH2);
    assertThat(bsTraceTranslator.translate(trace)).isEqualTo(traceS("name2", location(1)));
  }

  @Test
  public void two_elem_trace() {
    var bsTraceTranslator = new BsTraceTranslator(BS_MAPPING);
    var trace = traceB(HASH3, HASH4, traceB(HASH1, HASH2));
    assertThat(bsTraceTranslator.translate(trace))
        .isEqualTo(traceS("name4", location(3), "name2", location(1)));
  }

  @Test
  public void trace_with_unknown_name() {
    var bsTraceTranslator = new BsTraceTranslator(BS_MAPPING);
    var trace = traceB(HASH3, HASH4, traceB(HASH1, UNKNOWN_HASH));
    assertThat(bsTraceTranslator.translate(trace))
        .isEqualTo(traceS("name4", location(3), "???", location(1)));
  }

  @Test
  public void trace_with_unknown_loc() {
    var bsTraceTranslator = new BsTraceTranslator(BS_MAPPING);
    var trace = traceB(HASH3, HASH4, traceB(UNKNOWN_HASH, HASH2));
    assertThat(bsTraceTranslator.translate(trace))
        .isEqualTo(traceS("name4", location(3), "name2", unknownLocation()));
  }

  private static BsMapping createBsMapping() {
    ImmutableMap<Hash, String> names = ImmutableMap.of(
        HASH1, "name1",
        HASH2, "name2",
        HASH3, "name3",
        HASH4, "name4");
    ImmutableMap<Hash, Location> locs = ImmutableMap.of(
        HASH1, location(1),
        HASH2, location(2),
        HASH3, location(3),
        HASH4, location(4));
    return new BsMapping(names, locs);
  }
}
