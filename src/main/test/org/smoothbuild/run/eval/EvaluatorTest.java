package org.smoothbuild.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.compile.BytecodeLoader;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.algorithm.NativeMethodLoader;

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
      var callS = callS(intTS(), refS(defFuncS));
      assertThat(evaluate(callS, oneBinding(defFuncS)))
          .isEqualTo(intB(7));
    }

    @Test
    public void call_with_result_conversion() {
      var defFuncS = defFuncS(arrayTS(nothingTS()), "n", nlist(), orderS(nothingTS()));
      var callS = callS(arrayTS(intTS()), refS(defFuncS));
      assertThat(evaluate(callS, oneBinding(defFuncS)))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void call_polymorphic() {
      var a = varA();
      var funcS = poly(defFuncS(
          arrayTS(a), "n", nlist(itemS(a, "e")), orderS(a, paramRefS(a, "e"))));
      var actualTS = funcTS(arrayTS(intTS()), list(intTS()));
      var callS = callS(arrayTS(intTS()), monoizeS(actualTS, refS(funcS)), intS(7));
      assertThat(evaluate(callS, oneBinding(funcS)))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }
  }

  @Nested
  class _func {
    @Test
    public void def_func() {
      var defFuncS = defFuncS("myFunc", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertThat(evaluate(refS(defFuncS), oneBinding(defFuncS)))
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

      var byteFuncS = poly(byteFuncS(className, varA(), "myFunc", nlist(itemS(varA(), "p"))));
      var actualFuncTS = funcTS(intTS(), list(intTS()));
      assertThat(evaluate(monoizeS(actualFuncTS, refS(byteFuncS)), oneBinding(byteFuncS)))
          .isEqualTo(funcB);
    }

    @Test
    public void synt_ctor() {
      var syntCtorS = syntCtorS(structTS("MyStruct", nlist(sigS(intTS(), "myField"))));
      assertThat(evaluate(refS(syntCtorS), oneBinding(syntCtorS)))
          .isEqualTo(funcB(list(intTB()), combineB(paramRefB(intTB(), 0))));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void invoke_argless() throws Exception {
      var funcS = natFuncS(intTS(), "f", nlist(), nativeS(1, stringS("class binary name")));
      var callS = callS(intTS(), refS(funcS));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS, oneBinding(funcS)))
          .isEqualTo(intB(173));
    }

    @Test
    public void invoke_with_param() throws Exception {
      var funcS = natFuncS(intTS(), "f", nlist(itemS(intTS(), "p")),
          nativeS(1, stringS("class binary name")));
      var callS = callS(intTS(), refS(funcS), intS(77));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS, oneBinding(funcS)))
          .isEqualTo(intB(77));
    }

    @Test
    public void invoke_with_param_conversion() throws Exception {
      var funcS = natFuncS(arrayTS(intTS()), "f", nlist(itemS(arrayTS(intTS()), "p")),
          nativeS(1, stringS("class binary name")));
      var callS = callS(arrayTS(intTS()), refS(funcS), orderS(nothingTS()));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnArrayParam", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS, oneBinding(funcS)))
          .isEqualTo(arrayB(intTB()));
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
    public void monoize() {
      var a = varA();
      var funcS = poly(defFuncS("n", nlist(itemS(a, "e")), paramRefS(a, "e")));
      var actualTS = funcTS(intTS(), list(intTS()));
      var monoizeS = monoizeS(actualTS, refS(funcS));

      assertThat(evaluate(monoizeS, oneBinding(funcS)))
          .isEqualTo(idFuncB());
    }
  }

  @Nested
  class _order {
    @Test
    public void order() {
      assertThat(evaluate(orderS(intTS(), intS(7))))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }

    @Test
    public void order_with_element_conversion() {
      assertThat(evaluate(orderS(arrayTS(intTS()), orderS(nothingTS()))))
          .isEqualTo(arrayB(arrayTB(intTB()), arrayB(intTB())));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() {
      var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, refS(syntCtorS), intS(7));
      assertThat(evaluate(selectS(intTS(), callS, "f"), oneBinding(syntCtorS)))
          .isEqualTo(intB(7));
    }

    @Test
    public void select_with_conversion() {
      var structTS = structTS("MyStruct", nlist(sigS(arrayTS(nothingTS()), "f")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, refS(syntCtorS), orderS(nothingTS()));
      assertThat(evaluate(selectS(arrayTS(intTS()), callS, "f"), oneBinding(syntCtorS)))
          .isEqualTo(arrayB(intTB()));
    }
  }

  private ObjB evaluate(MonoObjS objS) {
    return evaluate(objS, immutableBindings());
  }

  private ObjB evaluate(MonoObjS objS, ImmutableBindings<TopRefableS> topRefables) {
    var defsS = new DefsS(immutableBindings(), topRefables);
    var resultMap = newEvaluator().evaluate(defsS, list(objS)).get();
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(0);
  }

  private Evaluator newEvaluator() {
    var sbConverterProv = sbConverterProv(fileLoader, bytecodeLoader);
    var vmProv = vmProv(nativeMethodLoader);
    return new Evaluator(sbConverterProv, vmProv, reporter());
  }
}
