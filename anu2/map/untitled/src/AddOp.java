public class AddOp implements Operation {
    @Override
    public NrComplex execute(NrComplex nr1, NrComplex nr2) {
        return nr1.add(nr2);
    }
}