package ecologylab.bigsemantics.metadata.builtins.declarations;

/**
 * Automatically generated by MetaMetadataJavaTranslator
 *
 * DO NOT modify this code manually: All your changes may get lost!
 *
 * Copyright (2014) Interface Ecology Lab.
 */

import ecologylab.bigsemantics.metadata.builtins.Audio;
import ecologylab.bigsemantics.metadata.builtins.Clipping;
import ecologylab.bigsemantics.metadata.builtins.CompoundDocument;
import ecologylab.bigsemantics.metadata.builtins.Document;
import ecologylab.bigsemantics.metadata.builtins.Image;
import ecologylab.bigsemantics.metadata.builtins.MetadataBuiltinsTypesScope;
import ecologylab.bigsemantics.metadata.builtins.Video;
import ecologylab.bigsemantics.metadata.mm_name;
import ecologylab.bigsemantics.metadata.scalar.MetadataString;
import ecologylab.bigsemantics.metametadata.MetaMetadataCompositeField;
import ecologylab.bigsemantics.namesandnums.SemanticsNames;
import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_collection;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_scope;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@simpl_inherit
public class CompoundDocumentDeclaration extends Document
{
	/** 
	 *For debugging. Type of the structure recognized by information extraction.
	 */ 
	@simpl_scalar
	private MetadataString pageStructure;

	/** 
	 *The search query
	 */ 
	@simpl_scalar
	@simpl_hints({Hint.XML_LEAF})
	private MetadataString query;

	/** 
	 *Clippings that this document contains.
	 */ 
	@simpl_collection
	@simpl_scope("repository_clippings")
	@mm_name("clippings")
	private List<Clipping> clippings;

	@simpl_composite
	@mm_name("root_document")
	private CompoundDocument rootDocument;

	@simpl_collection("image")
	@mm_name("main_images")
	private List<Image> mainImages;

	@simpl_collection("video")
	@mm_name("main_videos")
	private List<Video> mainVideos;

	@simpl_collection("audio")
	@mm_name("main_audio")
	private List<Audio> mainAudio;

	public CompoundDocumentDeclaration()
	{ super(); }

	public CompoundDocumentDeclaration(MetaMetadataCompositeField mmd) {
		super(mmd);
	}


	public MetadataString	pageStructure()
	{
		MetadataString	result = this.pageStructure;
		if (result == null)
		{
			result = new MetadataString();
			this.pageStructure = result;
		}
		return result;
	}

	public String getPageStructure()
	{
		return this.pageStructure == null ? null : pageStructure().getValue();
	}

	public MetadataString getPageStructureMetadata()
	{
		return pageStructure;
	}

	public void setPageStructure(String pageStructure)
	{
		if (pageStructure != null)
			this.pageStructure().setValue(pageStructure);
	}

	public void setPageStructureMetadata(MetadataString pageStructure)
	{
		this.pageStructure = pageStructure;
	}

	public MetadataString	query()
	{
		MetadataString	result = this.query;
		if (result == null)
		{
			result = new MetadataString();
			this.query = result;
		}
		return result;
	}

	public String getQuery()
	{
		return this.query == null ? null : query().getValue();
	}

	public MetadataString getQueryMetadata()
	{
		return query;
	}

	public void setQuery(String query)
	{
		if (query != null)
			this.query().setValue(query);
	}

	public void setQueryMetadata(MetadataString query)
	{
		this.query = query;
	}

	public List<Clipping> getClippings()
	{
		return clippings;
	}

  // lazy evaluation:
  public List<Clipping> clippings()
  {
    if (clippings == null)
      clippings = new ArrayList<Clipping>();
    return clippings;
  }

  // addTo:
  public void addToClippings(Clipping element)
  {
    clippings().add(element);
  }

  // size:
  public int clippingsSize()
  {
    return clippings == null ? 0 : clippings.size();
  }

	public void setClippings(List<Clipping> clippings)
	{
		this.clippings = clippings;
	}

	public CompoundDocument getRootDocument()
	{
		return rootDocument;
	}

	public void setRootDocument(CompoundDocument rootDocument)
	{
		this.rootDocument = rootDocument;
	}

	public List<Image> getMainImages()
	{
		return mainImages;
	}

  // lazy evaluation:
  public List<Image> mainImages()
  {
    if (mainImages == null)
      mainImages = new ArrayList<Image>();
    return mainImages;
  }

  // addTo:
  public void addToMainImages(Image element)
  {
    mainImages().add(element);
  }

  // size:
  public int mainImagesSize()
  {
    return mainImages == null ? 0 : mainImages.size();
  }

	public void setMainImages(List<Image> mainImages)
	{
		this.mainImages = mainImages;
	}

	public List<Video> getMainVideos()
	{
		return mainVideos;
	}

  // lazy evaluation:
  public List<Video> mainVideos()
  {
    if (mainVideos == null)
      mainVideos = new ArrayList<Video>();
    return mainVideos;
  }

  // addTo:
  public void addToMainVideos(Video element)
  {
    mainVideos().add(element);
  }

  // size:
  public int mainVideosSize()
  {
    return mainVideos == null ? 0 : mainVideos.size();
  }

	public void setMainVideos(List<Video> mainVideos)
	{
		this.mainVideos = mainVideos;
	}

	public List<Audio> getMainAudio()
	{
		return mainAudio;
	}

  // lazy evaluation:
  public List<Audio> mainAudio()
  {
    if (mainAudio == null)
      mainAudio = new ArrayList<Audio>();
    return mainAudio;
  }

  // addTo:
  public void addToMainAudio(Audio element)
  {
    mainAudio().add(element);
  }

  // size:
  public int mainAudioSize()
  {
    return mainAudio == null ? 0 : mainAudio.size();
  }

	public void setMainAudio(List<Audio> mainAudio)
	{
		this.mainAudio = mainAudio;
	}
}
