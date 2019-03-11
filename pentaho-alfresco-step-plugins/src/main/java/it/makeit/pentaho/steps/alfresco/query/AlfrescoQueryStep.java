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

package it.makeit.pentaho.steps.alfresco.query;

import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import it.makeit.pentaho.steps.alfresco.helper.AlfrescoStepHelper;

public class AlfrescoQueryStep extends BaseStep implements StepInterface {

	private static final Class<?> PKG = AlfrescoQueryStepMeta.class; // for i18n purposes

	public AlfrescoQueryStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoQueryStepMeta meta = (AlfrescoQueryStepMeta) smi;
		AlfrescoQueryStepData data = (AlfrescoQueryStepData) sdi;
		if (!super.init(meta, data)) {
			return false;
		}

		// true se non ci sono errori
		return true;
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		AlfrescoQueryStepMeta meta = (AlfrescoQueryStepMeta) smi;
		AlfrescoQueryStepData data = (AlfrescoQueryStepData) sdi;

		if (first) {

			first = false;

			String sql = environmentSubstitute(meta.getCmisQuery());

			String url = environmentSubstitute(meta.getCmisUrl());

			String user = environmentSubstitute(meta.getCmisUser());
			String password = environmentSubstitute(meta.getCmisPassword());

			Session session = AlfrescoStepHelper.createSession(url, user, password);
			session.getDefaultContext().setCacheEnabled(false);
			session.getDefaultContext().setMaxItemsPerPage(Integer.MAX_VALUE);
			
			ItemIterable<QueryResult> result = session.query(sql, false);

			if (result == null) {
				setOutputDone();
				return false;
			}

			data.currentPageIterator = result.iterator();
			
			if (!data.currentPageIterator.hasNext()) {
				setOutputDone();
				return false;
			}

			data.currentRow = result.iterator().next();
			incrementLinesInput();
			
			data.rowMeta = new RowMeta();

			for (PropertyData<?> property : data.currentRow.getProperties()) {
				String propertyName = property.getQueryName().replaceAll(":", "_");
				ValueMetaInterface v = new ValueMetaString(propertyName);
				v.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
				v.setOrigin(getStepname());
				data.rowMeta.addValueMeta(v);

			}

		}
		
		Object[] outputRow = new Object[ data.rowMeta.size()];

		for(int i = 0; i < outputRow.length; i++) {
			PropertyData<?> property = data.currentRow.getProperties().get(i);
			String propertyName = property.getQueryName().replaceAll(":", "_");
			int index = data.rowMeta.indexOfValue(propertyName);
			outputRow[index] = convert(property.getFirstValue());
			
		}
		
		
		putRow(data.rowMeta, outputRow);
		
	
		if (checkFeedback(getLinesRead())) {
			logBasic(BaseMessages.getString(PKG, "AlfrescoQueryStep.Linenr", getLinesRead())); // Some basic logging
		}

		// ritorna true se devo continuare con la riga seguente

		if (data.currentPageIterator.hasNext()) {
			data.currentRow = data.currentPageIterator.next();
			incrementLinesInput();
			return true;
		} else {
			
			setOutputDone();
			return false;
		}

	}

	private Object convert(Object firstValue) {
		if(firstValue instanceof GregorianCalendar) {
			return ((GregorianCalendar) firstValue).getTime();
		}
		return firstValue;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		AlfrescoQueryStepMeta meta = (AlfrescoQueryStepMeta) smi;
		AlfrescoQueryStepData data = (AlfrescoQueryStepData) sdi;

		super.dispose(meta, data);
	}
}
