public class DivOp implements Operation {
    @Override
    public NrComplex execute(NrComplex c1, NrComplex c2) {
//        //(a+bi)/(c+di)=[(ac+bd)/(c^2+d^2)]+[(bc-ad)/(c^2+d^2)]*i
//
//        double numitor = c2.getReal() * c2.getReal() + c2.getImag() * c2.getImag();
//        if (numitor == 0) {
//            throw new ArithmeticException("Impartire la zero");
//        }
//        double realPart = (c1.getReal() * c2.getReal() + c1.getImag() * c2.getImag()) / numitor;
//        double imagPart = (c1.getImag() * c2.getReal() - c1.getReal() * c2.getImag()) / numitor;
//
//        return new NrComplex(realPart, imagPart);
          return c1.divide(c2) ;
    }
}