/**
 * 
 */
package ecologylab.bigsemantics.actions;

import java.lang.reflect.Method;

import ecologylab.bigsemantics.actions.exceptions.SemanticActionExecutionException;
import ecologylab.generic.ReflectionTools;
import ecologylab.serialization.XMLTools;
import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * @author amathur
 */
@simpl_inherit
@simpl_tag(SemanticActionStandardMethods.SET_FIELD_ACTION)
public class SetFieldSemanticAction
		extends SemanticAction implements SemanticActionStandardMethods
{

	public static final String	VALUE	= "value";
	
	@simpl_scalar
	@simpl_tag(VALUE)
	@simpl_hints(Hint.XML_ATTRIBUTE)
	private String valueName;

	@Override
	public String getActionName()
	{
		return SET_FIELD_ACTION;
	}

	@Override
	public void handleError()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object perform(Object obj)
	{
		String setterName = "set" + XMLTools.javaNameFromElementName(getReturnObjectName(), true);
		Object value = null;
		if (valueName != null)
			value	= semanticActionHandler.getSemanticActionVariableMap().get(valueName);
		if (value == null)
		{
			return null;
//			String errorMessage = valueName == null ? 
//				"Can't set_field name=\"" + getReturnObjectName() + "\" in " + obj + " because value=\"null\"" : 
//				"Can't set_field name=\"" + getReturnObjectName() + " in " + obj + "\" because there's no value bound to " + valueName;
//			throw new SemanticActionExecutionException(this, errorMessage);
		}
		
		Class<? extends Object> valueClass = value.getClass();
		Method method = ReflectionTools.getMethod(obj.getClass(), setterName, new Class[] { valueClass });
		if (method != null)
			try
			{
				method.invoke(obj, value);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				error(String.format("set_field failed: object=%s, setter=%s, value=%s", obj, setterName,
						value));
			}
			else
			{
				throw new SemanticActionExecutionException(this, "set_field name=\"" + getReturnObjectName() + "\"\tCan't find set method in " + obj + "  for " + valueName + " of type " + valueClass + "");
			}
		return null;
	}

}
