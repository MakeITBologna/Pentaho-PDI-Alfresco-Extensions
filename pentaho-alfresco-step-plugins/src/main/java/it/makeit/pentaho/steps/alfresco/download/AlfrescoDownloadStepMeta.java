package it.makeit.pentaho.steps.alfresco.download;

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

@Step(id = "AlfrescoDownloadStep", 
		name = "AlfrescoDownloadStep.Name", 
		description = "AlfrescoDownloadStep.TooltipDesc", 
		image = "it/makeit/pentaho/steps/alfresco/Download/resources/alfresco_download.svg", 
		categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Output", 
		i18nPackageName = "it.makeit.pentaho.steps.alfresco.Download", documentationUrl = "AlfrescoDownloadStep.DocumentationURL", 
		casesUrl = "AlfrescoDownloadStep.CasesURL", forumUrl = "AlfrescoDownloadStep.ForumURL")
@InjectionSupported(localizationPrefix = "AlfrescoDownloadStepMeta.Injection.")
public class AlfrescoDownloadStepMeta extends BaseStepMeta implements StepMetaInterface {

	private static final Class<?> PKG = AlfrescoDownloadStepMeta.class; // for i18n purposes

	
	public static Integer PATH = 0, OBJECT_ID = 1;
	
	@Injection(name = "CMIS_URL")
	private String cmisUrl;
	@Injection(name = "CMIS_USER")
	private String cmisUser;
	@Injection(name = "CMIS_PASSOWRD")
	private String cmisPassword;

	@Injection(name = "FILE_Download")
	private String fileDownload;
	@Injection(name = "CMIS_FILE")
	private String cmisFile;
	@Injection(name = "CMIS_FILE_TYPE")
	private Integer cmisFileType;

	

	public AlfrescoDownloadStepMeta() {
		super();
	}

	@Override
	public void setDefault() {
		// SET DEFAULT
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new AlfrescoDownloadStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new AlfrescoDownloadStepData();
	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		
		
	    
	}
	
	
	// serializzazione dello step in xml
	public String getXML() throws KettleValueException {
		StringBuilder xml = new StringBuilder();

		xml.append(XMLHandler.addTagValue("cmisUrl", cmisUrl));
		xml.append(XMLHandler.addTagValue("cmisUser", cmisUser));
		xml.append(XMLHandler.addTagValue("cmisPassword", cmisPassword));

		xml.append(XMLHandler.addTagValue("fileDownload", fileDownload));
		xml.append(XMLHandler.addTagValue("cmisFile", cmisFile));
		if(cmisFileType != null) {
			xml.append(XMLHandler.addTagValue("cmisFileType", cmisFileType));
		}

		

		return xml.toString();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		try {
			setCmisUrl(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisUrl")));
			setCmisUser(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisUser")));
			setCmisPassword(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisPassword")));

			setFileDownload(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "fileDownload")));
			setCmisFile(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisFile")));
			
			String fileType = XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisFileType"));
			setCmisFileType(fileType != null ? Integer.parseInt(fileType) : PATH);

			
			
			
		} catch (Exception e) {
			throw new KettleXMLException("Alfresco Download Step plugin unable to read step info from XML node", e);
		}
	}

	// serializzazione sul repository delle trasformazioni
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "cmisUrl", cmisUrl); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisUser", cmisUser); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisPassword", cmisPassword); //$NON-NLS-1$

			rep.saveStepAttribute(id_transformation, id_step, "fileDownload", fileDownload); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisFile", cmisFile); //$NON-NLS-1$
			if(cmisFileType != null) {
				rep.saveStepAttribute(id_transformation, id_step, "cmisFileType", cmisFileType); //$NON-NLS-1$
			}

			

		} catch (Exception e) {
			throw new KettleException("Unable to save step into repository: " + id_step, e);
		}
	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
		try {
			cmisUrl = rep.getStepAttributeString(id_step, "cmisUrl"); //$NON-NLS-1$
			cmisUser = rep.getStepAttributeString(id_step, "cmisUser"); //$NON-NLS-1$
			cmisPassword = rep.getStepAttributeString(id_step, "cmisPassword"); //$NON-NLS-1$

			fileDownload = rep.getStepAttributeString(id_step, "fileDownload"); //$NON-NLS-1$
			cmisFile = rep.getStepAttributeString(id_step, "cmisFile"); //$NON-NLS-1$
			
			String cmisFileType = rep.getStepAttributeString(id_step, "cmisFileType");
			if(cmisFileType != null) {
				this.cmisFileType = Integer.parseInt(cmisFileType);  //$NON-NLS-1$	
			}
			

			

		} catch (Exception e) {
			throw new KettleException("Unable to load step from repository", e);
		}
	}

	// chiamato quando l'utente spoon fa verify transformation
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore) {
		
		if (input != null && input.length > 0) {
			CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "AlfrescoDownloadStep.CheckResult.ReceivingRows.OK"), stepMeta);
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AlfrescoDownloadStep.CheckResult.ReceivingRows.ERROR"), stepMeta);
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


	public String getFileDownload() {
		return fileDownload;
	}

	public void setFileDownload(String fileDownload) {
		this.fileDownload = fileDownload;
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
