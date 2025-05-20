package tucil_3_stima;

import java.io.IOException;

import tucil_3_stima.gui.MainApp;
import tucil_3_stima.utils.InputHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Main");
        // try {
        //     System.out.println("Parsing Test");
        //     String path = "./test/resources/parsing/";
        //     InputHandler.inputTestCaseFromFile(path + "tc" + args[0] + ".txt");          
        //     System.out.println("Done Testing");
        // } 
        // catch (IOException e) {
        //     System.out.println("IO error: \n" + e.getMessage());
        // }
        // catch (IllegalArgumentException e) {
        //     System.out.println("IA error: \n" + e.getMessage());
        // }
        // catch (IndexOutOfBoundsException e) {
        //     System.out.println("No arguments given");
        // }


        MainApp.show();
        
    }

}