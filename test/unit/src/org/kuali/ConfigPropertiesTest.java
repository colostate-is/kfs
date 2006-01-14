/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kuali.core.ConfigProperties;
import org.kuali.test.KualiTestBaseWithSpring;
import org.springframework.beans.BeanUtils;

/**
 * This class tests the Config constants.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ConfigPropertiesTest extends KualiTestBaseWithSpring {
    private static final String SIMPLE_TESTING_KEY = "simpleTestingKey";
    private static final String SIMPLE_TESTING_VALUE = "simpleTestingValue";
    private static final String COMPLEX_TESTING_KEY = "complex.testing.key";
    private static final String COMPLEX_TESTING_VALUE = "complex testing value";


    private static boolean setUpOnce = false;
    private static ConfigProperties configConstants;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        if (!setUpOnce) {
            setUpOnce = true;
            configConstants = new ConfigProperties();
        }
    }

    public final void testSize() throws Exception {
        assertTrue(configConstants.size() > 0);
    }

    public final void testIsEmpty() throws Exception {
        assertFalse(configConstants.isEmpty());
    }

    public final void testContainsKey_invalidKey() throws Exception {
        boolean failedAsExpected = false;

        try {
            configConstants.containsKey(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testContainsKey_unknownKey() throws Exception {
        assertFalse(configConstants.containsKey("hopefully unused key"));
    }

    public final void testContainsKey_simpleKey() throws Exception {
        assertTrue(configConstants.containsKey(SIMPLE_TESTING_KEY));
    }

    public final void testContainsKey_complexKey() throws Exception {
        assertTrue(configConstants.containsKey(COMPLEX_TESTING_KEY));
    }

    public final void testContainsValue_invalidValue() throws Exception {
        boolean failedAsExpected = false;

        try {
            configConstants.containsValue(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testContainsValue_unknownValue() throws Exception {
        assertFalse(configConstants.containsValue("hopefully unknown value"));
    }

    public final void testContainsValue_value1() throws Exception {
        assertTrue(configConstants.containsValue(SIMPLE_TESTING_VALUE));
    }

    public final void testContainsValue_value2() throws Exception {
        assertTrue(configConstants.containsValue(COMPLEX_TESTING_VALUE));
    }

    public final void testValues_readOnly() throws Exception {
        Collection values = configConstants.values();

        assertEquals(configConstants.size(), values.size());
        assertTrue(values.contains(SIMPLE_TESTING_VALUE));
        assertTrue(values.contains(COMPLEX_TESTING_VALUE));
    }

    public final void testValues_readWrite() throws Exception {
        Collection values = configConstants.values();

        boolean failedAsExpected = false;
        try {
            values.remove(SIMPLE_TESTING_VALUE);
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testEntrySet_readOnly() throws Exception {
        Set entrySet = configConstants.entrySet();

        assertEquals(configConstants.size(), entrySet.size());
        boolean foundSimple = false;
        boolean foundComplex = false;
        for (Iterator i = entrySet.iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            if (e.getKey().equals(SIMPLE_TESTING_KEY)) {
                foundSimple = true;
            }
            else if (e.getKey().equals(COMPLEX_TESTING_KEY)) {
                foundComplex = true;
            }
        }

        assertTrue(foundSimple);
        assertTrue(foundComplex);
    }

    public final void testEntrySet_readWrite() throws Exception {
        Set entrySet = configConstants.entrySet();

        boolean failedAsExpected = false;
        try {
            entrySet.clear();
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    public final void testKeySet_readOnly() throws Exception {
        Set keySet = configConstants.keySet();

        assertEquals(configConstants.size(), keySet.size());
        assertTrue(keySet.contains(SIMPLE_TESTING_KEY));
        assertTrue(keySet.contains(COMPLEX_TESTING_KEY));
    }

    public final void testKeySet_readWrite() throws Exception {
        Set keySet = configConstants.keySet();

        boolean failedAsExpected = false;
        try {
            keySet.clear();
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testPutAll() throws Exception {
        HashMap h = new HashMap();
        h.put("something", "new");

        boolean failedAsExpected = false;
        try {
            configConstants.putAll(h);
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testClear() throws Exception {
        boolean failedAsExpected = false;
        try {
            configConstants.clear();
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testRemove() throws Exception {
        boolean failedAsExpected = false;
        try {
            configConstants.remove(SIMPLE_TESTING_KEY);
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testPut() throws Exception {
        boolean failedAsExpected = false;
        try {
            configConstants.put("something", "new");
        }
        catch (UnsupportedOperationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    public final void testGet_invalidKey() {
        boolean failedAsExpected = false;

        try {
            configConstants.get(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGet_unknownKey() {
        Object value = configConstants.get("hopefully unknown key");

        assertNull(value);
    }

    public final void testGet_simpleKey() {
        String value = configConstants.get(SIMPLE_TESTING_KEY).toString();

        assertNotNull(value);
        assertEquals(SIMPLE_TESTING_VALUE, value);
    }

    public final void testGet_complexKey() {
        String value = configConstants.get(COMPLEX_TESTING_KEY).toString();

        assertNotNull(value);
        assertEquals(COMPLEX_TESTING_VALUE, value);
    }

    public final void testGet_multiLevel() throws Exception {
        Class[] getParamTypes = { Object.class };

        Object level1 = configConstants.get("complex");

        Method m1 = BeanUtils.findMethod(level1.getClass(), "get", getParamTypes);
        Object level2 = m1.invoke(level1, new Object[] { "testing" });

        Method m2 = BeanUtils.findMethod(level2.getClass(), "get", getParamTypes);
        Object level3 = m2.invoke(level2, new Object[] { "key" });

        String value = level3.toString();

        assertNotNull(value);
        assertEquals(COMPLEX_TESTING_VALUE, value);
    }
}
