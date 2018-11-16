package it.makeit.pentaho.steps.alfresco.upload.helper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

public class AlfrescoUploadStepHelper {

	
	public static Session createSession(String url, String user, String password) {
		
		Map<String, String> lMapParameter = new HashMap<String, String>();

		// I parametri di connessione vengono impostati per usare il binding
		// AtomPub CMIS 1.1
		lMapParameter.put(SessionParameter.USER, user);
		lMapParameter.put(SessionParameter.PASSWORD, password);
		lMapParameter.put(SessionParameter.ATOMPUB_URL, url + "/alfresco/api/-default-/public/cmis/versions/1.1/atom");
		lMapParameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		// creo la session factory
		SessionFactory lSessionFactory = SessionFactoryImpl.newInstance();

		// creo la sessione connessa al repository
		Session lSession = lSessionFactory.getRepositories(lMapParameter).get(0).createSession();
		
		
		return lSession;
	}
	
	
	public static Document createDocument(Session pSession, String pStrParentFolderId, String pStrFileName,	long pLngFileLength, String pStrContentType, InputStream pInputStream, Map<String, Object> pMapProperties,	Collection<String> pCollectionAspects, String pStrObjectType) {

		
		String lStrEffectiveContentType = pStrContentType != null ? pStrContentType : "application/octet-stream";
		
		// recupero la cartella padre
		Folder lFolder = (Folder) pSession.getObject(pStrParentFolderId);

		// creo il content stream, se necessario
		ContentStream lContentStream = null;
		if (pInputStream != null) {
			lContentStream = pSession.getObjectFactory().createContentStream(pStrFileName, pLngFileLength, lStrEffectiveContentType, pInputStream);
		}

		if (pMapProperties == null) {
			// La mappa delle proprietà è necessaria per la creazione
			pMapProperties = new HashMap<String, Object>();
		}

		// Aggiornamento proprietà con tipo ed eventuali aspetti
		String lStrObjectType = (pStrObjectType == null) ? "cmis:document" : pStrObjectType;
		pMapProperties.put(PropertyIds.OBJECT_TYPE_ID, lStrObjectType);
		if (pCollectionAspects != null) {
			updatePropertiesWithAspects(pSession, pMapProperties, pStrObjectType, pCollectionAspects);
		}
		pMapProperties.put(PropertyIds.NAME, pStrFileName);

		// Creazione effettiva
		Document lDocument = lFolder.createDocument(pMapProperties, lContentStream, VersioningState.MAJOR);

		return lDocument;
	}
	
	private static void updatePropertiesWithAspects(Session pSession, Map<String, Object> pMapProperties,
			String pStrDocTypeId, Collection<String> pCollectionAspects) {
		// Creazione mappa proprietà secondo la versione CMIS del repository
		StringBuilder lSBAspects = new StringBuilder(pStrDocTypeId);
		for (String aspect : pCollectionAspects) {
			lSBAspects.append(", ").append(aspect);
		}
		pMapProperties.put(PropertyIds.OBJECT_TYPE_ID, lSBAspects.toString());
	}
	
	public static Folder getOrCreateFolderByPath(Session session, String path) {
		if(!path.startsWith("/")) {
			path = "/" + path;
		}
		Folder folder = getFolderByPath(session, path);
		
		if(folder != null) {
			// exists
			return folder;
		} else {
			// need to create
			
			Folder root = session.getRootFolder();	
			List<String> subfolders = Arrays.asList(path.split("/")).stream().filter(s -> s != null && s.trim().length() > 0).collect(Collectors.toList());
			
			return getOrCreateFolder(session, root, subfolders.iterator());	
		}
		
		
	}
	
	private static Folder getOrCreateFolder(Session session, Folder currentFolder, Iterator<String> subfolders) {
		
		if(subfolders.hasNext()) {
			String newPath = subfolders.next();
			Folder newFolder = getFolderByPath(session, (currentFolder.getPath().endsWith("/") ? "":currentFolder.getPath()) + "/" + newPath);
			if(newFolder == null) {
				newFolder = createFolder(session, currentFolder.getId(), newPath);
			}
			return getOrCreateFolder(session, newFolder, subfolders);
		} else {
			return currentFolder;
		}
		
		
	}
	
	public static Folder getFolderByPath(Session pSession, String pStrPath) {
		Folder lFolder = null;

		try {
			lFolder = (Folder) pSession.getObjectByPath(pStrPath);
		} catch (CmisObjectNotFoundException e) {
			// non trovato
		}

		return lFolder;
	}
	
	private static Folder createFolder(Session pSession, String pStrParentFolderId, String pStrFolderName) {

		// recupero la cartella padre
		Folder lFolder = (Folder) pSession.getObject(pStrParentFolderId);

		// creo la cartella
		Map<String, String> lMapProperties = new HashMap<String, String>();
		lMapProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		lMapProperties.put(PropertyIds.NAME, pStrFolderName);
		
		try {
			return lFolder.createFolder(lMapProperties);

		} catch (CmisContentAlreadyExistsException e) {
			
			// potrebbe essere stata creta da un altro thread
			return (Folder) pSession.getObjectByPath(lFolder.getPath() + "/" + pStrFolderName);
		}
		

	}
}
