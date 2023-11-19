package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;
import static org.smoothbuild.vm.bytecode.type.Validator.validateTuple;

import com.google.common.base.Supplier;
import io.vavr.collection.Array;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValueB {
  private final Supplier<Array<ValueB>> elementsSupplier;

  public TupleB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.elementsSupplier = memoize(this::instantiateItems);
  }

  @Override
  public TupleTB evaluationT() {
    return type();
  }

  @Override
  public TupleTB type() {
    return (TupleTB) super.category();
  }

  public ValueB get(int index) {
    Array<ValueB> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public Array<ValueB> elements() {
    return elementsSupplier.get();
  }

  private Array<ValueB> instantiateItems() {
    var type = type();
    var expectedElementTs = type.elements();
    var elements = readDataSeqElems(expectedElementTs.size());
    var elementTs = elements.map(ValueB::type);
    validateTuple(type, elementTs, () -> {
      throw new DecodeExprWrongNodeTypeException(
          hash(), category(), DATA_PATH, type, asTupleToString(elementTs));
    });
    return elements;
  }

  private static String asTupleToString(Array<TypeB> elementTs) {
    return elementTs.mkString("`{", ",", "}`");
  }

  @Override
  public String exprToString() {
    return "{" + exprsToString(elements()) + '}';
  }
}
