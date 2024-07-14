# GHDF-Java
 Java implemenation of the GHDF format reader and writer
This implementation is almost on par with the specification found here: https://docs.google.com/document/d/1sjBgVGZOR6GMlaaR2V2GEGg6UK0UX02IyxWjgNq_3rk/edit?usp=sharing

It has two differeces: 
1: Lengths for compounds, arrays and strings are limited to a maximum value of (2^31 - 1) rather than (2^32 - 1) as specified in the specification. 
2: Unsigned integers are not fully supported. All unsigned integer types are converted to signed types when reading or writing.
