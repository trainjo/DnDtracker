package main;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.DataContainer;
import data.EvaluationException;
import data.TextValue;
import data.Value;
import data.VoidValue;
import data.command.ClearCommand;
import data.command.Command;
import data.command.CommandValue;
import data.command.CopyCommand;
import data.command.CreateCommand;
import data.command.EnvironmentCommand;
import data.command.ForCommand;
import data.command.IfCommand;
import data.command.ListCommand;
import data.command.LoadCommand;
import data.command.MoveCommand;
import data.command.PrintCommand;
import data.command.QuitCommand;
import data.command.RemoveCommand;
import data.command.RunCommand;
import data.command.SaveCommand;
import data.command.SetCommand;
import data.command.SortCommand;
import data.command.TextCommand;
import data.command.VoidCommand;
import data.command.WhileCommand;

public class Tracker {
	public static Tracker mainInstance;
	public static final String PROGRAM_NAME = "D&D tracker";
	public static final String STARTUP_SCRIPT_NAME = "startup.scr";
	
	public DataContainer currentContainer;
	public DataContainer mainContainer;
	public ScreenHandler screen;
	
	private Tracker(){
		mainContainer = new DataContainer();
		currentContainer = mainContainer;
	}

	public static void main(String[] args) {
		mainInstance = new Tracker();
		mainInstance.start();
	}

	public boolean dispatchLine(String line, PrintStream output){
		Scanner lineScanner = new Scanner(line);
		lineScanner.useDelimiter("\\s*");
		ValueReader valueReader = new ValueReader();
		if(lineScanner.hasNext()){
			try{
				Value value = valueReader.readValue(lineScanner).getPreEvaluation(currentContainer,  new Value[0], output);
				List<Value> args = new ArrayList<Value>();
				while(lineScanner.hasNext()){
					if(lineScanner.hasNext("\\,")) valueReader.readCharacter(lineScanner,',');
					args.add(valueReader.readValue(lineScanner).getPreEvaluation(currentContainer, new Value[0], output));
				}
				value = value.evaluate(currentContainer, args.toArray(new Value[args.size()]), output);
				printValue(value,output);
			} catch(ReadingException|EvaluationException e){
				output.printf("Error: %s\n", e.getMessage());
			}
			lineScanner.close();
			return true;
		}
		lineScanner.close();
		return false;
	}

	private void printValue(Value value, PrintStream output) {
		if(value instanceof TextValue) printTextValue((TextValue) value,output);
		else if(value instanceof VoidValue) return;
		else output.printf("Result: %s\n", value.toString());
	}

	private void printTextValue(TextValue value, PrintStream output) {
		output.printf("%s\n",value.getText());
	}

	public void start(){
		screen = new ScreenHandler(this,PROGRAM_NAME);
		loadSystemCommands(screen.getOutput());
		runStartupScript();
	}
	
	private void runStartupScript() {
		FileReader fileReader = null;
		Scanner input = null;
		try{
			fileReader = new FileReader(STARTUP_SCRIPT_NAME);
			input = new Scanner(fileReader);
			while(input.hasNextLine()){
				Tracker.mainInstance.dispatchLine(input.nextLine(), screen.getOutput());
			}
			input.close();
			fileReader.close();
		} catch(IOException e){
			closeReaders(fileReader, input);
		}
	}
	
	private void closeReaders(FileReader fileReader, Scanner input) {
		try{
			if(input != null) input.close();
			if(fileReader != null) fileReader.close();
		} catch(IOException e){
			//Do nothing
		}
	}

	public void quit(){
		System.exit(0);
	}

	private void loadSystemCommands(PrintStream output) {
		loadCommand(new CopyCommand(),output);
		loadCommand(new CreateCommand(),output);
		loadCommand(new ListCommand(),output);
		loadCommand(new LoadCommand(),output);
		loadCommand(new MoveCommand(),output);
		loadCommand(new QuitCommand(),output);
		loadCommand(new RemoveCommand(),output);
		loadCommand(new SaveCommand(),output);
		loadCommand(new SetCommand(),output);
		loadCommand(new EnvironmentCommand(),output);
		loadCommand(new ClearCommand(),output);
		loadCommand(new PrintCommand(),output);
		loadCommand(new SortCommand(),output);
		loadCommand(new TextCommand(),output);
		loadCommand(new VoidCommand(),output);
		loadCommand(new IfCommand(),output);
		loadCommand(new RunCommand(),output);
		loadCommand(new WhileCommand(),output);
		loadCommand(new ForCommand(),output);
	}
	
	private void loadCommand(CommandValue value, PrintStream output){
		if(!mainContainer.addData(new Command(value,mainContainer))) output.printf("Error: Could not load system command \'%s\'.\n", value.getDefaultName());
	}
}
