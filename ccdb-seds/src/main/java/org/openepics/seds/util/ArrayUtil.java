/* 
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *  
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *  
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.seds.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayLong;
import org.epics.util.array.ArrayShort;
import org.epics.util.array.IteratorNumber;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListLong;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListShort;

/**
 * Utility for converting containers (Lists and Arrays) into other container
 * types or adjusting the element type of the container.
 *
 * <p>
 * Specifically, {@code ArrayUtil} enables a collection of elements to switch
 * between the following types:
 * <ul>
 * <li>{@link JsonArray}
 * <li>{@link ListNumber} (and subtypes such as {@link ListInt}
 * <li>{@link List} (with a correct generic type)
 * <li>{@link AsUnboxedArray UnboxedArray} (arrays of Java's unboxed primitives,
 * such as int[])
 * <li>{@code AsBoxedArray BoxedArray} (arrays of Java's boxed primitives, such
 * as Integer[], as well as other object arrays (ie - Number[]))
 * </ul>
 *
 * <p>
 * The conversions are primarily supported for only primitive types (Boolean,
 * Integer, Number, String, Double, etc.), with a few exceptions (JsonArray).
 *
 * <p>
 * The utility functions ALWAYS returns null if the parameter container is null.
 * The utility functions ALWAYS include null elements while copying elements
 * from one container type to another.
 *
 * @author Aaron Barber
 */
public class ArrayUtil {

    private ArrayUtil() {

    }

    /**
     * Utility to convert containers into {@link AsJsonArray JsonArray}
     * containers.
     * <p>
     * The container types able to be converted to a JsonArray are:
     * {@link AsBoxedArray BoxedArray} containers, {@link ListNumber}
     * containers, and {@link List} containers.
     */
    public static class AsJsonArray {

        private static JsonArrayBuilder put(JsonArrayBuilder builder, Object value) {
            if (value == null) {
                builder.add(JsonValue.NULL);
            } else if (value instanceof JsonValue) {
                builder.add((JsonValue) value);
            } else if (value instanceof BigInteger) {
                builder.add((BigInteger) value);
            } else if (value instanceof BigDecimal) {
                builder.add((BigDecimal) value);
            } else if (value instanceof Boolean) {
                builder.add((Boolean) value);
            } else if (value instanceof Byte) {
                builder.add((Byte) value);
            } else if (value instanceof Double && !(Double.isNaN((double) value)) && !(Double.isInfinite((double) value))) {
                builder.add((Double) value);
            } else if (value instanceof Float && !(Float.isNaN((float) value)) && !(Float.isInfinite((float) value))) {
                builder.add((Float) value);
            } else if (value instanceof Integer) {
                builder.add((Integer) value);
            } else if (value instanceof Long) {
                builder.add((Long) value);
            } else if (value instanceof Short) {
                builder.add((Short) value);
            } else if (value instanceof String) {
                builder.add((String) value);
            } else if (value instanceof JsonObjectBuilder) {
                builder.add((JsonObjectBuilder) value);
            } else if (value instanceof JsonArrayBuilder) {
                builder.add((JsonArrayBuilder) value);
            } else {
                builder.add(JsonValue.NULL);
            }

            return builder;
        }

