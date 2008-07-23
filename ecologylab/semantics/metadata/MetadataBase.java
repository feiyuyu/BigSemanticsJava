/**
 * 
 */
package ecologylab.semantics.metadata;

import java.util.Iterator;

import ecologylab.generic.HashMapArrayList;
import ecologylab.model.ParticipantInterest;
import ecologylab.model.text.TermVector;
import ecologylab.model.text.WordForms;
import ecologylab.xml.ElementState;
import ecologylab.xml.FieldAccessor;
import ecologylab.xml.Optimizations;

/**
 * Base class for Metadata fields that represent scalar values.
 * 
 * These, for example, lack mixins.
 * 
 * @author andruid
 *
 */
public class MetadataBase extends ElementState implements Iterable<FieldAccessor>
{
	
	protected TermVector 				compositeTermVector;
	
	/**
	 * Represents interest in this field as a whole
	 * (Example: author = Ben Shneiderman), in addition to interest
	 * propagated into the termVector, which represents interest in
	 * particular Terms. While the latter is the basis of the information
	 * retrieval model (IR), this is what enables the model to function
	 * in the context of the semantic web / digital libraries.
	 */  
	ParticipantInterest				participantInterest = new ParticipantInterest();

	HashMapArrayList<String, FieldAccessor> metadataFieldAccessors;


	/**
	 * 
	 */
	public MetadataBase()
	{
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the compositeTermVector
	 */
	public TermVector compositeTermVector()
	{
		return compositeTermVector;
	}


	/**
	 * @param compositeTermVector the compositeTermVector to set
	 */
	public void setCompositeTermVector(TermVector compositeTermVector)
	{
		this.compositeTermVector = compositeTermVector;
	}


	/**
	 * @return the participantInterest
	 */
	public ParticipantInterest getParticipantInterest()
	{
		return participantInterest;
	}


	/**
	 * Increments interest in this MetadataField. 
	 * @param delta	The change in interest.
	 */
	public void incrementParticipantInterest(short delta)
	{
		participantInterest.increment(delta);
	}
	
	public void recycle()
	{
		//termVector.clear();
		compositeTermVector 				= null;
		participantInterest					= null;
	}
	
	/**
	 * Append the term to the field "value" as well as the term vector.
	 * 
	 * @param wf The term to add.
	 */
	public void addTerm(WordForms wf)
	{
		compositeTermVector.add(wf);

		//value = null; //force value to be updated later

		//if (termVector.size() > 0)

		//this concat is way cheaper than rebuilding the termvector toString().
		//value			= value.toString() + ' ' + wf.string();

		//this.value = termVector.toString();
		//this.value = termVector.toString(termVector.size(), ' ');
		//else
		//	   this.value = "";

		compositeTermVector.combine(compositeTermVector, false);
	}

	/**
	 * Append terms to the field "value" as well as the term vector.
	 * 
	 * @param wfv The vector of terms to add.
	 */
	public void addTerm(TermVector wfv)
	{
		Iterator it = wfv.iterator();
		while (it.hasNext())
		{ 
			WordForms wf = (WordForms) it.next();
			compositeTermVector.add(wf);
		}
	}
	
	/**
	 * Modifies interest by delta. This function modifies the interest
	 * in the composite TermVector, the constituent individual TermVectors,
	 * and the interest in actual fields themselves (for the semantic web/DLs) 
	 * 
	 * @param delta		The amount to modify interest
	 */
	public void incrementInterest(short delta)
	{
		if(compositeTermVector != null && !compositeTermVector.isEmpty())
		{
			compositeTermVector.incrementParticipantInterest(delta);
			incrementParticipantInterest(delta);
		}
//		if(compositeTermVector != null && !compositeTermVector.isEmpty())
//		{
//			// first modify the composite TermVector
//			compositeTermVector.incrementParticipantInterest(delta);
//			
//			//TODO Sashikanth: iterate on child fields
//			Iterator it = iterator();
//			while (it.hasNext())
//			{
//				
//				// TermVectors
//				Metadata mData = (Metadata) it.next();
//				mData.termVector().incrementParticipantInterest(delta);
//				
//				// Lastly the actual fields
//				mData.incrementParticipantInterest(delta);
//			}
//		}
		
		
	}
	

	/**
	 * Determine if the Metadata has any entries.
	 * @return	True if there are Metadata entries.
	 */
	public boolean hasCompositeTermVector()
	{
		return (compositeTermVector != null);
	}
	
	/**
	 * The weight of the composite TermVector.
	 * @return	The composite TermVector's weight.
	 * @see ecologylab.model.text.TermVector#getWeight()
	 */
	public float getWeight()
	{
		return compositeTermVector == null ? 1 : compositeTermVector.getWeight();
	}
	
	/**
	 * The lnWeight
	 * @return	The lnWeight() of the composite TermVector.
	 * @see ecologylab.model.text.TermVector#lnWeight()
	 */
	public float lnWeight()
	{
		return compositeTermVector == null ? 0 : compositeTermVector.lnWeight();
	}
	
	/**
	 * Efficiently retrieve appropriate MetadataFieldAccessor, using lazy evaluation.
	 * 
	 * @param fieldName
	 * @return
	 */
	public MetadataFieldAccessor getMetadataFieldAccessor(String fieldName)
	{
		return (MetadataFieldAccessor) metadataFieldAccessors().get(fieldName);
	}


	/**
	 * @param participantInterest the participantInterest to set
	 */
	public void setParticipantInterest(ParticipantInterest participantInterest)
	{
		this.participantInterest = participantInterest;
	}


	protected HashMapArrayList<String, FieldAccessor> metadataFieldAccessors()
	{
		HashMapArrayList<String, FieldAccessor> result	= this.metadataFieldAccessors;
		if (result == null)
		{
			result			= Optimizations.getFieldAccessors(this.getClass(), MetadataFieldAccessor.class);
			metadataFieldAccessors	= result;
		}
		return result;
	}


	public Iterator<FieldAccessor> iterator()
	{
		return metadataFieldAccessors().iterator();
	}

	//FIXEME:The method has to search even all the mixins for the key.
	public FieldAccessor get(String key)
	{
		HashMapArrayList<String, FieldAccessor> fieldAccessors = metadataFieldAccessors();
		return fieldAccessors.get(key);
	}
	

}
