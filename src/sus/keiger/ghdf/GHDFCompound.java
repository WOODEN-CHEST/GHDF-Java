package sus.keiger.ghdf;

import java.util.*;

public class GHDFCompound
{
    // Private fields.
    private final Map<Integer, GHDFEntry> _entries = new HashMap<>();


    // Methods.
    public void setEntry(int id, Object value)
    {
        verifyID(id);
        _entries.put(id, new GHDFEntry(value));
    }

    public void removeEntry(int id)
    {
        verifyID(id);
        _entries.remove(id);
    }

    public <T> T getEntry(int id)
    {
        return getEntry(id, null, null, false);
    }

    public <T> T getOrElse(int id, T elseValue)
    {
        return getEntry(id, elseValue, null, false);
    }

    public <T> T getVerifiedEntry(int id, GHDFType expectedType)
    {
        return getEntry(id, null, expectedType, true);
    }

    public <T> T getVerifiedOptionalEntry(int id, GHDFType expectedType)
    {
        return getEntry(id, null, expectedType, false);
    }

    public void clear()
    {
        _entries.clear();
    }

    public GHDFType getTypeOfEntry(int id)
    {
        GHDFEntry Entry = _entries.get(id);
        if (Entry == null)
        {
            return null;
        }
        return Entry.Type;
    }

    public Set<Integer> getIDs()
    {
        return _entries.keySet();
    }

    public List<Object> getValues()
    {
        return _entries.values().stream().map(entry -> entry.Value).toList();
    }

    public int size()
    {
        return _entries.size();
    }


    // Private methods.
    @SuppressWarnings("unchecked")
    private <T> T getEntry(int id, T elseValue, GHDFType expectedType, boolean isMandatory)
    {
        verifyID(id);
        GHDFEntry Entry = _entries.get(id);

        if (Entry == null)
        {
            if (isMandatory && (expectedType != null))
            {
                throw new GHDFEntryException("Mandatory entry of type %s with id %d not found"
                        .formatted(expectedType.toString(), id));
            }
            return elseValue;
        }

        try
        {
            return (T)Entry.Value;
        }
        catch (ClassCastException e)
        {
            if ( expectedType != null)
            {
                throw new GHDFEntryException("Mandatory entry with id %d is of wrong type. Got %s, expected %s."
                        .formatted(id, Entry.Type.toString(), expectedType.toString()));
            }
            return elseValue;
        }
    }

    private void verifyID(int id)
    {
        if (id == 0)
        {
            throw new GHDFEntryException("An entry with the ID 0 is not allowed");
        }
    }


    // Types.
    private static class GHDFEntry
    {
        // Fields.
        public Object Value;
        public GHDFType Type;


        // Constructors.
        public GHDFEntry(Object value)
        {
            if (value instanceof Byte)
            {
                Type = GHDFType.Int8;
            }
            else if (value instanceof Short)
            {
                Type = GHDFType.Int16;
            }
            else if (value instanceof Integer)
            {
                Type = GHDFType.Int32;
            }
            else if (value instanceof Long)
            {
                Type = GHDFType.Int64;
            }
            else if (value instanceof Float)
            {
                Type = GHDFType.Float;
            }
            else if (value instanceof Double)
            {
                Type = GHDFType.Double;
            }
            else if (value instanceof Boolean)
            {
                Type = GHDFType.Boolean;
            }
            else if (value instanceof String)
            {
                Type = GHDFType.String;
            }
            else if (value instanceof GHDFCompound)
            {
                Type = GHDFType.Compound;
            }
            else if (value instanceof Byte[])
            {
                Type = GHDFType.Int8Array;
            }
            else if (value instanceof Short[])
            {
                Type = GHDFType.Int16Array;
            }
            else if (value instanceof Integer[])
            {
                Type = GHDFType.Int32Array;
            }
            else if (value instanceof Long[])
            {
                Type = GHDFType.Int64Array;
            }
            else if (value instanceof Float[])
            {
                Type = GHDFType.FloatArray;
            }
            else if (value instanceof Double[])
            {
                Type = GHDFType.DoubleArray;
            }
            else if (value instanceof Boolean[])
            {
                Type = GHDFType.BooleanArray;
            }
            else if (value instanceof String[])
            {
                Type = GHDFType.StringArray;
            }
            else if (value instanceof GHDFCompound[])
            {
                Type = GHDFType.CompoundArray;
            }

        }
    }
}