package it.makeit.pentaho.steps.alfresco.query;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.ui.trans.steps.tableinput.SQLValuesHighlight;

import com.google.common.base.Strings;

public class AlfrescoQueryStepDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = AlfrescoQueryStepMeta.class; // for i18n purposes

	private AlfrescoQueryStepMeta meta;

	private Label wlCmisUrl;
	private TextVar wCmisUrl;

	private Label wlCmisUser;
	private TextVar wCmisUser;

	private Label wlCmisPassword;
	private TextVar wCmisPassword;

	// private LabelCombo wCmisUrl;
	// private LabelCombo wCmisUser;
	// private LabelCombo wCmisPassword;

	private StyledTextComp wSQL;

	public AlfrescoQueryStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (AlfrescoQueryStepMeta) in;
	}

	@Override
	public String open() {
		// store some convenient SWT variables
		Shell parent = getParent();
		Display display = parent.getDisplay();

		// La shell è la finestra
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);

		setShellImage(shell, meta);

		// valore changed se l'utente modifica la finestra
		changed = meta.hasChanged();

		// ogni volta che viene modificato un campo il meta deve essere imposto a
		// changed
		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				meta.setChanged();
			}
		};

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// RIGA STEP NAME
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		Group alfrescoGroup = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(alfrescoGroup);
		alfrescoGroup.setText(BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.groupAlfresco"));
		FormData fdAlfrescoGroup = new FormData();
		fdAlfrescoGroup.left = new FormAttachment(0, 0);
		fdAlfrescoGroup.right = new FormAttachment(100, 0);
		fdAlfrescoGroup.top = new FormAttachment(wStepname, margin);
		alfrescoGroup.setLayoutData(fdAlfrescoGroup);

		FormLayout alfrescoFormLayout = new FormLayout();
		alfrescoFormLayout.marginWidth = Const.FORM_MARGIN;
		alfrescoFormLayout.marginHeight = Const.FORM_MARGIN;
		alfrescoGroup.setLayout(alfrescoFormLayout);

		// RIGA CMIS URL
		Control lastControl = null;

		wlCmisUrl = new Label(alfrescoGroup, SWT.RIGHT);
		wlCmisUrl.setText(BaseMessages.getString(PKG, "AlfrescoQueryStep.ui.cmisUrl"));
		props.setLook(wlCmisUrl);
		FormData fdlCmisUrl = new FormData();
		fdlCmisUrl.left = new FormAttachment(0, 0);
		fdlCmisUrl.right = new FormAttachment(middle, -margin);
		fdlCmisUrl.top = new FormAttachment(lastControl, margin);
		wlCmisUrl.setLayoutData(fdlCmisUrl);

		wCmisUrl = new TextVar(transMeta, alfrescoGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wCmisUrl);
		wCmisUrl.addModifyListener(lsMod);
		FormData fdCmisUrl = new FormData();
		fdCmisUrl.left = new FormAttachment(middle, 0);
		fdCmisUrl.top = new FormAttachment(lastControl, margin);
		fdCmisUrl.right = new FormAttachment(100, 0);
		wCmisUrl.setLayoutData(fdCmisUrl);
		lastControl = wCmisUrl;

		// RIGA CMIS USER
		wlCmisUser = new Label(alfrescoGroup, SWT.RIGHT);
		wlCmisUser.setText(BaseMessages.getString(PKG, "AlfrescoQueryStep.ui.cmisUser"));
		props.setLook(wlCmisUser);
		FormData fdlCmisUser = new FormData();
		fdlCmisUser.left = new FormAttachment(0, 0);
		fdlCmisUser.right = new FormAttachment(middle, -margin);
		fdlCmisUser.top = new FormAttachment(lastControl, margin);
		wlCmisUser.setLayoutData(fdlCmisUser);

		wCmisUser = new TextVar(transMeta, alfrescoGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wCmisUser);
		wCmisUser.addModifyListener(lsMod);
		FormData fdCmisUser = new FormData();
		fdCmisUser.left = new FormAttachment(middle, 0);
		fdCmisUser.top = new FormAttachment(lastControl, margin);
		fdCmisUser.right = new FormAttachment(100, 0);
		wCmisUser.setLayoutData(fdCmisUser);
		lastControl = wCmisUser;

		// RIGA CMIS PASSWORD
		wlCmisPassword = new Label(alfrescoGroup, SWT.RIGHT);
		wlCmisPassword.setText(BaseMessages.getString(PKG, "AlfrescoQueryStep.ui.cmisPassword"));
		props.setLook(wlCmisPassword);
		FormData fdlCmisPassword = new FormData();
		fdlCmisPassword.left = new FormAttachment(0, 0);
		fdlCmisPassword.right = new FormAttachment(middle, -margin);
		fdlCmisPassword.top = new FormAttachment(lastControl, margin);
		wlCmisPassword.setLayoutData(fdlCmisPassword);

		wCmisPassword = new TextVar(transMeta, alfrescoGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wCmisPassword);
		wCmisPassword.addModifyListener(lsMod);
		FormData fdCmisPassword = new FormData();
		fdCmisPassword.left = new FormAttachment(middle, 0);
		fdCmisPassword.top = new FormAttachment(lastControl, margin);
		fdCmisPassword.right = new FormAttachment(100, 0);
		wCmisPassword.setLayoutData(fdCmisPassword);
		lastControl = wCmisPassword;
		
		
		
		

		Group queryGroup = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(queryGroup);
		queryGroup.setText(BaseMessages.getString(PKG, "AlfrescoQueryStep.ui.groupQuery"));
		FormData fdQueryGroup = new FormData();
		fdQueryGroup.left = new FormAttachment(0, 0);
		fdQueryGroup.right = new FormAttachment(100, 0);
		fdQueryGroup.top = new FormAttachment(alfrescoGroup, margin);
		queryGroup.setLayoutData(fdQueryGroup);

		FormLayout outputFormLayout = new FormLayout();
		outputFormLayout.marginWidth = Const.FORM_MARGIN;
		outputFormLayout.marginHeight = Const.FORM_MARGIN;
		queryGroup.setLayout(outputFormLayout);

		// RIGA STATUS
		wSQL = new StyledTextComp(transMeta, queryGroup, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, null);
		props.setLook(queryGroup, Props.WIDGET_STYLE_FIXED);
		wSQL.addModifyListener(lsMod);
		FormData fdSQL = new FormData();
		fdSQL.left = new FormAttachment(0, 0);
		fdSQL.top = new FormAttachment(wCmisPassword, margin);
		fdSQL.right = new FormAttachment(100, 0);
		fdSQL.bottom = new FormAttachment(100, -margin);
		fdSQL.height = 200;
		wSQL.setLayoutData(fdSQL);
		// Text Higlighting
		wSQL.addLineStyleListener(new SQLValuesHighlight());

		wSQL.setText("select * from cmis:document");

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		setButtonPositions(new Button[] { wOK, wCancel }, margin, queryGroup);

		// Add listeners for cancel and OK
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		// bottone invio
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};
		wStepname.addSelectionListener(lsDef);
		wCmisUrl.addSelectionListener(lsDef);
		wCmisUser.addSelectionListener(lsDef);
		wCmisPassword.addSelectionListener(lsDef);

		// X o ALT-F4
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// gestione del resize e riapertura
		setSize();

		setFieldValues();

		// restore the changed flag to original value, as the modify listeners fire
		// during dialog population
		meta.setChanged(changed);

		// open dialog and enter event loop
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// at this point the dialog has closed, so either ok() or cancel() have been
		// executed
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

		meta.setCmisQuery(!Strings.isNullOrEmpty(wSQL.getText()) ? wSQL.getText() : null);

		dispose();
	}

	private void setFieldValues() {

		wStepname.selectAll();

		if (meta.getCmisUrl() != null)
			wCmisUrl.setText(meta.getCmisUrl());

		if (meta.getCmisUser() != null)
			wCmisUser.setText(meta.getCmisUser());
		if (meta.getCmisPassword() != null)
			wCmisPassword.setText(meta.getCmisPassword());

		if (meta.getCmisQuery() != null) {
			wSQL.setText(meta.getCmisQuery());
		}

	}

	/*
	private void setComboFieldField(LabelCombo labelCombo) {
		try {
			labelCombo.removeAll();

			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null) {
				labelCombo.setItems(r.getFieldNames());
			}

		} catch (KettleException ke) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.FailedToGetFields.DialogTitle"), BaseMessages.getString(PKG, "AlfrescoUploadStep.ui.FailedToGetFields.DialogMessage"), ke);
		}
	}*/

}