        //List --> JsonArray
        //---------------------------------------------------------------------- 
        /**
         * Converts the container (List) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * <p>
         * The supported element types are those that are supported with
         * {@link JsonArrayBuilder} (JSON primitives, JSON Array, JSON Object).
         * If the type of element was not supported, a null value is place in
         * the JsonArray.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(List value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Object object : value) {
                put(arr, object);
            }

            return arr.build();
        }

        /**
         * Converts the container (ListInt) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(ListInt value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            IteratorNumber data = value.iterator();
            while (data.hasNext()) {
                arr.add(data.nextInt());
            }

            return arr.build();
        }

        /**
         * Converts the container (ListNumber) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(ListNumber value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            IteratorNumber data = value.iterator();
            while (data.hasNext()) {
                put(arr, data.nextDouble());
            }

            return arr.build();
        }
        //----------------------------------------------------------------------         

        //BoxedArray --> JsonArray
        //----------------------------------------------------------------------         
        /**
         * Converts the container (Array of JsonArray) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * <p>
         * For example,
         * <blockquote>
         * <pre>
         * Let F and G be JSON Arrays.
         *      F = JsonArray{1, 2}
         *      G = JsonArray{'a', 'b'}
         *
         * This function returns JsonArray{F, G}, or:
         *      returns JsonArray{JsonArray{1, 2}, JsonArray{'a', 'b'}}
         * </pre>
         * </blockquote>
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(JsonArray[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (JsonArray element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Number Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Number[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Number element : value) {
                put(arr, element.doubleValue());
            }

            return arr.build();
        }

        /**
         * Converts the container (Boolean Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Boolean[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Boolean element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Byte Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Byte[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Byte element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Double Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Double[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Double element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Float Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Float[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Float element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Integer Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Integer[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Integer element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Long Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Long[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Long element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (Short Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(Short[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (Short element : value) {
                put(arr, element);
            }

            return arr.build();
        }

        /**
         * Converts the container (String Array) into a JsonArray.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the JsonArray
         * @return JsonArray with elements from the container, returns null if
         * no container
         */
        public static JsonArray typeJson(String[] value) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (String element : value) {
                put(arr, element);
            }

