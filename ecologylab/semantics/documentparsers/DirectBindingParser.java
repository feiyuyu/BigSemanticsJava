/**
 * 
 */
package ecologylab.semantics.documentparsers;

import ecologylab.net.ParsedURL;
import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.actions.SemanticActionHandler;
import ecologylab.semantics.actions.SemanticActionsKeyWords;
import ecologylab.semantics.connectors.InfoCollector;
import ecologylab.semantics.metadata.Metadata;
import ecologylab.semantics.metadata.MetadataClassDescriptor;
import ecologylab.semantics.metadata.builtins.Document;
import ecologylab.semantics.metametadata.MetaMetadata;
import ecologylab.semantics.metametadata.MetaMetadataCompositeField;
import ecologylab.semantics.metametadata.MetaMetadataField;
import ecologylab.serialization.DeserializationHookStrategy;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.XMLTools;

/**
 * This class is used for directly biniding the XML document with metadata classes to build metadata
 * objects.
 * 
 * @author amathur
 * 
 */
public class DirectBindingParser
		extends ParserBase
{

	public DirectBindingParser(InfoCollector infoCollector)
	{
		super(infoCollector);
	}

	@Override
	public Document populateMetadata(SemanticActionHandler handler)
	{
		Document populatedMetadata	= directBindingPopulateMetadata();
		container.setMetadata(populatedMetadata);
		
		return populatedMetadata;
	}

	@Override
	protected org.w3c.dom.Document createDom()
	{
		return XMLTools.buildDOM(getTruePURL());
	}

}
