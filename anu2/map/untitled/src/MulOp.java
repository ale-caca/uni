public class MulOp implements Operation {
    @Override
    public NrComplex execute(NrComplex nr1, NrComplex nr2) {
        return nr1.multiply(nr2);
    }
}
