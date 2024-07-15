package sus.keiger.ghdf;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

public class test
{
    public static void main(String[] args)
    {
        GHDFCompound Compound = new GHDFCompound();

        GHDFCompound NestedCompound = new GHDFCompound();
        NestedCompound.SetEntry(1, (byte)3);
        NestedCompound.SetEntry(2, "Hello World!");
        NestedCompound.SetEntry(3, 3.1415f);
        NestedCompound.SetEntry(4, 2.718d);
        NestedCompound.SetEntry(5, (short)5);
        NestedCompound.SetEntry(6, 920L);
        NestedCompound.SetEntry(7, false);
        NestedCompound.SetEntry(8, true);
        NestedCompound.SetEntry(9, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        NestedCompound.SetEntry(10, new String[] { "a", "b", "c", "d", "e", "f", "g", "h" });

        GHDFCompound[] CompoundArray = new GHDFCompound[1_000_000];
        Arrays.fill(CompoundArray, NestedCompound);
        Compound.SetEntry(1, CompoundArray);

        long Start = System.nanoTime();
        IGHDFWriter Writer = IGHDFWriter.GetVersion1();
        try
        {
            Writer.Write(Compound, "C:/Users/User/Desktop/file.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        long End = System.nanoTime();
        double ElapsedMilliseconds = (double)(End - Start) / 1_000_000d;
        System.out.println("%sms".formatted(new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.US))
                .format(ElapsedMilliseconds)));
    }
}