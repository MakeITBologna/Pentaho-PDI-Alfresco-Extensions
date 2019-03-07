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

package it.makeit.pentaho.steps.alfresco.download;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.io.IOUtils;
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

public class AlfrescoDownloadStep extends BaseStep implements StepInterface {

	private static final Class<?> PKG = AlfrescoDownloadStepMeta.class; // for i18n purposes

	public AlfrescoDownloadStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoDownloadStepMeta meta = (AlfrescoDownloadStepMeta) smi;
		AlfrescoDownloadStepData data = (AlfrescoDownloadStepData) sdi;
		if (!super.init(meta, data)) {
			return false;
		}

		// true se non ci sono errori
		return true;
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		AlfrescoDownloadStepMeta meta = (AlfrescoDownloadStepMeta) smi;
		AlfrescoDownloadStepData data = (AlfrescoDownloadStepData) sdi;

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
				errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisUrl")));
			}

			if (meta.getCmisUser() == null || data.outputRowMeta.indexOfValue(meta.getCmisUser()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisUser")));
			}

			if (meta.getCmisPassword() == null || data.outputRowMeta.indexOfValue(meta.getCmisPassword()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisPassword")));
			}

			if (meta.getFileDownload() == null || data.outputRowMeta.indexOfValue(meta.getFileDownload()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.fileDownload")));
			}
			if (meta.getCmisFile() == null || data.outputRowMeta.indexOfValue(meta.getCmisFile()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisFile")));
			}
			
			
			if (errors.size() > 0) {
				errors.forEach(e -> log.logError(e));
				setErrors(1L);
				setOutputDone();
				return false;
			}
		}

		Object[] outputRow = RowDataUtil.resizeArray(r, data.outputRowMeta.size());

		
		try {
			List<String> errors = new ArrayList<>();
			String cmisUrl = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisUrl())];
			String cmisUser = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisUser())];
			String cmisPassword = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisPassword())];

			String sessionKey = cmisUrl + "_" + cmisUser;
			Session session = data.sessionsPerUser.get(sessionKey);
			if (!data.sessionsPerUser.containsKey(sessionKey)) {
				session = AlfrescoStepHelper.createSession(cmisUrl, cmisUser, cmisPassword);
				data.sessionsPerUser.put(sessionKey, session);
			}

			String fileDownload = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getFileDownload())];
			String cmisFile = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisFile())];

			
			if(Strings.isNullOrEmpty(cmisUrl)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisUrl")));
			if(Strings.isNullOrEmpty(cmisUser)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisUser")));
			if(Strings.isNullOrEmpty(cmisPassword)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisPassword")));
			
			if(Strings.isNullOrEmpty(fileDownload)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.fileDownload")));
			
			
			
			
			Document document; 
			if(AlfrescoDownloadStepMeta.PATH.equals( meta.getCmisFileType())) {
				document = (Document) session.getObjectByPath(cmisFile);	
			} else {
				document = (Document) session.getObject(session.createObjectId(cmisFile));
			}
			
			
			if(document.getContentStream() != null  && document.getContentStream().getStream() != null) {
				File file = new File(fileDownload);
				FileOutputStream os = new FileOutputStream(file);
				try {
					IOUtils.copy(document.getContentStream().getStream(), os);	
				} finally {
					IOUtils.closeQuietly(os);
					IOUtils.closeQuietly(document.getContentStream().getStream());
				}
				
			}

			
			
		} catch (Throwable t) {
			logError("Error processing row " + getLinesRead(), t);

		} finally {
			
		}

		// put the row to the output row stream
		putRow(data.outputRowMeta, outputRow);

		// log progress if it is time to to so
		if (checkFeedback(getLinesRead())) {
			logBasic(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Linenr", getLinesRead())); // Some basic logging
		}

		// ritorna true se devo continuare con la riga seguente
		return true;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoDownloadStepMeta meta = (AlfrescoDownloadStepMeta) smi;
		AlfrescoDownloadStepData data = (AlfrescoDownloadStepData) sdi;

		super.dispose(meta, data);
	}
}
