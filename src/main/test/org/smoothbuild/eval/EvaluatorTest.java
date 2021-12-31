package org.smoothbuild.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

public class EvaluatorTest  extends TestingContext {
  @Nested
  class _values {
    @Test
    public void blob() throws Exception {
      assertThat(evaluate(blobS(7)))
          .isEqualTo(blobB(7));
    }

    @Test
    public void bool() throws Exception {
      assertThat(evaluate(intS(8)))
          .isEqualTo(intB(8));
    }

    @Test
    public void string() throws Exception {
      assertThat(evaluate(stringS("abc")))
          .isEqualTo(stringB("abc"));
    }
  }

  @Nested
  class _call {
    @Test
    public void call() throws Exception {
      var defFuncS = defFuncS("n", nList(), intS(7));
      var callS = callS(intTS(), topRefS(defFuncS));
      assertThat(evaluate(callS, defFuncS))
          .isEqualTo(intB(7));
    }

    @Test
    public void call_with_result_conversion() throws Exception {
      var defFuncS = defFuncS(arrayTS(nothingTS()), "n", nList(), orderS(nothingTS()));
      var callS = callS(arrayTS(intTS()), topRefS(defFuncS));
      assertThat(evaluate(callS, defFuncS))
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void call_polymorphic() throws Exception {
      var a = varS("A");
      var defFuncS = defFuncS("n", nList(itemS(a, "e")), orderS(a, paramRefS(a, "e")));
      var callS = callS(arrayTS(intTS()), topRefS(defFuncS), intS(7));
      assertThat(evaluate(callS, defFuncS))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine() throws Exception {
      StructTS type = structTS("n", nList(sigS(intTS(), "f")));
      assertThat(evaluate(combineS(type, list(intS(7)))))
          .isEqualTo(tupleB(tupleTB(list(intTB())), list(intB(7))));
    }

    @Test
    public void combine_with_item_conversion() throws Exception {
      StructTS type = structTS("n", nList(sigS(arrayTS(intTS()), "f")));
      assertThat(evaluate(combineS(type, list(orderS(nothingTS())))))
          .isEqualTo(tupleB(tupleTB(list(arrayTB(intTB()))), list(arrayB(intTB()))));
    }
  }

  @Nested
  class _order {
    @Test
    public void order() throws Exception {
      assertThat(evaluate(orderS(intTS(), intS(7))))
          .isEqualTo(arrayB(intTB(), intB(7)));
    }

    @Test
    public void order_with_element_conversion() throws Exception {
      assertThat(evaluate(orderS(arrayTS(intTS()), orderS(nothingTS()))))
          .isEqualTo(arrayB(arrayTB(intTB()), arrayB(intTB())));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() throws Exception {
      StructTS type = structTS("n", nList(sigS(intTS(), "f")));
      var combineS = combineS(type, list(intS(7)));
      assertThat(evaluate(selectS(intTS(), combineS, "f")))
          .isEqualTo(intB(7));
    }

    @Test
    public void select_with_conversion() throws Exception {
      StructTS type = structTS("n", nList(sigS(arrayTS(nothingTS()), "f")));
      var combineS = combineS(type, list(orderS(nothingTS())));
      assertThat(evaluate(selectS(arrayTS(intTS()), combineS, "f")))
          .isEqualTo(arrayB(intTB()));
    }
  }

  private ObjB evaluate(ExprS exprS, TopEvalS other) throws InterruptedException {
    return evaluate(nList(defValS("myVal", exprS), other));
  }

  private ObjB evaluate(ExprS exprS) throws InterruptedException {
    return evaluate(nList(defValS("myVal", exprS)));
  }

  private ObjB evaluate(NList<TopEvalS> topEvals) throws InterruptedException {
    var defsS = new DefsS(nList(), topEvals);
    var topRefS = topRefS(topEvals.get(0));
    var resultMap = newEvaluator().evaluate(defsS, list(topRefS));
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(topRefS).get();
  }

  private Evaluator newEvaluator() {
    return new Evaluator(compilerProv(), vmProv());
  }
}
