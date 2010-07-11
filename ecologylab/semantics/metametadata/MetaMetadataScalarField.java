package ecologylab.semantics.metametadata;

import java.io.IOException;

import ecologylab.semantics.tools.MetadataCompilerUtils;
import ecologylab.serialization.Hint;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.XMLTools;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.types.scalar.ScalarType;
import ecologylab.serialization.types.scalar.StringType;

@simpl_inherit
@xml_tag("scalar")
public class MetaMetadataScalarField extends MetaMetadataField
{

	/**
	 * The type of the field -- only if it is a scalar.
	 */
	@simpl_scalar
	protected ScalarType	scalarType;

	@simpl_scalar
	protected Hint				hint;

	@simpl_composite
	protected RegexFilter	filter;

	public MetaMetadataScalarField()
	{
	}

	public MetaMetadataScalarField(MetaMetadataField mmf)
	{
		this.name = mmf.name;
		this.extendsAttribute = mmf.extendsAttribute;
		this.hide = mmf.hide;
		this.alwaysShow = mmf.alwaysShow;
		this.style = mmf.style;
		this.layer = mmf.layer;
		this.xpath = mmf.xpath;
		this.navigatesTo = mmf.navigatesTo;
		this.shadows = mmf.shadows;
		this.stringPrefix = mmf.stringPrefix;
		this.isFacet = mmf.isFacet;
		this.ignoreInTermVector = mmf.ignoreInTermVector;
		this.comment = mmf.comment;
		this.dontCompile = mmf.dontCompile;
		this.key = mmf.key;
		this.textRegex = mmf.textRegex;
		this.matchReplacement = mmf.matchReplacement;
		this.contextNode = mmf.contextNode;
		this.tag = mmf.tag;
		this.ignoreExtractionError = mmf.ignoreExtractionError;
		this.kids = mmf.kids;
	}

	/**
	 * @return the scalarType
	 */
	public ScalarType getScalarType()
	{
		return scalarType;
	}

	public Hint getHint()
	{
		return hint;
	}

	public void setHint(Hint hint)
	{
		this.hint = hint;
	}

	@Override
	protected void doAppending(Appendable appendable, int pass) throws IOException
	{
		if (scalarType != null)
			appendScalarLeaf(appendable, pass);
	}

	/**
	 * Writes a Scalar Nested attribute to the class file.It is of the form
	 * 
	 * @xml_tag(tagName) @simpl_composite scalarType name;
	 * @param appendable
	 *          The appendable in which this declaration has to be appended.
	 * @throws IOException
	 */
	protected void appendScalarLeaf(Appendable appendable, int pass) throws IOException
	{
		String fieldName = XMLTools.fieldNameFromElementName(getName());
		// fieldName = MetadataCompilerConstants.handleJavaKeyWord(fieldName);
		String fieldTypeName = scalarType.fieldTypeName();
		if (fieldTypeName.equals("int"))
		{
			// HACK FOR METADATAINTEGER
			fieldTypeName = "Integer";
		}

		if (pass == MetadataCompilerUtils.GENERATE_FIELDS_PASS)
		{
			// write the java doc comment for this field
			MetadataCompilerUtils.writeJavaDocComment(comment, appendable);

			// append the Nested field.
			appendLeaf(appendable, "private Metadata", scalarType.fieldTypeName(), fieldName);
		}
		else if (pass == MetadataCompilerUtils.GENERATE_METHODS_PASS)
		{
			// append the getter and setter methods
			appendLazyEvaluationMethod(appendable, fieldName, "Metadata" + fieldTypeName);
			appendGetter(appendable, fieldName, fieldTypeName);
			appendSetter(appendable, fieldName, fieldTypeName);
			appendHWSetter(appendable, fieldName, fieldTypeName);
			appendDirectSetMethod(appendable, fieldName, "Metadata" + fieldTypeName);
			appendDirectHWSetMethod(appendable, fieldName, "Metadata" + fieldTypeName);
			if (fieldTypeName.equals("StringBuilder"))
			{
				// appendAppendMethod(appendable,fieldName,"StringBuilder");
				appendAppendMethod(appendable, fieldName, "String");
				appendHWAppendMethod(appendable, fieldName, "StringBuilder");
				appendHWAppendMethod(appendable, fieldName, "String");
			}
		}
	}

	/**
	 * Appends scalar field with @simpl_scalar @simpl_hints(Hint.XML_LEAF) annotation
	 * 
	 * @param appendable
	 * @param classNamePrefix
	 * @param className
	 * @param fieldName
	 * @throws IOException
	 */
	protected void appendLeaf(Appendable appendable, String classNamePrefix, String className,
			String fieldName) throws IOException
	{
		if ("int".equals(className))
		{
			// HACK FOR METADATAINTEGER
			className = "Integer";
		}

		StringBuilder annotation = new StringBuilder(" @simpl_scalar");

		if (getHint() != null)
		{
			annotation.append(String.format(" @simpl_hints(Hint.%s)", getHint()));
		}

		if (filter != null && filter.regex != null)
		{
			if (filter.replace != null)
			{
				annotation.append(String.format(" @simpl_filter(regex=\"%s\", replace=\"%s\")",
						filter.regex, filter.replace));
			}
			else
			{
				annotation.append(String.format(" @simpl_filter(regex=\"%s\")", filter.regex));
			}
		}

		appendMetalanguageDecl(appendable, getTagDecl() + annotation, classNamePrefix, className,
				fieldName);
	}

	/**
	 * This method will always return false since scalar fields never generate classes.
	 */
	@Override
	public boolean isNewClass()
	{
		return false;
	}

	public static void main(String[] args) throws SIMPLTranslationException
	{
		testSerialization();
		testDeserialization();
	}

	public static void testSerialization() throws SIMPLTranslationException
	{
		MetaMetadataScalarField mmsf = new MetaMetadataScalarField();
		mmsf.scalarType = new StringType();
		mmsf.hint = Hint.XML_LEAF;
		mmsf.filter = new RegexFilter();
		mmsf.filter.regex = ".";
		mmsf.filter.replace = ".";
		System.out.println(mmsf.serialize());
	}

	public static void testDeserialization() throws SIMPLTranslationException
	{
		String xml = "<scalar name=\"example\" scalar_type=\"String\" hint=\"XML_ATTRIBUTE\"><filter regex=\".\" replace=\".\" /></scalar>";
		MetaMetadataScalarField m = (MetaMetadataScalarField) MetaMetadataTranslationScope.get()
				.deserializeCharSequence(xml);
		System.out.println(m);
	}

}
