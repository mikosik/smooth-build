package org.smoothbuild.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.expression.ExpressionId.BUILD_ROOT;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Test;

public class ExpressionIdFactoryTest {

  ExpressionIdFactory idFactory = new ExpressionIdFactory();

  @Test
  public void test() {
    assertThat(idFactory.createId("abc").resultDir()).isEqualTo(BUILD_ROOT.append(path("0abc")));
    assertThat(idFactory.createId("def").resultDir()).isEqualTo(BUILD_ROOT.append(path("1def")));
    assertThat(idFactory.createId("ghi").resultDir()).isEqualTo(BUILD_ROOT.append(path("2ghi")));
  }
}
