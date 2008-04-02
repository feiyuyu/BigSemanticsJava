/**
 * 
 */
package ecologylab.semantics.metadata;

import java.lang.reflect.Field;

import ecologylab.generic.HashMapArrayList;
//import ecologylab.model.MetadataEndEditListener;
import ecologylab.model.MetadataField;
import ecologylab.semantics.gui.MetadataValueChangedListener;
import ecologylab.xml.FieldAccessor;
import ecologylab.xml.Optimizations;
import ecologylab.xml.types.scalar.ScalarType;

/**
 * @author bharat
 *
 */
public class MetadataFieldAccessor<M extends Metadata, T> extends FieldAccessor
{

	public static final String NULL = "null";
	private M metadata;
	
	MetadataValueChangedListener	metadataValueChangedListener;
	
//	T	value = null;
	
	public MetadataFieldAccessor(M metadata, Field field, ScalarType<?> scalarType, String tagName)
	{
		super(field, scalarType, tagName);
		this.setMetadata(metadata);
	}
	
	public void editValue(Metadata context, String newValue)
	{
		if (metadataValueChangedListener != null)
			metadataValueChangedListener.fieldValueChanged(context);
		
		this.hwSet(context, newValue);
	}
	
	public void endEditHandlerDispatch(MetadataValueChangedListener listener, String iconID)
  	{
  		endEditHandler(listener, iconID);
  	}
	
	protected void endEditHandler(MetadataValueChangedListener listener, String iconID)
  	{
		listener.endEditHandler(iconID, this);
  	}
	
//	public HashMapArrayList<String, FieldAccessor> getFieldAccessorsForThis(Class<? extends FieldAccessor> fieldAccessorClass)
//	{
//		return Optimizations.getFieldAccessors(metadata, this);
//		
//		
//		HashMapArrayList<String, FieldAccessor> result	= fieldAccessors;
//		if (result == null)
//		{
//			result				= createFieldAccessors(fieldAccessorClass);
//			this.fieldAccessors	= result;
//		}
//		return result;
//	}

	public String getValueString()
	{
		return super.getValueString(metadata);
	}
	
	/**
	 * @return the metadata
	 */
	public M getMetadata()
	{
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(M metadata)
	{
		this.metadata = metadata;
	}
}
