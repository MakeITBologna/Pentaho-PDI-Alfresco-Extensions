package it.makeit.pentaho.steps.alfresco.upload;

import java.util.List;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(id = "AlfrescoUploadStep", 
		name = "AlfrescoUploadStep.Name", 
		description = "AlfrescoUploadStep.TooltipDesc", 
		image = "it/makeit/pentaho/steps/alfresco/upload/resources/alfresco_upload.svg", 
		categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Output", 
		i18nPackageName = "it.makeit.pentaho.steps.alfresco.upload", documentationUrl = "AlfrescoUploadStep.DocumentationURL", 
		casesUrl = "AlfrescoUploadStep.CasesURL", forumUrl = "AlfrescoUploadStep.ForumURL")
@InjectionSupported(localizationPrefix = "AlfrescoUploadStepMeta.Injection.")
public class AlfrescoUploadStepMeta extends BaseStepMeta implements StepMetaInterface {

	private static final Class<?> PKG = AlfrescoUploadStepMeta.class; // for i18n purposes

	
	public static Integer PATH = 0, OBJECT_ID = 1;
	
	@Injection(name = "CMIS_URL")
	private String cmisUrl;
	@Injection(name = "CMIS_USER")
	private String cmisUser;
	@Injection(name = "CMIS_PASSOWRD")
	private String cmisPassword;

	@Injection(name = "FILE_UPLOAD")
	private String fileUpload;
	@Injection(name = "CMIS_DIRECTORY")
	private String cmisDirectory;
	@Injection(name = "CMIS_DIRECTORY_TYPE")
	private Integer cmisDirectoryType;

	
	@Injection(name = "CMIS_DOCUMENT_TYPE")
	private String cmisDocumentType;
	@Injection(name = "CMIS_PROPERTIES")
	private String cmisProperties;
	
	@Injection(name = "OUTPUT_STATUS")
	private String outputStatus;
	@Injection(name = "OUTPUT_ERROR")
	private String outputError;
	@Injection(name = "OUTPUT_OBJECT_ID")
	private String outputObjectId;

	public AlfrescoUploadStepMeta() {
		super();
	}

