package tucil_3_stima;

import java.io.IOException;

import tucil_3_stima.utils.InputHandler;

public class Main {

    public static void main(String[] args) {
        System.out.println("Testing");
        try {
            InputHandler.inputTestCaseFromFile("test.txt");
            
        }
        catch (IOException e) {
            System.out.println("IO error: \n" + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            System.out.println("Board error: \n" + e.getMessage());
        }

        
    }

}