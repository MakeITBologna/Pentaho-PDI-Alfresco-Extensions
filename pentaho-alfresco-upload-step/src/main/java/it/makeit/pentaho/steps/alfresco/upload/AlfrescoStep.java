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

import java.util.Random;

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

  
  public AlfrescoStep( StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis ) {
    super( s, stepDataInterface, c, t, dis );
  }

 
  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    
	AlfrescoUploadStepMeta meta = (AlfrescoUploadStepMeta) smi;
    AlfrescoUploadStepData data = (AlfrescoUploadStepData) sdi;
    if ( !super.init( meta, data ) ) {
      return false;
    }

    // true se non ci sono errori
    return true;
  }

 
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {

	  AlfrescoUploadStepMeta meta = (AlfrescoUploadStepMeta) smi;
	  AlfrescoUploadStepData data = (AlfrescoUploadStepData) sdi;
	    
	   Object[] r = getRow();

    // se non ci sono più row interrompo l'esecuzione 
    if ( r == null ) {
      setOutputDone();
      return false;
    }

    
    
    if ( first ) {
      first = false;
      
      // necessario per passare i dati da input ad output
      data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
      meta.getFields( data.outputRowMeta, getStepname(), null, null, this, null, null );

      
      
      data.outputErrorIndex = data.outputRowMeta.indexOfValue( meta.getOutputError() );
      data.outputStatusIndex = data.outputRowMeta.indexOfValue( meta.getOutputStatus() );
      data.outputObjectIdIndex = data.outputRowMeta.indexOfValue( meta.getOutputObjectId() );
      
      if ( data.outputErrorIndex < 0 || data.outputStatusIndex < 0 || data.outputObjectIdIndex < 0) {
          log.logError( BaseMessages.getString( PKG, "AlfrescoUploadStep.Error.NoOutputFields" ) );
          setErrors( 1L );
          setOutputDone();
          return false;
       }
    }
    
    
    Object[] outputRow = RowDataUtil.resizeArray( r, data.outputRowMeta.size() );
    
    Random random = new Random();
    boolean ok = random.nextBoolean();
    
    if(ok) {
    	outputRow[data.outputErrorIndex] = null;
        outputRow[data.outputStatusIndex] = "ok!";
        outputRow[data.outputObjectIdIndex] = "3425254214!";
    	
    	
    } else {
    	outputRow[data.outputErrorIndex] = "Errore";
        outputRow[data.outputStatusIndex] = null;
        outputRow[data.outputObjectIdIndex] = null;
	
    }
    
    
    
    // put the row to the output row stream
    putRow( data.outputRowMeta, outputRow );

    // log progress if it is time to to so
    if ( checkFeedback( getLinesRead() ) ) {
      logBasic( BaseMessages.getString( PKG, "AlfrescoUploadStep.Linenr", getLinesRead() ) ); // Some basic logging
    }

    // ritorna true se devo continuare con la riga seguente
    return true;
  }

 
  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {

      AlfrescoUploadStepMeta meta = (AlfrescoUploadStepMeta) smi;
	    AlfrescoUploadStepData data = (AlfrescoUploadStepData) sdi;
	    
        super.dispose( meta, data );
  }
}
