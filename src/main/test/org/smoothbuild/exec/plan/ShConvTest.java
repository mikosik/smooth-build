package org.smoothbuild.exec.plan;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.exec.java.FileLoader;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.testing.TestingContext;

public class ShConvTest extends TestingContext {
  @Nested
  class _converting {
    @Test
    public void blob() {
      var blob = blobS(37);
      assertConversion(blob, blobH(37));
    }

    @Test
    public void call() {
      var defFunc = defFuncS("myFunc", nList(), stringS("abc"));
      var call = callS(stringTS(), topRefS(defFunc));
      assertConversion(defFunc, call, callH(defFuncH(stringH("abc")), list()));
    }

    @Test
    public void combine() {
      var combine = combineS(stringS("abc"), intS(1));
      assertConversion(combine, combineH(list(stringH("abc"), intH(1))));
    }

    @Test
    public void int_() {
      IntS int_ = intS(1);
      assertConversion(int_, intH(1));
    }

    @Test
    public void order() {
      var order = orderS(stringTS(), stringS("abc"), stringS("def"));
      assertConversion(order, orderH(list(stringH("abc"), stringH("def"))));
    }

    @Test
    public void paramRef() {
      var func = defFuncS("f", nList(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertConversion(func, topRefS(func), defFuncH(list(intTH()), paramRefH(intTH(), 0)));
    }

    @Test
    public void select() {
      var combine = combineS(stringS("abc"));
      var select = selectS(stringTS(), combine, "field0");
      assertConversion(select, selectH(combineH(list(stringH("abc"))), intH(0)));
    }

    @Test
    public void string() {
      var string = stringS("abc");
      assertConversion(string, stringH("abc"));
    }

    @Test
    public void topRef_to_val() {
      var defVal = defValS("myVal", stringS("abc"));
      assertConversion(defVal, topRefS(defVal), stringH("abc"));
    }

    @Test
    public void topRef_to_func() {
      var defFunc = defFuncS("myFunc", nList(), stringS("abc"));
      assertConversion(defFunc, topRefS(defFunc), defFuncH(stringH("abc")));
    }

    private void assertConversion(ExprS exprS, ObjH expected) {
      assertConversion(defs(), exprS, expected);
    }

    private void assertConversion(TopEvalS topEval, ExprS exprS, ObjH expected) {
      assertConversion(defs(topEval), exprS, expected);
    }

    private void assertConversion(DefsS defs, ExprS exprS, ObjH expected) {
      var shConv = newShConv(defs);
      assertThat(shConv.convertExpr(exprS))
          .isEqualTo(expected);
    }
  }

  @Nested
  class _caching {
    @Test
    public void val_conversion_result() {
      assertConversionIsCached(defValS("myVal", stringS("abcdefghi")));
    }

    @Test
    public void def_func_conversion_result() {
      assertConversionIsCached(defFuncS("myFunc", nList(), stringS("abcdefghi")));
    }

    @Test
    public void if_func_conversion_result() {
      assertConversionIsCached(ifFuncS());
    }

    @Test
    public void map_func_conversion_result() {
      assertConversionIsCached(mapFuncS());
    }

    @Test
    public void nat_func_conversion_result() {
      assertConversionIsCached(natFuncS(funcTS(stringTS()), "myFunc", nList()));
    }

    private void assertConversionIsCached(TopEvalS topEval) {
      var shConv = newShConv(topEval);
      assertThat(shConv.convertExpr(topRefS(topEval)))
          .isSameInstanceAs(shConv.convertExpr(topRefS(topEval)));
    }
  }

  private ShConv newShConv(TopEvalS topEval) {
    return newShConv(defs(topEval));
  }

  private ShConv newShConv(DefsS defs) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobH(1));
      return new ShConv(objFactory(), defs, typeShConv(), mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private DefsS defs() {
    return new DefsS(nList(), nList());
  }

  private DefsS defs(TopEvalS topEval) {
    return new DefsS(nList(), nList(topEval));
  }
}
