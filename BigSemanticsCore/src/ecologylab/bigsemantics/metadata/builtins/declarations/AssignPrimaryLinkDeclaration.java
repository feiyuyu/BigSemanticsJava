package ecologylab.bigsemantics.metadata.builtins.declarations;

/**
 * Automatically generated by MetaMetadataJavaTranslator
 *
 * DO NOT modify this code manually: All your changes may get lost!
 *
 * Copyright (2014) Interface Ecology Lab.
 */

import ecologylab.bigsemantics.metadata.builtins.CreativeAct;
import ecologylab.bigsemantics.metadata.builtins.MetadataBuiltinsTypesScope;
import ecologylab.bigsemantics.metadata.scalar.MetadataParsedURL;
import ecologylab.bigsemantics.metametadata.MetaMetadataCompositeField;
import ecologylab.bigsemantics.namesandnums.SemanticsNames;
import ecologylab.net.ParsedURL;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import java.util.List;
import java.util.Map;

@simpl_inherit
public class AssignPrimaryLinkDeclaration extends CreativeAct
{
	@simpl_scalar
	private MetadataParsedURL location;

	public AssignPrimaryLinkDeclaration()
	{ super(); }

	public AssignPrimaryLinkDeclaration(MetaMetadataCompositeField mmd) {
		super(mmd);
	}


	public MetadataParsedURL	location()
	{
		MetadataParsedURL	result = this.location;
		if (result == null)
		{
			result = new MetadataParsedURL();
			this.location = result;
		}
		return result;
	}

	public ParsedURL getLocation()
	{
		return this.location == null ? null : location().getValue();
	}

	public MetadataParsedURL getLocationMetadata()
	{
		return location;
	}

	public void setLocation(ParsedURL location)
	{
		if (location != null)
			this.location().setValue(location);
	}

	public void setLocationMetadata(MetadataParsedURL location)
	{
		this.location = location;
	}
}
