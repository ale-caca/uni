public class NrComplex {
    private final double real;
    private final double imag;

    public NrComplex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }
    public double getReal() {return real;}
    public double getImag() {return imag;}

    public NrComplex add(NrComplex nr) {
        return new NrComplex(this.real + nr.real, this.imag + nr.imag);
    }
    public NrComplex subtract(NrComplex nr) {
        return new NrComplex(this.real - nr.real, this.imag - nr.imag);
    }
    public NrComplex multiply(NrComplex nr) {
        double newReal = this.real * nr.real - this.imag * nr.imag;
        double newImag = this.real * nr.imag + this.imag * nr.real;
        return new NrComplex(newReal, newImag);
    }
    public NrComplex divide(NrComplex nr) {
        //return new NrComplex( this.real / nr.real, this.imag / nr.imag);
        //(a+bi)/(c+di)=[(ac+bd)/(c^2+d^2)]+[(bc-ad)/(c^2+d^2)]*i
        double numitor = nr.real * nr.real + nr.imag * nr.imag;
        //double numitor = c2.getReal() * c2.getReal() + c2.getImag() * c2.getImag();
        if (numitor == 0) {
            throw new ArithmeticException("Impartire la zero");
        }
        double realPart = (this.real * nr.real + this.imag * nr.imag) / numitor;
        double imagPart = (this.imag * nr.real - this.real * nr.imag) / numitor;

        return new NrComplex(realPart, imagPart);
    }

    //@Override pot sa l pun sau nu
    public String toString() {
        if (imag != 0) {
            return real + "+" + imag + "*i";
        }
        return real + "";
    }
}
