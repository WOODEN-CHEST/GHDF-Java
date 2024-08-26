package sus.keiger.ghdf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class GHDFWriterVersion1 implements IGHDFWriter
{
    // Private fields.
    private final int VERSION = 1;
    private final Map<GHDFType, TypeWriteMethod> _typeBasedWriteMethods = new HashMap<>();


    // Constructors.
    GHDFWriterVersion1()
    {
        _typeBasedWriteMethods.put(GHDFType.Int8, (stream, value) -> WriteByte(stream, (byte)value));
        _typeBasedWriteMethods.put(GHDFType.UInt8, (stream, value) -> WriteByte(stream, (byte)value));
        _typeBasedWriteMethods.put(GHDFType.Int16, (stream, value) -> WriteShort(stream, (short)value));
        _typeBasedWriteMethods.put(GHDFType.UInt16, (stream, value) -> WriteShort(stream, (short)value));
        _typeBasedWriteMethods.put(GHDFType.Int32, (stream, value) -> WriteInt(stream, (int)value));
        _typeBasedWriteMethods.put(GHDFType.UInt32, (stream, value) -> WriteInt(stream, (int)value));
        _typeBasedWriteMethods.put(GHDFType.Int64, (stream, value) -> WriteLong(stream, (long)value));
        _typeBasedWriteMethods.put(GHDFType.UInt64, (stream, value) -> WriteLong(stream, (long)value));
        _typeBasedWriteMethods.put(GHDFType.Float, (stream, value) -> WriteFloat(stream, (float)value));
        _typeBasedWriteMethods.put(GHDFType.Double, (stream, value) -> WriteDouble(stream, (double)value));
        _typeBasedWriteMethods.put(GHDFType.Boolean, (stream, value) -> WriteBoolean(stream, (boolean)value));
        _typeBasedWriteMethods.put(GHDFType.String, (stream, value) -> WriteString(stream, (String)value));
        _typeBasedWriteMethods.put(GHDFType.Compound, (stream, value) -> WriteCompound(stream, (GHDFCompound)value));
        _typeBasedWriteMethods.put(GHDFType.EncodedInteger,
                (stream, value) -> WriteEncodedInteger(stream, (GHDFEncodedInteger) value));

        _typeBasedWriteMethods.put(GHDFType.Int8Array,
                (stream, value) -> WriteByteArray(stream, (byte[])value));
        _typeBasedWriteMethods.put(GHDFType.UInt8Array,
                (stream, value) -> WriteByteArray(stream, (byte[])value));
        _typeBasedWriteMethods.put(GHDFType.Int16Array,
                (stream, value) -> WriteShortArray(stream, (short[])value));
        _typeBasedWriteMethods.put(GHDFType.UInt16Array,
                (stream, value) -> WriteShortArray(stream, (short[])value));
        _typeBasedWriteMethods.put(GHDFType.Int32Array,
                (stream, value) -> WriteIntArray(stream, (int[])value));
        _typeBasedWriteMethods.put(GHDFType.UInt32Array,
                (stream, value) -> WriteIntArray(stream, (int[])value));
        _typeBasedWriteMethods.put(GHDFType.Int64Array,
                (stream, value) -> WriteLongArray(stream, (long[])value));
        _typeBasedWriteMethods.put(GHDFType.UInt64Array
                , (stream, value) -> WriteLongArray(stream, (long[])value));
        _typeBasedWriteMethods.put(GHDFType.FloatArray,
                (stream, value) -> WriteFloatArray(stream, (float[])value));
        _typeBasedWriteMethods.put(GHDFType.DoubleArray,
                (stream, value) -> WriteDoubleArray(stream, (double[])value));
        _typeBasedWriteMethods.put(GHDFType.BooleanArray,
                (stream, value) -> WriteBooleanArray(stream, (boolean[])value));
        _typeBasedWriteMethods.put(GHDFType.StringArray,
                (stream, value) -> WriteStringArray(stream, (String[])value));
        _typeBasedWriteMethods.put(GHDFType.CompoundArray,
                (stream, value) -> WriteCompoundArray(stream, (GHDFCompound[])value));
        _typeBasedWriteMethods.put(GHDFType.EncodedIntegerArray,
                (stream, value) -> WriteEncodedIntegerArray(stream, (GHDFEncodedInteger[])value));
    }


    // Inherited methods.
    @Override
    public void Write(GHDFCompound compound, String filePath) throws IOException
    {
        Objects.requireNonNull(filePath, "filePath is null");
        
        try (FileOutputStream FileStream = new FileOutputStream(ChangeExtensionToGHDF(filePath)))
        {
            Write(compound, FileStream);
        }
    }

    @Override
    public void Write(GHDFCompound compound, OutputStream stream) throws IOException
    {
        Objects.requireNonNull(compound, "compound is null");
        Objects.requireNonNull(stream, "stream is null");

        WriteMetadata(stream);
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();

        _typeBasedWriteMethods.get(GHDFType.Compound).Write(ByteStream, compound);
        ByteStream.writeTo(stream);
    }

    // Private methods.
    private ByteBuffer GetByteBuffer(int size)
    {
        return ByteBuffer.allocate(size).order(GHDF.ENDIANNESS);
    }

    private String ChangeExtensionToGHDF(String path)
    {
        int Index = path.lastIndexOf('.');

        if (Index != -1)
        {
            if (path.endsWith(GHDF.EXTENSION))
            {
                return path;
            }
            return "%s%s".formatted(path.substring(0, Index), GHDF.EXTENSION);
        }
        return "%s%s".formatted(path, GHDF.EXTENSION);
    }

    private void VerifyID(long id) throws IOException
    {
        if (id == 0)
        {
            throw new GHDFWriteException("Cannot write entry with ID 0, invalid ID.");
        }
    }

    private void WriteMetadata(OutputStream stream) throws IOException
    {
        stream.write(GHDF.SIGNATURE);
        Write7BitEncodedInt(stream, VERSION);
    }

    private void Write7BitEncodedInt(OutputStream stream, long value) throws IOException
    {
        long CurrentValue = value;
        do
        {
            stream.write((int)((CurrentValue & 0b0111_1111)
                    | ((CurrentValue > 0b0111_1111 || CurrentValue < 0) ? 0b1000_0000 : 0)));
            CurrentValue = CurrentValue >>> 7;
        } while (CurrentValue != 0);
    }

    private void WriteByte(OutputStream stream, byte value) throws IOException
    {
        stream.write(value);
    }

    private void WriteShort(OutputStream stream, short value) throws IOException
    {
        stream.write(GetByteBuffer(2).putShort(value).array());
    }

    private void WriteInt(OutputStream stream, int value) throws IOException
    {
        stream.write(GetByteBuffer(4).putInt(value).array());
    }

    private void WriteLong(OutputStream stream, long value) throws IOException
    {
        stream.write(GetByteBuffer(8).putLong(value).array());
    }

    private void WriteFloat(OutputStream stream, float value) throws IOException
    {
        stream.write(GetByteBuffer(4).putFloat(value).array());
    }

    private void WriteDouble(OutputStream stream, double value) throws IOException
    {
        stream.write(GetByteBuffer(8).putDouble(value).array());
    }

    private void WriteBoolean(OutputStream stream, boolean value) throws IOException
    {
        stream.write(value ? 1 : 0);
    }

    private void WriteString(OutputStream stream, String value) throws IOException
    {
        byte[] StringBytes = value.getBytes(StandardCharsets.UTF_8);
        Write7BitEncodedInt(stream, StringBytes.length);
        stream.write(StringBytes);
    }

    private void WriteCompound(OutputStream stream, GHDFCompound value) throws IOException
    {
        Write7BitEncodedInt(stream, value.Size());

        long CurrentID = 0;
        try
        {
            for (long ID : value.GetIDs())
            {
                CurrentID = ID;
                VerifyID(ID);
                WriteEntry(stream, ID, value.GetEntry(ID), value.GetTypeOfEntry(ID));
            }
        }
        catch (GHDFWriteException e)
        {

            throw new GHDFWriteException("Failed to write compound entry with ID %s. Nested fail: { %s }"
                    .formatted(CurrentID != 0 ? Long.toString(CurrentID) : "[Invalid ID of 0]", e.getMessage()));
        }
    }

    private void WriteEncodedInteger(OutputStream stream, GHDFEncodedInteger value) throws IOException
    {
        Write7BitEncodedInt(stream, value.GetValue());
    }

    private void WriteByteArray(OutputStream stream, byte[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        stream.write(array);
    }

    private void WriteShortArray(OutputStream stream, short[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        ByteBuffer Buffer = GetByteBuffer(2 * array.length);
        for (short Value : array)
        {
            Buffer.putShort(Value);
        }
        stream.write(Buffer.array());
    }

    private void WriteIntArray(OutputStream stream, int[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        ByteBuffer Buffer = GetByteBuffer(4 * array.length);
        for (int Value : array)
        {
            Buffer.putInt(Value);
        }
        stream.write(Buffer.array());
    }

    private void WriteLongArray(OutputStream stream, long[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        ByteBuffer Buffer = GetByteBuffer(8 * array.length);
        for (long Value : array)
        {
            Buffer.putLong(Value);
        }
        stream.write(Buffer.array());
    }

    private void WriteFloatArray(OutputStream stream, float[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        ByteBuffer Buffer = GetByteBuffer(4 * array.length);
        for (float Value : array)
        {
            Buffer.putFloat(Value);
        }
        stream.write(Buffer.array());
    }

    private void WriteDoubleArray(OutputStream stream, double[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        ByteBuffer Buffer = GetByteBuffer(8 * array.length);
        for (double Value : array)
        {
            Buffer.putDouble(Value);
        }
        stream.write(Buffer.array());
    }

    private void WriteBooleanArray(OutputStream stream, boolean[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        byte[] Buffer = new byte[array.length];
        for (int i = 0; i < array.length; i++)
        {
            Buffer[i] = array[i] ? (byte)1 : (byte)0;
        }
        stream.write(Buffer);
    }

    private void WriteStringArray(OutputStream stream, String[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        for (String Value : array)
        {
            WriteString(stream, Value);
        }
    }

    private void WriteCompoundArray(OutputStream stream, GHDFCompound[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        for (GHDFCompound Value : array)
        {
            WriteCompound(stream, Value);
        }
    }

    private void WriteEncodedIntegerArray(OutputStream stream, GHDFEncodedInteger[] array) throws IOException
    {
        Write7BitEncodedInt(stream, array.length);
        for (GHDFEncodedInteger Value : array)
        {
            Write7BitEncodedInt(stream, Value.GetValue());
        }
    }

    private void WriteEntry(OutputStream stream, long id, Object value, GHDFType type) throws IOException
    {
        if (id == 0)
        {
            throw new GHDFWriteException("Invalid ID of 0 for entry.");
        }

        TypeWriteMethod ChosenMethod = _typeBasedWriteMethods.get(type);
        if (ChosenMethod == null)
        {
            throw new GHDFWriteException("Invalid GHDF type \"%s\", cannot write data.".formatted(type.toString()));
        }

        try
        {
            Write7BitEncodedInt(stream, id);
            stream.write(type.GetByteData() & 0xff);
            ChosenMethod.Write(stream, value);
        }
        catch (ClassCastException e)
        {
            throw new GHDFWriteException("Failed to write entry with ID %d of type %s.".formatted(
                    id, type.toString()));
        }
    }


    // Types.
    private interface TypeWriteMethod
    {
        void Write(OutputStream stream, Object value) throws IOException;
    }
}