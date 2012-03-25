package ecologylab.semantics.metadata.builtins.declarations;

/**
 * Automatically generated by MetaMetadataJavaTranslator
 *
 * DO NOT modify this code manually: All your changes may get lost!
 *
 * Copyright (2012) Interface Ecology Lab.
 */

import ecologylab.semantics.metadata.builtins.ClippableDocument;
import ecologylab.semantics.metadata.builtins.Clipping;
import ecologylab.semantics.metadata.builtins.MetadataBuiltinsTypesScope;
import ecologylab.semantics.metadata.mm_name;
import ecologylab.semantics.metadata.scalar.MetadataString;
import ecologylab.semantics.metametadata.MetaMetadataCompositeField;
import ecologylab.semantics.namesandnums.SemanticsNames;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_scope;
import ecologylab.serialization.annotations.simpl_wrap;
import java.lang.String;
import java.util.List;
import java.util.Map;

@simpl_inherit
public abstract class MediaClippingDeclaration<ME extends ClippableDocument<ME>> extends Clipping
{
	/** 
	 *The caption of the image.
	 */ 
	@simpl_scalar
	private MetadataString caption;

	@simpl_composite
	@simpl_wrap
	@simpl_scope("repository_media")
	@mm_name("media")
	private ME media;

	public MediaClippingDeclaration()
	{ super(); }

	public MediaClippingDeclaration(MetaMetadataCompositeField mmd) {
		super(mmd);
	}


	public MetadataString	caption()
	{
		MetadataString	result = this.caption;
		if (result == null)
		{
			result = new MetadataString();
			this.caption = result;
		}
		return result;
	}

	public String getCaption()
	{
		return this.caption == null ? null : caption().getValue();
	}

	public MetadataString getCaptionMetadata()
	{
		return caption;
	}

	public void setCaption(String caption)
	{
		if (caption != null)
			this.caption().setValue(caption);
	}

	public void setCaptionMetadata(MetadataString caption)
	{
		this.caption = caption;
	}

	public ME getMedia()
	{
		return media;
	}

	public void setMedia(ME media)
	{
		this.media = media;
	}
}
