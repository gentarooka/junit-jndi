package junitjndi.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.VisibleForTesting;

import junitjndi.types.JNDINamespace;

/**
 * This class resolves a JNDI entry for each part of the request.
 * 
 * @author PÉRIÉ Fabien
 */
public class JndiEntryResolver
{
	private static final String[] JNDI_NAMES_ALLOWED = new String[JNDINamespace.values().length];

	static
	{
		int index = 0;
		for (JNDINamespace tje : JNDINamespace.values())
		{
			JNDI_NAMES_ALLOWED[index] = tje.getJndiEntry();
			index++;
		}
	}

	@Nullable
	private final String originalName;

	@Nullable
	private final String originalParentName;
	
	@Nonnull
	private final JNDINamespace jndiType;

	@Nonnull
	private final String resolvedName;

	
	
	public JndiEntryResolver(@Nullable final String originalName) throws NamingException
	{
		this(originalName, null, null);
	}
	
	
	@SuppressWarnings("null")
	public JndiEntryResolver(@Nullable final String originalName, @Nullable JNDINamespace parentJndiEntry, @Nullable String originalParentName)
		throws NamingException
	{
		this.originalName = originalName;
		this.originalParentName = originalParentName;
		this.jndiType = parentJndiEntry == null ? detectJndiEntryType(originalName) : parentJndiEntry;
		this.resolvedName = detectResolvedName(this.originalName, this.jndiType, this.originalParentName);
		this.validate();
	}
	

	@Nullable
	public String getOriginalName()
	{
		return originalName;
	}

	@Nullable
	public String getOriginalParentName()
	{
		return originalParentName;
	}

	@SuppressWarnings("null")
	@Nonnull
	public JNDINamespace getJndiType()
	{
		return jndiType;
	}

	@SuppressWarnings("null")
	@Nonnull
	public String getResolvedName()
	{
		return resolvedName;
	}

	@Nonnull
	public String getFullQualifiedName()
	{
		return getJndiType().getJndiEntry() + getResolvedName();
	}

	@VisibleForTesting
	protected void validate() throws NamingException
	{
		if (StringUtils.isBlank(getResolvedName()))
		{
			throw new NamingException("JNDI name is required.");
		}
		
		if (StringUtils.startsWith(getResolvedName(), "/") || StringUtils.contains(getResolvedName(), "//") || StringUtils.endsWith(getResolvedName(), "/"))
		{
			throw new NamingException("JNDI name should not start and end with a slash and the string should not contain a double-slash.");
		}
	}

	@SuppressWarnings("null")
	@VisibleForTesting
	@Nonnull
	protected JNDINamespace detectJndiEntryType(@Nullable final String jndiName)
	{
		for (JNDINamespace tje : JNDINamespace.values())
		{
			if (StringUtils.startsWith(jndiName, tje.getJndiEntry()))
			{
				return tje;
			}
		}

		return JNDINamespace.ROOT;
	}

	@Nonnull
	@VisibleForTesting
	protected String detectResolvedName(@Nullable final String originalName, @Nonnull final JNDINamespace jndiType, @Nullable String originalParentName)
	{
		String parentNameResolved = detectResolvedParentName(jndiType, originalParentName);
		if (StringUtils.isNotBlank(parentNameResolved))
		{
			parentNameResolved += "/";
		}

		if (StringUtils.isBlank(originalName))
		{
			return parentNameResolved + "";
		}
		assert originalName != null;

		if (StringUtils.startsWithAny(originalName, JNDI_NAMES_ALLOWED))
		{
			return parentNameResolved + StringUtils.substringAfter(originalName, detectJndiEntryType(originalName).getJndiEntry());
		}

		if (StringUtils.startsWith(originalName, "/"))
		{
			return parentNameResolved + StringUtils.substringAfter(originalName, "/");
		}

		return parentNameResolved + originalName;
	}

	private String detectResolvedParentName(@Nonnull final JNDINamespace jndiType, @Nullable String originalParentName)
	{
		if (StringUtils.isBlank(originalParentName))
		{
			return "";
		}
		assert originalParentName != null;

		if (StringUtils.startsWith(originalParentName, jndiType.getJndiEntry()))
		{
			return StringUtils.substringAfter(originalParentName, jndiType.getJndiEntry());
		}

		if (StringUtils.startsWith(originalParentName, "/"))
		{
			return StringUtils.substringAfter(originalParentName, "/");
		}

		return originalParentName;
	}
}