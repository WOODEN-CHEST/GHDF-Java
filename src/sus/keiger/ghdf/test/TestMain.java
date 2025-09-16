package sus.keiger.ghdf.test;

import java.util.function.Supplier;

public class TestMain
{
    // Private static fields.
    private static final String ARG_TEST = "test";


    // Static methods.
    public static void main(String[] args)
    {
        if (args.length > 0 && args[0].equals(ARG_TEST))
        {
            ExecuteTests();
        }
    }



    // Private static methods.
    private static void ExecuteTests()
    {

    }

    private static void ExecuteSingleTest(Supplier<Boolean> test, String testName)
    {
        try
        {
            boolean Result = test.get();
            if (Result)
            {
                System.out.printf("Test \"%s\" passed\n", testName);
            }
            else
            {
                System.out.printf("Test \"%s\" failed\n", testName);
            }
        }
        catch (Exception e)
        {
            System.out.printf("Test \"%s\" failed with an exception: %s\n", testName, e.getMessage());
        }
    }
}