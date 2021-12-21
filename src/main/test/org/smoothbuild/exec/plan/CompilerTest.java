package org.smoothbuild.exec.plan;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.space.FilePath.filePath;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.lang.base.define.Loc.loc;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.exec.java.FileLoader;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CompilerTest extends TestingContext {
  @Nested
  class _converting {
    @Test
    public void blob() {
      var blob = blobS(37);
      assertConversion(blob, blobB(37));
    }

    @Test
    public void call() {
      var defFunc = defFuncS("myFunc", nList(), stringS("abc"));
      var call = callS(stringTS(), topRefS(defFunc));
      assertConversion(defFunc, call, callB(funcB(stringB("abc")), list()));
    }

    @Test
    public void combine() {
      var combine = combineS(stringS("abc"), intS(1));
      assertConversion(combine, combineB(list(stringB("abc"), intB(1))));
    }

    @Test
    public void int_() {
      IntS int_ = intS(1);
      assertConversion(int_, intB(1));
    }

    @Test
    public void order() {
      var order = orderS(stringTS(), stringS("abc"), stringS("def"));
      assertConversion(order, orderB(list(stringB("abc"), stringB("def"))));
    }

    @Test
    public void paramRef() {
      var func = defFuncS("f", nList(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertConversion(func, topRefS(func), funcB(list(intTB()), paramRefB(intTB(), 0)));
    }

    @Test
    public void select() {
      var combine = combineS(stringS("abc"));
      var select = selectS(stringTS(), combine, "field0");
      assertConversion(select, selectB(combineB(list(stringB("abc"))), intB(0)));
    }

    @Test
    public void string() {
      var string = stringS("abc");
      assertConversion(string, stringB("abc"));
    }

    @Test
    public void topRef_to_val() {
      var defVal = defValS("myVal", stringS("abc"));
      assertConversion(defVal, topRefS(defVal), stringB("abc"));
    }

    @Test
    public void topRef_to_func() {
      var defFunc = defFuncS("myFunc", nList(), stringS("abc"));
      assertConversion(defFunc, topRefS(defFunc), funcB(stringB("abc")));
    }

    @Test
    public void topRef_to_if_func() {
      var ifFuncS = ifFuncS();
      var varA = varTB("A");
      var bodyH = ifB(paramRefB(boolTB(), 0), paramRefB(varA, 1), paramRefB(varA, 2));
      var funcTH = funcTB(varA, list(boolTB(), varA, varA));
      var funcH = funcB(funcTH, bodyH);
      assertConversion(ifFuncS, topRefS(ifFuncS), funcH);
    }

    @Test
    public void topRef_to_map_func() {
      var mapFuncS = mapFuncS();
      var varA = varTB("E");
      var varB = varTB("R");
      var mappingFuncT = funcTB(varB, list(varA));
      var funcTH = funcTB(arrayTB(varB), list(arrayTB(varA), mappingFuncT));
      var bodyH = mapB(paramRefB(arrayTB(varA), 0), paramRefB(mappingFuncT, 1));
      var funcH = funcB(funcTH, bodyH);
      assertConversion(mapFuncS, topRefS(mapFuncS), funcH);
    }

    @Test
    public void topRef_to_native_func() {
      var funcTS = funcTS(intTS(), list(blobTS()));
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = "class.binary.name";
      var ann = annS(loc(filePath, 1), stringS(classBinaryName));
      var natFuncS = natFuncS(funcTS, "myFunc", nList(itemS(intTS(), "param")), ann);

      var resT = intTB();
      ImmutableList<TypeB> paramTs = list(blobTB());
      var funcTH = funcTB(resT, paramTs);
      var jar = blobB(37);
      var method = methodB(methodTB(resT, paramTs), jar, stringB(classBinaryName), boolB(true));
      var bodyH = invokeB(method, combineB(list(paramRefB(blobTB(), 0))));
      var funcH = funcB(funcTH, bodyH);

      var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
      var shConv = newShConv(defs(natFuncS), fileLoader);
      assertThat(shConv.convertExpr(topRefS(natFuncS)))
          .isEqualTo(funcH);
    }

    private void assertConversion(ExprS exprS, ObjB expected) {
      assertConversion(defs(), exprS, expected);
    }

    private void assertConversion(TopEvalS topEval, ExprS exprS, ObjB expected) {
      assertConversion(defs(topEval), exprS, expected);
    }

    private void assertConversion(DefsS defs, ExprS exprS, ObjB expected) {
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

  private Compiler newShConv(TopEvalS topEval) {
    return newShConv(defs(topEval));
  }

  private Compiler newShConv(DefsS defs) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return newShConv(defs, mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Compiler newShConv(DefsS defs, FileLoader fileLoader) {
    return new Compiler(objFactory(), defs, typeShConv(), fileLoader);
  }

  private FileLoader createFileLoaderMock(FilePath filePath, BlobB value) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(filePath)).thenReturn(value);
      return mock;
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
