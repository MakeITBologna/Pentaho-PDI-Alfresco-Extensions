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

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;

public class AlfrescoUploadStepTest {

  static final String STEP_NAME = "Test Alfresco Upload Step";

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }



  // If the step receives rows with existing fields, there should be a new field at the end of each output row
  @Test
  public void testInput() throws KettleException {
    AlfrescoUploadStepMeta meta = new AlfrescoUploadStepMeta();
    /*
    meta.setOutputField( "aFieldName" );
    
    
    TransMeta tm = TransTestFactory.generateTestTransformation( new Variables(), meta, STEP_NAME );

    List<RowMetaAndData> result = TransTestFactory.executeTestTransformation( tm, TransTestFactory.INJECTOR_STEPNAME,  STEP_NAME, TransTestFactory.DUMMY_STEPNAME, generateInputData( 5, true ) );

    assertNotNull( result );
    assertEquals( 5, result.size() );
    for ( int i = 0; i < 5; i++ ) {
      assertEquals( 2, result.get( i ).size() );
      assertEquals( "UUID", result.get( i ).getValueMeta( 0 ).getName() );
      try {
        UUID.fromString( result.get( i ).getString(0, "default value" ) );
      } catch ( IllegalArgumentException iae ) {
        fail(); // UUID field value was modified unexpectedly
      }
      assertEquals( "Hello World!", result.get( i ).getString( 1, "default value" ) );
    }*/
  }

/*
  public static List<RowMetaAndData> generateInputData( int rowCount, boolean hasFields ) {
    List<RowMetaAndData> retval = new ArrayList<RowMetaAndData>();
    RowMetaInterface rowMeta = new RowMeta();
    if ( hasFields ) {
      rowMeta.addValueMeta( new ValueMetaString( "UUID" ) );
	}

    for ( int i = 0; i < rowCount; i++ ) {
      Object[] data = new Object[0];
      if ( hasFields ) {
        data = new Object[] { UUID.randomUUID().toString() };
      }
      retval.add( new RowMetaAndData( rowMeta, data ) );
    }
    return retval;
  }*/
  
}
