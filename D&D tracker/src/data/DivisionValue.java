package data;

/**
 * A Value object that represents the sum of two Value objects.
 */
import java.io.PrintStream;

public class DivisionValue implements Value{
	public static final String[] VALUE_TYPE_NAMES = {"sum"};
	
	private Value a;
	private Value b;
	
	/**
	 * Initializes this DivisionValue object as the division of two given Value objects.
	 * @param a The first Value object.
	 * @param b The second Value object to be divide the first object by.
	 */
	public DivisionValue(Value a, Value b){
		this.a = a;
		this.b = b;
	}

	public String getTypeName() {
		return VALUE_TYPE_NAMES[0];
	}
	
	public String[] getAlternativeTypeNames() {
		return VALUE_TYPE_NAMES;
	}

	@Override
	public Value copy() {
		return new DivisionValue(a.copy(),b.copy());
	}
	
	/**
	 * Returns the division of the evaluation of the two stored Value objects.
	 */
	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		try{
			return a.evaluateToFirstDivisible(environment,args, output).divideBy(b.evaluateToFirstDivisible(environment, args, output));
		} catch(ComputationException e){
			throw new EvaluationException(String.format("Can not evaluate: %s",e.getMessage()),e);
		}
	}
	
	public String toString(){
		return String.format("(%s/%s)", a.toString(),b.toString());
	}

	@Override
	public int compareTo(Value o) {
		if(o instanceof DivisionValue){
			DivisionValue other = (DivisionValue) o;
			return a.compareTo(other.a);
		}
		return getTypeName().compareTo(o.getTypeName());
	}

	@Override
	public Value replaceArgumentsBy(Value[] args) {
		return new DivisionValue(a.replaceArgumentsBy(args),b.replaceArgumentsBy(args));
	}

	@Override
	public Value getPreEvaluation(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return new DivisionValue(a.getPreEvaluation(environment, args, output),b.getPreEvaluation(environment, args, output));
	}

	@Override
	public boolean equals(Value other) {
		if(!(other instanceof DivisionValue)) return false;
		DivisionValue o = (DivisionValue) other;
		return a.equals(o.a) && b.equals(o.b);
	}

	@Override
	public DataContainer evaluateToFirstDataContainer(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstDataContainer(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstAddable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstAddable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstSubtractible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstSubtractible(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstMultiplicable(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstMultiplicable(environment, args, output);
	}

	@Override
	public PrimitiveValue evaluateToFirstDivisible(DataContainer environment, Value[] args, PrintStream output)
			throws EvaluationException {
		return evaluate(environment, args, output).evaluateToFirstDivisible(environment, args, output);
	}
}
