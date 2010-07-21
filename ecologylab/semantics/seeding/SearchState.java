package ecologylab.semantics.seeding;

import java.io.File;

import ecologylab.collections.Scope;
import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.connectors.InfoCollector;
import ecologylab.semantics.connectors.SearchEngineNames;
import ecologylab.semantics.connectors.SeedPeer;
import ecologylab.semantics.documentparsers.MetaMetadataSearchParser;
import ecologylab.semantics.metametadata.MetaMetadataRepository;
import ecologylab.semantics.model.text.InterestModel;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.library.dc.Dc;

/**
 * {@link Seed Seed} element that directs combinFormation to perform a search.
 * 
 * Starts by providing a basis for specification of search seeds.
 * Then, keeps state during processing of the search.
 */
@simpl_inherit
public class SearchState extends Seed
implements SemanticsPrefs, SearchEngineNames
{
	private static final float	NO_BIAS												= 1.0f;
	
	private static final short	DEFAULT_SEARCH_INTEREST_LEVEL	= (short) 2;
	
	public static final int			NUM_IMAGE_RESULTS							= 40;
   
   /**
    * Search engine to use. Currently supported are google, flickr, yahoo, yahoo_image, yahoo_news, yahoo_buzz, delicious.
    */
   @simpl_scalar protected String			engine;

   
   protected String							queryNoPluses;
   
   /**
    * For del.icio.us only. Allows querying the delicious tags for a particular user.
    */
   @simpl_scalar protected String			creator;
   
   /**
    * For Yahoo Buzz. Queries to buzz can either be "leaders" (the default) or "movers".
    * The latter tend to be more current and interesting.
    */
   @simpl_scalar protected boolean			isMovers;
   
   /**
    * For any query that allows searching on DC fields.
    */
   @simpl_composite protected Dc 				dc; 
   
   private int				numResults		= NUM_SEARCH_RESULTS.value();
   
   /**
    * The result index for the first result in the current search (to be) issued for this Search seed.
    * Initially, this is 0.
    * Each time a subsequent search is run, it will be incremented by the number of searchResults that
    * were requested the previous time.
    */
   @simpl_scalar
   private int				currentFirstResultIndex;

   private int				searchType;
   
   /** 
    * how many results we have actually seen
    */
   private int numResultsFrom = 0;
   
   private String			site;
   
   /**
    * Initial level of interest in this seed.
    */
   private short			interestLevel	= DEFAULT_SEARCH_INTEREST_LEVEL;

   /**
    * Among the searches that were specified (as part of seeding), which one is this?
    */
   private int				searchNum;
   
   private boolean		generatingTermDictionary;
   
   public SearchState()
   {
	   super();
   }
   public SearchState(String query, String engine)
   {
  	 this(query, engine, DEFAULT_SEARCH_INTEREST_LEVEL);
   }
   public SearchState(String query, String engine, short interestLevel)
   {
  	 this(query, engine, interestLevel, YAHOO_IMAGE.equals(engine) ? NUM_IMAGE_RESULTS : NUM_SEARCH_RESULTS.value());
   }
   public SearchState(String query, String engine, short interestLevel, int numResults)
   {
  	 super();
	   setQuery(query);
	   this.engine 						= engine;
	   this.interestLevel			= interestLevel;
	   this.numResults				= numResults;
   }
   public SearchState(String query, String engine, short interestLevel, int numResults, boolean generatingTermDictionary)
   {
  	 this(query, engine, interestLevel, numResults);
	   this.noAggregator				= generatingTermDictionary;
	   this.generatingTermDictionary	= generatingTermDictionary;
   }
   /**
    * this is when we do a buzz, etc search, and seeds are created because they are a result.
    * 
    * @param ancestor the original seed (i.e. the seed that spawned this one)
    * @param query
    * @param engine
    */
   public SearchState(SeedPeer ancestor, InfoCollector infoProcessor, String query, String engine)
   {
	   this(query, engine);
	   //FIXME -- process ancestor
	   initialize(infoProcessor);
   }

   public static SearchState getGoogleSearchState(String query, int numResults)
   {
  	 return new SearchState(query, GOOGLE);
   }

   /**
    * Get the next results from the previous search seed.
    * 
    * @param previousSearch
    */
//   public SearchState(SearchState previousSearch)
//   {
//	   this(null, previousSearch.query, previousSearch.numResults, previousSearch.initialIntensity, previousSearch.bias);
//	   this.firstResult		= previousSearch.firstResult + numResults;
//	   this.engine			= previousSearch.engine;
//	   this.resultDistributer	= previousSearch.resultDistributer;
//   }
   

	/**
	 * Check the validity of this seed.
	 */
   public boolean isActive()
   {
	   if (engine == null)
	   {
		   error("Can't process search seed with null engine.");
		   return false;
	   }
	   if (((query == null) || (query.length()<=0) ) && !DELICIOUS.equals(engine)) // delicious handles empty queries nicely
	   {
		   try
		   {
			   error("Can't process search seed with null query: " + this.serialize());
		   } catch (SIMPLTranslationException e)
		   {
			   e.printStackTrace();
		   }
		   return false;
	   }
	   
	   return true;
   }

   public void eliminatePlusesFromQuery()
   {
  	 if (query != null && query.contains("+"))
  	 {
  		 query			= query.replace('+', ' ');
  	 }
   }
   /**
    * Bring this seed into the agent or directly into the composition.
    * 
    * @param objectRegistry		Context passed between services calls.
    * @param infoCollector TODO
    */
   @Override
   public void performInternalSeedingSteps(InfoCollector infoCollector)
   {
  	 //  	 InterestModel.expressInterest(query, interestLevel);
  	 //infoCollector.instantiateDocumentType(SEARCH_DOCUMENT_TYPE_REGISTRY, engine, this);		
  	 SemanticActionHandler actionHandler= infoCollector.createSemanticActionHandler();
  	 new MetaMetadataSearchParser(infoCollector,actionHandler, engine, this);
   }

	/**
	 * Do stuff in the DocumentType constructor to setup this search result Container.
	 * For Search, the extra stuff we do involves setting up the TermVector to match the query,
	 * and squirting some initial interest into these Terms.
	 * 
	 * @param container
	 */
//	public void bindToContainer(Container container)
//	{
//		super.bindToContainer(container);
//		//container.metadata.initializeFromSearch(query);
//		if(query != null)
//		{
//			String queryValue			= query.replace('+', ' ');
//			container.setQuery(queryValue);
//		}
////		container.setMetadataField("query", queryValue);
////		queryField.incrementParticipantInterest(initialIntensity);
////		container.incrementTermVectorInterest((short) initialIntensity);
//		
//		//container.setIntensity(this.initialIntensity);
//	}
	
	/**
	 * The String the dashboard needs to show.
	 * 
	 * @return	The search query.
	 */
	public String valueString()
	{
		return query;
	}
	/**
	 * The category the dashboard uses to show.
	 * 
	 * @return	The search category.
	 */
	public String categoryString()
	{
		return SEARCH;
	}
	
	/**
	 * The category the dashboard uses to show.
	 * 
	 * @return	The search engine category.
	 */
	public String detailedCategoryString()
	{
		return engine.toString();
	}
	
	/**
	 * @return Returns the initialIntensity.
	 */
	public short initialIntensity()
	{
		return interestLevel;
	}
	
	/**
	 * The requested number of results to retrieve.
	 * 
	 * @return Returns the numResults.
	 */
	public int numResults()
	{
		return numResults;
	}
	
	/**
	 * If multiple searches were specified within the {@link ecologylab.semantics.seeding.SeedSet SeedSet}, this field specifies
	 * the index of this one.
	 * 
	 * @return Returns the searchNum.
	 */
	public int searchNum()
	{
		return searchNum;
	}
	
	public int searchType()
	{
		return searchType;
	}
	
	protected boolean noAggregator()
	{
		return this.noAggregator || (seedSet() == null);
	}
	
	/**
	 * @return Returns the siteString.
	 */
	public String siteString()
	{
		return site;
	}
	/**
	 * @param query The query to set.
	 */
	public boolean setValue(String query)
	{
		this.query = query;
		return true;
	}
	
	/**
	 * @param engineTypeString
	 */
	public void setCategory(String engineTypeString)
	{
		this.engine = engineTypeString;
	}
	/**
	 * @return Returns the firstResult.
	 */
	public int currentFirstResultIndex()
	{
		return currentFirstResultIndex;
	}

	/**
	 * Called to specify that the next set of search results will be retrieved for this Search Seed.
	 */
	@Override
	public int nextResultSet()
	{
		 currentFirstResultIndex += numResults;
		 return currentFirstResultIndex;
	}
	
	/**
	 * @param queueInsteadOfImmediate The queueInsteadOfImmediate to set.
	 */
	public void setQueueInsteadOfImmediate(boolean queueInsteadOfImmediate)
	{
		this.queueInsteadOfImmediate = queueInsteadOfImmediate;
	}
	/**
	 * @return the creator
	 */
	public String creator()
	{
		return creator;
	}
	
	public boolean generatingTermDictionary()
	{
		return generatingTermDictionary;
	}
	
	
	/**
	 * @return true if specified that the user wants to see the most popular tagged 
	 * stuff in del.icio.us
	 */
	public boolean popular()
	{
		return false;
	}

	/**
	 * Set the seed to specify that  the user wants to see the most popular tagged 
	 * stuff in del.icio.us
	 * 
	 * @param popular the popular to set
	 */
	public void setPopular(boolean popular)
	{
		
	}
	
	public void setSearchNum(int searchNum)
	{
		this.searchNum	= searchNum;
	}

	/**
	 * Set query and queryNoPluses.
	 * 
	 * @param query
	 * @return	Query with any plus characters removed.
	 */
	public String setQuery(String query)
	{
		this.query = query;
		String result	= null;
		if (query != null)
		{
			result			= query.replace('+', ' ');
			queryNoPluses	= result;
		}
		return result;
	}
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	
	/**
	 * Pass back the array of choices, when an engine takes only a fixed set of them.
	 * 
	 * @return	null in the default implemenation, indicating vocabulary is not controlled.
	 */
	public String[] controlledVocabulary()
	{
		return null;
	}
	/**
	 * For Yahoo Buzz. Queries to buzz can either be "leaders" (the default) or "movers".
	 * The latter tend to be more current and interesting.
	 */
	public boolean isMovers()
	{
		return isMovers;
	}
	/**
	 * For Yahoo Buzz. Queries to buzz can either be "leaders" (the default) or "movers".
	 * The latter tend to be more current and interesting.
	 */
	public void setIsMovers(boolean isMovers)
	{
		this.isMovers = isMovers;
	}

	
	public boolean canChangeVisibility()
	{
		return true;
	}
	public boolean isDeletable()
	{
		return true;
	}
	public boolean isEditable()
	{
		return true;
	}
	public boolean isRejectable()
	{
		return false;
	}
	
	public void setDcQuery(Dc dcQuery)
	{
		this.dc = dcQuery;
	}
	
	public Dc getDcQuery()
	{
		return dc;
	}

	static final File	testFile	= new File("config/preferences/katrinaLocationAware.xml");
	
//	public static void main(String[] a)
//	{
//		try
//		{
//			PrefSet searchState		= (PrefSet) ElementState.translateFromXML(testFile, CFServicesTranslations.get());
//			searchState.translateToXML(System.out);
//		} catch (XMLTranslationException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
	public int numResultsFrom() 
	{
		// TODO Auto-generated method stub
		return numResultsFrom;
	}
	
	public void incrementNumResultsBy(int i)
	{
		numResultsFrom += i;
	}


  /**
   * Processing peformed for each Seed in a SeedSet in a loop before performSeeding() will be called in a separate loop.
   * 
   * @return	true if this is not an image search, and we are processing it.
   */
	@Override
	public boolean initializeSeedingSteps(SeedSet seedSet, int searchNum)
	{
		InterestModel.expressInterest(query, interestLevel);

		setSearchNum(searchNum);
		setSeedSet(seedSet);

		return true;
	}
	
	String toString;
	
	public String toString()
	{
		String	result		= this.toString;
		if (result == null)
		{
			if ((query != null) && engine != null)
			{
				StringBuilder buffy	= new StringBuilder();
				buffy.append("SearchState[").append(engine).append(']').append(' ');
				buffy.append(query);
				result				= buffy.toString();
				this.toString	= result;
			}
			else
				result				= super.toString();
		}
		return result;
	}
	
	/**
	 * Called after a seed is parsed to prevent it being parsed again later during re-seeding.
	 * This override does nothing, because Search seeds should remain active.
	 * 
	 * @param inActive the inActive to set
	 */
	@Override
	public void setActive(boolean value)
	{
		
	}
	
	/**
	 * Form the url for the particular query, engine and resultIndex
	 */
	public ParsedURL formSearchUrlBasedOnEngine()
	{
		/*
		 * if (engine.equals(SearchEngineNames.GOOGLE)) { searchURL = ParsedURL .getAbsolute(
		 * ((searchSeed.searchType() == SearchEngineNames.REGULAR) ?
		 * SearchEngineNames.regularGoogleSearchURLString :
		 * 
		 * (searchSeed.searchType() == SearchEngineNames.IMAGE) ?
		 * SearchEngineNames.imageGoogleSearchURLString : (searchSeed.searchType() ==
		 * SearchEngineNames.SITE) ? SearchEngineUtilities
		 * .siteGoogleLimitSearchURLString(searchSeed.siteString()) :
		 * SearchEngineNames.relatedGoogleSearchUrlString) + (searchSeed.valueString().replace(' ',
		 * '+')).replace("&quot;", "%22")
		 * 
		 * + (IGNORE_PDF.value() ? SearchEngineNames.GOOGLE_NO_PDF_ARG : "") + "&num=" +
		 * searchSeed.numResults() + "&start=" + firstResultIndex, "broken Google URL"); } else if
		 * (engine.equals(SearchEngineNames.FLICKR)) { searchURL =
		 * ParsedURL.getAbsolute("http://www.flickr.com/search/?q=" + searchSeed.getQuery()); } else
		 * if(engine.equals(SearchEngineNames.YAHOO)) { searchURL=ParsedURL.getAbsolute(
		 * "http://api.search.yahoo.com/WebSearchService/V1/webSearch?appid=yahoosearchwebrss&query="
		 * +searchSeed.getQuery()); } else if(engine.equals(SearchEngineNames.FLICK_AUTHOR)) { searchURL
		 * = ParsedURL.getAbsolute("http://www.flickr.com/photos/"+searchSeed.getQuery()+"/"); }
		 */
		
		ParsedURL resultURL; 
		MetaMetadataRepository mmdRepo = infoCollector.metaMetaDataRepository();
		String urlPrefix = mmdRepo.getSearchURL(engine);
		String query = getQuery();
		// we replace all white spaces by +
		query = query.replace(' ', '+');
		String urlSuffix = mmdRepo.getSearchURLSufix(engine);
		String numResultString = mmdRepo.getNumResultString(engine);
		String startString = mmdRepo.getStartString(engine);
		
		if (!startString.equals("") && !numResultString.equals(""))
			resultURL = ParsedURL.getAbsolute(urlPrefix + query + numResultString + numResults() + startString +  currentFirstResultIndex);
		else if (!startString.equals(""))
			resultURL = ParsedURL.getAbsolute(urlPrefix + query + startString + currentFirstResultIndex + urlSuffix);
		else
			resultURL = ParsedURL.getAbsolute(mmdRepo.getSearchURL(engine) + query + urlSuffix);
		return resultURL;
		
	}

  protected boolean useDistributor()
  {
  	return true;
  }
}