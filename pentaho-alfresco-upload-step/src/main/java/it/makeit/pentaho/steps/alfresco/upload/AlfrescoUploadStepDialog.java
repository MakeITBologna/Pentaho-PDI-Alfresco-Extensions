package it.makeit.pentaho.steps.alfresco.upload;

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
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.LabelText;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class AlfrescoUploadStepDialog extends BaseStepDialog implements StepDialogInterface {

	
	
	
	private static Class<?> PKG = AlfrescoUploadStepMeta.class; // for i18n purposes

	private AlfrescoUploadStepMeta meta;

	
	private LabelText wCmisUrl;
	private LabelText wCmisUser;
	private LabelText wCmisPassword;

	private LabelText wFileUpload;
	private LabelText wCmisDirectory;

	private LabelText wOutputStatus;
	private LabelText wOutputObjectId;
	private LabelText wOutputError;

	
	public AlfrescoUploadStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (AlfrescoUploadStepMeta) in;
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
	    shell.setText( BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.title" ) );
	    
	    
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
	    alfrescoGroup.setText(BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.groupAlfresco" ));
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
	    wCmisUrl = new LabelText( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.cmisUrl" ), null );
	    props.setLook( wCmisUrl );
	    wCmisUrl.addModifyListener( lsMod );
	    FormData fdCmisUrl = new FormData();
	    fdCmisUrl.left = new FormAttachment( 0, 0 );
	    fdCmisUrl.right = new FormAttachment( 100, 0 );
	    fdCmisUrl.top = new FormAttachment( 0, margin );
	    wCmisUrl.setLayoutData( fdCmisUrl );
	    
	    // RIGA CMIS USER
	    wCmisUser = new LabelText( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.cmisUser" ), null );
	    props.setLook( wCmisUser );
	    wCmisUser.addModifyListener( lsMod );
	    FormData fdCmisUser = new FormData();
	    fdCmisUser.left = new FormAttachment( 0, 0 );
	    fdCmisUser.right = new FormAttachment( 100, 0 );
	    fdCmisUser.top = new FormAttachment( wCmisUrl, margin );
	    wCmisUser.setLayoutData( fdCmisUser );
	    
	    // RIGA CMIS PASSWORD
	    wCmisPassword = new LabelText( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.cmisPassword" ), null );
	    props.setLook( wCmisPassword );
	    wCmisPassword.addModifyListener( lsMod );
	    FormData fdCmisPassword = new FormData();
	    fdCmisPassword.left = new FormAttachment( 0, 0 );
	    fdCmisPassword.right = new FormAttachment( 100, 0 );
	    fdCmisPassword.top = new FormAttachment( wCmisUser, margin );
	    wCmisPassword.setLayoutData( fdCmisPassword );
	    
	    // RIGA FILE UPLOAD
	    wFileUpload = new LabelText( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.fileUpload" ), null );
	    props.setLook( wFileUpload );
	    wFileUpload.addModifyListener( lsMod );
	    FormData fdFileUpload = new FormData();
	    fdFileUpload.left = new FormAttachment( 0, 0 );
	    fdFileUpload.right = new FormAttachment( 100, 0 );
	    fdFileUpload.top = new FormAttachment( wCmisPassword, margin );
	    wFileUpload.setLayoutData( fdFileUpload );
	    
	    // RIGA CMIS PASSWORD
	    wCmisDirectory = new LabelText( alfrescoGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.cmisDirectory" ), null );
	    props.setLook( wCmisDirectory );
	    wCmisDirectory.addModifyListener( lsMod );
	    FormData fdCmisDirectory = new FormData();
	    fdCmisDirectory.left = new FormAttachment( 0, 0 );
	    fdCmisDirectory.right = new FormAttachment( 100, 0 );
	    fdCmisDirectory.top = new FormAttachment( wFileUpload, margin );
	    wCmisDirectory.setLayoutData( fdCmisDirectory );
	    
	   
	    
	    Group outputGroup = new Group(shell, SWT.SHADOW_NONE);
	    props.setLook(outputGroup);
	    outputGroup.setText(BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.groupOutput" ));
	    FormData fdOutputGroup = new FormData();
	    fdOutputGroup.left = new FormAttachment( 0, 0 );
	    fdOutputGroup.right = new FormAttachment( 100, 0 );
	    fdOutputGroup.top = new FormAttachment( alfrescoGroup, margin );
	    outputGroup.setLayoutData( fdOutputGroup );
	    
	    
	    FormLayout outputFormLayout = new FormLayout();
	    outputFormLayout.marginWidth = Const.FORM_MARGIN;
	    outputFormLayout.marginHeight = Const.FORM_MARGIN;
	    outputGroup.setLayout(outputFormLayout);

	    // RIGA STATUS
	    wOutputStatus = new LabelText( outputGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.outputStatus" ), null );
	    props.setLook( wOutputStatus );
	    wFileUpload.addModifyListener( lsMod );
	    FormData fdOutputStatus = new FormData();
	    fdOutputStatus.left = new FormAttachment( 0, 0 );
	    fdOutputStatus.right = new FormAttachment( 100, 0 );
	    fdOutputStatus.top = new FormAttachment( 0, margin );
	    wOutputStatus.setLayoutData( fdOutputStatus );
	    
	    // RIGA OBJECT ID
	    wOutputObjectId = new LabelText( outputGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.outputObjectId" ), null );
	    props.setLook( wOutputObjectId );
	    wOutputObjectId.addModifyListener( lsMod );
	    FormData fdOutputObjectId = new FormData();
	    fdOutputObjectId.left = new FormAttachment( 0, 0 );
	    fdOutputObjectId.right = new FormAttachment( 100, 0 );
	    fdOutputObjectId.top = new FormAttachment( wOutputStatus, margin );
	    wOutputObjectId.setLayoutData( fdOutputObjectId );
	    
	   
	    // RIGA ERROR
	    wOutputError = new LabelText( outputGroup, BaseMessages.getString( PKG, "AlfrescoUploadStep.ui.outputError" ), null );
		props.setLook( wOutputError );
		wOutputError.addModifyListener( lsMod );
	    FormData fdOutputError = new FormData();
	    fdOutputError.left = new FormAttachment( 0, 0 );
	    fdOutputError.right = new FormAttachment( 100, 0 );
	    fdOutputError.top = new FormAttachment( wOutputObjectId, margin );
	    wOutputError.setLayoutData( fdOutputError );
	    
	    
	    
	    // OK and cancel buttons
	    wOK = new Button( shell, SWT.PUSH );
	    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
	    wCancel = new Button( shell, SWT.PUSH );
	    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
	    setButtonPositions(new Button[] { wOK, wCancel }, margin, outputGroup ); 

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

	    wFileUpload.addSelectionListener( lsDef );
	    wCmisDirectory.addSelectionListener( lsDef );

	    // X o ALT-F4
	    shell.addShellListener( new ShellAdapter() {
	      public void shellClosed( ShellEvent e ) {
	        cancel();
	      }
	    } );

	    // gestione del resize e riapertura
	    setSize();

	    populateDialog();
	    
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

		meta.setCmisUrl(wCmisUrl.getText());
		meta.setCmisUser(wCmisUser.getText());
		meta.setCmisPassword(wCmisPassword.getText());

		meta.setFileUpload(wFileUpload.getText());
		meta.setCmisDirectory(wCmisDirectory.getText());
		
		meta.setOutputStatus(wOutputStatus.getText());
		meta.setOutputObjectId(wOutputObjectId.getText());
		meta.setOutputError(wOutputError.getText());
		
		dispose();
	}
	
	
	
	
	private void populateDialog() {
		
		wStepname.selectAll();
		
		if( meta.getCmisUrl() != null) 	wCmisUrl.setText( meta.getCmisUrl() );
		
		if( meta.getCmisUser() != null) 	wCmisUser.setText( meta.getCmisUser() );
		if( meta.getCmisPassword() != null) 	wCmisPassword.setText( meta.getCmisPassword() );
		
		
		if( meta.getFileUpload() != null) 	wFileUpload.setText( meta.getFileUpload() );
		if( meta.getCmisDirectory() != null) 	wCmisDirectory.setText( meta.getCmisDirectory());
		
		if( meta.getOutputStatus() != null) 	wOutputStatus.setText( meta.getOutputStatus() );
		if( meta.getOutputObjectId() != null) 	wOutputObjectId.setText( meta.getOutputObjectId() );
		if( meta.getOutputError() != null) 	wOutputError.setText( meta.getOutputError() );
		
		
	}
}
