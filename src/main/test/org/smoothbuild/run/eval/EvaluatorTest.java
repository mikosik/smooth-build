package org.smoothbuild.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.compile.BytecodeLoader;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.TopEvalS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.job.algorithm.NativeMethodLoader;

public class EvaluatorTest  extends TestingContext {
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
    public void bool() {
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
      var defFuncS = defFuncS("n", nList(), intS(7));
      var callS = callS(intTS(), topRefS(defFuncS));
      assertThat(evaluate(callS, nList(defFuncS)))
          .isEqualTo(intB(7));
    }

    @Test
    public void call_with_result_conversion() {
      var defFuncS = defFuncS(arrayTS(nothingTS()), "n", nList(), orderS(nothingTS()));
      var callS = callS(arrayTS(intTS()), topRefS(defFuncS));
      assertThat(evaluate(callS, nList(defFuncS)))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void call_polymorphic() {
      var a = oVarTS("A");
      var ca = close(a);
      var funcS = defFuncS(arrayTS(a), "n", nList(itemS(a, "e")), orderS(ca, paramRefS(ca, "e")));
      var callS = callS(arrayTS(intTS()), topRefS(funcS), intS(7));
      assertThat(evaluate(callS, nList(funcS)))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }
  }

  @Nested
  class _func {
    @Test
    public void def_func() {
      var defFuncS = defFuncS("myFunc", nList(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertThat(evaluate(topRefS(defFuncS), nList(defFuncS)))
          .isEqualTo(funcB(list(intTB()), paramRefB(intTB(), 0)));
    }

    @Test
    public void ann_func() throws Exception {
      var jar = blobB(123);
      var className = ReturnIdFunc.class.getCanonicalName();
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jar);
      when(bytecodeLoader.load("myFunc", jar, className))
          .thenReturn(Try.result(ReturnIdFunc.bytecode(bytecodeF())));

      var byteFuncS = byteFuncS(className, oVarTS("A"), "myFunc", nList(itemS(oVarTS("A"), "p")));
      assertThat(evaluate(topRefS(byteFuncS), nList(byteFuncS)))
          .isEqualTo(ReturnIdFunc.bytecode(bytecodeF()));
    }

    @Test
    public void synt_ctor() {
      var syntCtorS = syntCtorS(structTS("MyStruct", nList(sigS(intTS(), "myField"))));
      assertThat(evaluate(topRefS(syntCtorS), nList(syntCtorS)))
          .isEqualTo(funcB(list(intTB()), combineB(paramRefB(intTB(), 0))));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void invoke_argless() throws Exception {
      var funcS = natFuncS(intTS(), "f", nList(), nativeS(1, stringS("class binary name")));
      var callS = callS(intTS(), topRefS(funcS));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS, nList(funcS)))
          .isEqualTo(intB(173));
    }

    @Test
    public void invoke_with_param() throws Exception {
      var funcS = natFuncS(intTS(), "f", nList(itemS(intTS(), "p")),
          nativeS(1, stringS("class binary name")));
      var callS = callS(intTS(), topRefS(funcS), intS(77));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS, nList(funcS)))
          .isEqualTo(intB(77));
    }

    @Test
    public void invoke_with_param_conversion() throws Exception {
      var funcS = natFuncS(arrayTS(intTS()), "f", nList(itemS(arrayTS(intTS()), "p")),
          nativeS(1, stringS("class binary name")));
      var callS = callS(arrayTS(intTS()), topRefS(funcS), orderS(nothingTS()));
      var jarB = blobB(137);
      when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
          .thenReturn(jarB);
      when(nativeMethodLoader.load(any(), any()))
          .thenReturn(Try.result(
              EvaluatorTest.class.getMethod("returnArrayParam", NativeApi.class, TupleB.class)));
      assertThat(evaluate(callS, nList(funcS)))
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
      var structTS = structTS("MyStruct", nList(sigS(intTS(), "f")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, topRefS(syntCtorS), intS(7));
      assertThat(evaluate(selectS(intTS(), callS, "f"), nList(syntCtorS)))
          .isEqualTo(intB(7));
    }

    @Test
    public void select_with_conversion() {
      var structTS = structTS("MyStruct", nList(sigS(arrayTS(nothingTS()), "f")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, topRefS(syntCtorS), orderS(nothingTS()));
      assertThat(evaluate(selectS(arrayTS(intTS()), callS, "f"), nList(syntCtorS)))
          .isEqualTo(arrayB(intTB()));
    }
  }

  private ObjB evaluate(ExprS exprS) {
    return evaluate(exprS, nList());
  }

  private ObjB evaluate(ExprS exprS, NList<TopEvalS> topEvals) {
    var defsS = new DefsS(nList(), topEvals);
    var resultMap = newEvaluator().evaluate(defsS, list(exprS)).get();
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(0);
  }

  private Evaluator newEvaluator() {
    var compilerProv = compilerProv(fileLoader, bytecodeLoader);
    var vmProv = vmProv(nativeMethodLoader);
    return new Evaluator(compilerProv, vmProv, reporter());
  }
}
