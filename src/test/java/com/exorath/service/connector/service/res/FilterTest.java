/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.service.connector.service.res;

import com.exorath.service.connector.res.Filter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by toonsev on 1/11/2017.
 */
public class FilterTest {
    @Test
    public void notEqualsTest1(){
        Filter filter1 = new Filter();
        Filter filter2 = new Filter("testGameId");
        assertNotEquals(filter1, filter2);
        assertNotEquals(filter1.hashCode(), filter2.hashCode());
    }
    @Test
    public void notEqualsTest2(){
        Filter filter1 = new Filter("testGameId");
        Filter filter2 = new Filter("testGameId").withFlavorId("testflavor");
        assertNotEquals(filter1, filter2);
        assertNotEquals(filter1.hashCode(), filter2.hashCode());
    }
    @Test
    public void notEqualsTest3(){
        Filter filter1 = new Filter("testGameId").withFlavorId("testflavor").withMapId("mapId");
        Filter filter2 = new Filter("testGameId").withFlavorId("testflavor").withMapId("mapId2");
        assertNotEquals(filter1, filter2);
        assertNotEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void equalsTest1(){
        Filter filter1 = new Filter("testGameId");
        Filter filter2 = new Filter("testGameId");
        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());
    }
    @Test
    public void equalsTest2(){
        Filter filter1 = new Filter("testGameId");
        assertEquals(filter1, filter1);
        assertEquals(filter1.hashCode(), filter1.hashCode());
    }
    @Test
    public void equalsTest3(){
        Filter filter1 = new Filter("testGameId").withFlavorId("flavorId2");
        Filter filter2 = new Filter("testGameId").withFlavorId("flavorId2");
        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());
    }
    @Test
    public void equalsTest4(){
        Filter filter1 = new Filter("testGameId").withFlavorId("flavorId2").withMapId("gameId");
        Filter filter2 = new Filter("testGameId").withFlavorId("flavorId2").withMapId("gameId");
        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
