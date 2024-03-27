package org.smoothbuild.virtualmachine.bytecode.expr;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;

import java.util.HashMap;
import java.util.List;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BExprCollisionTest extends TestingVirtualMachine {
  @Test
  public void collisions() throws Exception {
    HashMap<Hash, BExpr> map = new HashMap<>();
    for (var expr : exprBs()) {
      var hash = expr.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two ExprB: " + expr + " and " + map.get(hash) + ".");
      }
      map.put(hash, expr);
    }
  }

  private List<BExpr> exprBs() throws Exception {
    var blob1 = bBlob(0);
    var blob2 = bBlob(1);
    var blobEmpty = bBlob(ByteString.EMPTY);
    return List.of(
        // values
        bArray(bBlobType()),
        bArray(bStringType()),
        bArray(blob1),
        bArray(blob2),
        bArray(blobEmpty),
        blobEmpty,
        blob1,
        blob2,
        bBool(false),
        bBool(true),
        bLambda(bFuncType(bIntType()), bInt(0)),
        bLambda(bFuncType(bIntType()), bInt(1)),
        bLambda(bFuncType(bStringType()), bString("abc")),
        bLambda(bFuncType(bIntType(), bIntType()), bInt(0)),
        bLambda(bFuncType(bStringType(), bIntType()), bInt(0)),
        bInt(0),
        bInt(1),
        bInvoke(bIntType(), blob1, bString("binary name"), bBool(true), bTuple()),
        bInvoke(bIntType(), blob1, bString("binary name"), bBool(true), bTuple(bInt(1))),
        bInvoke(bIntType(), blob1, bString("binary name"), bBool(false), bTuple()),
        bInvoke(bIntType(), blob1, bString("binary name 2"), bBool(true), bTuple()),
        bInvoke(bIntType(), blob2, bString("binary name"), bBool(true), bTuple()),
        bInvoke(bStringType(), blob1, bString("binary name"), bBool(true), bTuple()),
        bString("abc"),
        bString("def"),
        bTuple(),
        bTuple(bInt(0)),
        bTuple(bInt(1)),
        bTuple(bString("abc")),
        bTuple(bString("def")),
        // expressions
        bCall(bLambda(bFuncType(bIntType(), bStringType()), bString("a")), bInt(1)),
        bCall(bLambda(bFuncType(bIntType(), bStringType()), bString("a")), bInt(2)),
        bCall(bLambda(bFuncType(bIntType(), bStringType()), bString("b")), bInt(1)),
        bCall(bInvoke(bFuncType(bIntType(), bIntType())), bInt(1)),
        bCall(bInvoke(bFuncType(bIntType(), bIntType())), bInt(2)),
        bCall(bInvoke(bFuncType(bIntType(), bStringType())), bInt(1)),
        bCombine(),
        bCombine(bInt(0)),
        bCombine(bInt(1)),
        bIf(bBool(false), bInt(1), bInt(2)),
        bIf(bBool(false), bInt(1), bInt(1)),
        bIf(bBool(false), bInt(2), bInt(1)),
        bIf(bBool(false), bInt(2), bInt(2)),
        bIf(bBool(true), bInt(1), bInt(2)),
        bMap(bArray(bIntType()), bIntIdFunc()),
        bMap(bArray(bInt(1)), bIntIdFunc()),
        bMap(bArray(bInt(2)), bIntIdFunc()),
        bMap(bArray(bInt(1)), bLambda(list(bIntType()), bInt(1))),
        bOrder(bStringType()),
        bOrder(bIntType(), bInt(1)),
        bOrder(bIntType(), bInt(2)),
        bOrder(bArrayType(bIntType()), bArray(bIntType())),
        bPick(bArray(bInt(1)), bInt(0)),
        bPick(bArray(bInt(1)), bInt(1)),
        bPick(bArray(bInt(1), bInt(2)), bInt(0)),
        bReference(bIntType(), 0),
        bReference(bIntType(), 1),
        bReference(bStringType(), 0),
        bSelect(bTuple(bInt(1), bString("a")), bInt(0)),
        bSelect(bTuple(bInt(1), bString("a")), bInt(1)),
        bSelect(bTuple(bInt(1), bString("b")), bInt(0)));
  }
}
