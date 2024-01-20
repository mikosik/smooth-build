package org.smoothbuild.vm.bytecode.expr;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class ExprBCollisionTest extends TestContext {
  @Test
  public void collisions() {
    HashMap<Hash, ExprB> map = new HashMap<>();
    for (ExprB expr : exprBs()) {
      Hash hash = expr.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two ExprB: " + expr + " and " + map.get(hash) + ".");
      }
      map.put(hash, expr);
    }
  }

  private List<ExprB> exprBs() {
    BlobB blob1 = blobB(0);
    BlobB blob2 = blobB(1);
    BlobB blobEmpty = blobB(ByteString.EMPTY);
    return List.of(
        // values
        arrayB(blobTB()),
        arrayB(stringTB()),
        arrayB(blob1),
        arrayB(blob2),
        arrayB(blobEmpty),
        blobEmpty,
        blob1,
        blob2,
        boolB(false),
        boolB(true),
        lambdaB(funcTB(intTB()), intB(0)),
        lambdaB(funcTB(intTB()), intB(1)),
        lambdaB(funcTB(stringTB()), stringB("abc")),
        lambdaB(funcTB(intTB(), intTB()), intB(0)),
        lambdaB(funcTB(stringTB(), intTB()), intB(0)),
        intB(0),
        intB(1),
        nativeFuncB(funcTB(intTB()), blob1, stringB("binary name"), boolB(true)),
        nativeFuncB(funcTB(intTB()), blob1, stringB("binary name"), boolB(false)),
        nativeFuncB(funcTB(intTB()), blob1, stringB("binary name 2"), boolB(true)),
        nativeFuncB(funcTB(intTB()), blob2, stringB("binary name"), boolB(true)),
        nativeFuncB(funcTB(stringTB()), blob1, stringB("binary name"), boolB(true)),
        stringB("abc"),
        stringB("def"),
        tupleB(),
        tupleB(intB(0)),
        tupleB(intB(1)),
        tupleB(stringB("abc")),
        tupleB(stringB("def")),
        // expressions
        callB(lambdaB(funcTB(intTB(), stringTB()), stringB("a")), intB(1)),
        callB(lambdaB(funcTB(intTB(), stringTB()), stringB("a")), intB(2)),
        callB(lambdaB(funcTB(intTB(), stringTB()), stringB("b")), intB(1)),
        callB(nativeFuncB(funcTB(intTB(), intTB())), intB(1)),
        callB(nativeFuncB(funcTB(intTB(), intTB())), intB(2)),
        callB(nativeFuncB(funcTB(intTB(), stringTB())), intB(1)),
        combineB(),
        combineB(intB(0)),
        combineB(intB(1)),
        ifFuncB(intTB()),
        ifFuncB(stringTB()),
        mapFuncB(intTB(), stringTB()),
        mapFuncB(intTB(), boolTB()),
        mapFuncB(boolTB(), stringTB()),
        orderB(stringTB()),
        orderB(intTB(), intB(1)),
        orderB(intTB(), intB(2)),
        orderB(arrayTB(intTB()), arrayB(intTB())),
        pickB(arrayB(intB(1)), intB(0)),
        pickB(arrayB(intB(1)), intB(1)),
        pickB(arrayB(intB(1), intB(2)), intB(0)),
        varB(intTB(), 0),
        varB(intTB(), 1),
        varB(stringTB(), 0),
        selectB(tupleB(intB(1), stringB("a")), intB(0)),
        selectB(tupleB(intB(1), stringB("a")), intB(1)),
        selectB(tupleB(intB(1), stringB("b")), intB(0)));
  }
}
