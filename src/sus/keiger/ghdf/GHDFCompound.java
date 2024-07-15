package sus.keiger.ghdf;

import java.util.*;

public class GHDFCompound
{
    // Private fields.
    private final Map<Integer, GHDFEntry> _entries = new HashMap<>();


    // Methods.
    public void SetEntry(int id, Object value)
    {
        VerifyID(id);
        _entries.put(id, new GHDFEntry(value));
    }

    public void RemoveEntry(int id)
    {
        VerifyID(id);
        _entries.remove(id);
    }

    public <T> T GetEntry(int id)
    {
        return GetEntry(id, null, null, false);
    }

    public <T> T GetOrElse(int id, T elseValue)
    {
        return GetEntry(id, elseValue, null, false);
    }

    public <T> T GetVerifiedEntry(int id, GHDFType expectedType)
    {
        return GetEntry(id, null, expectedType, true);
    }

    public <T> T GetVerifiedOptionalEntry(int id, GHDFType expectedType)
    {
        return GetEntry(id, null, expectedType, false);
    }

    public void Clear()
    {
        _entries.clear();
    }

    public GHDFType GetTypeOfEntry(int id)
    {
        GHDFEntry Entry = _entries.get(id);
        if (Entry == null)
        {
            return null;
        }
        return Entry.Type;
    }

    public Set<Integer> GetIDs()
    {
        return _entries.keySet();
    }

    public List<Object> GetValues()
    {
        return _entries.values().stream().map(entry -> entry.Value).toList();
    }

    public int Size()
    {
        return _entries.size();
    }


    // Private methods.
    @SuppressWarnings("unchecked")
    private <T> T GetEntry(int id, T elseValue, GHDFType expectedType, boolean isMandatory)
    {
        VerifyID(id);
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

    private void VerifyID(int id)
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
            Value = value;
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
            else if (value instanceof byte[])
            {
                Type = GHDFType.Int8Array;
            }
            else if (value instanceof short[])
            {
                Type = GHDFType.Int16Array;
            }
            else if (value instanceof int[])
            {
                Type = GHDFType.Int32Array;
            }
            else if (value instanceof long[])
            {
                Type = GHDFType.Int64Array;
            }
            else if (value instanceof float[])
            {
                Type = GHDFType.FloatArray;
            }
            else if (value instanceof double[])
            {
                Type = GHDFType.DoubleArray;
            }
            else if (value instanceof boolean[])
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
            else
            {
                throw new GHDFEntryException("Invalid entry type: %s".formatted(value.getClass().toString()));
            }
        }
    }
}