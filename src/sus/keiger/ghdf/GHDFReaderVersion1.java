package sus.keiger.ghdf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class GHDFReaderVersion1 implements IGHDFReader
{
    // Private fields.
    private final int VERSION = 1;
    private final Map<GHDFType, TypeReadMethod> _readMethods = new HashMap<>();



    // Constructors.
    public GHDFReaderVersion1()
    {
        _readMethods.put(GHDFType.Int8, this::ReadByte);
        _readMethods.put(GHDFType.UInt8, this::ReadByte);
        _readMethods.put(GHDFType.Int16, this::ReadShort);
        _readMethods.put(GHDFType.UInt16, this::ReadShort);
        _readMethods.put(GHDFType.Int32, this::ReadInt);
        _readMethods.put(GHDFType.UInt32, this::ReadInt);
        _readMethods.put(GHDFType.Int64, this::ReadLong);
        _readMethods.put(GHDFType.UInt64, this::ReadLong);
        _readMethods.put(GHDFType.Float, this::ReadFloat);
        _readMethods.put(GHDFType.Double, this::ReadDouble);
        _readMethods.put(GHDFType.Boolean, this::ReadBoolean);
        _readMethods.put(GHDFType.String, this::ReadString);
        _readMethods.put(GHDFType.Compound, this::ReadCompound);
        _readMethods.put(GHDFType.Int8Array, this::ReadByteArray);
        _readMethods.put(GHDFType.UInt8Array, this::ReadByteArray);
        _readMethods.put(GHDFType.Int16Array, this::ReadShortArray);
        _readMethods.put(GHDFType.UInt16Array, this::ReadShortArray);
        _readMethods.put(GHDFType.Int32Array, this::ReadIntArray);
        _readMethods.put(GHDFType.UInt32Array, this::ReadIntArray);
        _readMethods.put(GHDFType.Int64Array, this::ReadLongArray);
        _readMethods.put(GHDFType.UInt64Array, this::ReadLongArray);
        _readMethods.put(GHDFType.FloatArray, this::ReadFloatArray);
        _readMethods.put(GHDFType.DoubleArray, this::ReadDoubleArray);
        _readMethods.put(GHDFType.BooleanArray, this::ReadBooleanArray);
        _readMethods.put(GHDFType.StringArray, this::ReadStringArray);
        _readMethods.put(GHDFType.CompoundArray, this::ReadCompoundArray);
    }


    // Inherited methods.
    @Override
    public GHDFCompound Read(String filePath) throws IOException
    {
        InputStream FileStream = new FileInputStream(filePath);
        GHDFCompound Compound;
        try
        {
            Compound = Read(FileStream);
            FileStream.close();
        }
        catch (Exception e)
        {
            FileStream.close();
            throw e;
        }
        return Compound;
    }

    @Override
    public GHDFCompound Read(InputStream stream) throws IOException
    {
        ByteBuffer StreamData = ByteBuffer.wrap(stream.readAllBytes()).order(GHDF.ENDIANNESS);
        GHDFCompound Compound;

        try
        {
            VerifySignature(StreamData);
            VerifyVersion(StreamData);
            Compound = ReadCompound(StreamData);
        }
        catch (BufferUnderflowException e)
        {
            throw new GHDFReadException("Failed to read GHDF data because it was incomplete. Inner message: %s"
                    .formatted(e.getMessage()));
        }

        if (StreamData.remaining() > 0)
        {
            throw new GHDFReadException("Trailing data detected in GHDF data stream.");
        }

        return Compound;
    }


    // Private methods.
    private byte ReadByte(ByteBuffer data)
    {
        return data.get();
    }

    private short ReadShort(ByteBuffer data)
    {
        return data.getShort();
    }

    private int ReadInt(ByteBuffer data)
    {
        return data.getInt();
    }

    private long ReadLong(ByteBuffer data)
    {
        return data.getLong();
    }

    private float ReadFloat(ByteBuffer data)
    {
        return data.getFloat();
    }

    private double ReadDouble(ByteBuffer data)
    {
        return data.getDouble();
    }

    private boolean ReadBoolean(ByteBuffer data) throws IOException
    {
        byte Value = data.get();
        if (Value == 0)
        {
            return false;
        }
        else if (Value == 1)
        {
            return true;
        }
        else
        {
            throw new GHDFReadException("Found boolean with invalid value: %d".formatted(Value));
        }
    }

    private String ReadString(ByteBuffer data) throws IOException
    {
        long Length = Read7BitEncodedInt(data);
        if (Length < 0)
        {
            throw new GHDFReadException("Reader does not support strings longer than (2^31 - 1) bytes.");
        }

        byte[] StringBytes = new byte[(int)Length];
        data.get(StringBytes);
        return new String(StringBytes, StandardCharsets.UTF_8);
    }

    private GHDFCompound ReadCompound(ByteBuffer data) throws IOException
    {
        GHDFCompound Compound = new GHDFCompound();
        int EntryCount = Read7BitEncodedInt(data);

        for (int i = 0; i < EntryCount; i++)
        {
            ReadEntry(data, Compound);
        }

        return Compound;
    }

    private byte[] ReadByteArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        byte[] Values = new byte[Length];
        data.get(Values);
        return Values;
    }

    private short[] ReadShortArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        short[] Values = new short[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadShort(data);
        }
        return Values;
    }

    private int[] ReadIntArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        int[] Values = new int[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadInt(data);
        }
        return Values;
    }

    private long[] ReadLongArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        long[] Values = new long[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadLong(data);
        }
        return Values;
    }

    private float[] ReadFloatArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        float[] Values = new float[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadFloat(data);
        }
        return Values;
    }

    private double[] ReadDoubleArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        double[] Values = new double[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadDouble(data);
        }
        return Values;
    }

    private boolean[] ReadBooleanArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        boolean[] Values = new boolean[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadBoolean(data);
        }
        return Values;
    }

    private Object ReadStringArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        String[] Values = new String[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadString(data);
        }
        return Values;
    }

    private Object ReadCompoundArray(ByteBuffer data) throws IOException
    {
        int Length = GetArrayLength(data);
        GHDFCompound[] Values = new GHDFCompound[Length];
        for (int i = 0; i < Length; i++)
        {
            Values[i] = ReadCompound(data);
        }
        return Values;
    }

    private int GetArrayLength(ByteBuffer data) throws IOException
    {
        int Length = Read7BitEncodedInt(data);
        if (Length < 0)
        {
            throw new GHDFReadException("Reader does not support arrays longer than (2^31 - 1) bytes.");
        }
        return Length;
    }

    private void VerifyID(int id) throws IOException
    {
        if (id == 0)
        {
            throw new GHDFReadException("Illegal ID of 0 found in GHDF data stream.");
        }
    }

    private void VerifySignature(ByteBuffer data) throws IOException
    {
        byte[] ReadSignature = new byte[GHDF.SIGNATURE.length];
        data.get(ReadSignature);
        if (!Arrays.equals(GHDF.SIGNATURE, ReadSignature))
        {
            throw new GHDFReadException("Invalid signature, not a GHDF data stream");
        }
    }

    private void VerifyVersion(ByteBuffer data) throws IOException
    {
        int DataVersion = data.getInt();
        if (DataVersion != VERSION)
        {
            throw new GHDFReadException("Unsupported GHDF data version: %d, supported: %d)".formatted(
                    DataVersion, VERSION));
        }
    }

    private int Read7BitEncodedInt(ByteBuffer data) throws IOException
    {
        int Value = 0;
        byte CurrentByte;
        int ByteIndex = 0;
        do
        {
            CurrentByte = data.get();
            Value = Value | ((CurrentByte & 0b0111_1111) << (ByteIndex * 7));
            ByteIndex++;
        }
        while ((CurrentByte & 0b1000_0000) != 0);
        return Value;
    }

    private void ReadEntry(ByteBuffer data, GHDFCompound compound) throws IOException
    {
        int ID = Read7BitEncodedInt(data);
        VerifyID(ID);

        GHDFType EntryType;
        byte TypeByteValue = data.get();
        try
        {
            EntryType = GHDFType.ByteToEnum(TypeByteValue);
        }
        catch (GHDFTypeException e)
        {
            throw new GHDFReadException("Invalid data type for entry with ID %d in GHDF data stream: %d"
                    .formatted(ID, TypeByteValue & 0xFF));
        }

        Object Value;
        try
        {
            Value = _readMethods.get(EntryType).Read(data);
        }
        catch (GHDFReadException e)
        {
            throw new GHDFReadException("Exception reading entry with ID %d. Inner message: { %s }"
                    .formatted(ID, e.getMessage()));
        }

        compound.SetEntry(ID, Value);
    }


    // Types.
    private interface TypeReadMethod
    {
        Object Read(ByteBuffer data) throws IOException;
    }
}
