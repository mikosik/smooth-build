package org.smoothbuild.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.sb.BytecodeLoader;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.task.NativeMethodLoader;

import com.google.common.collect.ImmutableMap;

public class EvaluatorTest  extends TestContext {
  private final FileLoader fileLoader = mock(FileLoader.class);
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);
  private final BytecodeLoader bytecodeLoader = mock(BytecodeLoader.class);

  @Nested
  class _values {
    @Test
    public void blob() {
      assertThat(evaluate(blobS(7)))
          .isEqualTo(blobB(7));
    }

    @Test
    public void int_() {
      assertThat(evaluate(intS(8)))
          .isEqualTo(intB(8));
    }

    @Test
    public void string() {
      assertThat(evaluate(stringS("abc")))
          .isEqualTo(stringB("abc"));
    }
  }

  @Nested
  class _call {
    @Test
    public void call() {
      var defFuncS = defFuncS("n", nlist(), intS(7));
      var callS = callS(intTS(), defFuncS);
      assertThat(evaluate(callS))
          .isEqualTo(intB(7));
    }

    @Test
    public void call_polymorphic() {
      var a = varA();
      var funcS = polyDefFuncS(arrayTS(a), "n", nlist(itemS(a, "e")), orderS(a, refS(a, "e")));
      var callS = callS(arrayTS(intTS()), monoizeS(varMap(a, intTS()), funcS), intS(7));
      assertThat(evaluate(callS))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }

    @Test
    public void native_call_argless() throws Exception {
      var funcS = natFuncS(intTS(), "f", nlist(), natAnnS(1, stringS("class binary name")));
      var callS = callS(intTS(), funcS);
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS))
          .isEqualTo(intB(173));
    }

    @Test
    public void native_call_with_param() throws Exception {
      var funcS = natFuncS(intTS(), "f", nlist(itemS(intTS(), "p")),
          natAnnS(1, stringS("class binary name")));
      var callS = callS(intTS(), funcS, intS(77));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS))
          .isEqualTo(intB(77));
    }
  }

  @Nested
  class _func {
    @Test
    public void def_func() {
      assertThat(evaluate(intIdFuncS()))
          .isEqualTo(idFuncB());
    }

    @Test
    public void ann_func() throws Exception {
      var jar = blobB(123);
      var className = ReturnIdFunc.class.getCanonicalName();
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jar);
      var varMap = ImmutableMap.<String, TypeB>of("A", intTB());
      var funcB = ReturnIdFunc.bytecode(bytecodeF(), varMap);
      when(bytecodeLoader.load("myFunc", jar, className, varMap))
          .thenReturn(Try.result(funcB));

      var a = varA();
      var byteFuncS = polyByteFuncS(className, a, "myFunc", nlist(itemS(a, "p")));
      assertThat(evaluate(monoizeS(varMap(a, intTS()), byteFuncS)))
          .isEqualTo(funcB);
    }

    @Test
    public void synt_ctor() {
      var syntCtorS = syntCtorS(structTS("MyStruct", nlist(sigS(intTS(), "myField"))));
      assertThat(evaluate(syntCtorS))
          .isEqualTo(defFuncB(list(intTB()), combineB(refB(intTB(), 0))));
    }
  }

  public static IntB returnInt(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }

  public static ArrayB returnArrayParam(NativeApi nativeApi, TupleB args) {
    return (ArrayB) args.get(0);
  }

  @Nested
  class _monoize {
    @Test
    public void monoize_poly_func() {
      var a = varA();
      var funcS = polyDefFuncS("n", nlist(itemS(a, "e")), refS(a, "e"));
      var monoizeS = monoizeS(varMap(a, intTS()), funcS);
      assertThat(evaluate(monoizeS))
          .isEqualTo(idFuncB());
    }

    @Test
    public void monoize_poly_val() {
      var a = varA();
      var val = polyDefValS(1, arrayTS(a), "name", orderS(a));
      var monoizeS = monoizeS(varMap(a, intTS()), val);
      assertThat(evaluate(monoizeS))
          .isEqualTo(arrayB(intTB()));
    }
  }

  @Nested
  class _order {
    @Test
    public void order() {
      assertThat(evaluate(orderS(intTS(), intS(7), intS(8))))
          .isEqualTo(arrayB(intTB(), intB(7), intB(8)));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() {
      var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, syntCtorS, intS(7));
      assertThat(evaluate(selectS(intTS(), callS, "f")))
          .isEqualTo(intB(7));
    }
  }

  private ExprB evaluate(ExprS exprS) {
    var resultMap = newEvaluator().evaluate(list(exprS)).get();
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(0);
  }

  private Evaluator newEvaluator() {
    var sbConverterProv = sbTranslatorProv(fileLoader, bytecodeLoader);
    var vm = vm(nativeMethodLoader);
    return new Evaluator(sbConverterProv, vm, reporter());
  }
}
