package sus.keiger.ghdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

class GHDFWriterVersion1 implements IGHDFWriter
{
    // Private static fields.
    private static final byte[] SIGNATURE = new byte[] { (byte)102, (byte)37, (byte)143, (byte)181, (byte)3,
            (byte)205, (byte)123, (byte)185, (byte)148, (byte)157, (byte)98,
            (byte)177, (byte)178, (byte) 151, (byte)43, (byte)170 };

    private static final int VERSION = 1;


    // Methods.
    @Override
    public void Write(GHDFCompound compound, String filePath) throws IOException
    {
        if (filePath == null)
        {
            throw new IllegalArgumentException("filePath is null");
        }
        Write(compound, new FileOutputStream(filePath));
    }

    @Override
    public void Write(GHDFCompound compound, OutputStream stream) throws IOException
    {
        if (compound == null)
        {
            throw new IllegalArgumentException("compound is null");
        }
        if (stream == null)
        {
            throw new IllegalArgumentException("stream is null");
        }

        if ()
    }

    // Private methods.
    private ByteBuffer GetByteBuffer(int capacity)
    {
        return ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
    }

    private void WriteMetadata(OutputStream stream) throws IOException
    {
        stream.write(SIGNATURE);
        stream.write(GetByteBuffer(4).putInt(VERSION).array());
    }

    private void Write7BitEncodedInt(OutputStream stream, int value) throws IOException
    {
        long CurrentValue = value & 0xffffffffL;
        do
        {
            stream.write((int)((CurrentValue & 0b1111_1111) | (CurrentValue > 0b0111_1111 ? 0b1000_0000 : 0)));
            CurrentValue = CurrentValue >> 7;
        } while (CurrentValue > 0);
    }

    private void WriteCompound(OutputStream stream, GHDFCompound compound) throws IOException
    {
        Write7BitEncodedInt(stream, compound.size());
        for (int ID : compound.getIDs())
        {
            WriteEntry(stream, compound.getEntry(ID), compound.getTypeOfEntry(ID));
        }
    }

    private void WriteEntry(OutputStream stream, Object value, GHDFType type) throws IOException
    {

    }
}