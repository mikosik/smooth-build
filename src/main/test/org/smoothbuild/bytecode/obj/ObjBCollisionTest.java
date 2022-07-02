package org.smoothbuild.bytecode.obj;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestContext;

import okio.ByteString;

public class ObjBCollisionTest extends TestContext {
  @Test
  public void collisions() {
    HashMap<Hash, ObjB> map = new HashMap<>();
    for (ObjB obj : objectsH()) {
      Hash hash = obj.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two ObjH " + obj + " and " + map.get(hash) + ".");
      }
      map.put(hash, obj);
    }
  }

  private List<ObjB> objectsH() {
    BlobB blob1 = blobB(0);
    BlobB blob2 = blobB(1);
    BlobB blobEmpty = blobB(ByteString.EMPTY);
    // @formatter:off
    return List.of(
        // values
        arrayB(blobTB()),
        arrayB(nothingTB()),
        arrayB(stringTB()),
        arrayB(blob1),
        arrayB(blob2),
        arrayB(blobEmpty),
        blobEmpty,
        blob1,
        blob2,
        boolB(false),
        boolB(true),
        funcB(funcTB(intTB(), list()), intB(0)),
        funcB(funcTB(intTB(), list()), intB(1)),
        funcB(funcTB(stringTB(), list()), stringB("abc")),
        funcB(funcTB(intTB(), list(intTB())), intB(0)),
        funcB(funcTB(intTB(), list(stringTB())), intB(0)),
        intB(0),
        intB(1),
        methodB(methodTB(intTB(), list()), blob1, stringB("binary name"), boolB(true)),
        methodB(methodTB(intTB(), list()), blob1, stringB("binary name"), boolB(false)),
        methodB(methodTB(intTB(), list()), blob1, stringB("binary name 2"), boolB(true)),
        methodB(methodTB(intTB(), list()), blob2, stringB("binary name"), boolB(true)),
        methodB(methodTB(stringTB(), list()), blob1, stringB("binary name"), boolB(true)),
        stringB("abc"),
        stringB("def"),
        tupleB(),
        tupleB(intB(0)),
        tupleB(intB(1)),
        tupleB(stringB("abc")),
        tupleB(stringB("def")),
        // expressions
        callB(funcB(funcTB(stringTB(), list(intTB())), stringB("a")), intB(1)),
        callB(funcB(funcTB(stringTB(), list(intTB())), stringB("a")), intB(2)),
        callB(funcB(funcTB(stringTB(), list(intTB())), stringB("b")), intB(1)),
        combineB(),
        combineB(intB(0)),
        combineB(intB(1)),
        ifB(intTB(), boolB(false), intB(1), intB(2)),
        ifB(intTB(), boolB(false), intB(1), intB(3)),
        ifB(intTB(), boolB(false), intB(3), intB(2)),
        ifB(intTB(), boolB(true), intB(1), intB(2)),
        invokeB(methodB(methodTB(intTB(), list(intTB()))), intB(1)),
        invokeB(methodB(methodTB(intTB(), list(intTB()))), intB(2)),
        invokeB(methodB(methodTB(stringTB(), list(intTB()))), intB(1)),
        mapB(arrayB(intB(1)), funcB(funcTB(intTB(), list(intTB())), paramRefB(intTB(), 0))),
        mapB(arrayB(intB(1)), funcB(funcTB(intTB(), list(intTB())), intB(3))),
        mapB(arrayB(intB(2)), funcB(funcTB(intTB(), list(intTB())), paramRefB(intTB(), 0))),
        orderB(stringTB()),
        orderB(nothingTB()),
        orderB(intTB(), intB(1)),
        orderB(intTB(), intB(2)),
        orderB(arrayTB(intTB()), arrayB(intTB())),
        orderB(arrayTB(intTB()), arrayB(nothingTB())),
        paramRefB(intTB(), 0),
        paramRefB(intTB(), 1),
        paramRefB(stringTB(), 0),
        selectB(tupleB(intB(1), stringB("a")), intB(0)),
        selectB(tupleB(intB(1), stringB("a")), intB(1)),
        selectB(tupleB(intB(1), stringB("b")), intB(0))
    );
    // @formatter:on
  }
}
