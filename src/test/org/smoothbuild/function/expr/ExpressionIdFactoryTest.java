package org.smoothbuild.function.expr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Test;

public class ExpressionIdFactoryTest {
  ExpressionIdFactory idFactory = new ExpressionIdFactory();

  @Test
  public void test() {
    assertThat(idFactory.createId("abc").resultDir()).isEqualTo(BUILD_DIR.append(path("0abc")));
    assertThat(idFactory.createId("def").resultDir()).isEqualTo(BUILD_DIR.append(path("1def")));
    assertThat(idFactory.createId("ghi").resultDir()).isEqualTo(BUILD_DIR.append(path("2ghi")));
  }
}
