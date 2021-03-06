package data.command;

import java.io.PrintStream;

import data.DataContainer;
import data.DataPair;
import data.EvaluationException;
import data.PrimitiveValue;
import data.Value;
import data.VoidValue;

public class ListCommand extends CommandValue{
	public static String COMMAND_WORD = "list";
	public static String USAGE_DESCRIPTION = "list [collection] [listSubCollections?] [listHiddenVariables?]";
	public static String TAB = "   ";

	@Override
	public PrimitiveValue evaluate(DataContainer environment, Value[] args, PrintStream output) throws EvaluationException {
		if(args.length >= 1) environment = args[0].evaluateToFirstDataContainer(environment, new Value[0], output);
		printDataContainer(environment,0,output,args.length >= 2 ? args[1].evaluate(environment, new VoidValue[0], output).getBool() : false , args.length >= 3 ? args[2].evaluate(environment, new VoidValue[0], output).getBool() : false);
		return new VoidValue();
	}

	private void printDataContainer(DataContainer environment, int level, PrintStream output, boolean printSubDataContainers, boolean printHiddenDataPairs) {
		for(DataPair d : environment){
			if(printHiddenDataPairs || d.getName().charAt(0) != '_'){
				printTabs(level, output);
				printDataPair(d, level, output, printSubDataContainers, printHiddenDataPairs);
			}
		}
	}

	private void printDataPair(DataPair d, int level, PrintStream output, boolean printSubDataContainers, boolean printHiddenDataPairs) {
		output.printf("%s: ", d.getName());
		printValue(d.getValue(), level, output, printSubDataContainers, printHiddenDataPairs);
	}

	private void printValue(Value value, int level, PrintStream output, boolean printSubDataContainers, boolean printHiddenDataPairs) {
		if(value instanceof DataContainer){
			if(printSubDataContainers){
				output.printf("\n");
				printDataContainer((DataContainer) value, level+1, output, printSubDataContainers, printHiddenDataPairs);
			}else{
				output.printf("{...}\n");
			}
		}
		else{
			output.print(value.toString());
			output.print("\n");
		}
	}

	private void printTabs(int number, PrintStream output){
		for(int i = 0; i < number; i++){
			output.print(TAB);
		}
	}

	@Override
	public Value copy() {
		return new ListCommand();
	}

	@Override
	public String getDefaultName() {
		return COMMAND_WORD;
	}
}
