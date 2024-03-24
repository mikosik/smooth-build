package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public class UnusedVarsGeneratorTest {
  @Test
  public void test() {
    var generator = new UnusedVarsGenerator(varSetS(new SVar("B"), new SVar("C")));
    assertThat(List.of(generator.next(), generator.next(), generator.next()))
        .isEqualTo(List.of(new SVar("A"), new SVar("D"), new SVar("E")));
  }
}
