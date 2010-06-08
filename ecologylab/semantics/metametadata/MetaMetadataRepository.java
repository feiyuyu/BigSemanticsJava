/**
 * 
 */
package ecologylab.semantics.metametadata;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.collections.PrefixCollection;
import ecologylab.generic.Debug;
import ecologylab.generic.HashMapArrayList;
import ecologylab.net.ParsedURL;
import ecologylab.net.UserAgent;
import ecologylab.semantics.actions.NestedSemanticActionsTranslationScope;
import ecologylab.semantics.metadata.DocumentParserTagNames;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metadata.builtins.DebugMetadata;
import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metadata.builtins.Media;
import ecologylab.semantics.metadata.scalar.MetadataInteger;
import ecologylab.semantics.metadata.scalar.MetadataParsedURL;
import ecologylab.semantics.metadata.scalar.MetadataString;
import ecologylab.semantics.metadata.scalar.MetadataStringBuilder;
import ecologylab.semantics.metadata.scalar.types.MetadataScalarScalarType;
import ecologylab.textformat.NamedStyle;
import ecologylab.xml.ElementState;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.XMLTranslationException;

/**
 * @author damaraju
 * 
 */

public class MetaMetadataRepository extends ElementState implements PackageSpecifier,
		DocumentParserTagNames
{
	private static final String	FIREFOX_3_6_4_AGENT_STRING	= "Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.4) Gecko/20100513 Firefox/3.6.4";

	private static final String																	DEFAULT_STYLE_NAME					= "default";

	/**
	 * The name of the repository.
	 */
	@xml_attribute
	private String																							name;

	/**
	 * The package in which the class files have to be generated.
	 */
	@xml_tag("package")
	@xml_attribute
	private String																							packageName;

	@xml_map("user_agent")
	private HashMap<String, UserAgent>											userAgents;

	@xml_map("search_engine")
	private HashMap<String, SearchEngine>									searchEngines;

	@xml_map("named_style")
	private HashMap<String, NamedStyle>										namedStyles;

	@xml_attribute
	private String																							defaultUserAgentName;

	private String																							defaultUserAgentString			= null;

	/**
	 * The keys for this hashmap are the values within TypeTagNames. This map is filled out
	 * automatically, by translateFromXML(). It contains all bindings, for both Document and Media
	 * subtypes.
	 */
	@xml_map("meta_metadata")
	@xml_nowrap
	private HashMapArrayList<String, MetaMetadata>							repositoryByTagName;

	/**
	 * Repository for Document and its subclasses.
	 */
	private HashMap<String, MetaMetadata>												documentRepositoryByURL			= new HashMap<String, MetaMetadata>();

	/**
	 * Repository for Media and its subclasses.
	 */
	private HashMap<String, MetaMetadata>												mediaRepositoryByURL				= new HashMap<String, MetaMetadata>();

	private HashMap<String, ArrayList<RepositoryPatternEntry>>	documentRepositoryByPattern	= new HashMap<String, ArrayList<RepositoryPatternEntry>>();

	private HashMap<String, ArrayList<RepositoryPatternEntry>>	mediaRepositoryByPattern		= new HashMap<String, ArrayList<RepositoryPatternEntry>>();

	/**
	 * We have only documents as direct binding will be used only in case of feeds and XML
	 */
	private HashMap<String, MetaMetadata>												repositoryByMime						= new HashMap<String, MetaMetadata>();

	private HashMap<String, MetaMetadata>												repositoryBySuffix					= new HashMap<String, MetaMetadata>();

	private PrefixCollection																		urlprefixCollection					= new PrefixCollection(
																																															'/');

	// public static final TranslationScope META_METADATA_TSCOPE = MetaMetadataTranslationScope.get();

	private TranslationScope																		metadataTScope;

	// for debugging
	protected static File																				REPOSITORY_FILE;
	
	File																												file;
	
	static
	{
		MetadataScalarScalarType.init();	// register metadata-specific scalar types
		ecologylab.semantics.metadata.MetadataBuiltinsTranslationScope.get();
	}

	public static void main(String args[])
	{
		REPOSITORY_FILE = new File(
				/* PropertiesAndDirectories.thisApplicationDir(), */"C:\\abhinavThesisCode\\cf\\config\\semantics\\metametadata\\metaMetadataRepository.xml");
		MetaMetadataRepository metaMetaDataRepository = load(REPOSITORY_FILE);
		try
		{
			metaMetaDataRepository.writePrettyXML(System.out);
		}
		catch (XMLTranslationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Load MetaMetadata from repository files from the file directory. Loads the base level xml files
	 * first, then the xml files in the repositorySources folder and lastly the files in the powerUser
	 * folder. Does not build repository maps, because this requires a Metadata TranslationScope,
	 * which comes from ecologylabGeneratedSemantics.
	 * 
	 * @param file
	 *          directory
	 * @param metadataTScope
	 * @return
	 */
	public static MetaMetadataRepository load(File dir)
	{
		println("MetaMetadataRepository directory: " + dir + "\n");
		MetaMetadataRepository result = null;

		FileFilter xmlFilter = new FileFilter()
		{
			public boolean accept(File dir)
			{
				return dir.getName().endsWith(".xml");
			}
		};

		File powerUserDir = new File(dir, "powerUser");
		File repositorySources = new File(dir, "repositorySources");

		TranslationScope metaMetadataTScope = MetaMetadataTranslationScope.get();
		// need to instantiate scope so that meta-metadata translation works properly.
		NestedSemanticActionsTranslationScope.get();
		
		for (File file : dir.listFiles(xmlFilter))
		{
			MetaMetadataRepository repos = readRepository(file, metaMetadataTScope);
			if (result == null)
				result = repos;
			else
				result.joinRepository(repos);
			// result.populateURLBaseMap();
			// // necessary to get, for example, fields for document into pdf...
			// result.populateInheritedValues();
			//			
			// result.populateMimeMap();
			// For debug
			// this.metaMetaDataRepository.writePrettyXML(System.out);
		}

		if (repositorySources.exists())
		{
			for (File file : repositorySources.listFiles(xmlFilter))
				result.joinRepository(readRepository(file, metaMetadataTScope));
		}

		if (powerUserDir.exists())
		{
			for (File file : powerUserDir.listFiles(xmlFilter))
				result.joinRepository(readRepository(file, metaMetadataTScope));
		}

		//FIXME -- get rid of this?!
		Metadata.setRepository(result);

		return result;
	}

	/**
	 * Load MetaMetadataRepository from one file.
	 * 
	 * @param file
	 * @param metadataTScope
	 * @return repository
	 */
	private static MetaMetadataRepository readRepository(File file,
			TranslationScope metaMetadataTScope)
	{
		MetaMetadataRepository repos = null;
		println("MetaMetadataRepository:\t" + new File(file.getParent()).getName() + "/" + file.getName());

		try
		{
			repos = (MetaMetadataRepository) ElementState.translateFromXML(file, metaMetadataTScope);
			repos.file	= file;
		}
		catch (XMLTranslationException e)
		{
			Debug.error("MetaMetadataRepository", "translating repository source file "
					+ file.getAbsolutePath());
		}

		return repos;
	}

	/**
	 * Combines the HashMaps and HashMapStates of the parameter repository to this repository.
	 * 
	 * @param repository
	 * @return
	 */
	public void joinRepository(MetaMetadataRepository repository)
	{
		// combine userAgents
		if (!combineMapStates(repository.userAgents, this.userAgents))
			this.userAgents = repository.userAgents;

		// combine searchEngines
		if (!combineMapStates(repository.searchEngines, this.searchEngines))
			this.searchEngines = repository.searchEngines;

		// combine namedStyles
		if (!combineMapStates(repository.namedStyles, this.namedStyles))
			this.namedStyles = repository.namedStyles;

		// set metaMetadata to have the correct parent repository
		HashMapArrayList<String, MetaMetadata> repositoryByTagName = repository.repositoryByTagName;
		if (repositoryByTagName != null)
		{
			for (MetaMetadata metametadata : repositoryByTagName)
			{
				metametadata.setParent(this);
				metametadata.file	= repository.file;
			}
		}
		
		// combine metaMetadata
		if (!combineMaps(repositoryByTagName, this.repositoryByTagName))
			this.repositoryByTagName = repositoryByTagName;
	}

	private boolean combineMaps(HashMap srcMap, HashMap destMap)
	{
		if (destMap == null)
			return false;
		if (srcMap != null)
			destMap.putAll(srcMap);
		return true;
	}

	private boolean combineMapStates(HashMap srcMap, HashMap destMap)
	{
		if (destMap == null)
			return false;
		if (srcMap != null)
			destMap.putAll(srcMap);
		return true;
	}

	/**
	 * Initialize repository maps using the TranslationScope to find out if classes are Document or
	 * Media.
	 * 
	 * @param metadataTScope
	 */
	public void initializeRepository(TranslationScope metadataTScope)
	{
		this.metadataTScope = metadataTScope;
		initializeDefaultUserAgent();
		
		findAndDeclareNestedMetaMetadata();
		initializeLocationBasedMaps();
		initializeSuffixAndMimeBasedMaps();
		System.out.println();
	}

	/**
	 * 
	 */
	private void initializeDefaultUserAgent()
	{
		if (defaultUserAgentString == null)
		{
			if (userAgents().size() > 0)
			{
				if (defaultUserAgentName == null)
				{
						defaultUserAgentString	= (String) userAgents().values().toArray()[0];
				}
				else
					userAgents.get(defaultUserAgentName).userAgentString();
			}
			this.defaultUserAgentString	= FIREFOX_3_6_4_AGENT_STRING;
		}
	}

	private void findAndDeclareNestedMetaMetadata()
	{
		ArrayList<MetaMetadata> nestedDeclarations = new ArrayList<MetaMetadata>();
		for (MetaMetadata metaMetadata : repositoryByTagName)
		{
			nestedDeclarations.addAll(generateNestedDeclarations(metaMetadata));
		}
		
		for(MetaMetadata metaMetadata : nestedDeclarations)
		{
			//if(!repositoryByTagName.containsKey(metaMetadata.getTag()))
				repositoryByTagName.put(metaMetadata.resolveTag(), metaMetadata);
		}
	}

	/**
	 * Recursively Copying MetadataFields from srcMetaMetadata to destMetaMetadata.
	 * 
	 * @param destMetaMetadata
	 * @param srcMetaMetadata
	 */
	protected void recursivePopulate(MetaMetadata destMetaMetadata)
	{
		// recursivePopulate(destMetaMetadata, destMetaMetadata.getExtendsClass());
	}

	public MetaMetadata getMM(Class<? extends Metadata> thatClass)
	{
		String tag = metadataTScope.getTag(thatClass);

		return (tag == null) ? null : repositoryByTagName.get(tag);
	}

	/**
	 * Get MetaMetadata. First, try matching by url_base. If this fails, including if the attribute is
	 * null, then try by url_prefix. If this fails, including if the attribute is null, then try by
	 * url_pattern (regular expression).
	 * <p/>
	 * If that lookup fails, then lookup by tag name, to acquire the default.
	 * 
	 * @param purl
	 * @param tagName
	 * @return
	 */
	public MetaMetadata getDocumentMM(ParsedURL purl, String tagName)
	{
		MetaMetadata result = null;
		if (purl != null)
		{
			if (!purl.isFile())
			{
				result = documentRepositoryByURL.get(purl.noAnchorNoQueryPageString());

				if (result == null)
				{
					String protocolStrippedURL = purl.toString().split("://")[1];
					String matchingPhrase = urlprefixCollection.getMatchingPhrase(protocolStrippedURL, '/');
					// FIXME -- andruid needs abhinav to explain this code better and make more clear!!!
					if (matchingPhrase != null)
					{
						String key = purl.url().getProtocol() + "://" + matchingPhrase;

						result = documentRepositoryByURL.get(key);
					}
				}

				if (result == null)
				{
					String domain = purl.domain();
					if (domain != null)
					{
						ArrayList<RepositoryPatternEntry> entries = documentRepositoryByPattern.get(domain);
						if (entries != null)
						{
							for (RepositoryPatternEntry entry : entries)
							{
								Matcher matcher = entry.getPattern().matcher(purl.toString());
								if (matcher.find())
								{
									result = entry.getMetaMetadata();
								}
							}
						}
					}
				}
			}
			// Lastly, check for MMD by suffix
			if (result == null)
			{
				String suffix = purl.suffix();

				if (suffix != null)
					result = getMMBySuffix(suffix);
			}
		}

		return (result != null) ? result : getByTagName(tagName);
	}

	// TODO implement get by domain too
	/**
	 * Find the best matching MetaMetadata for the ParsedURL. Otherwise, return the default Document
	 * metadata.
	 * 
	 * @param purl
	 * @return appropriate MetaMetadata.
	 */
	public MetaMetadata getDocumentMM(ParsedURL purl)
	{
		return getDocumentMM(purl, DOCUMENT_TAG);
	}

	public MetaMetadata getMMBySuffix(String suffix)
	{
		return repositoryBySuffix.get(suffix);
	}

	public MetaMetadata getMMByMime(String mimeType)
	{
		return repositoryByMime.get(mimeType);
	}

	public MetaMetadata getDocumentMM(Document metadata)
	{
		return getDocumentMM(metadata.getLocation(), metadataTScope.getTag(metadata.getClass()));
	}

	public MetaMetadata getImageMM(ParsedURL purl)
	{
		return getMediaMM(purl, IMAGE_TAG);
	}

	public MetaMetadata getMediaMM(ParsedURL purl, String tagName)
	{
		MetaMetadata result = null;
		if (purl != null && !purl.isFile())
		{
			result = mediaRepositoryByURL.get(purl.noAnchorNoQueryPageString());

			if (result == null)
			{
				String protocolStrippedURL = purl.toString().split("://")[1];

				String key = purl.url().getProtocol() + "://"
						+ urlprefixCollection.getMatchingPhrase(protocolStrippedURL, '/');

				result = mediaRepositoryByURL.get(key);

				if (result == null)
				{
					String domain = purl.domain();
					if (domain != null)
					{
						ArrayList<RepositoryPatternEntry> entries = mediaRepositoryByPattern.get(domain);
						if (entries != null)
						{
							for (RepositoryPatternEntry entry : entries)
							{
								Matcher matcher = entry.getPattern().matcher(purl.toString());
								if (matcher.find())
								{
									result = entry.getMetaMetadata();
								}
							}
						}
					}
				}
			}
		}
		return (result != null) ? result : getByTagName(tagName);
	}

	/**
	 * Look-up MetaMetadata for this purl. If there is no special MetaMetadata, use Image. Construct
	 * Metadata of the correct subtype, base on the MetaMetadata.
	 * 
	 * @param purl
	 * @return A Metadata object, either of type Image, or a subclass. Never null!
	 */
	public Media constructImage(ParsedURL purl)
	{
		MetaMetadata metaMetadata = getImageMM(purl);
		Media result = null;
		if (metaMetadata != null)
		{
			result = (Media) metaMetadata.constructMetadata(metadataTScope);
		}
		return result;
	}

	/**
	 * Look-up MetaMetadata for this purl. If there is no special MetaMetadata, use Document.
	 * Construct Metadata of the correct subtype, base on the MetaMetadata. Set its location field to
	 * purl.
	 * 
	 * @param purl
	 * @return
	 */
	public Document constructDocument(ParsedURL purl)
	{
		MetaMetadata metaMetadata = getDocumentMM(purl);
		Document result = (Document) metaMetadata.constructMetadata(metadataTScope);
		result.setLocation(purl);
		return result;
	}

	/**
	 * Initializes HashMaps for MetaMetadata selectors by URL or pattern. Uses the Media and Document
	 * base classes to ensure that maps are only filled with appropriate matching MetaMetadata.
	 */
	private void initializeLocationBasedMaps()
	{
		// 1st pass -- resolve nested and collection types as needed -- fill in all child metadata
		// fields
		/*
		 * for (MetaMetadata metaMetadata : repositoryByTagName) { metaMetadata.bindNonScalarChildren();
		 * }
		 */

		for (MetaMetadata metaMetadata : repositoryByTagName)
		{
			metaMetadata.inheritMetaMetadata(this);
			
			Class<? extends Metadata> metadataClass = metaMetadata.getMetadataClass(metadataTScope);
			if (metadataClass == null)
			{
				continue;
			}

			HashMap<String, MetaMetadata> repositoryByPURL;
			HashMap<String, ArrayList<RepositoryPatternEntry>> repositoryByPattern;

			if (Media.class.isAssignableFrom(metadataClass))
			{
				repositoryByPURL = mediaRepositoryByURL;
				repositoryByPattern = mediaRepositoryByPattern;
			}
			else if (Document.class.isAssignableFrom(metadataClass))
			{
				repositoryByPURL = documentRepositoryByURL;
				repositoryByPattern = documentRepositoryByPattern;
			}
			else
				continue;

			ParsedURL purl = metaMetadata.getUrlBase();
			if (purl != null)
				repositoryByPURL.put(purl.noAnchorNoQueryPageString(), metaMetadata);
			else
			{
				ParsedURL urlPrefix = metaMetadata.getUrlPrefix();
				if (urlPrefix != null)
				{
					urlprefixCollection.add(urlPrefix);
					repositoryByPURL.put(urlPrefix.toString(), metaMetadata);
				}
				else
				{
					String domain = metaMetadata.getDomain();
					Pattern urlPattern = metaMetadata.getUrlRegex();
					if (domain != null && urlPattern != null)
					{
						ArrayList<RepositoryPatternEntry> bucket = repositoryByPattern.get(domain);
						if (bucket == null)
						{
							bucket = new ArrayList<RepositoryPatternEntry>(2);
							repositoryByPattern.put(domain, bucket);
						}
						bucket.add(new RepositoryPatternEntry(urlPattern, metaMetadata));
					}
				}
			}
		}
	}

	/**
	 * Recursively looks for nested declarations of meta-metadata by iterating over the fields of existing meta-metadata objects. 
	 * Defines new MetaMetadata objects and adds them to the repository.
	 *  
	 * @param metaMetadata
	 * @return a collection of new meta-metadata objects to add to repository
	 */
	private ArrayList<MetaMetadata> generateNestedDeclarations(MetaMetadata metaMetadata)
	{
		ArrayList<MetaMetadata> result = new ArrayList<MetaMetadata>();
		for(MetaMetadataField metaMetadataField : metaMetadata)
		{
			if (metaMetadataField.isNewClass())
			{
				String mmName 								= metaMetadataField.getTagForTranslationScope();
				MetaMetadata newMetaMetadata 	= new MetaMetadata(metaMetadataField, mmName);
				//newMetaMetadata.setName(mmName);
				//newMetaMetadata.setChildMetaMetadata(metaMetadataField.childMetaMetadata);
				//repositoryByTagName.put(className, newMetaMetadata);
				result.add(newMetaMetadata);
				
				// recurse to find deeper nested declarations
				result.addAll(generateNestedDeclarations(newMetaMetadata));
			}
		}
		
		return result;
	}

	/**
	 * This initalizes the map based on mime type and suffix.
	 */
	private void initializeSuffixAndMimeBasedMaps()
	{
		for (MetaMetadata metaMetadata : repositoryByTagName)
		{
			metaMetadata.inheritMetaMetadata(this);
			
			if (!metaMetadata.getClassAndBindDescriptors(metadataTScope))
				continue;
			
//			Class<? extends Metadata> metadataClass = metaMetadata.getMetadataClass(metadataTScope);
//			if (metadataClass == null)
//			{
////				error(metaMetadata + "\tCan't resolve in TranslationScope " + metadataTScope);
//				continue;
//			}
//			//
//			metaMetadata.bindClassDescriptor(metadataClass, metadataTScope);
			
			ArrayList<String> suffixes = metaMetadata.getSuffixes();
			if (suffixes != null)
			{
				for (String suffix : suffixes)
				{
					// FIXME-- Ask whether the suffix and mime should be inherited or not
					if (!repositoryBySuffix.containsKey(suffix))
						repositoryBySuffix.put(suffix, metaMetadata);
				}
			}

			ArrayList<String> mimeTypes = metaMetadata.getMimeTypes();
			if (mimeTypes != null)
			{
				for (String mimeType : mimeTypes)
				{
					// FIXME -- Ask whether the suffix and mime should be inherited or not
					if (!repositoryByMime.containsKey(mimeType))
						repositoryByMime.put(mimeType, metaMetadata);
				}
			}

		}
	}

	public MetaMetadata getByTagName(String tagName)
	{
		if (tagName == null)
			return null;
		return repositoryByTagName.get(tagName);
	}

	public MetaMetadata getByClass(Class<? extends Metadata> metadataClass)
	{
		if (metadataClass == null)
			return null;
		return repositoryByTagName.get(metadataTScope.getTag(metadataClass));
	}

	public Collection<MetaMetadata> values()
	{
		return (repositoryByTagName == null) ? null : repositoryByTagName.values();
	}

	public String packageName()
	{
		return packageName;
	}

	public static String documentTag()
	{
		return DOCUMENT_TAG;
	}

	public MetaMetadata lookupByMime(String mimeType)
	{
		return null;
	}

	public MetaMetadata lookupBySuffix(String suffix)
	{
		return null;
	}

	public TranslationScope metadataTranslationScope()
	{
		return metadataTScope;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName()
	{
		return packageName;
	}

	/**
	 * @param packageName
	 *          the packageName to set
	 */
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public NamedStyle lookupStyle(String styleName)
	{
		return namedStyles.get(styleName);
	}

	public NamedStyle getDefaultStyle()
	{
		return namedStyles.get(DEFAULT_STYLE_NAME);
	}

	public HashMap<String, UserAgent> userAgents()
	{
		if (userAgents == null)
			userAgents = new HashMap<String, UserAgent>();

		return userAgents;

	}

	public String getUserAgentString(String name)
	{
		return userAgents().get(name).userAgentString();
	}

	public String getDefaultUserAgentString()
	{
		if (defaultUserAgentString == null)
		{
			for (UserAgent userAgent : userAgents().values())
			{
				if (userAgent.isDefaultAgent())
				{
					defaultUserAgentString = userAgent.userAgentString();
					break;
				}
			}
		}

		return defaultUserAgentString;
	}

	public String getSearchURL(String searchEngine)
	{
		if (searchEngines != null)
		{
			return searchEngines.get(searchEngine).getUrlPrefix();
		}
		return null;
	}

	public String getSearchURLSufix(String searchEngine)
	{
		String returnVal = "";
		if (searchEngines != null)
		{
			return searchEngines.get(searchEngine).getUrlSuffix();
		}
		return returnVal;
	}

	public String getNumResultString(String searchEngine)
	{
		String returnVal = "";
		if (searchEngine != null)
			return searchEngines.get(searchEngine).getNumResultString();
		return returnVal;
	}

	public String getStartString(String searchEngine)
	{
		String returnVal = "";
		if (searchEngine != null)
			return searchEngines.get(searchEngine).getStartString();
		return returnVal;
	}

	public static TranslationScope scalarMetadataTranslations()
	{
		return TranslationScope.get("scalar_metadata", DebugMetadata.class, MetadataString.class,
				MetadataStringBuilder.class, MetadataParsedURL.class, MetadataInteger.class);
	}

	void bindChildren(MetaMetadataField childField, String tag)
	{
		MetaMetadata newChildMM = getByTagName(tag);
		childField.bindChildren(newChildMM);
	}

}
