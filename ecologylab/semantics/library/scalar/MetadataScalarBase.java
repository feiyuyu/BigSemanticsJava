package ecologylab.semantics.library.scalar;

import ecologylab.generic.FeatureVector;
import ecologylab.semantics.metadata.MetadataBase;
import ecologylab.semantics.model.text.Term;

public class MetadataScalarBase extends MetadataBase
{

	protected FeatureVector<Term> termVector;

	public MetadataScalarBase()
	{
		super();
	}

}