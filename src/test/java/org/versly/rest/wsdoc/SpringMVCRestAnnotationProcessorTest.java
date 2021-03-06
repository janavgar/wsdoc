/*
 * Copyright 2011 TaskDock, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.versly.rest.wsdoc;

import freemarker.template.TemplateException;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class SpringMVCRestAnnotationProcessorTest extends AbstractRestAnnotationProcessorTest {

    @BeforeClass
    public void setUp() throws IOException, ClassNotFoundException, URISyntaxException, TemplateException {
        super.setUp();
    }

    @Override
    protected String getPackageToTest() {
        return "springmvc";
    }

    @Test
    public void assertMultipart() {
        processResource("RestDocEndpoint.java", "html");
        AssertJUnit.assertTrue("expected multipart info docs; got: \n" + output,
                output.contains("Note: this endpoint expects a multipart"));
    }

    @Test
    public void processControllerThatReturnsDomainObjectWithGenericParentsExpectsSuccess() {
        processResource("genericdomain/ChildController.java", "html");
        AssertJUnit.assertTrue("expected firstGrandparentField and secondGrandparentField in docs; got: \n" + output,
                output.contains(">firstGrandparentField<") && output.contains(">secondGrandparentField<")
                        && output.contains(">parentField<") && output.contains(">childField<")
        );
    }

    @Test
    public void processControllerThatReturnsGenericDomainObjectExpectsSuccess() {
        processResource("genericdomain/ParentController.java", "html");
        AssertJUnit.assertTrue("expected parentField in docs; got: \n" + output,
                output.contains(">parentField<"));
    }

    @Test
    public void assertQueryParams() {
        processResource("RestDocEndpoint.java", "html");
        AssertJUnit.assertTrue("expected queryParam1 and queryParam2 in docs; got: \n" + output,
                               output.contains(">queryParamVal1<") && output.contains(">queryParamVal2<"));
    }

    @Test
    public void processControllerThatReturnsEnumSetExpectsSuccess() {
        processResource("EnumSetController.java", "html");
        AssertJUnit.assertTrue("expected enumsets in docs; got: \n" + output,
                               output.contains(">myEnumSet<") && output.contains(">myEnum<") && output.contains(">one of [ TEST1, TEST2 ]<"));
    }

    @Test
    public void multipleBindingsForOneEndpoint() {
        processResource("RestDocEndpoint.java", "html");
        AssertJUnit.assertTrue("expected multiple-bindings-a and multiple-bindings-b in docs; got: \n" + output,
                output.contains("multiple-bindings-a<") && output.contains("multiple-bindings-b<"));
    }

    // issue #29
    @Test
    public void assertNoRedundantUriParametersForResource() {
        processResource("RestDocEndpoint.java", "raml");
        int firstOccurrence = output.indexOf("uriParameters:\n            id2:");
        AssertJUnit.assertTrue("No occurrence of widgets/{id1}/gizmos/{id2} 'uriParameters' found in RAML",
                firstOccurrence != -1);
        int secondOccurrence = output.indexOf("uriParameters:\n            id2:", firstOccurrence + 1);
        AssertJUnit.assertTrue("Occurrence of multiple widgets/{id1}/gizmos/{id2} 'uriParameters' found in RAML",
                secondOccurrence == -1);
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        File dir = new File(args[0]);
        for (int i = 1; i < args.length; i++) {
            runAnnotationProcessor(dir,
                    args[i].substring(0, args[i].lastIndexOf('/')),
                    args[i].substring(args[i].lastIndexOf('/')));
        }
    }

}
