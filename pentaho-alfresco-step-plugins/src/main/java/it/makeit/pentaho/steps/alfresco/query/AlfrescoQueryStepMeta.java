package it.makeit.pentaho.steps.alfresco.query;

import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.PropertyBoolean;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.PropertyDateTime;
import org.apache.chemistry.opencmis.commons.data.PropertyDecimal;
import org.apache.chemistry.opencmis.commons.data.PropertyHtml;
import org.apache.chemistry.opencmis.commons.data.PropertyId;
import org.apache.chemistry.opencmis.commons.data.PropertyInteger;
import org.apache.chemistry.opencmis.commons.data.PropertyString;
import org.apache.chemistry.opencmis.commons.data.PropertyUri;
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
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
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

import com.google.common.base.Strings;

import it.makeit.pentaho.steps.alfresco.helper.AlfrescoStepHelper;


@Step(id = "AlfrescoQueryStep", 
		name = "AlfrescoQueryStep.Name", 
		description = "AlfrescoQueryStep.TooltipDesc", 
		image = "it/makeit/pentaho/steps/alfresco/upload/resources/alfresco_query.svg", 
		categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Input", 
		i18nPackageName = "it.makeit.pentaho.steps.alfresco.query", documentationUrl = "AlfrescoQueryStep.DocumentationURL", 
		casesUrl = "AlfrescoQueryStep.CasesURL", forumUrl = "AlfrescoQueryStep.ForumURL")
@InjectionSupported(localizationPrefix = "AlfrescoQueryStepMeta.Injection.")
public class AlfrescoQueryStepMeta extends BaseStepMeta implements StepMetaInterface {

	//private static final Class<?> PKG = AlfrescoQueryStepMeta.class; // for i18n purposes

	@Injection(name = "CMIS_URL")
	private String cmisUrl;
	@Injection(name = "CMIS_USER")
	private String cmisUser;
	@Injection(name = "CMIS_PASSOWRD")
	private String cmisPassword;
	@Injection(name = "CMIS_QUERY")
	private String cmisQuery;


	public AlfrescoQueryStepMeta() {
		super();
	}

	@Override
	public void setDefault() {
		// SET DEFAULT
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new AlfrescoQueryStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new AlfrescoQueryStepData();
	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		
		
		
		String sql = space.environmentSubstitute(getCmisQuery());

		String url = space.environmentSubstitute(getCmisUrl());

		String user = space.environmentSubstitute(getCmisUser());
		String password = space.environmentSubstitute(getCmisPassword());

		if(Strings.isNullOrEmpty(sql) || 
				Strings.isNullOrEmpty(url) || 
				Strings.isNullOrEmpty(user) || 
				Strings.isNullOrEmpty(password)) {
		
			return;
		}
		
		
		Session session = AlfrescoStepHelper.createSession(url, user, password);
		session.getDefaultContext().setCacheEnabled(false);
		session.getDefaultContext().setMaxItemsPerPage(Integer.MAX_VALUE);
		
		ItemIterable<QueryResult> result = session.query(sql, false);
		
		Iterator<QueryResult> it = result.iterator();
		if(it.hasNext()) {
			QueryResult firstRow = it.next();
			for (PropertyData<?> property : firstRow.getProperties()) {

				String propertyName = property.getQueryName().replaceAll(":", "_");

				ValueMetaInterface v = null;
				Object value = property.getFirstValue();
				if (value == null) {
					v = new ValueMetaString(propertyName);
				} else if (property instanceof PropertyString) {
					v = new ValueMetaString(propertyName);
				} else if (property instanceof PropertyInteger) {
					v = new ValueMetaInteger(propertyName);
				} else if (property instanceof PropertyDecimal) {
					v = new ValueMetaBigNumber(propertyName);
				} else if (property instanceof PropertyBoolean) {
					v = new ValueMetaBoolean(propertyName);
				} else if (property instanceof PropertyDateTime) {
					v = new ValueMetaDate(propertyName);
				} else if (property instanceof PropertyId || property instanceof PropertyUri || property instanceof PropertyHtml) {
					v = new ValueMetaString(propertyName);
				} else {
					throw new IllegalArgumentException("Unsupported type: " + value.getClass());
				}
				v.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
				v.setOrigin(name);
				inputRowMeta.addValueMeta(v);

			}
			
		}
		
	}
	
	
	// serializzazione dello step in xml
	public String getXML() throws KettleValueException {
		StringBuilder xml = new StringBuilder();

		xml.append(XMLHandler.addTagValue("cmisUrl", cmisUrl));
		xml.append(XMLHandler.addTagValue("cmisUser", cmisUser));
		xml.append(XMLHandler.addTagValue("cmisPassword", cmisPassword));
		xml.append(XMLHandler.addTagValue("cmisQuery", cmisQuery));


		return xml.toString();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		try {
			setCmisUrl(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisUrl")));
			setCmisUser(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisUser")));
			setCmisPassword(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisPassword")));
			setCmisQuery(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "cmisQuery")));

			
			
			
		} catch (Exception e) {
			throw new KettleXMLException("Alfresco Query Step plugin unable to read step info from XML node", e);
		}
	}

	// serializzazione sul repository delle trasformazioni
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "cmisUrl", cmisUrl); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisUser", cmisUser); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "cmisPassword", cmisPassword); //$NON-NLS-1$

			rep.saveStepAttribute(id_transformation, id_step, "cmisQuery", cmisQuery); //$NON-NLS-1$

			
		} catch (Exception e) {
			throw new KettleException("Unable to save step into repository: " + id_step, e);
		}
	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
		try {
			cmisUrl = rep.getStepAttributeString(id_step, "cmisUrl"); //$NON-NLS-1$
			cmisUser = rep.getStepAttributeString(id_step, "cmisUser"); //$NON-NLS-1$
			cmisPassword = rep.getStepAttributeString(id_step, "cmisPassword"); //$NON-NLS-1$
			cmisQuery = rep.getStepAttributeString(id_step, "cmisQuery"); //$NON-NLS-1$

			

		} catch (Exception e) {
			throw new KettleException("Unable to load step from repository", e);
		}
	}

	// chiamato quando l'utente spoon fa verify transformation
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore) {
		
		
		
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

	public String getCmisQuery() {
		return cmisQuery;
	}

	public void setCmisQuery(String cmisQuery) {
		this.cmisQuery = cmisQuery;
	}

	

}
