package it.makeit.pentaho.steps.alfresco.download;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.LabelCombo;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.google.common.base.Strings;

public class AlfrescoDownloadStepDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = AlfrescoDownloadStepMeta.class; // for i18n purposes

	private AlfrescoDownloadStepMeta meta;

	
	private LabelCombo wCmisUrl;
	private LabelCombo wCmisUser;
	private LabelCombo wCmisPassword;

	private LabelCombo wFileDownload;
	private LabelCombo wCmisFile;
	private LabelCombo wCmisFileType;

	private String[] cmisFileTypes = new String[] {
    		BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisFileTypePath" ),  
    		BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisFileTypeObjectId" ) 
    	};
	
	public AlfrescoDownloadStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (AlfrescoDownloadStepMeta) in;
	}

	@Override
	public String open() {
		// store some convenient SWT variables  
	    Shell parent = getParent();
	    Display display = parent.getDisplay();

	    // La shell è la finestra
	    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
	    props.setLook( shell );
	    
	    setShellImage( shell, meta );

	    // valore changed se l'utente modifica la finestra
	    changed = meta.hasChanged();

	    // ogni volta che viene modificato un campo il meta deve essere imposto a changed
	    ModifyListener lsMod = new ModifyListener() {
	      public void modifyText( ModifyEvent e ) {
	        meta.setChanged();
	      }
	    };

	    
	    FormLayout formLayout = new FormLayout();
	    formLayout.marginWidth = Const.FORM_MARGIN;
	    formLayout.marginHeight = Const.FORM_MARGIN;
	    
	    
	    shell.setLayout( formLayout );
	    shell.setText( BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.title" ) );
	    
	    
	    int middle = props.getMiddlePct();
	    int margin = Const.MARGIN;

	    
	    
	    // RIGA STEP NAME
	    wlStepname = new Label( shell, SWT.RIGHT );
	    wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
	    props.setLook( wlStepname );
	    fdlStepname = new FormData();
	    fdlStepname.left = new FormAttachment( 0, 0 );
	    fdlStepname.right = new FormAttachment( middle, -margin );
	    fdlStepname.top = new FormAttachment( 0, margin );
	    wlStepname.setLayoutData( fdlStepname );

	    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wStepname.setText( stepname );
	    props.setLook( wStepname );
	    wStepname.addModifyListener( lsMod );
	    fdStepname = new FormData();
	    fdStepname.left = new FormAttachment( middle, 0 );
	    fdStepname.top = new FormAttachment( 0, margin );
	    fdStepname.right = new FormAttachment( 100, 0 );
	    wStepname.setLayoutData( fdStepname );
	    
	    
	    
	    Group alfrescoGroup = new Group(shell, SWT.SHADOW_NONE);
	    props.setLook(alfrescoGroup);
	    alfrescoGroup.setText(BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.groupAlfresco" ));
	    FormData fdAlfrescoGroup = new FormData();
	    fdAlfrescoGroup.left = new FormAttachment( 0, 0 );
	    fdAlfrescoGroup.right = new FormAttachment( 100, 0 );
	    fdAlfrescoGroup.top = new FormAttachment( wStepname, margin );
	    alfrescoGroup.setLayoutData( fdAlfrescoGroup );
	    
	    
	    FormLayout alfrescoFormLayout = new FormLayout();
	    alfrescoFormLayout.marginWidth = Const.FORM_MARGIN;
	    alfrescoFormLayout.marginHeight = Const.FORM_MARGIN;
	    alfrescoGroup.setLayout(alfrescoFormLayout);

	    
	    // RIGA CMIS URL
	    wCmisUrl = new LabelCombo( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisUrl" ), null );
	    setComboFieldField(wCmisUrl);
	    props.setLook( wCmisUrl );
	    wCmisUrl.addModifyListener( lsMod );
	    FormData fdCmisUrl = new FormData();
	    fdCmisUrl.left = new FormAttachment( 0, 0 );
	    fdCmisUrl.right = new FormAttachment( 100, 0 );
	    fdCmisUrl.top = new FormAttachment( 0, margin );
	    wCmisUrl.setLayoutData( fdCmisUrl );
	    
	    // RIGA CMIS USER
	    wCmisUser = new LabelCombo( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisUser" ), null );
	    setComboFieldField(wCmisUser);
	    props.setLook( wCmisUser );
	    wCmisUser.addModifyListener( lsMod );
	    FormData fdCmisUser = new FormData();
	    fdCmisUser.left = new FormAttachment( 0, 0 );
	    fdCmisUser.right = new FormAttachment( 100, 0 );
	    fdCmisUser.top = new FormAttachment( wCmisUrl, margin );
	    wCmisUser.setLayoutData( fdCmisUser );
	    
	    // RIGA CMIS PASSWORD
	    wCmisPassword = new LabelCombo( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisPassword" ), null );
	    setComboFieldField(wCmisPassword);
		props.setLook( wCmisPassword );
	    wCmisPassword.addModifyListener( lsMod );
	    FormData fdCmisPassword = new FormData();
	    fdCmisPassword.left = new FormAttachment( 0, 0 );
	    fdCmisPassword.right = new FormAttachment( 100, 0 );
	    fdCmisPassword.top = new FormAttachment( wCmisUser, margin );
	    wCmisPassword.setLayoutData( fdCmisPassword );
	    
	    // RIGA FILE Download
	    wFileDownload = new LabelCombo( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.fileDownload" ), null );
	    setComboFieldField(wFileDownload);
	    props.setLook( wFileDownload );
	    wFileDownload.addModifyListener( lsMod );
	    FormData fdFileDownload = new FormData();
	    fdFileDownload.left = new FormAttachment( 0, 0 );
	    fdFileDownload.right = new FormAttachment( 100, 0 );
	    fdFileDownload.top = new FormAttachment( wCmisPassword, margin );
	    wFileDownload.setLayoutData( fdFileDownload );
	    
	    // RIGA CMIS DIRECTORY
	    wCmisFile = new LabelCombo( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisFile" ), null );
	    setComboFieldField(wCmisFile);
	    props.setLook( wCmisFile );
	    wCmisFile.addModifyListener( lsMod );
	    FormData fdCmisFile = new FormData();
	    fdCmisFile.left = new FormAttachment( 0, 0 );
	    fdCmisFile.right = new FormAttachment( 100, 0 );
	    fdCmisFile.top = new FormAttachment( wFileDownload, margin );
	    wCmisFile.setLayoutData( fdCmisFile );
	    
	    // RIGA CMIS DIRECTORY Type
		wCmisFileType = new LabelCombo( alfrescoGroup, SWT.READ_ONLY, BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.cmisFileType" ), null );
	    wCmisFileType.setItems(cmisFileTypes);
	    wCmisFileType.setText(cmisFileTypes[0]);
	    props.setLook( wCmisFileType );
	    wCmisFileType.addModifyListener( lsMod );
		FormData fdCmisDirectoryType = new FormData();
	    fdCmisDirectoryType.left = new FormAttachment( 0, 0 );
	    fdCmisDirectoryType.right = new FormAttachment( 100, 0 );
	    fdCmisDirectoryType.top = new FormAttachment( wCmisFile, margin );
	    wCmisFileType.setLayoutData( fdCmisDirectoryType );
	    
	    
	    
	   
	    // OK and cancel buttons
	    wOK = new Button( shell, SWT.PUSH );
	    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
	    wCancel = new Button( shell, SWT.PUSH );
	    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
	    setButtonPositions(new Button[] { wOK, wCancel }, margin, alfrescoGroup ); 

	    // Add listeners for cancel and OK
	    lsCancel = new Listener() {
	      public void handleEvent( Event e ) {
	        cancel();
	      }
	    };
	    lsOK = new Listener() {
	      public void handleEvent( Event e ) {
	        ok();
	      }
	    };
	    wCancel.addListener( SWT.Selection, lsCancel );
	    wOK.addListener( SWT.Selection, lsOK );

	    // bottone invio
	    lsDef = new SelectionAdapter() {
	      public void widgetDefaultSelected( SelectionEvent e ) {
	        ok();
	      }
	    };
	    wStepname.addSelectionListener( lsDef );
	    wCmisUrl.addSelectionListener( lsDef );
	    wCmisUser.addSelectionListener( lsDef );
	    wCmisPassword.addSelectionListener( lsDef );

	    wFileDownload.addSelectionListener( lsDef );
	    wCmisFile.addSelectionListener( lsDef );
	    wCmisFileType.addSelectionListener( lsDef );

	    // X o ALT-F4
	    shell.addShellListener( new ShellAdapter() {
	      public void shellClosed( ShellEvent e ) {
	        cancel();
	      }
	    });

	    // gestione del resize e riapertura
	    setSize();

	    setFieldValues();
	    
	    // restore the changed flag to original value, as the modify listeners fire during dialog population  
	    meta.setChanged( changed );

	    // open dialog and enter event loop  
	    shell.open();
	    while ( !shell.isDisposed() ) {
	      if ( !display.readAndDispatch() ) {
	        display.sleep();
	      }
	    }

	    // at this point the dialog has closed, so either ok() or cancel() have been executed
	    // The "stepname" variable is inherited from BaseStepDialog
	    return stepname;
	}

	private void cancel() {
		// The "stepname" variable will be the return value for the open() method.
		// Setting to null to indicate that dialog was cancelled.
		stepname = null;
		// Restoring original "changed" flag on the met aobject
		meta.setChanged(changed);
		// close the SWT dialog window
		dispose();
	}




	private void ok() {
		stepname = wStepname.getText();

		meta.setCmisUrl(!Strings.isNullOrEmpty(wCmisUrl.getText()) ? wCmisUrl.getText() : null);
		meta.setCmisUser(!Strings.isNullOrEmpty(wCmisUser.getText()) ? wCmisUser.getText() : null);
		meta.setCmisPassword(!Strings.isNullOrEmpty(wCmisPassword.getText()) ? wCmisPassword.getText() : null);

		meta.setFileDownload(!Strings.isNullOrEmpty(wFileDownload.getText()) ? wFileDownload.getText() : null);
		meta.setCmisFile(!Strings.isNullOrEmpty(wCmisFile.getText()) ? wCmisFile.getText() : null);
		
		meta.setCmisFileType(!Strings.isNullOrEmpty(wCmisFileType.getText()) ? wCmisFileType.getText().equals(cmisFileTypes[0]) ? 0 : 1  : 0 );
		
		
		dispose();
	}
	
	
	
	
	private void setFieldValues() {
		
		wStepname.selectAll();
		
		if( meta.getCmisUrl() != null && Arrays.asList(wCmisUrl.getItems()).contains(meta.getCmisUrl()) ) wCmisUrl.setText( meta.getCmisUrl() );
		
		if( meta.getCmisUser() != null && Arrays.asList(wCmisUser.getItems()).contains(meta.getCmisUser()) ) 	wCmisUser.setText( meta.getCmisUser() );
		if( meta.getCmisPassword() != null && Arrays.asList(wCmisPassword.getItems()).contains(meta.getCmisPassword()) ) 	wCmisPassword.setText( meta.getCmisPassword() );
		
		
		if( meta.getFileDownload() != null && Arrays.asList(wFileDownload.getItems()).contains( meta.getFileDownload()) ) 	wFileDownload.setText( meta.getFileDownload() );
		if( meta.getCmisFile() != null && Arrays.asList(wCmisFile.getItems()).contains(meta.getCmisFile()) ) 	wCmisFile.setText( meta.getCmisFile());
		if( meta.getCmisFileType() != null ) 	wCmisFileType.setText( cmisFileTypes[meta.getCmisFileType()]);
		
		
		
		
	}
	
	private void setComboFieldField(LabelCombo labelCombo) {
	    try {
	      labelCombo.removeAll();

	      RowMetaInterface r = transMeta.getPrevStepFields( stepname );
	      if ( r != null ) {
	    	  labelCombo.setItems( r.getFieldNames() );
	      }
	      
	    } catch ( KettleException ke ) {
	    	new ErrorDialog( shell, 
	    			BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.FailedToGetFields.DialogTitle" ),  
	    			BaseMessages.getString( PKG, "AlfrescoDownloadStep.ui.FailedToGetFields.DialogMessage" ), 
	    			ke );
	    }
	  }

	
}
