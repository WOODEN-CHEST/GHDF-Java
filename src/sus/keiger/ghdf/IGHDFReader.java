package sus.keiger.ghdf;

import java.io.IOException;
import java.io.InputStream;

public interface IGHDFReader
{
    // Static methods.
    static IGHDFReader GetVersion1()
    {
        return new GHDFReaderVersion1();
    }

    static IGHDFReader GetAutoDetectVersion()
    {
        return new GHDFReaderVersion1();
    }


    // Methods.
    GHDFCompound Read(String filePath) throws IOException;
    GHDFCompound Read(InputStream stream) throws IOException;
}