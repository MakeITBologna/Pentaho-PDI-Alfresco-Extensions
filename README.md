# Pentaho-PDI-Alfresco-Extensions
Pentaho PDI Alfresco Extensions

Pentaho-PDI-Alfresco-Extensions introduces Alfresco/CMIS file upload feature in PDI.

The extension permits to: 
* **query** Alfresco using a CMIS query
* **download** document from Alfresco
* get document **properties and relations**
* **upload** documents to Alfresco, specifying  DocumentType, Metadata, Destination Folder and to get output in a human-readable format. 
* **update** document properties

## Query
### Input
* URL Alfresco
* Username Alfresco
* Password Alfresco
* CMIS query
### Output
CMIS query results as rows

## Download

### Input
* URL Alfresco
* Username Alfresco
* Password Alfresco
* File to dowload: destination path of the downloaded file
* CMIS File to download
* CMIS File path: file identification mode: path or objectid
### Output
No output

## DOCUMENT PROPERTIES AND RELATIONS

### Input
* URL Alfresco
* Username Alfresco
* Password Alfresco
* Detail: variable in which properties and relations will be copied in json format
* CMIS File to get info
* CMIS File path: file identification mode: path or objectid
### Output
No output

## Upload

### Input
* URL Alfresco
* Username Alfresco
* Password Alfresco
* Path of the file to upload
* Path of Alfresco directory
* DocumentType in Alfresco
* CMIS properties expressed in JSON format

#### CMIS PROPERTIES

| __Data type Alfresco__ | __Mapping Conventions__ |
|-------------|------------|
| d:text | S# ... | 
| d:int | I# ... | 
| d:long | L# ... |
| d:double | D# ... |
| d:boolean | B#true (or) B#false |
| d:date | DT# + date with "dd/MM/yyyy" format |
| d:datetime | TS# + timestamp with "dd/MM/yyyy HH:mm:ss" format |

Example:

```json
{
"myInv:description":"S#80 2018 Favourite Customer",
"myInv:amount":"D#1233.45",
"myInv:date":"TS#01/02/2018 10:35:00"
}
```

### Output
* Upload Status (ok/ko)
* Alfresco Noderef (ObjectID)
* ErrorLog (if any). *Please note*: errors are in output and the transformations never get aborted by an error.

## How to install
1) Download last version of extensions from releases page: https://github.com/MakeITBologna/Pentaho-PDI-Alfresco-Extensions/releases
2) Unzip the downloaded file in plugins directory of Pentaho Data Integration

## Compatibility

Pentaho PDI Extensions is compatible with PDI version 7 and above

## License

Pentaho PDI Extensions is licensed under the terms of [GNU GPL License](https://www.gnu.org/licenses/gpl-3.0.en.html).



