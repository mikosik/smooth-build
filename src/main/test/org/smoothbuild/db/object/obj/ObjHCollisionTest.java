package org.smoothbuild.db.object.obj;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjHCollisionTest extends TestingContext {
  @Test
  public void collisions() {
    HashMap<Hash, ObjH> map = new HashMap<>();
    for (ObjH obj : objectsH()) {
      Hash hash = obj.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two ObjH " + obj + " and " + map.get(hash) + ".");
      }
      map.put(hash, obj);
    }
  }

  private List<ObjH> objectsH() {
    BlobH blob1 = blobH(0);
    BlobH blob2 = blobH(1);
    BlobH blobEmpty = blobH(ByteString.EMPTY);
    // @formatter:off
    return List.of(
        // values
        arrayH(blobTH()),
        arrayH(nothingTH()),
        arrayH(stringTH()),
        arrayH(blob1),
        arrayH(blob2),
        arrayH(blobEmpty),
        blobEmpty,
        blob1,
        blob2,
        boolH(false),
        boolH(true),
        funcH(funcTH(intTH(), list()), intH(0)),
        funcH(funcTH(intTH(), list()), intH(1)),
        funcH(funcTH(stringTH(), list()), stringH("abc")),
        funcH(funcTH(intTH(), list(intTH())), intH(0)),
        funcH(funcTH(intTH(), list(stringTH())), intH(0)),
        intH(0),
        intH(1),
        methodH(methodTH(intTH(), list()), blob1, stringH("binary name"), boolH(true)),
        methodH(methodTH(intTH(), list()), blob1, stringH("binary name"), boolH(false)),
        methodH(methodTH(intTH(), list()), blob1, stringH("binary name 2"), boolH(true)),
        methodH(methodTH(intTH(), list()), blob2, stringH("binary name"), boolH(true)),
        methodH(methodTH(stringTH(), list()), blob1, stringH("binary name"), boolH(true)),
        stringH("abc"),
        stringH("def"),
        tupleH(list()),
        tupleH(list(intH(0))),
        tupleH(list(intH(1))),
        tupleH(list(stringH("abc"))),
        tupleH(list(stringH("def"))),
        // expressions
        callH(funcH(funcTH(stringTH(), list(intTH())), stringH("a")), list(intH(1))),
        callH(funcH(funcTH(stringTH(), list(intTH())), stringH("a")), list(intH(2))),
        callH(funcH(funcTH(stringTH(), list(intTH())), stringH("b")), list(intH(1))),
        combineH(list()),
        combineH(list(intH(0))),
        combineH(list(intH(1))),
        ifH(boolH(false), intH(1), intH(2)),
        ifH(boolH(false), intH(1), intH(3)),
        ifH(boolH(false), intH(3), intH(2)),
        ifH(boolH(true), intH(1), intH(2)),
        invokeH(methodH(methodTH(intTH(), list(intTH()))), combineH(list(intH(1)))),
        invokeH(methodH(methodTH(intTH(), list(intTH()))), combineH(list(intH(2)))),
        invokeH(methodH(methodTH(stringTH(), list(intTH()))), combineH(list(intH(1)))),
        mapH(arrayH(intH(1)), funcH(funcTH(intTH(), list(intTH())), paramRefH(intTH(), 0))),
        mapH(arrayH(intH(1)), funcH(funcTH(intTH(), list(intTH())), intH(3))),
        mapH(arrayH(intH(2)), funcH(funcTH(intTH(), list(intTH())), paramRefH(intTH(), 0))),
        orderH(stringTH(), list()),
        orderH(nothingTH(), list()),
        orderH(intTH(), list(intH(1))),
        orderH(intTH(), list(intH(2))),
        orderH(arrayTH(intTH()), list(arrayH(intTH()))),
        orderH(arrayTH(intTH()), list(arrayH(nothingTH()))),
        paramRefH(intTH(), 0),
        paramRefH(intTH(), 1),
        paramRefH(stringTH(), 0),
        selectH(tupleH(list(intH(1), stringH("a"))), intH(0)),
        selectH(tupleH(list(intH(1), stringH("a"))), intH(1)),
        selectH(tupleH(list(intH(1), stringH("b"))), intH(0))
    );
    // @formatter:on
  }
}
