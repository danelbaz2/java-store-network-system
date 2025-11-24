import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ScannerProvider {

	private static final Scanner CONSOLE_SCANNER = new Scanner(System.in);
	private static Scanner FILE_SCANNER;

	private ScannerProvider() {
	}
	
	public static Scanner getFileScanner(File f) throws FileNotFoundException {
		FILE_SCANNER = new Scanner(f);
		return FILE_SCANNER;
	}

	public static Scanner getConsoleScanner() {
		return CONSOLE_SCANNER;
	}

}
