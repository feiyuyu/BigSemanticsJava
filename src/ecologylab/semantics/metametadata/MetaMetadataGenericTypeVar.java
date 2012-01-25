package ecologylab.semantics.metametadata;

import java.io.IOException;
import java.util.List;

import ecologylab.generic.Debug;
import ecologylab.generic.StringTools;
import ecologylab.semantics.metadata.MetadataClassDescriptor;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.simpl_collection;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_nowrap;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * the generic variable in meta-metadata.
 * 
 * @author quyin
 * 
 */
@simpl_tag("generic_type_var")
@simpl_inherit
public class MetaMetadataGenericTypeVar extends ElementState
{

	/**
	 * the name of the generic type variable. recommend capital for each letter.
	 */
	@simpl_scalar
	private String														name;

	/**
	 * the name of the bound (e.g. Media in &lt;M extends Media&gt;). cound be another generic type
	 * variable name that has been defined before.
	 */
	@simpl_scalar
	private String														bound;

	/**
	 * used only for parameterization of this generic type variable, with either a concrete
	 * meta-metadata name or an already-defined generic type variable name.
	 */
	@simpl_scalar
	private String														parameter;

	/**
	 * used for specifying generic types for composite fields.
	 */
	@simpl_scalar
	private String														genericType;
	
	/**
	 * a list of nested generic type variables. e.g. A, B in &lt;M extends Media&lt;A, B&gt;&gt;.
	 */
	@simpl_collection("generic_type_var")
	@simpl_nowrap
	private List<MetaMetadataGenericTypeVar>	genericTypeVars;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getBound()
	{
		return bound;
	}

	public void setBound(String bound)
	{
		this.bound = bound;
	}

	public String getParameter()
	{
		return parameter;
	}

	public void setParameter(String parameter)
	{
		this.parameter = parameter;
	}

	public String getGenericType()
	{
		return genericType;
	}

	public void setGenericType(String genericType)
	{
		this.genericType = genericType;
	}

	public List<MetaMetadataGenericTypeVar> getGenericTypeVars()
	{
		return genericTypeVars;
	}

	public void setGenericTypeVars(List<MetaMetadataGenericTypeVar> genericTypeVars)
	{
		this.genericTypeVars = genericTypeVars;
	}

	public static void appendGenericTypeVarDefinitions(Appendable appendable,
			List<MetaMetadataGenericTypeVar> mmdGenericTypeVars, MetaMetadataRepository repository,
			MmdCompilerService compilerService) throws IOException
	{
		if (mmdGenericTypeVars != null && mmdGenericTypeVars.size() > 0)
		{
			boolean first = true;
			for (MetaMetadataGenericTypeVar mmdGenericTypeVar : mmdGenericTypeVars)
			{
				String varName = mmdGenericTypeVar.getName();
				String boundName = mmdGenericTypeVar.getBound();
				String paramName = mmdGenericTypeVar.getParameter();
				if (varName != null && boundName != null && paramName == null)
				{
					if (StringTools.isUpperCase(varName))
					{
						Debug.warning(MetaMetadataGenericTypeVar.class,
								"We recommend capital letters for generic variable names!");
					}
					if (first)
					{
						appendable.append("<");
						first = false;
					}
					else
						appendable.append(", ");
					appendable
							.append(varName)
							.append(" extends ")
							.append(
									MetaMetadataGenericTypeVar.getMdClassNameFromMmdOrNoChange(boundName, repository,
											compilerService));
					MetaMetadataGenericTypeVar.appendGenericTypeVarDefinitions(appendable,
							mmdGenericTypeVar.getGenericTypeVars(), repository, compilerService);
				}
			}
			if (!first)
				appendable.append(">");
		}
	}

	public static void appendGenericTypeVarParameterizations(Appendable appendable,
			List<MetaMetadataGenericTypeVar> mmdGenericTypeVars, MetaMetadataRepository repository,
			MmdCompilerService compilerService) throws IOException
	{
		if (mmdGenericTypeVars != null && mmdGenericTypeVars.size() > 0)
		{
			boolean first = true;
			for (MetaMetadataGenericTypeVar mmdGenericTypeVar : mmdGenericTypeVars)
			{
				String varName = mmdGenericTypeVar.getName();
				String boundName = mmdGenericTypeVar.getBound();
				String paramName = mmdGenericTypeVar.getParameter();
				if (paramName != null && varName == null && boundName == null)
				{
					if (first)
					{
						appendable.append("<");
						first = false;
					}
					else
						appendable.append(",");
					appendable
							.append(getMdClassNameFromMmdOrNoChange(paramName, repository, compilerService));
					appendGenericTypeVarParameterizations(appendable, mmdGenericTypeVar.getGenericTypeVars(),
							repository, compilerService);
				}
			}
			if (!first)
				appendable.append(">");
		}
	}

	public static String getMdClassNameFromMmdOrNoChange(String mmdName,
			MetaMetadataRepository repository, MmdCompilerService compilerService)
	{
		MetaMetadata mmd = repository.getMMByName(mmdName);
		if (mmd == null)
		{
			return mmdName;
		}
		else
		{
			MetadataClassDescriptor metadataClassDescriptor = mmd.getMetadataClassDescriptor();
			if (compilerService != null)
				compilerService.addCurrentClassDependency(metadataClassDescriptor.getDescribedClassName());
			return metadataClassDescriptor.getDescribedClassSimpleName();
		}
	}

}
