package org.openepics.discs.conf.ent.values;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ValueDisplayTest {

    @Test
    public void intVectorTest() {
        List<Integer> testValues = new ArrayList<>();
        testValues.add(0);
        testValues.add(1);
        testValues.add(2);
        testValues.add(3);
        testValues.add(4);
        testValues.add(5);
        testValues.add(6);

        final List<Integer> list1 = testValues.subList(0, 1);
        IntVectorValue intVectorValue = new IntVectorValue(list1);
        final String expectedOutput1 = "[0]";
        assertEquals("Outputs differ. Expected: " + expectedOutput1 + ", Actual: " + intVectorValue.toString(), expectedOutput1, intVectorValue.toString());

        final List<Integer> list2 = testValues.subList(0, 2);
        intVectorValue = new IntVectorValue(list2);
        final String expectedOutput2 = "[0, 1]";
        assertEquals("Outputs differ. Expected: " + expectedOutput2 + ", Actual: " + intVectorValue.toString(), expectedOutput2, intVectorValue.toString());

        final List<Integer> list5 = testValues.subList(0, 5);
        intVectorValue = new IntVectorValue(list5);
        final String expectedOutput5 = "[0, 1, 2, 3, 4]";
        assertEquals("Outputs differ. Expected: " + expectedOutput5 + ", Actual: " + intVectorValue.toString(), expectedOutput5, intVectorValue.toString());

        intVectorValue = new IntVectorValue(testValues);
        final String expectedOutput7 = "[0, 1, 2, 3, ..., 6]";
        assertEquals("Outputs differ. Expected: " + expectedOutput7 + ", Actual: " + intVectorValue.toString(), expectedOutput7, intVectorValue.toString());
    }

    @Test
    public void dblVectorTest() {
        List<Double> testValues = new ArrayList<>();
        testValues.add(0.0);
        testValues.add(1.1);
        testValues.add(2.2);
        testValues.add(3.3);
        testValues.add(4.4);
        testValues.add(5.5);
        testValues.add(6.6);

        final List<Double> list1 = testValues.subList(0, 1);
        DblVectorValue dblVectorValue = new DblVectorValue(list1);
        final String expectedOutput1 = "[0.0]";
        assertEquals("Outputs differ. Expected: " + expectedOutput1 + ", Actual: " + dblVectorValue.toString(), expectedOutput1, dblVectorValue.toString());

        final List<Double> list2 = testValues.subList(0, 2);
        dblVectorValue = new DblVectorValue(list2);
        final String expectedOutput2 = "[0.0, 1.1]";
        assertEquals("Outputs differ. Expected: " + expectedOutput2 + ", Actual: " + dblVectorValue.toString(), expectedOutput2, dblVectorValue.toString());

        final List<Double> list5 = testValues.subList(0, 5);
        dblVectorValue = new DblVectorValue(list5);
        final String expectedOutput5 = "[0.0, 1.1, 2.2, 3.3, 4.4]";
        assertEquals("Outputs differ. Expected: " + expectedOutput5 + ", Actual: " + dblVectorValue.toString(), expectedOutput5, dblVectorValue.toString());

        dblVectorValue = new DblVectorValue(testValues);
        final String expectedOutput7 = "[0.0, 1.1, 2.2, 3.3, ..., 6.6]";
        assertEquals("Outputs differ. Expected: " + expectedOutput7 + ", Actual: " + dblVectorValue.toString(), expectedOutput7, dblVectorValue.toString());
    }

    @Test
    public void strVectorTest() {
        List<String> testValues = new ArrayList<>();
        testValues.add("one");
        testValues.add("two");
        testValues.add("three");
        testValues.add("four");
        testValues.add("five");
        testValues.add("six");
        testValues.add("seven");

        final List<String> list1 = testValues.subList(0, 1);
        StrVectorValue strVectorValue = new StrVectorValue(list1);
        final String expectedOutput1 = "[\"one\"]";
        assertEquals("Outputs differ. Expected: " + expectedOutput1 + ", Actual: " + strVectorValue.toString(), expectedOutput1, strVectorValue.toString());

        final List<String> list2 = testValues.subList(0, 2);
        strVectorValue = new StrVectorValue(list2);
        final String expectedOutput2 = "[\"one\", \"two\"]";
        assertEquals("Outputs differ. Expected: " + expectedOutput2 + ", Actual: " + strVectorValue.toString(), expectedOutput2, strVectorValue.toString());

        final List<String> list5 = testValues.subList(0, 5);
        strVectorValue = new StrVectorValue(list5);
        final String expectedOutput5 = "[\"one\", \"two\", \"three\", \"four\", \"five\"]";
        assertEquals("Outputs differ. Expected: " + expectedOutput5 + ", Actual: " + strVectorValue.toString(), expectedOutput5, strVectorValue.toString());

        strVectorValue = new StrVectorValue(testValues);
        final String expectedOutput7 = "[\"one\", \"two\", \"three\", \"four\", ..., \"seven\"]";
        assertEquals("Outputs differ. Expected: " + expectedOutput7 + ", Actual: " + strVectorValue.toString(), expectedOutput7, strVectorValue.toString());
    }

    @Test
    public void dblTableTest() {
        List<List<Double>> testValues = new ArrayList<>();

        final List<List<Double>> table1 = prepareTableTestData(1, 1);
        DblTableValue dblTableValue = new DblTableValue(table1);
        final String expectedOutput1 = "[[0.0]]";
        assertEquals("Outputs differ. Expected: " + expectedOutput1 + ", Actual: " + dblTableValue.toString(), expectedOutput1, dblTableValue.toString());

        final List<List<Double>> table2 = prepareTableTestData(2, 2);
        dblTableValue = new DblTableValue(table2);
        final String expectedOutput2 = "[[0.0, 0.1], [1.0, 1.1]]";
        assertEquals("Outputs differ. Expected: " + expectedOutput2 + ", Actual: " + dblTableValue.toString(), expectedOutput2, dblTableValue.toString());

        final List<List<Double>> table3 = prepareTableTestData(3, 5);
        dblTableValue = new DblTableValue(table3);
        final String expectedOutput5 = "[[0.0, 0.1, 0.2, 0.30000000000000004, 0.4], [1.0, 1.1, 1.2000000000000002, 1.3000000000000003, 1.4000000000000004], "
                + "[2.0, 2.1, 2.2, 2.3000000000000003, 2.4000000000000004]]";
        assertEquals("Outputs differ. Expected: " + expectedOutput5 + ", Actual: " + dblTableValue.toString(), expectedOutput5, dblTableValue.toString());

        final List<List<Double>> table4 = prepareTableTestData(5, 7);
        dblTableValue = new DblTableValue(table4);
        final String expectedOutput7 = "[[0.0, 0.1, 0.2, 0.30000000000000004, ..., 0.6], "
                + "[1.0, 1.1, 1.2000000000000002, 1.3000000000000003, ..., 1.6000000000000005], ..., "
                + "[4.0, 4.1, 4.199999999999999, 4.299999999999999, ..., 4.599999999999998]]";
        assertEquals("Outputs differ. Expected: " + expectedOutput7 + ", Actual: " + dblTableValue.toString(), expectedOutput7, dblTableValue.toString());
    }

    private List<List<Double>> prepareTableTestData(int columns, int rows) {
        final double step = 0.1;
        List<List<Double>> testValues = new ArrayList<>();
        for (int col = 0; col < columns; col++) {
            final List<Double> column = new ArrayList<Double>();
            double value = col;
            for (int row = 0; row < rows; row++) {
                column.add(value);
                value += step;
            }
            testValues.add(column);
        }
        return testValues;
    }
}
