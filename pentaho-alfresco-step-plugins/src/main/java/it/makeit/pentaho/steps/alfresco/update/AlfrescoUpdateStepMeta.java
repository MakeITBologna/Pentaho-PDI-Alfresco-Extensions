package it.makeit.pentaho.steps.alfresco.update;

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

@Step(id = "AlfrescoUpdateStep", 
		name = "AlfrescoUpdateStep.Name", 
		description = "AlfrescoUpdateStep.TooltipDesc", 
		image = "it/makeit/pentaho/steps/alfresco/update/resources/alfresco_update.svg", 
		categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Output", 
		i18nPackageName = "it.makeit.pentaho.steps.alfresco.update", documentationUrl = "AlfrescoUpdateStep.DocumentationURL", 
		casesUrl = "AlfrescoUpdateStep.CasesURL", forumUrl = "AlfrescoUpdateStep.ForumURL")
@InjectionSupported(localizationPrefix = "AlfrescoUpdateStepMeta.Injection.")
public class AlfrescoUpdateStepMeta extends BaseStepMeta implements StepMetaInterface {

	private static final Class<?> PKG = AlfrescoUpdateStepMeta.class; // for i18n purposes

	
	public static Integer PATH = 0, OBJECT_ID = 1;
	
	@Injection(name = "CMIS_URL")
	private String cmisUrl;
	@Injection(name = "CMIS_USER")
	private String cmisUser;
	@Injection(name = "CMIS_PASSOWRD")
	private String cmisPassword;

	@Injection(name = "CMIS_FILE")
	private String cmisFile;
	@Injection(name = "CMIS_FILE_TYPE")
	private Integer cmisFileType;

	
	@Injection(name = "CMIS_PROPERTIES")
	private String cmisProperties;
	
	@Injection(name = "OUTPUT_STATUS")
	private String outputStatus;
	@Injection(name = "OUTPUT_ERROR")
	private String outputError;
	@Injection(name = "OUTPUT_OBJECT_ID")
	private String outputObjectId;

	public AlfrescoUpdateStepMeta() {
		super();
	}

	@Override
	public void setDefault() {
		// SET DEFAULT
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new AlfrescoUpdateStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new AlfrescoUpdateStepData();
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

		xml.append(XMLHandler.addTagValue("cmisFile", cmisFile));
		if(cmisFileType != null) {
			xml.append(XMLHandler.addTagValue("cmisFileType", cmisFileType));
		}
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

			setCmisFile(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisFile")));
			
			String fileType = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisFileType"));
			setCmisFileType(fileType != null ? Integer.parseInt(fileType) : PATH);

			
			setCmisProperties(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisProperties")));

			setOutputStatus(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputStatus")));
			setOutputError(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputError")));
			setOutputObjectId(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputObjectId")));
			
			
		} catch (Exception e) {
			throw new KettleXMLException("Alfresco Update Step plugin unable to read step info from XML node", e);
		}
	}

	// serializzazione sul repository delle trasformazioni
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "cmisUrl", cmisUrl); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisUser", cmisUser); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisPassword", cmisPassword); //$NON-NLS-1$

			rep.saveStepAttribute(id_transformation, id_step, "cmisFile", cmisFile); //$NON-NLS-1$
			if(cmisFileType != null) {
				rep.saveStepAttribute(id_transformation, id_step, "cmisFileType", cmisFileType); //$NON-NLS-1$
			}
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

			cmisFile = rep.getStepAttributeString(id_step, "cmisFile"); //$NON-NLS-1$
			
			String cmisFileType = rep.getStepAttributeString(id_step, "cmisFileType");
			if(cmisFileType != null) {
				this.cmisFileType = Integer.parseInt(cmisFileType);  //$NON-NLS-1$	
			}
			

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
			CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "AlfrescoUpdateStep.CheckResult.ReceivingRows.OK"), stepMeta);
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AlfrescoUpdateStep.CheckResult.ReceivingRows.ERROR"), stepMeta);
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

	public String getCmisProperties() {
		return cmisProperties;
	}

	public void setCmisProperties(String cmisProperties) {
		this.cmisProperties = cmisProperties;
	}

	public String getCmisFile() {
		return cmisFile;
	}

	public void setCmisFile(String cmisFile) {
		this.cmisFile = cmisFile;
	}

	public Integer getCmisFileType() {
		return cmisFileType;
	}

	public void setCmisFileType(Integer cmisFileType) {
		this.cmisFileType = cmisFileType;
	}

}
