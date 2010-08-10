package ecologylab.semantics.metametadata.example;

import ecologylab.semantics.actions.SemanticAction;
import ecologylab.semantics.metametadata.example.bingImage.bingImageSemanticAction;
import ecologylab.semantics.tools.MetadataCompiler;
import ecologylab.serialization.SIMPLTranslationException;

/**
 * Use the MetadataCompiler class to compile a meta-metadata repository. The generated class
 * definitions will go to the current directory, in the package specified in the repository file.
 * 
 * @author quyin
 */
public class MyMetadataCompiler
{

	public static void main(String[] args)
	{
		try
		{
			SemanticAction.register(SaveReportSemanticAction.class);
			SemanticAction.register(bingImageSemanticAction.class);
			
			// use the default repository location
			// needed to provide this!!
			MetadataCompiler compiler = new MetadataCompiler(args);
			compiler.compile("repo", ".");
		}
		catch (SIMPLTranslationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}