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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
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

public class AlfrescoStep extends BaseStep implements StepInterface {

	private static final Class<?> PKG = AlfrescoUploadStepMeta.class; // for i18n purposes

	public AlfrescoStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoUploadStepMeta meta = (AlfrescoUploadStepMeta) smi;
		AlfrescoUploadStepData data = (AlfrescoUploadStepData) sdi;
		if (!super.init(meta, data)) {
			return false;
		}

		// true se non ci sono errori
		return true;
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		AlfrescoUploadStepMeta meta = (AlfrescoUploadStepMeta) smi;
		AlfrescoUploadStepData data = (AlfrescoUploadStepData) sdi;

		Object[] r = getRow();

		// se non ci sono più row interrompo l'esecuzione
		if (r == null) {
			setOutputDone();
			return false;
		}

		if (first) {

			first = false;

			// necessario per passare i dati da input ad output
			data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, null, null);

			// gestione degli errori e campi invalidi
			List<String> errors = new ArrayList<String>();

			if (meta.getCmisUrl() == null || getInputRowMeta().indexOfValue(meta.getCmisUrl()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.cmisUrl")));
			}

			if (meta.getCmisUser() == null || getInputRowMeta().indexOfValue(meta.getCmisUser()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.cmisUser")));
			}

			if (meta.getCmisPassword() == null || getInputRowMeta().indexOfValue(meta.getCmisPassword()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.cmisPassword")));
			}

			if (meta.getFileUpload() == null || getInputRowMeta().indexOfValue(meta.getFileUpload()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.fileUpload")));
			}
			if (meta.getCmisDirectory() == null || getInputRowMeta().indexOfValue(meta.getCmisDirectory()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.cmisDirectory")));
			}

			data.outputStatusIndex = data.outputRowMeta.indexOfValue(meta.getOutputStatus());
			if (data.outputStatusIndex == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoOutputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.outputStatus")));
			}
			data.outputObjectIdIndex = data.outputRowMeta.indexOfValue(meta.getOutputObjectId());
			if (data.outputObjectIdIndex == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoOutputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.outputObjectId")));
			}
			data.outputErrorIndex = data.outputRowMeta.indexOfValue(meta.getOutputError());
			if (data.outputErrorIndex == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoUploadStep.Error.NoOutputField", BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.outputError")));
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
			String cmisUrl = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisUrl())];
			String cmisUser = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisUser())];
			String cmisPassword = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisPassword())];

			String sessionKey = cmisUrl + "_" + cmisUser;
			Session session = data.sessionsPerUser.get(sessionKey);
			if (!data.sessionsPerUser.containsKey(sessionKey)) {
				session = AlfrescoStepHelper.createSession(cmisUrl, cmisUser, cmisPassword);
				data.sessionsPerUser.put(sessionKey, session);
			}

			String fileUpload = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getFileUpload())];
			String cmisDirectory = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisDirectory())];

			Folder folder = AlfrescoStepHelper.getOrCreateFolderByPath(session, cmisDirectory);

			File file = new File(fileUpload);
			if (!file.exists()) {
				throw new java.io.FileNotFoundException(file.getAbsolutePath());
			}
			inputStream = new FileInputStream(file);

			Document document = AlfrescoStepHelper.createDocument(session, folder.getId(), file.getName(), -1, "application/octet-stream", inputStream, new HashMap<>(), null, "cmis:document");

			outputRow[data.outputStatusIndex] = "ok";
			outputRow[data.outputObjectIdIndex] = document.getId();
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
			logBasic(BaseMessages.getString(PKG, "AlfrescoUploadStep.Linenr", getLinesRead())); // Some basic logging
		}

		// ritorna true se devo continuare con la riga seguente
		return true;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoUploadStepMeta meta = (AlfrescoUploadStepMeta) smi;
		AlfrescoUploadStepData data = (AlfrescoUploadStepData) sdi;

		super.dispose(meta, data);
	}
}