	@Override
	public void setDefault() {
		// SET DEFAULT
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new AlfrescoUploadStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new AlfrescoUploadStepData();
	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		
		ValueMetaInterface v1 = new ValueMetaString( outputStatus );
	    v1.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
	    v1.setOrigin( name );
	    inputRowMeta.addValueMeta( v1 );
	    
	    ValueMetaInterface v2 = new ValueMetaString( outputError );
	    v2.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
	    v2.setOrigin( name );
	    inputRowMeta.addValueMeta( v2 );
	    
	    
	    ValueMetaInterface v3 = new ValueMetaString( outputObjectId );
	    v3.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
	    v3.setOrigin( name );
	    inputRowMeta.addValueMeta( v3 );
	    
	    
	}
	
	
	// serializzazione dello step in xml
	public String getXML() throws KettleValueException {
		StringBuilder xml = new StringBuilder();

		xml.append(XMLHandler.addTagValue("cmisUrl", cmisUrl));
		xml.append(XMLHandler.addTagValue("cmisUser", cmisUser));
		xml.append(XMLHandler.addTagValue("cmisPassword", cmisPassword));

		xml.append(XMLHandler.addTagValue("fileUpload", fileUpload));
		xml.append(XMLHandler.addTagValue("cmisDirectory", cmisDirectory));
		if(cmisDirectoryType != null) {
			xml.append(XMLHandler.addTagValue("cmisDirectoryType", cmisDirectoryType));
		}

		xml.append(XMLHandler.addTagValue("cmisDocumentType", cmisDocumentType));
		xml.append(XMLHandler.addTagValue("cmisProperties", cmisProperties));
		
		xml.append(XMLHandler.addTagValue("outputStatus", outputStatus));
		xml.append(XMLHandler.addTagValue("outputError", outputError));
		xml.append(XMLHandler.addTagValue("outputObjectId", outputObjectId));

		return xml.toString();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		try {
			setCmisUrl(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisUrl")));
			setCmisUser(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisUser")));
			setCmisPassword(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisPassword")));

			setFileUpload(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "fileUpload")));
			setCmisDirectory(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisDirectory")));
			
			String directoryType = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisDirectoryType"));
			setCmisDirectoryType(directoryType != null ? Integer.parseInt(directoryType) : PATH);

			setCmisDocumentType(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisDocumentType")));
			setCmisProperties(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisProperties")));

			setOutputStatus(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputStatus")));
			setOutputError(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputError")));
			setOutputObjectId(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputObjectId")));
			
			
		} catch (Exception e) {
			throw new KettleXMLException("Alfresco Upload Step plugin unable to read step info from XML node", e);
		}
	}

	// serializzazione sul repository delle trasformazioni
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "cmisUrl", cmisUrl); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisUser", cmisUser); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisPassword", cmisPassword); //$NON-NLS-1$

			rep.saveStepAttribute(id_transformation, id_step, "fileUpload", fileUpload); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisDirectory", cmisDirectory); //$NON-NLS-1$
			if(cmisDirectoryType != null) {
				rep.saveStepAttribute(id_transformation, id_step, "cmisDirectoryType", cmisDirectoryType); //$NON-NLS-1$
			}

			rep.saveStepAttribute(id_transformation, id_step, "cmisDocumentType", cmisDocumentType); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisProperties", cmisProperties); //$NON-NLS-1$
			
			rep.saveStepAttribute(id_transformation, id_step, "outputStatus", outputStatus); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "outputError", outputError); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "outputObjectId", outputObjectId); //$NON-NLS-1$

		} catch (Exception e) {
			throw new KettleException("Unable to save step into repository: " + id_step, e);
		}
	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
		try {
			cmisUrl = rep.getStepAttributeString(id_step, "cmisUrl"); //$NON-NLS-1$
			cmisUser = rep.getStepAttributeString(id_step, "cmisUser"); //$NON-NLS-1$
			cmisPassword = rep.getStepAttributeString(id_step, "cmisPassword"); //$NON-NLS-1$

			fileUpload = rep.getStepAttributeString(id_step, "fileUpload"); //$NON-NLS-1$
			cmisDirectory = rep.getStepAttributeString(id_step, "cmisDirectory"); //$NON-NLS-1$
			
			String cmisDirectoryType = rep.getStepAttributeString(id_step, "cmisDirectoryType");
			if(cmisDirectoryType != null) {
				this.cmisDirectoryType = Integer.parseInt(cmisDirectoryType);  //$NON-NLS-1$	
			}
			

			cmisDocumentType = rep.getStepAttributeString(id_step, "cmisDocumentType"); //$NON-NLS-1$
			cmisProperties = rep.getStepAttributeString(id_step, "cmisProperties"); //$NON-NLS-1$
			
			outputStatus = rep.getStepAttributeString(id_step, "outputStatus"); //$NON-NLS-1$
			outputError = rep.getStepAttributeString(id_step, "outputError"); //$NON-NLS-1$
			outputObjectId = rep.getStepAttributeString(id_step, "outputObjectId"); //$NON-NLS-1$

		} catch (Exception e) {
			throw new KettleException("Unable to load step from repository", e);
		}
	}

	// chiamato quando l'utente spoon fa verify transformation
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore) {
		
		if (input != null && input.length > 0) {
			CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "AlfrescoUploadStep.CheckResult.ReceivingRows.OK"), stepMeta);
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AlfrescoUploadStep.CheckResult.ReceivingRows.ERROR"), stepMeta);
			remarks.add(cr);
		}
		
	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	// GETTERS AND SETTERS

	public String getCmisUrl() {
		return cmisUrl;
	}

	public void setCmisUrl(String cmisUrl) {
		this.cmisUrl = cmisUrl;
	}

	public String getCmisUser() {
		return cmisUser;
	}

	public void setCmisUser(String cmisUser) {
		this.cmisUser = cmisUser;
	}

	public String getCmisPassword() {
		return cmisPassword;
	}

	public void setCmisPassword(String cmisPassword) {
		this.cmisPassword = cmisPassword;
	}

	public String getCmisDirectory() {
		return cmisDirectory;
	}

	public void setCmisDirectory(String cmisDirectory) {
		this.cmisDirectory = cmisDirectory;
	}
	
	public Integer getCmisDirectoryType() {
		return cmisDirectoryType;
	}

	public void setCmisDirectoryType(Integer cmisDirectoryType) {
		this.cmisDirectoryType = cmisDirectoryType;
	}

	public String getOutputStatus() {
		return outputStatus;
	}

	public void setOutputStatus(String outputStatus) {
		this.outputStatus = outputStatus;
	}

	public String getOutputError() {
		return outputError;
	}

	public void setOutputError(String outputError) {
		this.outputError = outputError;
	}

	public String getOutputObjectId() {
		return outputObjectId;
	}

	public void setOutputObjectId(String outputObjectId) {
		this.outputObjectId = outputObjectId;
	}

	public String getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(String fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getCmisDocumentType() {
		return cmisDocumentType;
	}

	public void setCmisDocumentType(String cmisDocumentType) {
		this.cmisDocumentType = cmisDocumentType;
	}

	public String getCmisProperties() {
		return cmisProperties;
	}

	public void setCmisProperties(String cmisProperties) {
		this.cmisProperties = cmisProperties;
	}

}
