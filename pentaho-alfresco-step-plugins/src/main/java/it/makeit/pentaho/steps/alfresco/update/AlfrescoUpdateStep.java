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

package it.makeit.pentaho.steps.alfresco.update;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.google.common.base.Strings;

import it.makeit.pentaho.steps.alfresco.helper.AlfrescoStepHelper;
import it.makeit.pentaho.steps.alfresco.helper.AlfrescoStepJsonHelper;

public class AlfrescoUpdateStep extends BaseStep implements StepInterface {

	private static final Class<?> PKG = AlfrescoUpdateStepMeta.class; // for i18n purposes

	public AlfrescoUpdateStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoUpdateStepMeta meta = (AlfrescoUpdateStepMeta) smi;
		AlfrescoUpdateStepData data = (AlfrescoUpdateStepData) sdi;
		if (!super.init(meta, data)) {
			return false;
		}

		// true se non ci sono errori
		return true;
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		AlfrescoUpdateStepMeta meta = (AlfrescoUpdateStepMeta) smi;
		AlfrescoUpdateStepData data = (AlfrescoUpdateStepData) sdi;

		Object[] r = getRow();

		// se non ci sono più row interrompo l'esecuzione
		if (r == null) {
			setOutputDone();
			return false;
		}

		if (first) {

			first = false;

			// necessario per passare i dati da input ad output
			data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone(); // clonare come da esempio
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, null, null);

			// gestione degli errori e campi invalidi
			List<String> errors = new ArrayList<String>();

			if (meta.getCmisUrl() == null || data.outputRowMeta.indexOfValue(meta.getCmisUrl()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisUrl")));
			}

			if (meta.getCmisUser() == null || data.outputRowMeta.indexOfValue(meta.getCmisUser()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisUser")));
			}

			if (meta.getCmisPassword() == null || data.outputRowMeta.indexOfValue(meta.getCmisPassword()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisPassword")));
			}

			if (meta.getCmisFile() == null || data.outputRowMeta.indexOfValue(meta.getCmisFile()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisFile")));
			}
			
			
			data.outputStatusIndex = data.outputRowMeta.indexOfValue(meta.getOutputStatus());
			if (data.outputStatusIndex == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoOutputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.outputStatus")));
			}
			data.outputObjectIdIndex = data.outputRowMeta.indexOfValue(meta.getOutputObjectId());
			if (data.outputObjectIdIndex == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoOutputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.outputObjectId")));
			}
			data.outputErrorIndex = data.outputRowMeta.indexOfValue(meta.getOutputError());
			if (data.outputErrorIndex == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoOutputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.outputError")));
			}

			if (errors.size() > 0) {
				errors.forEach(e -> log.logError(e));
				setErrors(1L);
				setOutputDone();
				return false;
			}
		}

		Object[] outputRow = RowDataUtil.resizeArray(r, data.outputRowMeta.size());

		
		FileInputStream inputStream = null;
		try {
			List<String> errors = new ArrayList<>();
			String cmisUrl = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisUrl())];
			String cmisUser = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisUser())];
			String cmisPassword = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisPassword())];
			String cmisFile = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisFile())];
			String cmisProperties = meta.getCmisProperties() != null ? (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisProperties())] : null;
			
			if(Strings.isNullOrEmpty(cmisUrl)) errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisUrl")));
			if(Strings.isNullOrEmpty(cmisUser)) errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisUser")));
			if(Strings.isNullOrEmpty(cmisPassword)) errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisPassword")));
			if(Strings.isNullOrEmpty(cmisFile)) errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisFile")));
			if(Strings.isNullOrEmpty(cmisProperties)) errors.add(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUpdateStep.ui.cmisProperties")));
			
			
			String sessionKey = cmisUrl + "_" + cmisUser;
			Session session = data.sessionsPerUser.get(sessionKey);
			if (!data.sessionsPerUser.containsKey(sessionKey)) {
				session = AlfrescoStepHelper.createSession(cmisUrl, cmisUser, cmisPassword);
				data.sessionsPerUser.put(sessionKey, session);
			}

			
			CmisObject documentOrItem; 
			if(AlfrescoUpdateStepMeta.PATH.equals( meta.getCmisFileType())) {
				documentOrItem = session.getObjectByPath(cmisFile);	
			} else {
				documentOrItem = session.getObject(session.createObjectId(cmisFile));
			}
			
			Map<String, Object> propertiesMap = AlfrescoStepJsonHelper.jsonProperties(cmisProperties);
			
			
			documentOrItem = documentOrItem.updateProperties(propertiesMap);
			



			outputRow[data.outputStatusIndex] = "ok";
			outputRow[data.outputObjectIdIndex] = documentOrItem.getId();
		} catch (Throwable t) {
			outputRow[data.outputErrorIndex] = t.getClass().getSimpleName() + ": " + t.getMessage();
			outputRow[data.outputStatusIndex] = "ko";
			logError("Error processing row " + getLinesRead(), t);

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logError("Close File error", e);
				}
			}
		}

		// put the row to the output row stream
		putRow(data.outputRowMeta, outputRow);

		// log progress if it is time to to so
		if (checkFeedback(getLinesRead())) {
			logBasic(BaseMessages.getString(PKG, "AlfrescoUpdateStep.Linenr", getLinesRead())); // Some basic logging
		}

		// ritorna true se devo continuare con la riga seguente
		return true;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoUpdateStepMeta meta = (AlfrescoUpdateStepMeta) smi;
		AlfrescoUpdateStepData data = (AlfrescoUpdateStepData) sdi;

		super.dispose(meta, data);
	}
}
