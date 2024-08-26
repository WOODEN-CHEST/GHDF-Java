package sus.keiger.ghdf;

public class GHDFEncodedInteger
{
    // Fields.
    private long _value = 0L;

    // Constructors.
    public GHDFEncodedInteger() { }

    public GHDFEncodedInteger(long value)
    {
        _value = value;
    }


    // Methods.
    public long GetValue()
    {
        return _value;
    }

    public void SetValue(long value)
    {
        _value = value;
    }


    // Inherited methods.
    @Override
    public int hashCode()
    {
        return Long.hashCode(_value);
    }

    @Override
    public String toString()
    {
        return Long.toString(_value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof GHDFEncodedInteger EncodedInt)
        {
            return EncodedInt._value == _value;
        }
        return false;
    }
}