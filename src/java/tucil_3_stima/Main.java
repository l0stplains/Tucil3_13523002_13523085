package tucil_3_stima;

import tucil_3_stima.test.RushHourTest;
import tucil_3_stima.gui.MainApp;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World");
        RushHourTest test = new RushHourTest();
        test.Run();

        MainApp.show();

    }

}