package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.type.TestingTS;

import com.google.common.collect.ImmutableSet;

public class NormalizerSTest extends TestingTS {
  @Nested
  class _array_type {
    @Test
    public void lower() {
      assertThat(normalize(constrS(array(blob()), varA())))
          .isEqualTo(set(
              constrS(blob(), v0()),
              constrS(array(v0()), varA())
          ));
    }

    @Test
    public void upper() {
      assertThat(normalize(constrS(varA(), array(blob()))))
          .isEqualTo(set(
              constrS(varA(), array(v0())),
              constrS(v0(), blob())
          ));
    }

    @Test
    public void lower_with_array_as_elem() {
      assertThat(normalize(constrS(array(array(blob())), varA())))
          .isEqualTo(set(
              constrS(blob(), v0()),
              constrS(array(v0()), v1()),
              constrS(array(v1()), varA())
          ));
    }

    @Test
    public void upper_with_array_as_elem() {
      assertThat(normalize(constrS(varA(), array(array(blob())))))
          .isEqualTo(set(
              constrS(v0(), blob()),
              constrS(v1(), array(v0())),
              constrS(varA(), array(v1()))
          ));
    }
  }

  @TestInstance(PER_CLASS)
  @Nested
  class _base_type {
    @ParameterizedTest
    @MethodSource("lower_test_cases")
    public void lower(TypeS type) {
      assertThat(normalize(constrS(type, varA())))
          .isEqualTo(set(constrS(type, varA())));
    }

    public List<TypeS> lower_test_cases() {
      return baseTypes();
    }

    @ParameterizedTest
    @MethodSource("upper_test_cases")
    public void upper() {
      assertThat(normalize(constrS(varA(), blob())))
          .isEqualTo(set(constrS(varA(), blob())));
    }

    public List<TypeS> upper_test_cases() {
      return baseTypes();
    }
  }

  @Nested
  class _func {
    @Test
    public void lower() {
      assertThat(normalize(constrS(func(bool(), list(blob(), int_())), varA())))
          .isEqualTo(set(
              constrS(bool(), v0()),
              constrS(v1(), blob()),
              constrS(v2(), int_()),
              constrS(func(v0(), list(v1(), v2())), varA())
          ));
    }

    @Test
    public void upper() {
      assertThat(normalize(constrS(varA(), func(bool(), list(blob(), int_())))))
          .isEqualTo(set(
              constrS(v0(), bool()),
              constrS(blob(), v1()),
              constrS(int_(), v2()),
              constrS(varA(), func(v0(), list(v1(), v2())))
          ));
    }

    @Test
    public void lower_with_func_as_res() {
      var f2 = func(bool(), list(blob()));
      assertThat(normalize(constrS(func(f2, list(int_())), varA())))
          .isEqualTo(set(
              constrS(func(v0(), list(v1())), v2()),
              constrS(bool(), v0()),
              constrS(v1(), blob()),
              constrS(v3(), int_()),
              constrS(func(v2(), list(v3())), varA())
          ));
    }

    @Test
    public void upper_with_func_as_res() {
      var f2 = func(bool(), list(blob()));
      assertThat(normalize(constrS(varA(), func(f2, list(int_())))))
          .isEqualTo(set(
              constrS(v2(), func(v0(), list(v1()))),
              constrS(v0(), bool()),
              constrS(blob(), v1()),
              constrS(int_(), v3()),
              constrS(varA(), func(v2(), list(v3())))
          ));
    }

    @Test
    public void lower_with_func_as_parameter() {
      var f2 = func(bool(), list(blob()));
      assertThat(normalize(constrS(func(int_(), list(f2)), varA())))
          .isEqualTo(set(
              constrS(v3(), func(v1(), list(v2()))),
              constrS(v1(), bool()),
              constrS(blob(), v2()),
              constrS(int_(), v0()),
              constrS(func(v0(), list(v3())), varA())
          ));
    }

    @Test
    public void upper_with_func_as_parameter() {
      var f2 = func(bool(), list(blob()));
      assertThat(normalize(constrS(varA(), func(int_(), list(f2)))))
          .isEqualTo(set(
              constrS(func(v1(), list(v2())), v3()),
              constrS(bool(), v1()),
              constrS(v2(), blob()),
              constrS(v0(), int_()),
              constrS(varA(), func(v0(), list(v3())))
          ));
    }
  }

  @Nested
  class _struct_type {
    @Test
    public void lower() {
      StructTS struct = struct("MyType", nList());
      assertThat(normalize(constrS(struct, varA())))
          .isEqualTo(set(constrS(struct, varA())));
    }

    @Test
    public void upper() {
      StructTS struct = struct("MyType", nList());
      assertThat(normalize(constrS(varA(), struct)))
          .isEqualTo(set(constrS(varA(), struct)));
    }
  }

  @Nested
  class _var {
    @Test
    public void lower() {
      assertThat(normalize(constrS(varA(), varB())))
          .isEqualTo(set(constrS(varA(), varB())));
    }
  }

  private VarS v0() {
    return var("_0");
  }

  private VarS v1() {
    return var("_1");
  }

  private VarS v2() {
    return var("_2");
  }

  private VarS v3() {
    return var("_3");
  }

  private ImmutableSet<ConstrS> normalize(ConstrS constr) {
    return normalizer().normalize(constr);
  }

  private NormalizerS normalizer() {
    return new NormalizerS(FACTORY);
  }
}
