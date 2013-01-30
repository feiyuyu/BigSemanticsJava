package ecologylab.semantics.metadata.builtins.declarations;

/**
 * Automatically generated by MetaMetadataJavaTranslator
 *
 * DO NOT modify this code manually: All your changes may get lost!
 *
 * Copyright (2013) Interface Ecology Lab.
 */

import ecologylab.semantics.metadata.builtins.MetadataBuiltinsTypesScope;
import ecologylab.semantics.metametadata.MetaMetadataCompositeField;
import ecologylab.semantics.namesandnums.SemanticsNames;
import ecologylab.serialization.SimplTypesScope;
import java.util.List;
import java.util.Map;

public class MetadataBuiltinDeclarationsTranslationScope
{

	protected static final Class TRANSLATIONS[] =
	{
		ecologylab.semantics.metadata.Metadata.class,

		ecologylab.semantics.metadata.builtins.Annotation.class,

		ecologylab.semantics.metadata.builtins.ClippableDocument.class,

		ecologylab.semantics.metadata.builtins.Clipping.class,

		ecologylab.semantics.metadata.builtins.CompoundDocument.class,

		ecologylab.semantics.metadata.builtins.DebugMetadata.class,

		ecologylab.semantics.metadata.builtins.Document.class,

		ecologylab.semantics.metadata.builtins.DocumentMetadataWrap.class,

		ecologylab.semantics.metadata.builtins.Image.class,

		ecologylab.semantics.metadata.builtins.ImageClipping.class,

		ecologylab.semantics.metadata.builtins.MediaClipping.class,

		ecologylab.semantics.metadata.builtins.TextClipping.class,

	};

	public static SimplTypesScope get()
	{
		return SimplTypesScope.get("repository_builtin_declarations_scope", TRANSLATIONS);
	}

}