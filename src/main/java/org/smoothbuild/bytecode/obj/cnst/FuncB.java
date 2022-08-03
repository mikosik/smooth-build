package org.smoothbuild.bytecode.obj.cnst;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.cnst.FuncTB;

/**
 * Function.
 * This class is thread-safe.
 */
public final class FuncB extends CnstB {
  public FuncB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public FuncTB type() {
    return (FuncTB) super.type();
  }

  @Override
  public FuncTB cat() {
    return (FuncTB) super.cat();
  }

  public ObjB body() {
    var body = readObj(DATA_PATH, dataHash(), ObjB.class);
    var resT = cat().res();
    var bodyT = body.type();
    if (!resT.equals(bodyT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), DATA_PATH, resT, bodyT);
    }
    return body;
  }

  @Override
  public String objToString() {
    return "Func(" + cat().name() + ")";
  }
}
