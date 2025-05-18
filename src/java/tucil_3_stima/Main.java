package tucil_3_stima;

import tucil_3_stima.utils.InputHandler;

public class Main {

    public static void main(String[] args) {
        System.err.println("testing niggz");
        try {
            InputHandler.inputTestCaseFromFile("test.txt");
            System.err.println("no error");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}