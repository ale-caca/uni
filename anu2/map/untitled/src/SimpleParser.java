public class SimpleParser implements exprParser {
    @Override
    public boolean isValid(String[] args) {
        //o expresie valida are un numar impar de argumente
        if (args == null || args.length % 2 == 0) return false;
        return true;
    }

    @Override
    public NrComplex[] parseNumbers(String[] args) {
        //creaza lista cu numerele doar
        int count = (args.length / 2) + 1;
        NrComplex[] numbers = new NrComplex[count];
        int index = 0;

        for (int i = 0; i < args.length; i += 2) {
            numbers[index++] = parseComplex(args[i]);
        }
        return numbers;
    }

    @Override
    public Operation[] parseOperations(String[] args) {
        //creaza lista cu semnele de operatii
        int count = args.length / 2;
        Operation[] ops = new Operation[count];
        int index = 0;

        for (int i = 1; i < args.length; i += 2) {
            ops[index++] = OperationFactory.createOperation(args[i]);
        }
        return ops;
    }

    private NrComplex parseComplex(String str) {
        double re = 0;
        double im = 0;

       //scot * din *i
        str = str.replace(" ", "").replace("*", "");
        //din - fac +- ca dupa sa despart totul dupa +
        str = str.replace("-", "+-");

        //despart dupa +
        String[] parts = str.split("\\+");

        for (String part : parts) {
            if (part.isEmpty()) continue;
            //partea imag
            if (part.contains("i")) {
                part = part.replace("i", "");

                //daca era doar i il fac sa fie 1/-1
                if (part.equals("") || part.equals("-")) {
                    part += "1"; //
                }
                im += Double.parseDouble(part);
            } else {
                //partea reala
                re += Double.parseDouble(part);
            }
        }
        return new NrComplex(re, im);
    }
}