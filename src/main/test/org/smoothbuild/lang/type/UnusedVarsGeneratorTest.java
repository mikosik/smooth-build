package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.type.VarSetS.varSetS;

import java.util.List;

import org.junit.jupiter.api.Test;

public class UnusedVarsGeneratorTest {
  @Test
  public void test() {
    var generator = new UnusedVarsGenerator(varSetS(new VarS("B"), new VarS("C")));
    assertThat(List.of(generator.next(), generator.next(), generator.next()))
        .isEqualTo(List.of(new VarS("A"), new VarS("D"), new VarS("E")));
  }
}
