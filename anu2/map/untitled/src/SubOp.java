public class SubOp implements Operation {
    @Override
    public NrComplex execute(NrComplex nr1, NrComplex nr2) {
        return nr1.subtract(nr2);
    }
}
