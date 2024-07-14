package sus.keiger.ghdf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public interface IGHDFWriter
{
    // Static methods.
    static IGHDFWriter Version1()
    {
        return null;
    }


    // Methods.
    public void Write(GHDFCompound compound, String filePath) throws IOException;
    public void Write(GHDFCompound compound, OutputStream stream) throws IOException;
}