            return arr.build();
        }
        //----------------------------------------------------------------------  

    }

    /**
     * Utility to convert containers into {@link ListNumber} containers.
     * <p>
     * The container types able to be converted to a JsonArray are:
     * {@link AsBoxedArray BoxedArray} containers.
     */
    public static class AsListNumber {

        //BoxedArray --> ListNumber
        //----------------------------------------------------------------------
        /**
         * Converts the container (Number Array) into a ListNumber.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the ListNumber
         * @return ListNumber with elements from the parameter container,
         * returns null if no container
         */
        public static ListNumber typeNumber(Number[] value) {
            if (value == null) {
                return null;
            }

            double[] out = new double[value.length];

            for (int i = 0; i < out.length; i++) {
                out[i] = value[i].doubleValue();
            }

            return new ArrayDouble(out);
        }

        /**
         * Converts the container (Double Array) into a ListDouble.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the ListDouble
         * @return ListDouble with elements from the parameter container,
         * returns null if no container
         */
        public static ListDouble typeDouble(Double[] value) {
            if (value == null) {
                return null;
            }

            double[] out = new double[value.length];

            for (int i = 0; i < out.length; i++) {
                out[i] = value[i];
            }

            return new ArrayDouble(out);
        }

        /**
         * Converts the container (Float Array) into a ListFloat.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the ListFloat
         * @return ListFloat with elements from the parameter container, returns
         * null if no container
         */
        public static ListFloat typeFloat(Float[] value) {
            if (value == null) {
                return null;
            }

            float[] out = new float[value.length];

            for (int i = 0; i < out.length; i++) {
                out[i] = value[i];
            }

            return new ArrayFloat(out);
        }

        /**
         * Converts the container (Integer Array) into a ListInt.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the ListInt
         * @return ListInt with elements from the parameter container, returns
         * null if no container
         */
        public static ListInt typeInteger(Integer[] value) {
            if (value == null) {
                return null;
            }

            int[] out = new int[value.length];

            for (int i = 0; i < out.length; i++) {
                out[i] = value[i];
            }

            return new ArrayInt(out);
        }

        /**
         * Converts the container (Long Array) into a ListLong.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the ListLong
         * @return ListLong with elements from the parameter container, returns
         * null if no container
         */
        public static ListLong typeLong(Long[] value) {
            if (value == null) {
                return null;
            }

            long[] out = new long[value.length];

            for (int i = 0; i < out.length; i++) {
                out[i] = value[i];
            }

            return new ArrayLong(out);
        }

        /**
         * Converts the container (Short Array) into a ListShort.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the ListShort
         * @return ListShort with elements from the parameter container, returns
         * null if no container
         */
        public static ListShort typeShort(Short[] value) {
            if (value == null) {
                return null;
            }

            short[] out = new short[value.length];

            for (int i = 0; i < out.length; i++) {
                out[i] = value[i];
            }

            return new ArrayShort(out);
        }
        //----------------------------------------------------------------------

    }

    /**
     * Utility to convert containers into {@link List} containers.
     * <p>
     * The container types able to be converted to a JsonArray are:
     * {@link AsBoxedArray BoxedArray} containers.
     */
    public static class AsList {

        //BoxedArray --> List
        //----------------------------------------------------------------------
        /**
         * Converts the container (Boolean Array) into a List (of Boolean
         * elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Boolean elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Boolean> typeBoolean(Boolean[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (Byte Array) into a List (of Byte elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Byte elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Byte> typeByte(Byte[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (Double Array) into a List (of Double
         * elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Double elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Double> typeDouble(Double[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (Float Array) into a List (of Float elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Float elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Float> typeFloat(Float[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (Integer Array) into a List (of Integer
         * elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Integer elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Integer> typeInteger(Integer[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (Long Array) into a List (of Long elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Long elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Long> typeLong(Long[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (Short Array) into a List (of Short elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of Short elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<Short> typeShort(Short[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }

        /**
         * Converts the container (String Array) into a List (of String
         * elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the List
         * @return List (of String elements) with elements from the parameter
         * container, returns null if no container
         */
        public static List<String> typeString(String[] value) {
            if (value == null) {
                return null;
            }

            return Arrays.asList(value);
        }
        //----------------------------------------------------------------------
    }

    /**
     * Utility to convert containers into {@link AsUnboxedArray UnboxedArray}
     * containers.
     * <p>
     * An unboxed array is defined as a container containing Java unboxed
     * primitive elements (an int[], not an Integer[]).
     *
     * <p>
     * The container types able to be converted to a JsonArray are: {@link List}
     * containers.
     */
    public static class AsUnboxedArray {

        //List --> UnboxedArray
        //----------------------------------------------------------------------
        /**
         * Converts the container (Boolean List) into a {@code UnboxedArray} (of
         * Boolean elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Boolean elements) with elements from the
         * parameter container, returns null if no container
         */
        public static boolean[] typeBoolean(List<Boolean> in) {
            if (in == null) {
                return null;
            }

            boolean[] out = new boolean[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (Byte List) into a {@code UnboxedArray} (of
         * Byte elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Byte elements) with elements from the
         * parameter container, returns null if no container
         */
        public static byte[] typeByte(List<Byte> in) {
            if (in == null) {
                return null;
            }

            byte[] out = new byte[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (Double List) into a {@code UnboxedArray} (of
         * Double elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Double elements) with elements from the
         * parameter container, returns null if no container
         */
        public static double[] typeDouble(List<Double> in) {
            if (in == null) {
                return null;
            }

            double[] out = new double[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (Float List) into a {@code UnboxedArray} (of
         * Float elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Float elements) with elements from the
         * parameter container, returns null if no container
         */
        public static float[] typeFloat(List<Float> in) {
            if (in == null) {
                return null;
            }

            float[] out = new float[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (Integer List) into a {@code UnboxedArray} (of
         * Integer elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Integer elements) with elements from the
         * parameter container, returns null if no container
         */
        public static int[] typeInteger(List<Integer> in) {
            if (in == null) {
                return null;
            }

            int[] out = new int[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (Long List) into a {@code UnboxedArray} (of
         * Long elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Long elements) with elements from the
         * parameter container, returns null if no container
         */
        public static long[] typeLong(List<Long> in) {
            if (in == null) {
                return null;
            }

            long[] out = new long[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (Short List) into a {@code UnboxedArray} (of
         * Short elements).
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the
         * {@code UnboxedArray}
         * @return UnboxedArray (of Short elements) with elements from the
         * parameter container, returns null if no container
         */
        public static short[] typeShort(List<Short> in) {
            if (in == null) {
                return null;
            }

            short[] out = new short[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }
        //----------------------------------------------------------------------  

    }

    /**
     * Utility to convert containers into {@link AsBoxedArray BoxedArray}
     * containers.
     * <p>
     * A boxed array is defined as a container containing Java boxed primitive
     * elements (an Integer[], not an int[]).
     * <p>
     * A boxed array also includes common object arrays such as
     * {@code JsonArray[]} and {@code Number[]}.
     * <p>
     * The container types able to be converted to a JsonArray are:
     * {@link JsonArray} containers, {@link ListNumber} containers, and
     * {@link List} containers.
     */
    public static class AsBoxedArray {

        //JsonArray --> BoxedArray
        //---------------------------------------------------------------------- 
        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as the type JsonArray.
         *
         * <p>
         * For example,
         * <pre>
         * Let X, Y, Z be JSON Arrays.
         *      X = JsonArray{1, 2}
         *      Y = JsonArray{'a', 'b'}
         *      Z = JsonArray{X, Y}
         * or...
         *      Z = JsonArray{JsonArray{1, 2}, JsonArray{'a', 'b'}}
         *
         * This function, <i>typeJson(Z)</i> returns an array of JsonArrays:
         *      returns JsonArray[]{X, Y}
         *      or...   JsonArray[]{JsonArray{1, 2}, JsonArray{'a', 'b'}}
         * </pre>
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with elements of type JsonArray) where the
         * elements are from the parameter container, returns null if no
         * parameter container
         */
        public static JsonArray[] typeJson(JsonArray value) {
            if (value == null) {
                return null;
            }

            JsonArray[] arr = new JsonArray[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getJsonArray(i);
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Number types.
         *
         * <p>
         * In practice, all elements of the JsonArray are parsed as
         * {@code Double} elements. For example,
         * <pre>
         * Let X be a JSON Array:
         *      X = JsonArray{1, 3.14, 6.28}
         *
         * <i>typeNumber(X)</i> returns a Number array with Double elements:
         *      returns Number[]{(Double) 1, (Double) 3.14, (Double) 6.28}
         * </pre>
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Number elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Number[] typeNumber(JsonArray value) {
            if (value == null) {
                return null;
            }

            Number[] arr = new Number[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getJsonNumber(i).doubleValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Boolean types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Boolean elements) where the elements are
         * from the parameter container, returns null if no parameter container
         */
        public static Boolean[] typeBoolean(JsonArray value) {
            if (value == null) {
                return null;
            }

            Boolean[] arr = new Boolean[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getBoolean(i);
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Byte types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Byte elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Byte[] typeByte(JsonArray value) {
            if (value == null) {
                return null;
            }

            Byte[] arr = new Byte[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = (byte) value.getJsonNumber(i).doubleValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Double types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Double elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Double[] typeDouble(JsonArray value) {
            if (value == null) {
                return null;
            }

            Double[] arr = new Double[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getJsonNumber(i).doubleValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Float types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Float elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Float[] typeFloat(JsonArray value) {
            if (value == null) {
                return null;
            }

            Float[] arr = new Float[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = (float) value.getJsonNumber(i).doubleValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Integer types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Integer elements) where the elements are
         * from the parameter container, returns null if no parameter container
         */
        public static Integer[] typeInteger(JsonArray value) {
            if (value == null) {
                return null;
            }

            Integer[] arr = new Integer[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getJsonNumber(i).intValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Long types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Long elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Long[] typeLong(JsonArray value) {
            if (value == null) {
                return null;
            }

            Long[] arr = new Long[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getJsonNumber(i).longValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as Short types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with Short elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Short[] typeShort(JsonArray value) {
            if (value == null) {
                return null;
            }

            Short[] arr = new Short[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = (short) value.getJsonNumber(i).doubleValue();
                }
            }

            return arr;
        }

        /**
         * Converts the container (JsonArray) into a {@code BoxedArray} by
         * parsing the elements of the JsonArray as String types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param value container whose elements are put in the
         * {@code BoxedArray}
         * @return BoxedArray (with String elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static String[] typeString(JsonArray value) {
            if (value == null) {
                return null;
            }

            String[] arr = new String[value.size()];

            for (int i = 0; i < value.size(); ++i) {
                if (value.get(i).getValueType().compareTo(JsonValue.ValueType.NULL) == 0) {
                    arr[i] = null;
                } else {
                    arr[i] = value.getString(i);
                }
            }

            return arr;
        }
        //---------------------------------------------------------------------- 

        //ListNumber --> BoxedArray
        //----------------------------------------------------------------------
        /**
         * Converts the container (ListNumber) into a {@code BoxedArray} by
         * parsing the elements of the ListNumber as Number types.
         *
         * <p>
         * In practice, all elements of the ListNumber are parsed as
         * {@code Double} elements. For example,
         * <pre>
         * Let X be a ListNumber:
         *      X = ListNumber{1, 3.14, 6.28}
         *
         * <i>typeNumber(X)</i> returns a Number array with Double elements:
         *      returns Number[]{(Double) 1, (Double) 3.14, (Double) 6.28}
         * </pre>
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Number elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Number[] typeNumber(ListNumber in) {
            if (in == null) {
                return null;
            }

            Number[] out = new Number[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextDouble();
            }

            return out;
        }

        /**
         * Converts the container (ListByte) into a {@code BoxedArray} by
         * parsing the elements of the ListByte as Byte types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Byte elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Byte[] typeByte(ListByte in) {
            if (in == null) {
                return null;
            }

            Byte[] out = new Byte[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextByte();
            }

            return out;
        }

        /**
         * Converts the container (ListDouble) into a {@code BoxedArray} by
         * parsing the elements of the ListDouble as Double types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Double elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Double[] typeDouble(ListDouble in) {
            if (in == null) {
                return null;
            }

            Double[] out = new Double[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextDouble();
            }

            return out;
        }

        /**
         * Converts the container (ListFloat) into a {@code BoxedArray} by
         * parsing the elements of the ListFloat as Float types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Float elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Float[] typeFloat(ListFloat in) {
            if (in == null) {
                return null;
            }

            Float[] out = new Float[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextFloat();
            }

            return out;
        }

        /**
         * Converts the container (ListInteger) into a {@code BoxedArray} by
         * parsing the elements of the ListInteger as Integer types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Integer elements) where the elements are
         * from the parameter container, returns null if no parameter container
         */
        public static Integer[] typeInteger(ListInt in) {
            if (in == null) {
                return null;
            }

            Integer[] out = new Integer[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextInt();
            }

            return out;
        }

        /**
         * Converts the container (ListLong) into a {@code BoxedArray} by
         * parsing the elements of the ListLong as Long types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Long elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Long[] typeLong(ListLong in) {
            if (in == null) {
                return null;
            }

            Long[] out = new Long[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextLong();
            }

            return out;
        }

        /**
         * Converts the container (ListShort) into a {@code BoxedArray} by
         * parsing the elements of the ListShort as Short types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Short elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static Short[] typeShort(ListShort in) {
            if (in == null) {
                return null;
            }

            Short[] out = new Short[in.size()];
            int index = 0;

            IteratorNumber i = in.iterator();
            while (i.hasNext()) {
                out[index++] = i.nextShort();
            }

            return out;
        }

        /**
         * Converts the container (ListBoolean) into a {@code BoxedArray} by
         * parsing the elements of the ListBoolean as Boolean types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with Boolean elements) where the elements are
         * from the parameter container, returns null if no parameter container
         */
        public static Boolean[] typeBoolean(List<Boolean> in) {
            if (in == null) {
                return null;
            }

            Boolean[] out = new Boolean[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }

        /**
         * Converts the container (ListString) into a {@code BoxedArray} by
         * parsing the elements of the ListString as String types.
         *
         * <p>
         * Null elements ARE include when copying from container to container.
         *
         * @param in container whose elements are put in the {@code BoxedArray}
         * @return BoxedArray (with String elements) where the elements are from
         * the parameter container, returns null if no parameter container
         */
        public static String[] typeString(List<String> in) {
            if (in == null) {
                return null;
            }

            String[] out = new String[in.size()];

            for (int i = 0; i < out.length; i++) {
                out[i] = in.get(i);
            }

            return out;
        }
        //----------------------------------------------------------------------           
    }

}
