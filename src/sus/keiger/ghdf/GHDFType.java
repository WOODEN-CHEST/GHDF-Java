package sus.keiger.ghdf;

public enum GHDFType
{
    UInt8((byte)1, false),
    Int8((byte)2, false),
    UInt16((byte)3, false),
    Int16((byte)4, false),
    UInt32((byte)5, false),
    Int32((byte)6, false),
    UInt64((byte)7, false),
    Int64((byte)8, false),
    Float((byte)9, false),
    Double((byte)10, false),
    Boolean((byte)11, false),
    String((byte)12, false),
    Compound((byte)13, false),
    UInt8Array((byte)1, true),
    Int8Array((byte)2, true),
    UInt16Array((byte)3, true),
    Int16Array((byte)4, true),
    UInt32Array((byte)5, true),
    Int32Array((byte)6, true),
    UInt64Array((byte)7, true),
    Int64Array((byte)8, true),
    FloatArray((byte)9, true),
    DoubleArray((byte)10, true),
    BooleanArray((byte)11, true),
    StringArray((byte)12, true),
    CompoundArray((byte)13, true);


    // Static fields.
    public static final byte ARRAY_BIT = (byte)0b1000_0000;


    // Private fields.
    private final byte _byteData;


    // Constructors.
    GHDFType(byte valueType, boolean isArray)
    {
        _byteData = (byte)(valueType | (isArray ? ARRAY_BIT : 0));
    }


    // Static methods.
    public static GHDFType ByteToEnum(byte value)
    {
        if ((value & ARRAY_BIT) != 0)
        {
            byte ModifiedValue = (byte)(value & (~ARRAY_BIT));
            return switch (ModifiedValue)
            {
                case 1 -> UInt8Array;
                case 2 -> Int8Array;
                case 3 -> UInt16Array;
                case 4 -> Int16Array;
                case 5 -> UInt32Array;
                case 6 -> Int32Array;
                case 7 -> UInt64Array;
                case 8 -> Int64Array;
                case 9 -> FloatArray;
                case 10 -> DoubleArray;
                case 11 -> BooleanArray;
                case 12 -> StringArray;
                case 13 -> CompoundArray;
                default -> throw new GHDFTypeException("Invalid type byte value: %d".formatted(value));
            };
        }

        return switch (value)
        {
            case 1 -> UInt8;
            case 2 -> Int8;
            case 3 -> UInt16;
            case 4 -> Int16;
            case 5 -> UInt32;
            case 6 -> Int32;
            case 7 -> UInt64;
            case 8 -> Int64;
            case 9 -> Float;
            case 10 -> Double;
            case 11 -> Boolean;
            case 12 -> String;
            case 13 -> Compound;
            default -> throw new GHDFTypeException("Invalid type byte value: %d".formatted(value));
        };
    }


    // Methods.
    public byte GetOnlyType()
    {
        return (byte)(_byteData & (~ARRAY_BIT));
    }

    public boolean IsArray()
    {
        return (_byteData & ARRAY_BIT) != 0;
    }

    public byte GetByteData()
    {
        return _byteData;
    }
}