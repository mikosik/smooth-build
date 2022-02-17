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
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.TopEvalS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.type.impl.StructTS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Result;
import org.smoothbuild.vm.job.algorithm.NativeMethodLoader;

public class EvaluatorTest  extends TestingContext {
  private final FileLoader fileLoader = mock(FileLoader.class);
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);

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
  class _combine {
    @Test
    public void combine() {
      StructTS type = structTS("n", nList(sigS(intTS(), "f")));
      assertThat(evaluate(combineS(type, intS(7))))
          .isEqualTo(tupleB(tupleTB(intTB()), intB(7)));
    }

    @Test
    public void combine_with_item_conversion() {
      StructTS type = structTS("n", nList(sigS(arrayTS(intTS()), "f")));
      assertThat(evaluate(combineS(type, orderS(nothingTS()))))
          .isEqualTo(tupleB(tupleTB(arrayTB(intTB())), arrayB(intTB())));
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
          .thenReturn(Result.of(EvaluatorTest.class.getMethod("returnInt", NativeApi.class)));
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
          .thenReturn(Result.of(
              EvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, IntB.class)));
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
          .thenReturn(Result.of(
              EvaluatorTest.class.getMethod("returnArrayParam", NativeApi.class, ArrayB.class)));
      assertThat(evaluate(callS, nList(funcS)))
          .isEqualTo(arrayB(intTB()));
    }
  }

  public static IntB returnInt(NativeApi nativeApi) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, IntB param) {
    return param;
  }

  public static ArrayB returnArrayParam(NativeApi nativeApi, ArrayB param) {
    return param;
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
      StructTS type = structTS("n", nList(sigS(intTS(), "f")));
      var combineS = combineS(type, intS(7));
      assertThat(evaluate(selectS(intTS(), combineS, "f")))
          .isEqualTo(intB(7));
    }

    @Test
    public void select_with_conversion() {
      StructTS type = structTS("n", nList(sigS(arrayTS(nothingTS()), "f")));
      var combineS = combineS(type, orderS(nothingTS()));
      assertThat(evaluate(selectS(arrayTS(intTS()), combineS, "f")))
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
    return new Evaluator(compilerProv(fileLoader), vmProv(nativeMethodLoader), reporter());
  }
}
