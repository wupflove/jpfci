package org.jpf.ci.gitlabs;

public class Identities
{

	private String provider;
	private String extern_uid;

	public String getProvider()
	{
		return provider;
	}

	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	public String getExtern_uid()
	{
		return extern_uid;
	}

	public void setExtern_uid(String extern_uid)
	{
		this.extern_uid = extern_uid;
	}

	public Identities(String provider, String extern_uid)
	{
		super();
		this.provider = provider;
		this.extern_uid = extern_uid;
	}

	public Identities()
	{
	}

	@Override
	public String toString()
	{
		return "Identities [provider=" + provider + ", extern_uid=" + extern_uid + "]";
	}
}
