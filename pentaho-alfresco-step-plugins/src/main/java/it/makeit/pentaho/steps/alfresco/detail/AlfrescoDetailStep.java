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

package it.makeit.pentaho.steps.alfresco.detail;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import it.makeit.pentaho.steps.alfresco.download.AlfrescoDownloadStepMeta;
import it.makeit.pentaho.steps.alfresco.helper.AlfrescoStepHelper;
import it.makeit.pentaho.steps.alfresco.helper.AlfrescoStepJsonHelper;

public class AlfrescoDetailStep extends BaseStep implements StepInterface {

	private static final Class<?> PKG = AlfrescoDetailStepMeta.class; // for i18n purposes

	public AlfrescoDetailStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoDetailStepMeta meta = (AlfrescoDetailStepMeta) smi;
		AlfrescoDetailStepData data = (AlfrescoDetailStepData) sdi;
		if (!super.init(meta, data)) {
			return false;
		}

		// true se non ci sono errori
		return true;
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		AlfrescoDetailStepMeta meta = (AlfrescoDetailStepMeta) smi;
		AlfrescoDetailStepData data = (AlfrescoDetailStepData) sdi;

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
				errors.add(BaseMessages.getString(PKG, "AlfrescoDetailStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDetailStep.ui.cmisUrl")));
			}

			if (meta.getCmisUser() == null || data.outputRowMeta.indexOfValue(meta.getCmisUser()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDetailStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDetailStep.ui.cmisUser")));
			}

			if (meta.getCmisPassword() == null || data.outputRowMeta.indexOfValue(meta.getCmisPassword()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDetailStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDetailStep.ui.cmisPassword")));
			}

			if (meta.getDetail() == null || data.outputRowMeta.indexOfValue(meta.getDetail()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDetailStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDetailStep.ui.detail")));
			}
			if (meta.getCmisFile() == null || data.outputRowMeta.indexOfValue(meta.getCmisFile()) == -1) {
				errors.add(BaseMessages.getString(PKG, "AlfrescoDetailStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDetailStep.ui.cmisFile")));
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
			String cmisFile = (String) outputRow[data.outputRowMeta.indexOfValue(meta.getCmisFile())];

			
			if(Strings.isNullOrEmpty(cmisUrl)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisUrl")));
			if(Strings.isNullOrEmpty(cmisUser)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisUser")));
			if(Strings.isNullOrEmpty(cmisPassword)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisPassword")));
			if(Strings.isNullOrEmpty(cmisFile)) errors.add(BaseMessages.getString(PKG, "AlfrescoDownloadStep.Error.NoInputField", BaseMessages.getString(PKG, "AlfrescoDownloadStep.ui.cmisFile")));
			
			
			// controllo su ogni riga -> potremmo avere molteplici sessioni
			if (errors.size() > 0) {
				errors.forEach(e -> log.logError(e));
				setErrors(1L);
				return true;
			}
			
			String sessionKey = cmisUrl + "_" + cmisUser;
			Session session = data.sessionsPerUser.get(sessionKey);
			if (!data.sessionsPerUser.containsKey(sessionKey)) {
				session = AlfrescoStepHelper.createSession(cmisUrl, cmisUser, cmisPassword);
				
				session.getDefaultContext().setIncludeRelationships(IncludeRelationships.BOTH);
				session.getDefaultContext().setIncludeAcls(true);
				
				data.sessionsPerUser.put(sessionKey, session);
			}

			
			
			
			CmisObject documentOrItem; 
			if(AlfrescoDownloadStepMeta.PATH.equals( meta.getCmisFileType())) {
				documentOrItem = session.getObjectByPath(cmisFile);	
			} else {
				documentOrItem = session.getObject(session.createObjectId(cmisFile));
			}
			
			
			JsonObject obj = new JsonObject();
			
			JsonArray properties = new JsonArray();
			obj.add("properties", properties);
			
			for (PropertyData<?> property : documentOrItem.getProperties()) {
				
				JsonObject prop = new JsonObject();
				
				String propertyName = property.getQueryName();
				prop.addProperty("name", propertyName);	
				
				
				Object value = property.getFirstValue();
				if(value == null) {
					prop.add("value", JsonNull.INSTANCE);	
				} else if(value instanceof Boolean) {
					prop.addProperty("value", (Boolean) value);
				} else if(value instanceof Number) {
					prop.addProperty("value", (Number) value);
				} else if(value instanceof GregorianCalendar) {
					prop.addProperty("value", AlfrescoStepJsonHelper.dateToString(((GregorianCalendar) value).getTime()));
				} else {
					prop.addProperty("value", value.toString());	
				}
				
				properties.add(prop);
				
			
			}
			
			JsonArray relationships = new JsonArray();
			obj.add("relationships", relationships);
			documentOrItem.getRelationships().forEach(relationship ->{
				JsonObject rel = new JsonObject();
				if(relationship.getSourceId() != null) {
					rel.addProperty("source", relationship.getSourceId().getId());	
				}
				if(relationship.getTargetId() != null) {
					rel.addProperty("target", relationship.getTargetId().getId());	
				}
				if(relationship.getType() != null) {
					rel.addProperty("relationship", relationship.getType().getId());
				}
				
				relationships.add(rel);
			});
			
			outputRow[data.outputRowMeta.indexOfValue(meta.getDetail())] = data.gson.toJson(obj);
			
		} catch (Throwable t) {
			logError("Error processing row " + getLinesRead(), t);

			setErrors(1L);
			return true;
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

		AlfrescoDetailStepMeta meta = (AlfrescoDetailStepMeta) smi;
		AlfrescoDetailStepData data = (AlfrescoDetailStepData) sdi;

		super.dispose(meta, data);
	}
}
