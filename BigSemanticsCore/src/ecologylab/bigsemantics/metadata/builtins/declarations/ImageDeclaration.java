package ecologylab.bigsemantics.metadata.builtins.declarations;

/**
 * Automatically generated by MetaMetadataJavaTranslator
 *
 * DO NOT modify this code manually: All your changes may get lost!
 *
 * Copyright (2014) Interface Ecology Lab.
 */

import ecologylab.bigsemantics.metadata.builtins.ClippableDocument;
import ecologylab.bigsemantics.metadata.builtins.MetadataBuiltinsTypesScope;
import ecologylab.bigsemantics.metadata.scalar.MetadataDate;
import ecologylab.bigsemantics.metametadata.MetaMetadataCompositeField;
import ecologylab.bigsemantics.namesandnums.SemanticsNames;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** 
 *The Image Base Class
 */ 
@simpl_inherit
public class ImageDeclaration extends ClippableDocument
{
	@simpl_scalar
	private MetadataDate creationDate;

	public ImageDeclaration()
	{ super(); }

	public ImageDeclaration(MetaMetadataCompositeField mmd) {
		super(mmd);
	}


	public MetadataDate	creationDate()
	{
		MetadataDate	result = this.creationDate;
		if (result == null)
		{
			result = new MetadataDate();
			this.creationDate = result;
		}
		return result;
	}

	public Date getCreationDate()
	{
		return this.creationDate == null ? null : creationDate().getValue();
	}

	public MetadataDate getCreationDateMetadata()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		if (creationDate != null)
			this.creationDate().setValue(creationDate);
	}

	public void setCreationDateMetadata(MetadataDate creationDate)
	{
		this.creationDate = creationDate;
	}
}
