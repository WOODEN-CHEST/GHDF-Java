package sus.keiger.ghdf;

import java.io.IOException;
import java.io.OutputStream;

public interface IGHDFWriter
{
    // Static methods.
    static IGHDFWriter GetVersion1()
    {
        return new GHDFWriterVersion1();
    }


    // Methods.
    public void Write(GHDFCompound compound, String filePath) throws IOException;
    public void Write(GHDFCompound compound, OutputStream stream) throws IOException;
}