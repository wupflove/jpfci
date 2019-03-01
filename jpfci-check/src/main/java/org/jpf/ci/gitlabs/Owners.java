package org.jpf.ci.gitlabs;

public class Owners
{

	private String name;
	private String username;
	private String id;
	private String state;
	private String avatar_url;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getAvatar_url()
	{
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url)
	{
		this.avatar_url = avatar_url;
	}

	@Override
	public String toString()
	{
		return "Owners [name=" + name + ", username=" + username + ", id=" + id
				+ ", state=" + state + ", avatar_url=" + avatar_url + "]";
	}

}
