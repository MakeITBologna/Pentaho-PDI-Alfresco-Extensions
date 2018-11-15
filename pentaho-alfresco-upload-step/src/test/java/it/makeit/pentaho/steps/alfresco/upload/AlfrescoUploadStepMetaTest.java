/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
*
*******************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
******************************************************************************/

package it.makeit.pentaho.steps.alfresco.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;

public class AlfrescoUploadStepMetaTest {

	@BeforeClass
	public static void setUpBeforeClass() throws KettleException {
		KettleEnvironment.init(false);
	}

	@Test
	public void testLoadSave() throws KettleException {

		// test serializzazione ad xml

		List<String> attributes = Arrays.asList("cmisUrl", "cmisUser", "cmisPassword");

		LoadSaveTester<AlfrescoUploadStepMeta> tester = new LoadSaveTester<AlfrescoUploadStepMeta>(AlfrescoUploadStepMeta.class, attributes);

		tester.testSerialization();
	}

	@Test
	public void testChecks() {
		AlfrescoUploadStepMeta m = new AlfrescoUploadStepMeta();

		// Test null input array
		List<CheckResultInterface> checkResults = new ArrayList<CheckResultInterface>();
		m.check(checkResults, new TransMeta(), new StepMeta(), null, null, null, null, new Variables(), new MemoryRepository(), null);
		assertFalse(checkResults.isEmpty());
		boolean foundMatch = false;
		for (CheckResultInterface result : checkResults) {
			if (result.getType() == CheckResultInterface.TYPE_RESULT_ERROR && result.getText().equals(BaseMessages.getString(AlfrescoUploadStepMeta.class, "AlfrescoUploadStep.CheckResult.ReceivingRows.ERROR"))) {
				foundMatch = true;
			}
		}
		assertTrue("The step checks should fail if no input fields are given", foundMatch);

		// Test zero-length input array
		checkResults.clear();
		m.check(checkResults, new TransMeta(), new StepMeta(), null, new String[0], null, null, new Variables(), new MemoryRepository(), null);
		assertFalse(checkResults.isEmpty());
		foundMatch = false;
		for (CheckResultInterface result : checkResults) {
			if (result.getType() == CheckResultInterface.TYPE_RESULT_ERROR && result.getText().equals(BaseMessages.getString(AlfrescoUploadStepMeta.class, "AlfrescoUploadStep.CheckResult.ReceivingRows.ERROR"))) {
				foundMatch = true;
			}
		}
		assertTrue("The step checks should fail if no input fields are given", foundMatch);

		// Test non-zero input array
		checkResults.clear();
		m.check(checkResults, new TransMeta(), new StepMeta(), null, new String[1], null, null, new Variables(), new MemoryRepository(), null);
		assertFalse(checkResults.isEmpty());
		foundMatch = false;
		for (CheckResultInterface result : checkResults) {
			if (result.getType() == CheckResultInterface.TYPE_RESULT_OK && result.getText().equals(BaseMessages.getString(AlfrescoUploadStepMeta.class, "AlfrescoUploadStep.CheckResult.ReceivingRows.OK"))) {
				foundMatch = true;
			}
		}
		assertTrue("The step checks should fail if no input fields are given", foundMatch);
	}
}
