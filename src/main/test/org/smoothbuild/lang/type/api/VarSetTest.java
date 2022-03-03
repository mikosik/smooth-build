package org.smoothbuild.lang.type.api;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class VarSetTest {
  @Test
  public void as_list() {
    var varSet = new VarSet<>(Set.of(var("B"), var("C"), var("A")));
    assertThat(varSet.asList())
        .isEqualTo(list(var("A"), var("B"), var("C")));
  }

  @Test
  public void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(
            new VarSet<>(Set.of()),
            new VarSet<>(Set.of()))
        .addEqualityGroup(
            new VarSet<>(Set.of(var("A"))),
            new VarSet<>(Set.of(var("A"))))
        .addEqualityGroup(
            new VarSet<>(Set.of(var("A"), var("B"))),
            new VarSet<>(Set.of(var("A"), var("B"))))
        .addEqualityGroup(
            new VarSet<>(Set.of(var("C"), var("A"), var("B"))),
            new VarSet<>(Set.of(var("A"), var("B"), var("C"))),
            new VarSet<>(Set.of(var("C"), var("A"), var("B"))))
        .testEquals();
  }

  @Test
  public void to_string() {
    var varSet = new VarSet<>(Set.of(var("C"), var("A"), var("B")));
    assertThat(varSet.toString())
        .isEqualTo("<A,B,C>");
  }

  private MyVar var(String name) {
    return new MyVar(name);
  }

  private record MyVar(String name) implements VarT {
    @Override
    public VarSet<?> vars() {
      return null;
    }
  }
}
